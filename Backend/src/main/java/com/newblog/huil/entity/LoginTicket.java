package com.newblog.huil.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author HuilLIN
 */
@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
