package cn.damon.session;

import java.io.Serializable;

public class UserForm implements Serializable{
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String backurl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBackurl() {
        return backurl;
    }

    public void setBackurl(String backurl) {
        this.backurl = backurl;
    }
}
