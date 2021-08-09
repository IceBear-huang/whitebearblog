package com.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 
 * @TableName comment
 */
@TableName(value ="comment")
@Data
public class Comment implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long blogId;
    private String nickname;
    private String email;
    private LocalDateTime createTime;
    private String content;
    private String avatar;
    private Long superiorId;
    private boolean ifAdmin;

    /**
     * 这一个是子级的评论
     */
    @TableField(exist = false)
    private List<Comment> commentList = new ArrayList<>();

    @TableField(exist = false)
    private String superiorName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}