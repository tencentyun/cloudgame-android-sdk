package com.tencent.tcr.demo;

import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.tencent.demo.R;
import com.tencent.tcr.TcrDemoApplication;

public class PreferenceMgr {

    public static String getExperienceCode() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(TcrDemoApplication.getContext());
        return pref.getString(TcrDemoApplication.getContext().getResources().getString(R.string.pref_key_experienceCode), "");
    }

    public static boolean isEnableWideFov() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(TcrDemoApplication.getContext());
        return pref.getBoolean(TcrDemoApplication.getContext().getResources().getString(R.string.pref_key_enable_fovPlus), false);
    }

    public static boolean isEnableWideFFR() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(TcrDemoApplication.getContext());
        return pref.getBoolean(TcrDemoApplication.getContext().getResources().getString(R.string.pref_key_enable_ffr), false);
    }

    public static int getTargetResolution() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(TcrDemoApplication.getContext());
        return pref.getInt(TcrDemoApplication.getContext().getResources().getString(R.string.pref_key_resolution), 4000);
    }

    public static float getSrRatio() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(TcrDemoApplication.getContext());
        return (float) (pref.getInt(TcrDemoApplication.getContext().getResources().getString(R.string.pref_key_sr_option), 10) / 10.0);
    }
}

