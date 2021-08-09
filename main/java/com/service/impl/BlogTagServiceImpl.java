package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.domain.Blog;
import com.domain.BlogTag;
import com.domain.Tag;
import com.domain.Type;
import com.service.BlogTagService;
import com.mapper.BlogTagMapper;
import com.service.TagService;
import com.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
@Transactional
public class BlogTagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements BlogTagService{

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @Override
    public void viewTagAndTypeQuery (List<Blog> blogList) {
        //查询每一个blog所有的tag和type
        QueryWrapper<BlogTag> blogTagQueryWrapper = new QueryWrapper<>();
        blogList.forEach(blog -> {
            Type type = typeService.getById(blog.getTypeId());
            blog.setType(type);

            blogTagQueryWrapper.clear();
            blogTagQueryWrapper.eq("blogs_id",blog.getId());

            List<Tag> tagList = new ArrayList<>();
            List<BlogTag> blogTagList = list(blogTagQueryWrapper);
            blogTagList.forEach(blogTag -> {
                Tag tag = tagService.getById(blogTag.getTagsId());
                tagList.add(tag);
            });
            blog.setTagList(tagList);
        });
    }
}




