package com.sequenceiq.cloudbreak.api.model.imagecatalog;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sequenceiq.cloudbreak.api.model.JsonEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmbariImageJson implements JsonEntity {

    @JsonProperty("date")
    private String date;

    @JsonProperty("description")
    private String description;

    @JsonProperty("os")
    private String os;

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("version")
    private String version;

    @JsonProperty("prewarm")
    private boolean prewarm;

    @JsonProperty("repo")
    private Map<String, String> repo;

    @JsonProperty("hdp")
    private HDPDetailsJson hdp;

    @JsonProperty("images")
    private Map<String, Map<String, String>> imageSetsByProvider;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isPrewarm() {
        return prewarm;
    }

    public void setPrewarm(boolean prewarm) {
        this.prewarm = prewarm;
    }

    public Map<String, String> getRepo() {
        return repo;
    }

    public void setRepo(Map<String, String> repo) {
        this.repo = repo;
    }

    public HDPDetailsJson getHdp() {
        return hdp;
    }

    public void setHdp(HDPDetailsJson hdp) {
        this.hdp = hdp;
    }

    public Map<String, Map<String, String>> getImageSetsByProvider() {
        return imageSetsByProvider;
    }

    public void setImageSetsByProvider(Map<String, Map<String, String>> imageSetsByProvider) {
        this.imageSetsByProvider = imageSetsByProvider;
    }
}
