package com.sequenceiq.cloudbreak.service.image;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.model.catalog.AmbariImage;
import com.sequenceiq.cloudbreak.cloud.model.catalog.CloudbreakImageCatalogV2;
import com.sequenceiq.cloudbreak.cloud.model.catalog.CloudbreakVersion;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Images;

@Component
public class ImageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);

    private static final String RELEASED_VERSION_PATTERN = "^\\d+\\.\\d+\\.\\d+";

    private static final String UNRELEASED_VERSION_PATTERN = "^\\d+\\.\\d+\\.\\d+-[d,r][c,e][v]?";

    @Value("${info.app.version:}")
    private String cbVersion;

    @Inject
    private ImageCatalogProvider imageCatalogProvider;

    public Images getImages(String provider) {
        return getImages(provider, cbVersion);
    }

    public Images getImages(String platform, String cbVersion) {
        LOGGER.info("Determine images for platform: '{}' and Cloudbreak version: '{}'.", platform, cbVersion);
        Images images = new Images();
        List<AmbariImage> ambariImages = new ArrayList<>();
        CloudbreakImageCatalogV2 imageCatalog = imageCatalogProvider.getImageCatalogV2();
        if (imageCatalog != null) {
            Set<String> vMImageUUIDs = new HashSet<>();
            List<CloudbreakVersion> cloudbreakVersions = imageCatalog.getVersions().getCloudbreakVersions();
            List<CloudbreakVersion> exactMatchedImgs = cloudbreakVersions.stream()
                    .filter(cloudbreakVersion -> cloudbreakVersion.getVersions().contains(cbVersion)).collect(Collectors.toList());

            if (!exactMatchedImgs.isEmpty()) {
                exactMatchedImgs.forEach(cloudbreakVersion -> vMImageUUIDs.addAll(cloudbreakVersion.getImageIds()));
            } else {
                vMImageUUIDs.addAll(prefixMatchForCBVersion(cbVersion, cloudbreakVersions));
            }

            vMImageUUIDs.forEach(imgUUID -> ambariImages.addAll(
                    imageCatalog.getImages().getAmbariImages().stream()
                            .filter(ambariImage -> ambariImage.getUuid().equals(imgUUID))
                            .filter(ambariImage -> ambariImage.getImageSetsByProvider().keySet().stream().anyMatch(p -> p.equalsIgnoreCase(platform)))
                            .collect(Collectors.toSet())));
        }
        images.setAmbariImages(ambariImages);
        return images;
    }

    private Set<String> prefixMatchForCBVersion(String cbVersion, List<CloudbreakVersion> cloudbreakVersions) {
        Set<String> vMImageUUIDs = new HashSet<>();
        String unReleasedVersion = extractCbVersion(UNRELEASED_VERSION_PATTERN, cbVersion);
        boolean versionIsReleased = unReleasedVersion.equals(cbVersion);

        if (!versionIsReleased) {
            Set<CloudbreakVersion> unReleasedCbVersions = cloudbreakVersions.stream()
                    .filter(cloudbreakVersion -> cloudbreakVersion.getVersions().stream().anyMatch(aVersion -> aVersion.startsWith(unReleasedVersion)))
                    .collect(Collectors.toSet());
            unReleasedCbVersions.stream().forEach(cloudbreakVersion -> vMImageUUIDs.addAll(cloudbreakVersion.getImageIds()));
        }

        if (versionIsReleased || vMImageUUIDs.isEmpty()) {
            String releasedVersion = extractCbVersion(RELEASED_VERSION_PATTERN, cbVersion);
            Set<CloudbreakVersion> releasedCbVersions = cloudbreakVersions.stream()
                    .filter(cloudbreakVersion -> cloudbreakVersion.getVersions().contains(releasedVersion)).collect(Collectors.toSet());
            releasedCbVersions.stream().forEach(cloudbreakVersion -> vMImageUUIDs.addAll(cloudbreakVersion.getImageIds()));
        }
        return vMImageUUIDs;
    }

    private String extractCbVersion(String pattern, String cbVersion) {
        Matcher matcher = Pattern.compile(pattern).matcher(cbVersion);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return cbVersion;
    }
}
