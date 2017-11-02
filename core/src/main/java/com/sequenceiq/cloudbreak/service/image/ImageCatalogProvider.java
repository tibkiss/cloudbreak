package com.sequenceiq.cloudbreak.service.image;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.client.RestClientUtil;
import com.sequenceiq.cloudbreak.cloud.model.catalog.CloudbreakImageCatalogV2;
import com.sequenceiq.cloudbreak.cloud.model.catalog.CloudbreakVersion;
import com.sequenceiq.cloudbreak.core.CloudbreakImageInvalidException;
import com.sequenceiq.cloudbreak.util.FileReaderUtils;
import com.sequenceiq.cloudbreak.util.JsonUtil;

@Service
public class ImageCatalogProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCatalogProvider.class);

    @Value("${cb.image.catalog.url}")
    private String defaultCatalogUrl;

    @Value("${cb.etc.config.dir}")
    private String etcConfigDir;

    public CloudbreakImageCatalogV2 getImageCatalogV2() {
        CloudbreakImageCatalogV2 catalog = null;
        if (defaultCatalogUrl == null) {
            LOGGER.warn("No image catalog was defined!");
            return catalog;
        }

        try {
            long started = System.currentTimeMillis();
            if (defaultCatalogUrl.startsWith("http")) {
                Client client = RestClientUtil.get();
                WebTarget target = client.target(defaultCatalogUrl);
                catalog = target.request().get().readEntity(CloudbreakImageCatalogV2.class);
            } else {
                String content = readCatalogFromFile(defaultCatalogUrl);
                catalog = JsonUtil.readValue(content, CloudbreakImageCatalogV2.class);
            }
            long timeOfParse = System.currentTimeMillis() - started;
            LOGGER.info("ImageCatalog has been get and parsed from '{}' and took '{}' ms.", defaultCatalogUrl, timeOfParse);
        } catch (RuntimeException e) {
            LOGGER.warn("Failed to get image catalog", e);
        } catch (IOException e) {
            LOGGER.warn(String.format("Failed to read image catalog from file: '%s'", defaultCatalogUrl), e);
        }
        return catalog;
    }

    public void validateImageCatalogUuids(CloudbreakImageCatalogV2 imageCatalog) throws CloudbreakImageInvalidException {
        List<String> uuidList = imageCatalog.getImages().getBaseImages().stream().map(images -> images.getUuid()).collect(Collectors.toList());
        for (CloudbreakVersion version :imageCatalog.getVersions().getCloudbreakVersions()) {
            for (String imageId : version.getImageIds()) {
                if (!uuidList.contains(imageId)) {
                    throw new CloudbreakImageInvalidException(String.format("Image with id: %s is not present in ambari-images block", imageId));
                }

            }
        }
    }

    public String getDefaultCatalogUrl() {
        return defaultCatalogUrl;
    }

    public void setDefaultCatalogUrl(String defaultCatalogUrl) {
        this.defaultCatalogUrl = defaultCatalogUrl;
    }

    void setEtcConfigDir(String etcConfigDir) {
        this.etcConfigDir = etcConfigDir;
    }

    private String readCatalogFromFile(String catalogUrl) throws IOException {
        File customCatalogFile = new File(etcConfigDir, catalogUrl);
        return FileReaderUtils.readFileFromPath(customCatalogFile.toPath());
    }
}
