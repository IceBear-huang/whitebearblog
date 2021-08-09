package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.domain.Comment;
import com.service.CommentService;
import com.mapper.CommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
@Transactional
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService{

    private List<Comment> tempReplys = new ArrayList<>();

    @Override
    public List<Comment> getAllReply (Long blogId) {
        //查询所以顶级评论
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("superior_id",-1).eq("blog_id",blogId).orderByDesc("create_time");
        List<Comment> comments = list(commentQueryWrapper);

        for(Comment comment : comments){
            Long id = comment.getId();
            String superiorName = comment.getNickname();
            commentQueryWrapper.clear();
            //查询出子一级评论
            commentQueryWrapper.eq("blog_id",blogId).eq("superior_id",id).orderByDesc("create_time");
            List<Comment> childComments = list(commentQueryWrapper);

            commentChildren(blogId, childComments,superiorName);
            comment.setCommentList(tempReplys);
            tempReplys = new ArrayList<>();
        }
        return comments;
    }

    private void commentChildren(Long blogId,List<Comment> childComments,String superiorName){
        if (childComments.size() > 0){
            for (Comment childComment : childComments) {
                childComment.setSuperiorName(superiorName);
                tempReplys.add(childComment);
                Long childId = childComment.getId();
                String superiorName1 = childComment.getNickname();
                //查询出子二级评论
                recursively(blogId,childId,superiorName1);
            }
        }
    }

    private void recursively(Long blogId,Long childId,String superiorName1){
        //根据子一级评论的id找到子二级评论
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("superior_id",childId).eq("blog_id",blogId).orderByDesc("create_time");
        List<Comment> replayComments = list(commentQueryWrapper);

        if(replayComments.size() > 0){
            for(Comment replayComment : replayComments){
                replayComment.setSuperiorName(superiorName1);
                Long replayId = replayComment.getId();
                String replaySuperiorName = replayComment.getNickname();
                tempReplys.add(replayComment);
                recursively(blogId,replayId,replaySuperiorName);
            }
        }
    }
}




