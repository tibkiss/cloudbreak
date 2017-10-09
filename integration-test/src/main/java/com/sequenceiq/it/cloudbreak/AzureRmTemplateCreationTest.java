package com.sequenceiq.it.cloudbreak;

import java.util.List;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.sequenceiq.cloudbreak.api.model.TemplateRequest;

public class AzureRmTemplateCreationTest extends AbstractCloudbreakIntegrationTest {
    @Inject
    private TemplateAdditionHelper templateAdditionHelper;

    private List<TemplateAddition> additions;

    @BeforeMethod
    @Parameters("templateAdditions")
    public void setup(@Optional("master,1;slave_1,3") String templateAdditions) {
        additions = templateAdditionHelper.parseTemplateAdditions(templateAdditions);
    }

    @Test
    @Parameters({ "azureTemplateName", "azureVmType", "azureVolumeCount", "azureVolumeSize" })
    public void testAzureTemplateCreation(@Optional("it-azure-template") String azureTemplateName, @Optional("MEDIUM") String azureVmType,
            @Optional("1") String azureVolumeCount, @Optional("10") String azureVolumeSize) throws Exception {
        // GIVEN
        // WHEN
        TemplateRequest templateRequest = new TemplateRequest();
        templateRequest.setDescription("AZURE_RM template for integration testing");
        templateRequest.setInstanceType(azureVmType);
        templateRequest.setVolumeType("Standard_LRS");
        templateRequest.setVolumeCount(Integer.valueOf(azureVolumeCount));
        templateRequest.setVolumeSize(Integer.valueOf(azureVolumeSize));
        templateAdditionHelper.handleTemplateAdditions(getItContext(), templateRequest, additions);
    }
}
