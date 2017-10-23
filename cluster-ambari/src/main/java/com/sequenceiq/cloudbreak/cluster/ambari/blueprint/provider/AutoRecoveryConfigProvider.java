package com.sequenceiq.cloudbreak.cluster.ambari.blueprint.provider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cluster.model.BlueprintConfigurationEntry;
import com.sequenceiq.cloudbreak.cluster.ambari.blueprint.BlueprintProcessor;

@Component
public class AutoRecoveryConfigProvider {
    @Inject
    private BlueprintProcessor blueprintProcessor;

    public String addToBlueprint(String blueprint) {
        blueprint = blueprintProcessor.addConfigEntries(blueprint, getConfigs(), true);
        blueprint = blueprintProcessor.addSettingsEntries(blueprint, getSettings(), true);
        return blueprint;
    }

    private List<BlueprintConfigurationEntry> getConfigs() {
        List<BlueprintConfigurationEntry> bpConfigs = new ArrayList<>();
        bpConfigs.add(new BlueprintConfigurationEntry("cluster-env", "recovery_enabled", "true"));
        bpConfigs.add(new BlueprintConfigurationEntry("cluster-env", "recovery_type", "AUTO_START"));
        return bpConfigs;
    }

    private List<BlueprintConfigurationEntry> getSettings() {
        List<BlueprintConfigurationEntry> bpConfigs = new ArrayList<>();
        bpConfigs.add(new BlueprintConfigurationEntry("recovery_settings", "recovery_enabled", "true"));
        return bpConfigs;
    }
}
