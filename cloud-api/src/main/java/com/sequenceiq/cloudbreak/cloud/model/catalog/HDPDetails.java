package com.sequenceiq.cloudbreak.cloud.model.catalog;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HDPDetails {

    @JsonProperty("version")
    private String version;

    @JsonProperty("images")
    private Map<String, Map<String, String>> imageSetsByProvider;

    @JsonProperty("repo")
    private HDPRepoDetails repo;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Map<String, String>> getImageSetsByProvider() {
        return imageSetsByProvider;
    }

    public void setImageSetsByProvider(Map<String, Map<String, String>> imageSetsByProvider) {
        this.imageSetsByProvider = imageSetsByProvider;
    }

    public HDPRepoDetails getRepo() {
        return repo;
    }

    public void setRepo(HDPRepoDetails repo) {
        this.repo = repo;
    }
}
