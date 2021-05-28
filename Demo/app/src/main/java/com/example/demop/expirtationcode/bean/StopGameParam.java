package com.example.demop.expirtationcode.bean;

public class StopGameParam extends Param{
    public String ExperienceCode;
    public String UserId;

    public StopGameParam(String experienceCode, String userId) {
        ExperienceCode = experienceCode;
        UserId = userId;
    }
}