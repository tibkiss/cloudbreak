package com.sequenceiq.cloudbreak.converter;

import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.model.InstanceGroupResponse;
import com.sequenceiq.cloudbreak.api.model.InstanceMetaDataJson;
import com.sequenceiq.cloudbreak.api.model.SecurityGroupResponse;
import com.sequenceiq.cloudbreak.api.model.TemplateResponse;
import com.sequenceiq.cloudbreak.domain.InstanceGroup;
import com.sequenceiq.cloudbreak.domain.InstanceMetaData;
import com.sequenceiq.cloudbreak.domain.json.Json;

@Component
public class InstanceGroupToJsonConverter extends AbstractConversionServiceAwareConverter<InstanceGroup, InstanceGroupResponse> {

    @Override
    public InstanceGroupResponse convert(InstanceGroup entity) {
        InstanceGroupResponse instanceGroupResponse = new InstanceGroupResponse();
        instanceGroupResponse.setGroup(entity.getGroupName());
        instanceGroupResponse.setId(entity.getId());
        instanceGroupResponse.setNodeCount(entity.getNodeCount());
        if (entity.getTemplate() != null) {
            instanceGroupResponse.setTemplate(getConversionService().convert(entity.getTemplate(), TemplateResponse.class));
        }
        instanceGroupResponse.setType(entity.getInstanceGroupType());
        instanceGroupResponse.setMetadata(convertEntitiesToJson(entity.getInstanceMetaData()));
        if (entity.getSecurityGroup() != null) {
            instanceGroupResponse.setSecurityGroup(getConversionService().convert(entity.getSecurityGroup(), SecurityGroupResponse.class));
        }
        Json attributes = entity.getAttributes();
        if (attributes != null) {
            instanceGroupResponse.setParameters(attributes.getMap());
        }
        return instanceGroupResponse;
    }

    private Set<InstanceMetaDataJson> convertEntitiesToJson(Set<InstanceMetaData> metadata) {
        return (Set<InstanceMetaDataJson>) getConversionService().convert(metadata,
                TypeDescriptor.forObject(metadata),
                TypeDescriptor.collection(Set.class, TypeDescriptor.valueOf(InstanceMetaDataJson.class)));
    }

}
