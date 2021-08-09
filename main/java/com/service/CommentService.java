package com.service;

import com.domain.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface CommentService extends IService<Comment> {
    List<Comment> getAllReply(Long blogId);
}
