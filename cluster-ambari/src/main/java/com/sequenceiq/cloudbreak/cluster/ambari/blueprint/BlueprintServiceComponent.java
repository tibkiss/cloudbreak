package com.sequenceiq.cloudbreak.cluster.ambari.blueprint;

import java.util.List;

import com.google.common.collect.Lists;
import com.sequenceiq.cloudbreak.domain.HostGroup;

public class BlueprintServiceComponent {

    private final String name;

    private int nodeCount;

    private final List<String> hostgroups;

    public BlueprintServiceComponent(String name, String hostgroup, int nodeCount) {
        this.name = name;
        this.nodeCount = nodeCount;
        hostgroups = Lists.newArrayList(hostgroup);
    }

    public void update(HostGroup hostGroup) {
        nodeCount += hostGroup.getConstraint().getHostCount();
        hostgroups.add(hostGroup.getName());
    }

    public String getName() {
        return name;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public List<String> getHostgroups() {
        return hostgroups;
    }
}