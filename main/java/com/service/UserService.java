package com.service;

import com.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 47550
 */
public interface UserService extends IService<User> {
    User getUser();
}
