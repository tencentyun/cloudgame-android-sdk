package com.tencent.tcr.sdk.demo.cloudstream.ui;

public class ScreenshotItem {
    public String instanceId;
    public String imageUrl;
    public boolean isSelected;

    public ScreenshotItem(String instanceId, String imageUrl, boolean isSelected) {
        this.instanceId = instanceId;
        this.imageUrl = imageUrl;
        this.isSelected = isSelected;
    }
}