package tk.lenkyun.foodbook.server.UserManagement.Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.Utils.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by lenkyun on 6/11/2558.
 */
public class Token {
    @JsonProperty("v")
    private int version = 1;
    @JsonProperty("u")
    private String uid;
    @JsonProperty("c")
    private String clientId;
    @JsonProperty("l")
    private long limit = 0;
    @JsonProperty("s")
    private String salt;

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @JsonIgnore
    public boolean isTimedOut(){
        return System.currentTimeMillis() / 1000 > limit;
    }
}
