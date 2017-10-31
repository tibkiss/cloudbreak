package com.sequenceiq.cloudbreak.cloud.model.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HDPImage extends Image {

    @JsonProperty("hdp")
    private StackDetails hdp;

    public StackDetails getHdp() {
        return hdp;
    }

    public void setHdp(StackDetails hdp) {
        this.hdp = hdp;
    }
}
