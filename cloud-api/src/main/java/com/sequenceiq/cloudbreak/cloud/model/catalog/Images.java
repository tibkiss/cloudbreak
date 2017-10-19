package com.sequenceiq.cloudbreak.cloud.model.catalog;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Images {

    @JsonProperty("ambari-images")
    private List<AmbariImage> ambariImages;

    public List<AmbariImage> getAmbariImages() {
        return ambariImages;
    }

    public void setAmbariImages(List<AmbariImage> ambariImages) {
        this.ambariImages = ambariImages;
    }
}
