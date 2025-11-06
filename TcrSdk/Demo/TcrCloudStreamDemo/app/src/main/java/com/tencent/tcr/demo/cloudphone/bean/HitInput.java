package com.tencent.tcr.demo.cloudphone.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Current hit input box state
 */
public class HitInput {
    /**
     * The text content in the remote input box.
     */
    @SerializedName("text")
    public String text;

    @SerializedName("field_type")
    public String field_type;

    @Override
    public String toString() {
        return "HitInput{"
                + "text='" + text + '\''
                + ", field_type='" + field_type + '\''
                + '}';
    }
}
