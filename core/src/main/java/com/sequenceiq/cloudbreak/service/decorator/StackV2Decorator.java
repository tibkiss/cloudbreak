package com.sequenceiq.cloudbreak.service.decorator;

import static com.sequenceiq.cloudbreak.common.type.CloudConstants.BYOS;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.model.AdjustmentType;
import com.sequenceiq.cloudbreak.api.model.InstanceGroupType;
import com.sequenceiq.cloudbreak.api.model.TemplateRequest;
import com.sequenceiq.cloudbreak.api.model.v2.StackV2Request;
import com.sequenceiq.cloudbreak.cloud.model.Orchestrator;
import com.sequenceiq.cloudbreak.cloud.model.Platform;
import com.sequenceiq.cloudbreak.cloud.model.PlatformOrchestrators;
import com.sequenceiq.cloudbreak.common.model.user.IdentityUser;
import com.sequenceiq.cloudbreak.controller.BadRequestException;
import com.sequenceiq.cloudbreak.controller.validation.template.TemplateValidator;
import com.sequenceiq.cloudbreak.domain.Credential;
import com.sequenceiq.cloudbreak.domain.FailurePolicy;
import com.sequenceiq.cloudbreak.domain.FlexSubscription;
import com.sequenceiq.cloudbreak.domain.InstanceGroup;
import com.sequenceiq.cloudbreak.domain.Network;
import com.sequenceiq.cloudbreak.domain.SecurityGroup;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.domain.Template;
import com.sequenceiq.cloudbreak.service.credential.CredentialService;
import com.sequenceiq.cloudbreak.service.flex.FlexSubscriptionService;
import com.sequenceiq.cloudbreak.service.network.NetworkService;
import com.sequenceiq.cloudbreak.service.securitygroup.SecurityGroupService;
import com.sequenceiq.cloudbreak.service.stack.CloudParameterService;
import com.sequenceiq.cloudbreak.service.stack.connector.adapter.ServiceProviderCredentialAdapter;
import com.sequenceiq.cloudbreak.service.stack.flow.ConsulUtils.ConsulServers;
import com.sequenceiq.cloudbreak.service.template.TemplateService;

@Service
public class StackV2Decorator implements Decorator<Stack, StackV2Request> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackV2Decorator.class);

    private static final double ONE_HUNDRED = 100.0;

    @Inject
    private CredentialService credentialService;

    @Inject
    private NetworkService networkService;

    @Inject
    private TemplateService templateService;

    @Inject
    private SecurityGroupService securityGroupService;

    @Inject
    private Decorator<Template, TemplateRequest> templateDecorator;

    @Inject
    private TemplateValidator templateValidator;

    @Inject
    private ServiceProviderCredentialAdapter credentialAdapter;

    @Inject
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Inject
    private CloudParameterService cloudParameterService;

    @Inject
    private FlexSubscriptionService flexSubscriptionService;


    @Override
    public Stack decorate(Stack subject, StackV2Request request, IdentityUser user, Object... data) {
        prepareDomainIfDefined(subject, user);
        Object credentialId = request.getCredentialId();
        String credentialName = request.getCredentialName();
        if (credentialId != null || subject.getCredential() != null || credentialName != null) {
            Object networkId = request.getNetworkId();

            prepareCredential(subject, credentialId, credentialName, user);
            subject.setCloudPlatform(subject.getCredential().cloudPlatform());
            if (subject.getInstanceGroups() == null || (networkId == null && subject.getNetwork() == null
                    && !BYOS.equals(subject.getCredential().cloudPlatform()))) {
                throw new BadRequestException("Instance groups and network must be specified!");
            }
            prepareNetwork(subject, networkId);
            prepareOrchestratorIfNotExist(subject, subject.getCredential());
            if (subject.getFailurePolicy() != null) {
                validatFailurePolicy(subject, subject.getFailurePolicy());
            }
            prepareInstanceGroups(subject, user);
            prepareFlexSubscription(subject, request.getFlexId());
            validate(subject);
        }
        return subject;
    }

    private void prepareCredential(Stack subject, Object credentialId, String credentialName, IdentityUser user) {
        if (credentialId != null) {
            Credential credential = credentialService.get((Long) credentialId);
            subject.setCredential(credential);
        }
        if (credentialName != null) {
            Credential credential = credentialService.getPublicCredential(credentialName, user);
            subject.setCredential(credential);
        }
    }

    private void prepareNetwork(Stack subject, Object networkId) {
        if (networkId != null) {
            subject.setNetwork(networkService.getById((Long) networkId));
            if (subject.getOrchestrator() != null && ((subject.getOrchestrator().getApiEndpoint() != null || subject.getOrchestrator().getType() == null)
                    && !BYOS.equals(subject.cloudPlatform()))) {
                throw new BadRequestException("Orchestrator cannot be configured for the stack!");
            }
        }
    }

    private void prepareInstanceGroups(Stack subject, IdentityUser user) {
        for (InstanceGroup instanceGroup : subject.getInstanceGroups()) {
            if (instanceGroup.getTemplate() != null) {
                Template template = instanceGroup.getTemplate();
                template.setPublicInAccount(subject.isPublicInAccount());
                template.setCloudPlatform(subject.cloudPlatform());
                templateValidator.validateTemplateRequest(instanceGroup.getTemplate());
                template = templateDecorator.decorate(template, null, user);
                template = templateService.create(user, template);
                instanceGroup.setTemplate(template);
            }
            if (instanceGroup.getSecurityGroup() != null) {
                SecurityGroup securityGroup = instanceGroup.getSecurityGroup();
                securityGroup.setPublicInAccount(subject.isPublicInAccount());
                securityGroup.setCloudPlatform(subject.cloudPlatform());
                securityGroup = securityGroupService.create(user, securityGroup);
                instanceGroup.setSecurityGroup(securityGroup);
            }
        }
    }

    private void prepareDomainIfDefined(Stack subject, IdentityUser user) {
        if (subject.getNetwork() != null) {
            Network network = subject.getNetwork();
            network.setPublicInAccount(subject.isPublicInAccount());
            network.setCloudPlatform(subject.cloudPlatform());
            network = networkService.create(user, network);
            subject.setNetwork(network);
        }
        if (subject.getCredential() != null) {
            Credential credential = subject.getCredential();
            credential.setPublicInAccount(subject.isPublicInAccount());
            credential.setCloudPlatform(subject.cloudPlatform());
            credential = credentialAdapter.init(credential);
            credential = credentialService.create(user, credential);
            subject.setCredential(credential);
        }
    }

    private void prepareOrchestratorIfNotExist(Stack subject, Credential credential) {
        if (subject.getOrchestrator() == null) {
            PlatformOrchestrators orchestrators = cloudParameterService.getOrchestrators();
            Orchestrator orchestrator = orchestrators.getDefaults().get(Platform.platform(credential.cloudPlatform()));
            com.sequenceiq.cloudbreak.domain.Orchestrator orchestratorObject = new com.sequenceiq.cloudbreak.domain.Orchestrator();
            orchestratorObject.setType(orchestrator.value());
            subject.setOrchestrator(orchestratorObject);
        }
    }

    private void validate(Stack stack) {
        long instanceGroups = stack.getInstanceGroups().stream().filter(ig -> InstanceGroupType.GATEWAY.equals(ig.getInstanceGroupType())).count();
        if (instanceGroups == 0 && !BYOS.equals(stack.cloudPlatform())) {
            throw new BadRequestException("Gateway instance group not configured");
        }
        int minNodeCount = ConsulServers.SINGLE_NODE_COUNT_LOW.getMin();
        int fullNodeCount = stack.getFullNodeCount();
        if (fullNodeCount < minNodeCount) {
            throw new BadRequestException(String.format("At least %s nodes are required to launch the stack", minNodeCount));
        }
    }

    private void validatFailurePolicy(Stack stack, FailurePolicy failurePolicy) {
        if (failurePolicy.getThreshold() == 0L && !AdjustmentType.BEST_EFFORT.equals(failurePolicy.getAdjustmentType())) {
            throw new BadRequestException("The threshold can not be 0");
        }
        if (AdjustmentType.EXACT.equals(failurePolicy.getAdjustmentType())) {
            validateExactCount(stack, failurePolicy);
        } else if (AdjustmentType.PERCENTAGE.equals(failurePolicy.getAdjustmentType())) {
            validatePercentageCount(failurePolicy);
        }
    }

    private void validatePercentageCount(FailurePolicy failurePolicy) {
        if (failurePolicy.getThreshold() < 0L || failurePolicy.getThreshold() > ONE_HUNDRED) {
            throw new BadRequestException("The percentage of the threshold has to be between 0 an 100.");
        }
    }

    private void validateExactCount(Stack stack, FailurePolicy failurePolicy) {
        if (failurePolicy.getThreshold() > stack.getFullNodeCount()) {
            throw new BadRequestException("Threshold can not be higher than the node count of the stack.");
        }
    }

    private void prepareFlexSubscription(Stack subject, Long flexId) {
        if (flexId != null) {
            FlexSubscription flexSubscription = flexSubscriptionService.findOneById(flexId);
            subject.setFlexSubscription(flexSubscription);
        }
    }

}