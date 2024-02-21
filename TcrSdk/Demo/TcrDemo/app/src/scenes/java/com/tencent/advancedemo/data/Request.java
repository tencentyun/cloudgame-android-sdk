package com.tencent.advancedemo.data;

import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 请求业务后台的公共参数
 */
public class Request {

    // 请求的命令字
    public final transient String cmd;
    /**
     * 请求业务后台接口的签名key, 在部署业务后台的时候生成的字符串。(可选)
     * 文档说明:https://github.com/tencentyun/car-server-demo#2-%E7%94%9F%E6%88%90%E9%85%8D%E7%BD%AE
     */
    public static final String SALT = "";
    @SerializedName("RequestId")
    public String requestId;
    @SerializedName("UserId")
    public String userId;
    @SerializedName("TimeStamp")
    public long timeStamp;

    public Request(String cmd, String userId) {
        this.cmd = cmd;
        this.userId = userId;
        this.requestId = UUID.randomUUID().toString();
        this.timeStamp = System.currentTimeMillis();
    }

    /**
     * 使用SHA256对文本内容{@code input}进行加密。
     */
    protected static String getSHA256(String input) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(input.getBytes(StandardCharsets.UTF_8));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    /**
     * 将字节流转为16进制字符
     */
    private static String byte2Hex(byte[] input) {
        StringBuilder stringBuilder = new StringBuilder();
        String temp;
        for (byte b : input) {
            temp = Integer.toHexString(b & 0xFF);
            if (temp.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }

}
