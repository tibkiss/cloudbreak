package com.sequenceiq.cloudbreak.api.model.imagecatalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel("HDPImagesResponse")
@JsonIgnoreProperties(ignoreUnknown = true)
public class HDPBaseImageResponse extends BaseImageResponse {

    @JsonProperty("hdp")
    private StackDetailsJson hdp;

    public StackDetailsJson getHdp() {
        return hdp;
    }

    public void setHdp(StackDetailsJson hdp) {
        this.hdp = hdp;
    }
}
