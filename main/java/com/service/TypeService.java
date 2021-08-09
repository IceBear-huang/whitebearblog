package com.service;

import com.domain.Type;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 47550
 */
public interface TypeService extends IService<Type> {
    Map<String,Integer> viewTypeMapQuery();
}
