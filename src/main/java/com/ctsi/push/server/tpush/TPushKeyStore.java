package com.ctsi.push.server.tpush;

/**
 * Created by doulala on 2016/12/19.
 */
public class TPushKeyStore {
    long accessId;
    String secretKey;

    public TPushKeyStore(long accessId, String secretKey) {
        this.accessId = accessId;
        this.secretKey = secretKey;
    }

    public long getAccessId() {
        return accessId;
    }

    public void setAccessId(long accessId) {
        this.accessId = accessId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
