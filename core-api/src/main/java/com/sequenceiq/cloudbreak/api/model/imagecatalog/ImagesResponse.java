package com.sequenceiq.cloudbreak.api.model.imagecatalog;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sequenceiq.cloudbreak.api.model.JsonEntity;

import io.swagger.annotations.ApiModel;

@ApiModel("ImagesResponse")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImagesResponse implements JsonEntity {

    private List<BaseImageResponse> baseImages;

    private List<HDPImageResponse> hdpImages;

    private List<HDFImageResponse> hdfImages;

    public List<BaseImageResponse> getBaseImages() {
        return baseImages;
    }

    public void setBaseImages(List<BaseImageResponse> baseImages) {
        this.baseImages = baseImages;
    }

    public List<HDPImageResponse> getHdpImages() {
        return hdpImages;
    }

    public void setHdpImages(List<HDPImageResponse> hdpImages) {
        this.hdpImages = hdpImages;
    }

    public List<HDFImageResponse> getHdfImages() {
        return hdfImages;
    }

    public void setHdfImages(List<HDFImageResponse> hdfImages) {
        this.hdfImages = hdfImages;
    }
}
