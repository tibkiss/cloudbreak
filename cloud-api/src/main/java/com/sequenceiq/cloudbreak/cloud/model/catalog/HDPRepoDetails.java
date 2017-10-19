package com.sequenceiq.cloudbreak.cloud.model.catalog;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HDPRepoDetails {

    public static final String REPO_ID_TAG = "repoid";

    public static final String MPACK_TAG = "mpack";

    @JsonProperty("stack")
    private Map<String, String> stack;

    @JsonProperty("util")
    private Map<String, String> util;

    public Map<String, String> getStack() {
        return stack;
    }

    public void setStack(Map<String, String> stack) {
        this.stack = stack;
    }

    public Map<String, String> getUtil() {
        return util;
    }

    public void setUtil(Map<String, String> util) {
        this.util = util;
    }

    @Override
    public String toString() {
        return "HDPRepo{stack='" + stack.get(REPO_ID_TAG) + "'; utils='" + util.get(REPO_ID_TAG) + "'}";
    }
}
