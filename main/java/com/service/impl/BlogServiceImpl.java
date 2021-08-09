package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.domain.Blog;
import com.domain.SimplifyBlog;
import com.domain.Type;
import com.mapper.BlogMapper;
import com.service.BlogService;
import com.service.TypeService;
import com.util.MarkdownUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
@Transactional
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private TypeService typeService;

    @Override
    public List<SimplifyBlog> adminBlogPageQuery (Page<Blog> blogPage, QueryWrapper<Blog> blogQueryWrapper) {
        List<SimplifyBlog> simplifyBlogList = new ArrayList<>();
        page(blogPage, blogQueryWrapper);
        List<Blog> blogList = blogPage.getRecords();
        blogList.forEach(blog -> {
            SimplifyBlog simplifyBlog = new SimplifyBlog();
            simplifyBlog.setId(blog.getId());
            simplifyBlog.setPublished(blog.getPublished());
            simplifyBlog.setRecommend(blog.getRecommend());
            simplifyBlog.setTitle(blog.getTitle());
            QueryWrapper<Type> typeQueryWrapper = new QueryWrapper<>();
            typeQueryWrapper.eq("id", blog.getTypeId());
            simplifyBlog.setTypename(typeService.getOne(typeQueryWrapper).getName());
            if (blog.getUpdateTime() != null) {
                String updateTime = blog.getUpdateTime() + "";
                int len = updateTime.length();
                simplifyBlog.setUpdateTime(updateTime.substring(0, len - 7).replaceAll("T", " "));
            }
            simplifyBlogList.add(simplifyBlog);
        });
        return simplifyBlogList;
    }


    @Override
    public List<Blog> viewNewFiveBlogQuery () {
        //获取最新五条博客
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq("recommend", true).orderByDesc("create_time");
        Page<Blog> newBlogPage = new Page<>(0, 5);
        page(newBlogPage, blogQueryWrapper);
        List<Blog> newBlogList = newBlogPage.getRecords();
        newBlogList.forEach(blog -> {
            Type type = typeService.getById(blog.getTypeId());
            blog.setType(type);
        });

        return newBlogList;
    }

    @Override
    public Blog getBlog (Long blogId) {
        Blog blog = getById(blogId);
        blog.setContent(MarkdownUtils.markdownToHtmlExtensions(blog.getContent()));
        return blog;
    }

    @Override
    public Map<String, List<Blog>> archivesQuery () {
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        Map<String, List<Blog>> listMapBlog = new LinkedHashMap<>();
        blogQueryWrapper.select("create_time").groupBy("create_time").orderByDesc("create_time");
        List<Blog> years = list(blogQueryWrapper);
        years.forEach(year -> {
            String y = String.valueOf(year.getCreateTime()).substring(0, 4);
            blogQueryWrapper.clear();
            blogQueryWrapper.like("create_time", y).orderByDesc("create_time");
            List<Blog> blogList = list(blogQueryWrapper);
            listMapBlog.put(y, blogList);
        });
        return listMapBlog;
    }
}




