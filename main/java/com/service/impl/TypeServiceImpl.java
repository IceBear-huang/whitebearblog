package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.domain.Blog;
import com.domain.Type;
import com.mapper.TypeMapper;
import com.service.BlogService;
import com.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 47550
 */
@Service
@Transactional
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {

    @Autowired
    private BlogService blogService;

    @Override
    public Map<String, Integer> viewTypeMapQuery () {
        //查询所有的type的前面6个，以及每一个type被使用的次数（统计的是发表的blog，保存的并不算）
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        List<Type> typeList = list();
        Map<String, Integer> typeMap = new HashMap<>();
        typeList.forEach(type -> {
            blogQueryWrapper.clear();
            blogQueryWrapper.eq("type_id", type.getId()).eq("published", true);
            int count = blogService.count(blogQueryWrapper);
            if (count > 0) {
                typeMap.put(type.getName(), count);
            }
        });
        return typeMap;
    }
}




