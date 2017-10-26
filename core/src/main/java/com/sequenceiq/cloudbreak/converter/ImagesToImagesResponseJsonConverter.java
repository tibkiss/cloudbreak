package com.sequenceiq.cloudbreak.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.model.imagecatalog.AmbariImageJson;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.HDPDetailsJson;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.HDPRepoDetailsJson;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.ImagesResponse;
import com.sequenceiq.cloudbreak.cloud.model.catalog.AmbariImage;
import com.sequenceiq.cloudbreak.cloud.model.catalog.HDPDetails;
import com.sequenceiq.cloudbreak.cloud.model.catalog.HDPRepoDetails;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Images;

@Component
public class ImagesToImagesResponseJsonConverter extends AbstractConversionServiceAwareConverter<Images, ImagesResponse> {

    @Override
    public ImagesResponse convert(Images source) {
        ImagesResponse res = new ImagesResponse();
        List<AmbariImageJson> ambariImageJsons = new ArrayList<>();
        for (AmbariImage ambariImage: source.getAmbariImages()) {
            AmbariImageJson ambariImageJson = convertAmbariImageToJson(ambariImage);
            ambariImageJsons.add(ambariImageJson);
        }
        res.setAmbariImages(ambariImageJsons);
        return res;
    }

    private AmbariImageJson convertAmbariImageToJson(AmbariImage ambariImage) {
        AmbariImageJson json = new AmbariImageJson();
        json.setDate(ambariImage.getDate());
        json.setDescription(ambariImage.getDescription());
        json.setOs(ambariImage.getOs());
        json.setPrewarm(ambariImage.isPrewarm());
        json.setUuid(ambariImage.getUuid());
        json.setVersion(ambariImage.getVersion());
        json.setRepo(new HashMap<>(ambariImage.getRepo()));
        json.setHdp(convertHDPDetailsToJson(ambariImage.getHdp()));
        json.setImageSetsByProvider(new HashMap<>(ambariImage.getImageSetsByProvider()));
        return json;
    }

    private HDPDetailsJson convertHDPDetailsToJson(HDPDetails hdpDetails) {
        HDPDetailsJson json = new HDPDetailsJson();
        json.setVersion(hdpDetails.getVersion());
        json.setRepo(convertHDPRepoDetailsToJson(hdpDetails.getRepo()));
        return json;
    }

    private HDPRepoDetailsJson convertHDPRepoDetailsToJson(HDPRepoDetails repo) {
        HDPRepoDetailsJson json = new HDPRepoDetailsJson();
        json.setStack(new HashMap<>(repo.getStack()));
        json.setUtil(new HashMap<>(repo.getUtil()));
        return json;
    }
}
