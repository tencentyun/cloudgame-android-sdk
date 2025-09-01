package com.tencent.tcr.sdk.demo.cloudphone.ui;

public class ScreenshotItem {
    private String instanceId;
    private String imageUrl;

    public ScreenshotItem(String instanceId, String imageUrl) {
        this.instanceId = instanceId;
        this.imageUrl = imageUrl;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}