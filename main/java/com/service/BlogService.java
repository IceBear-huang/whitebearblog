package com.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.domain.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.domain.SimplifyBlog;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface BlogService extends IService<Blog> {
    List<SimplifyBlog> adminBlogPageQuery(Page<Blog> blogPage, QueryWrapper<Blog> blogQueryWrapper);

    List<Blog> viewNewFiveBlogQuery();

    Blog getBlog(Long blogId);

    Map<String,List<Blog>> archivesQuery();
}
