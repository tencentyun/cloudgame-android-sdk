package com.example.demop.server.param;

/**
 * 该类为业务后台返回的参数
 * 返回参数是云游团队和后台体验服务协定，客户请参考业务后台API参数
 */
public class ServerResponse {
    public int Code;
    public String Message;
    public String ServerSession;

    @Override
    public String toString() {
        return "ServerResponse{" +
                "Code=" + Code +
                ", Message='" + Message + '\'' +
                ", ServerSession='" + ServerSession + '\'' +
                '}';
    }
}
