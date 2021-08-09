package com.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Administrator
 */
@Data
public class SimplifyBlog {

    private Long id;

    private String title;

    private String typename;

    private Boolean recommend;

    private Boolean published;

    private String updateTime;
}
