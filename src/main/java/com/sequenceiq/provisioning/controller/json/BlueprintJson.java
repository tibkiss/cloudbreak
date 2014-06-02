package com.sequenceiq.provisioning.controller.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;

public class BlueprintJson implements JsonEntity {

    private String id;
    private String name;
    private String url;
    private String ambariBlueprint;
    private String blueprintName;

    @JsonRawValue
    public String getAmbariBlueprint() {
        return ambariBlueprint;
    }

    public void setAmbariBlueprint(JsonNode node) {
        this.ambariBlueprint = node.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonIgnore
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("blueprintName")
    public String getBlueprintName() {
        return blueprintName;
    }

    @JsonIgnore
    public void setBlueprintName(String blueprintName) {
        this.blueprintName = blueprintName;
    }
}
