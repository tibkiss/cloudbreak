package com.sequenceiq.cloudbreak.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sequenceiq.cloudbreak.cloud.model.catalog.HDPInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultHDFInfo extends HDPInfo {


}
