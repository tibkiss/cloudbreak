package com.sequenceiq.cloudbreak.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.model.AmbariRepoDetailsJson;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.BaseImageResponse;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.HDFImageResponse;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.HDPImageResponse;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.ImageResponseBase;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.ImagesResponse;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.StackDetailsJson;
import com.sequenceiq.cloudbreak.api.model.imagecatalog.StackRepoDetailsJson;
import com.sequenceiq.cloudbreak.cloud.model.AmbariRepo;
import com.sequenceiq.cloudbreak.cloud.model.catalog.HDFImage;
import com.sequenceiq.cloudbreak.cloud.model.catalog.HDPImage;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Image;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Images;
import com.sequenceiq.cloudbreak.cloud.model.catalog.StackDetails;
import com.sequenceiq.cloudbreak.cloud.model.catalog.StackRepoDetails;
import com.sequenceiq.cloudbreak.cloud.model.component.DefaultHDFEntries;
import com.sequenceiq.cloudbreak.cloud.model.component.DefaultHDPEntries;
import com.sequenceiq.cloudbreak.cloud.model.component.StackInfo;

@Component
public class ImagesToImagesResponseJsonConverter extends AbstractConversionServiceAwareConverter<Images, ImagesResponse> {

    @Inject
    private DefaultHDPEntries defaultHDPEntries;

    @Inject
    private DefaultHDFEntries defaultHDFEntries;

    @Inject
    private AmbariRepoDetailsJsonToAmbariRepoConverter ambariRepoConverter;

    @Override
    public ImagesResponse convert(Images source) {
        ImagesResponse res = new ImagesResponse();
        List<BaseImageResponse> baseImages = getBaseImageResponses(source);
        res.setBaseImages(baseImages);

        List<HDPImageResponse> hdpImages = new ArrayList<>();
        for (HDPImage hdpImg : source.getHdpImages()) {
            HDPImageResponse hdpImgJson = new HDPImageResponse();
            copyImageFieldsToJson(hdpImg, hdpImgJson);
            hdpImgJson.setHdp(convertStackDetailsToJson(hdpImg.getHdp()));
            hdpImages.add(hdpImgJson);
        }
        res.setHdpImages(hdpImages);

        List<HDFImageResponse> hdfImages = new ArrayList<>();
        for (HDFImage hdfImg : source.getHdfImages()) {
            HDFImageResponse hdfImgJson = new HDFImageResponse();
            copyImageFieldsToJson(hdfImg, hdfImgJson);
            hdfImgJson.setHdf(convertStackDetailsToJson(hdfImg.getHdf()));
            hdfImages.add(hdfImgJson);
        }
        res.setHdfImages(hdfImages);

        return res;
    }

    private List<BaseImageResponse> getBaseImageResponses(Images source) {
        List<BaseImageResponse> baseImages = new ArrayList<>();
        List<StackDetailsJson> defaultHdpStacks = getDefaultStackInfos(defaultHDPEntries.getEntries().values());
        List<StackDetailsJson> defaultHdfStacks = getDefaultStackInfos(defaultHDFEntries.getEntries().values());
        AmbariRepo ambariRepoDetails = ambariRepoConverter.convert(new AmbariRepoDetailsJson());
        Map<String, String> repoJson = new HashMap<>();
        repoJson.put("baseurl", ambariRepoDetails.getBaseUrl());
        repoJson.put("gpgkey", ambariRepoDetails.getGpgKeyUrl());

        for (Image baseImg : source.getBaseImages()) {
            BaseImageResponse imgJson = new BaseImageResponse();
            copyImageFieldsToJson(baseImg, imgJson);
            imgJson.setHdpStacks(defaultHdpStacks);
            imgJson.setHdfStacks(defaultHdfStacks);
            imgJson.setVersion(ambariRepoDetails.getVersion());
            imgJson.setRepo(repoJson);
            baseImages.add(imgJson);
        }
        return baseImages;
    }

    private List<StackDetailsJson> getDefaultStackInfos(Collection<? extends StackInfo> defaultStackInfos) {
        List<StackDetailsJson> result = new ArrayList<>();
        for (StackInfo info : defaultStackInfos) {
            StackDetailsJson json = new StackDetailsJson();
            StackRepoDetailsJson repoJson = new StackRepoDetailsJson();
            Map<String, String> stackRepo = info.getRepo().getStack();
            if (stackRepo != null) {
                repoJson.setStack(stackRepo);
            }
            Map<String, String> utilRepo = info.getRepo().getUtil();
            if (utilRepo != null) {
                repoJson.setUtil(utilRepo);
            }
            Map<String, String> knoxRepo = info.getRepo().getKnox();
            if (knoxRepo != null) {
                repoJson.setKnox(knoxRepo);
            }
            json.setRepo(repoJson);
            json.setVersion(info.getVersion());
            result.add(json);
        }
        return result;
    }

    private void copyImageFieldsToJson(Image source, ImageResponseBase json) {
        json.setDate(source.getDate());
        json.setDescription(source.getDescription());
        json.setOs(source.getOs());
        json.setUuid(source.getUuid());
        json.setVersion(source.getVersion());
        if (source.getRepo() != null) {
            json.setRepo(new HashMap<>(source.getRepo()));
        } else {
            json.setRepo(new HashMap<>());
        }
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
