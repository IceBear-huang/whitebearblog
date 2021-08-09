package com.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
 * @TableName blog
 */
@TableName(value ="blog")
@Data
public class Blog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Boolean appreciation;
    private Boolean commentabled;
    private String content;
    private LocalDateTime createTime;
    private String firstPicture;
    private String flag;
    private Boolean published;
    private Boolean recommend;
    private Boolean shareStatement;
    private String title;
    private LocalDateTime updateTime;
    private Integer views;
    private Long typeId;
    private String introduction;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private List<Tag> tagList;

    @TableField(exist = false)
    private Type type;

}