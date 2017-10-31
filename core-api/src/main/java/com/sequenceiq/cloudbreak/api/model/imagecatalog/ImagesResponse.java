package com.sequenceiq.cloudbreak.api.model.imagecatalog;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sequenceiq.cloudbreak.api.model.JsonEntity;

import io.swagger.annotations.ApiModel;

@ApiModel("ImagesResponse")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImagesResponse implements JsonEntity {

    @JsonProperty("base-images")
    private List<ImageJson> baseImages;

    @JsonProperty("hdp-images")
    private List<HDPImageJson> hdpImages;

    @JsonProperty("hdf-images")
    private List<HDFImageJson> hdfImages;

    public List<ImageJson> getBaseImages() {
        return baseImages;
    }

    public void setBaseImages(List<ImageJson> baseImages) {
        this.baseImages = baseImages;
    }

    public List<HDPImageJson> getHdpImages() {
        return hdpImages;
    }

    public void setHdpImages(List<HDPImageJson> hdpImages) {
        this.hdpImages = hdpImages;
    }

    public List<HDFImageJson> getHdfImages() {
        return hdfImages;
    }

    public void setHdfImages(List<HDFImageJson> hdfImages) {
        this.hdfImages = hdfImages;
    }
}
