package com.newblog.huil.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author HuilLIN
 */
@Data
public class Comment {
    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}
