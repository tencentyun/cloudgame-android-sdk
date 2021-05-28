package com.example.demop.expirtationcode.bean;

public class ExperienceCodeParam extends Param{
    public String ExperienceCode;
    public String ClientSession;
    public String UserId;

    public ExperienceCodeParam(String experienceCode, String clientSession, String userId) {
        ExperienceCode = experienceCode;
        ClientSession = clientSession;
        UserId = userId;
    }
}
