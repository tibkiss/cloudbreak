package com.sequenceiq.cloudbreak.api.model.imagecatalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sequenceiq.cloudbreak.api.model.JsonEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HDPDetailsJson implements JsonEntity {

    @JsonProperty("version")
    private String version;

    @JsonProperty("repo")
    private HDPRepoDetailsJson repo;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HDPRepoDetailsJson getRepo() {
        return repo;
    }

    public void setRepo(HDPRepoDetailsJson repo) {
        this.repo = repo;
    }
}
