package com.lixn.login.POJO;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/19
 * @描述
 */
public class UserBO {
    private String username;

    private String password;

    private String realName;

    private boolean kickout;

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isKickout() {
        return kickout;
    }

    public void setKickout(boolean kickout) {
        this.kickout = kickout;
    }
}
