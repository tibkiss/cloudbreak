package com.sequenceiq.cloudbreak.cloud.model.catalog;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Images {

    @JsonProperty("base-images")
    private List<Image> baseImages;

    @JsonProperty("hdp-images")
    private List<HDPImage> hdpImages;

    @JsonProperty("hdf-images")
    private List<HDFImage> hdfImages;

    public List<Image> getBaseImages() {
        return baseImages;
    }

    public void setBaseImages(List<Image> baseImages) {
        this.baseImages = baseImages;
    }

    public List<HDPImage> getHdpImages() {
        return hdpImages;
    }

    public void setHdpImages(List<HDPImage> hdpImages) {
        this.hdpImages = hdpImages;
    }

    public List<HDFImage> getHdfImages() {
        return hdfImages;
    }

    public void setHdfImages(List<HDFImage> hdfImages) {
        this.hdfImages = hdfImages;
    }
}
