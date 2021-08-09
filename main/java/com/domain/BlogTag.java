package com.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @author 47550
 * @TableName blog_tag
 */
@TableName(value ="blog_tag")
@Data
public class BlogTag implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long blogsId;

    private Long tagsId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}