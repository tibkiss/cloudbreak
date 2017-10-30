package com.sequenceiq.cloudbreak.service.image;

import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sequenceiq.cloudbreak.cloud.model.catalog.CloudbreakImageCatalogV2;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Images;
import com.sequenceiq.cloudbreak.util.FileReaderUtils;
import com.sequenceiq.cloudbreak.util.JsonUtil;

@RunWith(MockitoJUnitRunner.class)
public class ImageCatalogServiceTest {

    @Mock
    private ImageCatalogProvider imageCatalogProvider;

    @InjectMocks
    private ImageCatalogService underTest;

    @Before
    public void beforeTest() throws IOException {
        String catalogJson = FileReaderUtils.readFileFromClasspath("com/sequenceiq/cloudbreak/service/image/cb-image-catalog-v2.json");
        CloudbreakImageCatalogV2 cloudbreakImageCatalog = JsonUtil.readValue(catalogJson, CloudbreakImageCatalogV2.class);
        when(imageCatalogProvider.getImageCatalogV2()).thenReturn(cloudbreakImageCatalog);
    }

    @Test
    public void testGetImagesWhenExactVersionExistsInCatalog() throws Exception {
        Images images = underTest.getImages("aws", "1.16.4");
        Assert.assertFalse("Result contains no Ambari Image for the version.", images.getAmbariImages().isEmpty());
        boolean exactImageIdMatch = images.getAmbariImages().stream()
                .anyMatch(ambariImage -> ambariImage.getUuid().equals("2.5.1.9-4-a3613863-37c0-4046-8cd8-cfe52f10f7fa-2.6.1.4-2"));
        Assert.assertTrue("Result doesn't contain the required Ambari image with id.", exactImageIdMatch);
    }

    @Test
    public void testGetImagesWhenSimilarDevVersionDoesntExistInCatalog() throws Exception {
        Images images = underTest.getImages("aws", "1.16.4-dev.132");
        Assert.assertFalse("Result contains no Ambari Image for the version.", images.getAmbariImages().isEmpty());
        boolean exactImageIdMatch = images.getAmbariImages().stream()
                .anyMatch(ambariImage -> ambariImage.getUuid().equals("2.5.1.9-4-a3613863-37c0-4046-8cd8-cfe52f10f7fa-2.6.1.4-2"));
        Assert.assertTrue("Result doesn't contain the required Ambari image with id.", exactImageIdMatch);
    }

    @Test
    public void testGetImagesWhenSimilarRcVersionDoesntExistInCatalog() throws Exception {
        Images images = underTest.getImages("aws", "1.16.4-rc.13");
        Assert.assertFalse("Result contains no Ambari Image for the version.", images.getAmbariImages().isEmpty());
        boolean exactImageIdMatch = images.getAmbariImages().stream()
                .anyMatch(ambariImage -> ambariImage.getUuid().equals("2.5.1.9-4-a3613863-37c0-4046-8cd8-cfe52f10f7fa-2.6.1.4-2"));
        Assert.assertTrue("Result doesn't contain the required Ambari image with id.", exactImageIdMatch);
    }

    @Test
    public void testGetImagesWhenSimilarDevVersionExistsInCatalog() throws Exception {
        Images images = underTest.getImages("aws", "2.1.0-dev.4000");
        Assert.assertFalse("Result contains no Ambari Image for the version.", images.getAmbariImages().isEmpty());
        boolean exactImageIdMatch = images.getAmbariImages().stream()
                .anyMatch(ambariImage -> ambariImage.getUuid().equals("2.5.0.2-65-4efa2a89-0c08-4a54-b9cd-c94bf7a11eb3-2.6.0.1-152"));
        Assert.assertTrue("Result doesn't contain the required Ambari image with id.", exactImageIdMatch);
    }

    @Test
    public void testGetImagesWhenSimilarRcVersionExistsInCatalog() throws Exception {
        Images images = underTest.getImages("aws", "2.0.0-rc.4");
        Assert.assertFalse("Result contains no Ambari Image for the version.", images.getAmbariImages().isEmpty());
        boolean exactImageIdMatch = images.getAmbariImages().stream()
                .anyMatch(ambariImage -> ambariImage.getUuid().equals("2.5.0.2-65-4efa2a89-0c08-4a54-b9cd-c94bf7a11eb3-2.6.0.1-152"));
        Assert.assertTrue("Result doesn't contain the required Ambari image with id.", exactImageIdMatch);
    }

    @Test
    public void testGetImagesWhenExactVersionExistsInCatalogForPlatform() throws Exception {
        Images images = underTest.getImages("AWS", "1.16.4");
        Assert.assertFalse("Result contains no Ambari Image for the version and platform.", images.getAmbariImages().isEmpty());
        boolean exactImageIdMatch = images.getAmbariImages().stream()
                .anyMatch(ambariImage -> ambariImage.getUuid().equals("2.5.1.9-4-a3613863-37c0-4046-8cd8-cfe52f10f7fa-2.6.1.4-2"));
        Assert.assertTrue("Result doesn't contain the required Ambari image with id for the platform.", exactImageIdMatch);
    }

    @Test
    public void testGetImagesWhenExactVersionDoesnotExistInCatalogForPlatform() throws Exception {
        Images images = underTest.getImages("owncloud", "1.16.4");
        Assert.assertTrue("Result contains no Ambari Image for the version and platform.", images.getAmbariImages().isEmpty());
    }

    @Test
    public void testGetImagesWhenOnlyVersionWithSimilarPatchVersionExistInCatalog() throws Exception {

    }

    @Test
    public void testGetImagesWhenOnlyVersionWithSimilarMinorVersionExistInCatalog() throws Exception {

    }

    @Test
    public void testGetImagesWhenOnlyVersionWithSimilarMajorVersionExistInCatalog() throws Exception {

    }
}