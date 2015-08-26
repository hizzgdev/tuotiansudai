package com.tuotiansudai.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class MyUser extends User {
    private String salt;

    private String mobile;

    private String umpUserId;

    public MyUser(String username,
                  String password,
                  boolean enabled,
                  boolean accountNonExpired,
                  boolean credentialsNonExpired,
                  boolean accountNonLocked,
                  List<GrantedAuthority> authorities,
                  String mobile,
                  String umpUserId,
                  String salt) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.mobile = mobile;
        this.umpUserId = umpUserId;
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUmpUserId() {
        return umpUserId;
    }

    public void setUmpUserId(String umpUserId) {
        this.umpUserId = umpUserId;
    }
}
