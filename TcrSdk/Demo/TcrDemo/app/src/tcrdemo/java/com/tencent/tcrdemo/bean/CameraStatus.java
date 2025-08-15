package com.tencent.tcrdemo.bean;

import com.google.gson.annotations.SerializedName;


public class CameraStatus {
    // `open_back`: Activates the back camera.
    // `open_front`: Activates the front camera.
    // `close`: Deactivates the camera.
    @SerializedName("status")
    public String status;

    @SerializedName("width")
    public int width;

    @SerializedName("height")
    public int height;
}
