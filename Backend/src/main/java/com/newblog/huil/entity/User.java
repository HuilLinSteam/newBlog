package com.newblog.huil.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author HuilLIN
 */
@Data
public class User {
    private int id;
    private String uid;
    private String phone;
    private String username;
    private String password;
    private String name;
    private String salt;
    private String email;
    private int type;
    private int status;
    private Date createTime;
    private String activationCode;
    private String headerUrl;
}
