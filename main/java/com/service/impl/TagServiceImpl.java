package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.domain.BlogTag;
import com.domain.Tag;
import com.domain.Type;
import com.service.BlogTagService;
import com.service.TagService;
import com.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 47550
 */
@Service
@Transactional
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService{

    @Autowired
    private BlogTagService blogTagService;

    @Override
    public Map<String, Integer> viewTagMapQuery () {
        //查询所有的tag的前面6个，以及每一个tag被使用的次数（统计的是发表的blog，保存的并不算）
        QueryWrapper<BlogTag> blogTagQueryWrapper = new QueryWrapper<>();
        List<Tag> tagList = list();
        Map<String,Integer> tagMap = new HashMap<>();
        tagList.forEach(tag -> {
            blogTagQueryWrapper.clear();
            blogTagQueryWrapper.eq("tags_id",tag.getId());
            int count = blogTagService.count(blogTagQueryWrapper);
            if (count > 0){
                tagMap.put(tag.getName(),count);
            }
        });
        return tagMap;
    }
}




