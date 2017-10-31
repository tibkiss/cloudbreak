package com.sequenceiq.cloudbreak.cloud.model.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HDFImage extends Image {

    @JsonProperty("hdf")
    private StackDetails hdf;

    public StackDetails getHdf() {
        return hdf;
    }

    public void setHdf(StackDetails hdf) {
        this.hdf = hdf;
    }
}
