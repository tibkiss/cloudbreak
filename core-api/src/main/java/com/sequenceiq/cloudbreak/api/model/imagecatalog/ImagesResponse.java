package com.sequenceiq.cloudbreak.api.model.imagecatalog;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sequenceiq.cloudbreak.api.model.JsonEntity;

import io.swagger.annotations.ApiModel;

@ApiModel("ImagesResponse")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImagesResponse implements JsonEntity {

    @JsonProperty("ambari-images")
    private List<AmbariImageJson> ambariImages;

    public List<AmbariImageJson> getAmbariImages() {
        return ambariImages;
    }

    public void setAmbariImages(List<AmbariImageJson> ambariImages) {
        this.ambariImages = ambariImages;
    }
}
