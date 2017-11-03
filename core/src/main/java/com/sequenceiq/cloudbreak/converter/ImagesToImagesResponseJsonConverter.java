package com.sequenceiq.cloudbreak.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.model.imagecatalog.HDFImageJson;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.HDPImageJson;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.ImageJson;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.ImagesResponse;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.StackDetailsJson;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.StackRepoDetailsJson;
import com.sequenceiq.cloudbreak.cloud.model.catalog.HDFImage;
import com.sequenceiq.cloudbreak.cloud.model.catalog.HDPImage;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Image;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Images;
import com.sequenceiq.cloudbreak.cloud.model.catalog.StackDetails;
import com.sequenceiq.cloudbreak.cloud.model.catalog.StackRepoDetails;

@Component
public class ImagesToImagesResponseJsonConverter extends AbstractConversionServiceAwareConverter<Images, ImagesResponse> {

    @Override
    public ImagesResponse convert(Images source) {
        ImagesResponse res = new ImagesResponse();
        List<ImageJson> baseImages = new ArrayList<>();
        for (Image baseImg: source.getBaseImages()) {
            ImageJson imgJson = new ImageJson();
            copyImageFieldsToJson(baseImg, imgJson);
            baseImages.add(imgJson);
        }
        res.setBaseImages(baseImages);

        List<HDPImageJson> hdpImages = new ArrayList<>();
        for (HDPImage hdpImg: source.getHdpImages()) {
            HDPImageJson hdpImgJson = new HDPImageJson();
            copyImageFieldsToJson(hdpImg, hdpImgJson);
            hdpImgJson.setHdp(convertStackDetailsToJson(hdpImg.getHdp()));
            hdpImages.add(hdpImgJson);
        }
        res.setHdpImages(hdpImages);

        List<HDFImageJson> hdfImages = new ArrayList<>();
        for (HDFImage hdfImg: source.getHdfImages()) {
            HDFImageJson hdfImgJson = new HDFImageJson();
            copyImageFieldsToJson(hdfImg, hdfImgJson);
            hdfImgJson.setHdf(convertStackDetailsToJson(hdfImg.getHdf()));
            hdfImages.add(hdfImgJson);
        }
        res.setHdfImages(hdfImages);

        return res;
    }

    private void copyImageFieldsToJson(Image source, ImageJson json) {
        json.setDate(source.getDate());
        json.setDescription(source.getDescription());
        json.setOs(source.getOs());
        json.setUuid(source.getUuid());
        json.setVersion(source.getVersion());
        json.setRepo(new HashMap<>(source.getRepo()));
        json.setImageSetsByProvider(new HashMap<>(source.getImageSetsByProvider()));
    }

    private StackDetailsJson convertStackDetailsToJson(StackDetails stackDetails) {
        StackDetailsJson json = new StackDetailsJson();
        json.setVersion(stackDetails.getVersion());
        json.setRepo(convertStackRepoDetailsToJson(stackDetails.getRepo()));
        return json;
    }

    private StackRepoDetailsJson convertStackRepoDetailsToJson(StackRepoDetails repo) {
        StackRepoDetailsJson json = new StackRepoDetailsJson();
        json.setStack(new HashMap<>(repo.getStack()));
        json.setUtil(new HashMap<>(repo.getUtil()));
        if (repo.getKnox() != null && !repo.getKnox().isEmpty()) {
            json.setKnox(repo.getKnox());
        }
        return json;
    }
}
