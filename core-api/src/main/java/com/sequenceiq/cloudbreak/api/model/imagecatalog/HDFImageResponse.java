package com.sequenceiq.cloudbreak.api.model.imagecatalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel("HDFImageResponse")
@JsonIgnoreProperties(ignoreUnknown = true)
public class HDFImageResponse extends BaseImageResponse {

    @JsonProperty("hdf")
    private StackDetailsJson hdf;

    public StackDetailsJson getHdf() {
        return hdf;
    }

    public void setHdf(StackDetailsJson hdf) {
        this.hdf = hdf;
    }
}
