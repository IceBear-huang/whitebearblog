package com.service;

import com.domain.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 *
 * @author 47550
 */
public interface TagService extends IService<Tag> {
    Map<String,Integer> viewTagMapQuery();
}
