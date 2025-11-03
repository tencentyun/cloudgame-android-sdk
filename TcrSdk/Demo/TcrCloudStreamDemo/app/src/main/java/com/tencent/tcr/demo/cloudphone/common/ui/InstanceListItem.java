package com.tencent.tcr.demo.cloudphone.common.ui;

public class InstanceListItem {
    public String instanceId;
    public String imageUrl;
    public boolean isSelected;

    public InstanceListItem(String instanceId, String imageUrl, boolean isSelected) {
        this.instanceId = instanceId;
        this.imageUrl = imageUrl;
        this.isSelected = isSelected;
    }
}