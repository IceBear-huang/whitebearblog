package com.service;

import com.domain.Blog;
import com.domain.BlogTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface BlogTagService extends IService<BlogTag> {
    void viewTagAndTypeQuery(List<Blog> blogList);
}
