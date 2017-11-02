package com.sequenceiq.cloudbreak.service.image;

import static com.sequenceiq.cloudbreak.cloud.model.Platform.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sequenceiq.cloudbreak.api.model.InstanceGroupType;
import com.sequenceiq.cloudbreak.client.PkiUtil;
import com.sequenceiq.cloudbreak.cloud.PlatformParameters;
import com.sequenceiq.cloudbreak.cloud.model.AmbariRepo;
import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.cloud.model.Platform;
import com.sequenceiq.cloudbreak.cloud.model.catalog.HDFImage;
import com.sequenceiq.cloudbreak.cloud.model.catalog.HDPImage;
import com.sequenceiq.cloudbreak.common.type.ComponentType;
import com.sequenceiq.cloudbreak.core.CloudbreakImageNotFoundException;
import com.sequenceiq.cloudbreak.domain.Component;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.domain.json.Json;
import com.sequenceiq.cloudbreak.service.CloudbreakServiceException;
import com.sequenceiq.cloudbreak.service.ComponentConfigProvider;

@Service
@Transactional
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private static final String DEFAULT_REGION = "default";

    @Inject
    private UserDataBuilder userDataBuilder;

    @Inject
    private ComponentConfigProvider componentConfigProvider;

    @Inject
    private ImageCatalogService imageCatalogService;

    public Image getImage(Long stackId) throws CloudbreakImageNotFoundException {
        return componentConfigProvider.getImage(stackId);
    }

    @Transactional(TxType.NEVER)
    public void create(Stack stack, PlatformParameters params, String ambariVersion, String hdpVersion, String imageCatalog, Optional<String> imageId)
            throws CloudbreakImageNotFoundException {
        try {
            Platform platform = platform(stack.cloudPlatform());
            String platformString = platform(stack.cloudPlatform()).value().toLowerCase();
            String region = stack.getRegion();
            String cbPrivKey = stack.getSecurityConfig().getCloudbreakSshPrivateKeyDecoded();
            String cbSshKey = stack.getSecurityConfig().getCloudbreakSshPublicKeyDecoded();
            byte[] cbSshKeyDer = PkiUtil.getPublicKeyDer(cbPrivKey);
            String sshUser = stack.getStackAuthentication().getLoginUserName();
            Map<InstanceGroupType, String> userData = userDataBuilder.buildUserData(platform, cbSshKeyDer, cbSshKey, sshUser, params,
                    stack.getSecurityConfig().getSaltBootPassword());

            com.sequenceiq.cloudbreak.cloud.model.catalog.Image imgFromCatalog = determineImageFromCatalog(imageId, platformString);
            LOGGER.info("Determined image from catalog: {}", imgFromCatalog);

            String imageName = determineImageName(platformString, region, imgFromCatalog);
            LOGGER.info("Selected VM image for CloudPlatform '{}' and region '{}' is: {}", platformString, region, imageName);

            List<Component> components = getComponents(stack, userData, imgFromCatalog, imageName);
            componentConfigProvider.store(components);
        } catch (JsonProcessingException e) {
            throw new CloudbreakServiceException("Failed to create json", e);
        }
    }

    private com.sequenceiq.cloudbreak.cloud.model.catalog.Image determineImageFromCatalog(Optional<String> imageId, String platformString)
            throws CloudbreakImageNotFoundException {
        com.sequenceiq.cloudbreak.cloud.model.catalog.Image imgFromCatalog = null;
        if (imageId.isPresent()) {
            imgFromCatalog = imageCatalogService.getImage(imageId.get());
        } else {
            LOGGER.warn("Image id hasn't been specified for the stack, falling back to a base image.");
            imgFromCatalog = imageCatalogService.getBaseImages(platformString).stream().findFirst().get();
        }
        return imgFromCatalog;
    }

    private String determineImageName(String platformString, String region, com.sequenceiq.cloudbreak.cloud.model.catalog.Image imgFromCatalog)
            throws CloudbreakImageNotFoundException {
        String imageName;
        Optional<Map<String, String>> imagesForPlatform = findStringKeyWithEqualsIgnoreCase(platformString, imgFromCatalog.getImageSetsByProvider());
        if (imagesForPlatform.isPresent()) {
            Map<String, String> imagesByRegion = imagesForPlatform.get();
            Optional<String> imageNameOpt = findStringKeyWithEqualsIgnoreCase(region, imagesByRegion);
            if (!imageNameOpt.isPresent()) {
                imageNameOpt = findStringKeyWithEqualsIgnoreCase(DEFAULT_REGION, imagesByRegion);
            }
            if (imageNameOpt.isPresent()) {
                imageName = imageNameOpt.get();
            } else {
                String msg = String.format("Virtual machine image couldn't found in image: '%s' for the selected platform: '%s' and region: '%s'.",
                        imgFromCatalog, platformString, region);
                throw new CloudbreakImageNotFoundException(msg);
            }
        } else {
            String msg = String.format("The selected image: '%s' doesn't contain virtual machine image for the selected platform: '%s'.",
                    imgFromCatalog, platformString);
            throw new CloudbreakImageNotFoundException(msg);
        }
        return imageName;
    }

    private <T> Optional<T> findStringKeyWithEqualsIgnoreCase(String key, Map<String, T> map) {
        return map.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(key))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private List<Component> getComponents(Stack stack, Map<InstanceGroupType, String> userData,
            com.sequenceiq.cloudbreak.cloud.model.catalog.Image imgFromCatalog, String imageName) throws JsonProcessingException {
        List<Component> components = new ArrayList<>();
        Image image = new Image(imageName, userData);
        Component imageComponent = new Component(ComponentType.IMAGE, ComponentType.IMAGE.name(), new Json(image), stack);
        components.add(imageComponent);

        if (imgFromCatalog instanceof HDPImage) {
            HDPImage hdpImage = (HDPImage) imgFromCatalog;
            components.add(getAmbariComponent(stack, hdpImage.getVersion()));
            Component hdpRepoComponent = new Component(ComponentType.HDP_REPO_DETAILS, ComponentType.HDP_REPO_DETAILS.name(),
                    new Json(hdpImage.getHdp()), stack);
            components.add(hdpRepoComponent);
        } else if (imgFromCatalog instanceof HDFImage) {
            HDFImage hdfImage = (HDFImage) imgFromCatalog;
            components.add(getAmbariComponent(stack, hdfImage.getVersion()));
            Component hdpRepoComponent = new Component(ComponentType.HDF_REPO_DETAILS, ComponentType.HDF_REPO_DETAILS.name(),
                    new Json(hdfImage.getHdf()), stack);
            components.add(hdpRepoComponent);
        }
        return components;
    }

    private Component getAmbariComponent(Stack stack, String version) throws JsonProcessingException {
        AmbariRepo ambariRepo = new AmbariRepo();
        ambariRepo.setPredefined(Boolean.TRUE);
        ambariRepo.setVersion(version);
        return new Component(ComponentType.AMBARI_REPO_DETAILS, ComponentType.AMBARI_REPO_DETAILS.name(),
                new Json(ambariRepo), stack);
    }
}
