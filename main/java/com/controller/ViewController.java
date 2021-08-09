package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.domain.*;
import com.service.*;
import com.util.SortMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author 47550
 */
@Controller
@RequestMapping("/view")
public class ViewController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogTagService blogTagService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/index")
    public String index(Model model,@RequestParam(value = "pc",required = false,defaultValue = "1") Integer pc) {

        //查询所有的blog 并且分页（统计的是发表的blog，保存的并不算）
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq("published",true).orderByDesc("create_time");
        Page<Blog> blogPage = new Page<>(pc,5);
        blogService.page(blogPage,blogQueryWrapper);
        List<Blog> blogList = blogPage.getRecords();

        //查询每一个blog所有的tag和type
        blogTagService.viewTagAndTypeQuery(blogList);

        //查询所有的type的前面6个，以及每一个type被使用的次数（统计的是发表的blog，保存的并不算）
        Map<String,Integer> typeMapSort = SortMap.sortDescend(typeService.viewTypeMapQuery());

        //查询所有的tag的前面6个，以及每一个tag被使用的次数（统计的是发表的blog，保存的并不算）
        Map<String,Integer> tagMapSort = SortMap.sortDescend(tagService.viewTagMapQuery());

        model.addAttribute("blogList",blogList);
        model.addAttribute("hasPrevious",blogPage.hasPrevious());
        model.addAttribute("hasNext",blogPage.hasNext());
        model.addAttribute("blogCount",blogPage.getTotal());
        model.addAttribute("pc",blogPage.getCurrent());
        model.addAttribute("user",userService.getUser());
        model.addAttribute("typeMapSort",typeMapSort);
        model.addAttribute("tagMapSort",tagMapSort);
        model.addAttribute("newBlogList",blogService.viewNewFiveBlogQuery());
        model.addAttribute("blogSize",blogService.count() == 0 ? 0 : blogService.count());
        model.addAttribute("commentSize",commentService.count() == 0 ? 0 : commentService.count());
        return "index";
    }

    @RequestMapping("/types")
    public String types(Model model,String typeName,
                        @RequestParam(value = "pc",defaultValue = "1",required = false) Integer pc){

        //查询所有的type,以及每一个type被使用的次数（统计的是发表的blog，保存的并不算）
        Map<String,Integer> typeMapSort = SortMap.sortDescend(typeService.viewTypeMapQuery());

        //假如没有传typeName 则取使用次数最多的type的名字
        if (typeName == null) {
            Iterator<Map.Entry<String, Integer>> iterator = typeMapSort.entrySet().iterator();
            typeName = iterator.next().getKey();
        }

        //把typeName对应的type查出来
        QueryWrapper<Type> typeQueryWrapper = new QueryWrapper<>();
        typeQueryWrapper.eq("name",typeName);
        Type type = typeService.getOne(typeQueryWrapper);

        //查询所有的blog 并且分页（统计的是发表的blog，保存的并不算）
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq("type_id",type.getId()).
                eq("published",true).orderByDesc("create_time");
        Page<Blog> blogPage = new Page<>(pc,5);
        blogService.page(blogPage,blogQueryWrapper);
        List<Blog> blogList = blogPage.getRecords();

        //查询每一个blog所有的tag和type
        blogTagService.viewTagAndTypeQuery(blogList);

        //查看所有type一共使用在多少篇blog里面
        int countType = 0;
        for (String next : typeMapSort.keySet()) {
            countType += typeMapSort.get(next);
        }

        model.addAttribute("countType",countType);
        model.addAttribute("user",userService.getUser());
        model.addAttribute("typeMapSort",typeMapSort);
        model.addAttribute("typeName",typeName);
        model.addAttribute("blogList",blogList);
        model.addAttribute("hasPrevious",blogPage.hasPrevious());
        model.addAttribute("hasNext",blogPage.hasNext());
        model.addAttribute("pc",blogPage.getCurrent());
        model.addAttribute("blogSize",blogService.count() == 0 ? 0 : blogService.count());
        model.addAttribute("commentSize",commentService.count() == 0 ? 0 : commentService.count());
        return "types";
    }

    @GetMapping("/tags")
    public String tags(Model model,String tagName,
                       @RequestParam(value = "pc",defaultValue = "1",required = false) Integer pc){
        int count = 0;

        //查询所有的tag,以及每一个tag被使用的次数（统计的是发表的blog，保存的并不算）
        List<Tag> tagList = tagService.list();
        Map<String, Integer> tagMap = new HashMap<>();
        QueryWrapper<BlogTag> blogTagQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();

        for (Tag tag : tagList) {
            blogTagQueryWrapper.clear();
            blogTagQueryWrapper.eq("tags_id",tag.getId());
            List<BlogTag> blogTagList = blogTagService.list(blogTagQueryWrapper);
            for (BlogTag blogTag : blogTagList) {
                blogQueryWrapper.clear();
                blogQueryWrapper.eq("id",blogTag.getBlogsId()).eq("published",true);
                count += blogService.count(blogQueryWrapper);
            }
            if (count > 0){
                tagMap.put(tag.getName(),count);
            }
            count = 0;
        }

        Map<String,Integer> tagMapSort = SortMap.sortDescend(tagMap);

        //假如没有传typeName 则取使用次数最多的type的名字
        if (tagName == null) {
            Iterator<Map.Entry<String, Integer>> iterator = tagMapSort.entrySet().iterator();
            tagName = iterator.next().getKey();
        }

        //把tagName对应的tag查出来
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("name",tagName);
        Tag tag = tagService.getOne(tagQueryWrapper);

        //查出当前tag所有blog的id
        blogTagQueryWrapper.clear();
        blogTagQueryWrapper.eq("tags_id",tag.getId());
        List<BlogTag> blogTagList = blogTagService.list(blogTagQueryWrapper);

        //记录发布的blog的id
        List<Long> blogIdList = new ArrayList<>();
        blogTagList.forEach(blogTag -> {
            Blog blog = blogService.getById(blogTag.getBlogsId());
            if (blog.getPublished()){
                blogIdList.add(blog.getId());
            }
        });

        //把BlogTag分页
        blogTagQueryWrapper.clear();
        blogTagQueryWrapper.eq("tags_id",tag.getId());
        blogTagQueryWrapper.in("blogs_id",blogIdList);
        blogTagQueryWrapper.orderByDesc("id");
        Page<BlogTag> blogTagPage = new Page<>(pc,5);
        blogTagService.page(blogTagPage,blogTagQueryWrapper);
        List<BlogTag> blogTags = blogTagPage.getRecords();

        //把BlogTag对应的blog查询出来
        List<Blog> blogList = new ArrayList<>();
        blogTags.forEach(blogTag -> {
            blogQueryWrapper.clear();
            blogQueryWrapper.eq("published",true).eq("id",blogTag.getBlogsId());
            Blog blog = blogService.getOne(blogQueryWrapper);

            List<Tag> tags = new ArrayList<>();
            if (blog != null){
                blog.setType(typeService.getById(blog.getTypeId()));
                blogTagQueryWrapper.clear();
                blogTagQueryWrapper.eq("blogs_id",blog.getId());
                List<BlogTag> blogTagList1 = blogTagService.list(blogTagQueryWrapper);
                blogTagList1.forEach(blogTag1 -> {
                    tagQueryWrapper.clear();
                    tagQueryWrapper.eq("id",blogTag1.getTagsId());
                    tags.add(tagService.getOne(tagQueryWrapper));
                });
                blog.setTagList(tags);
            }

            blogList.add(blog);

        });

        //一共多少篇blog
        count = 0;
        for (String next : tagMapSort.keySet()) {
            count = Math.toIntExact(count + tagMapSort.get(next));
        }

        model.addAttribute("countTag",count);
        model.addAttribute("user",userService.getUser());
        model.addAttribute("tagMapSort",tagMapSort);
        model.addAttribute("tagName",tagName);
        model.addAttribute("blogList",blogList);
        model.addAttribute("hasPrevious",blogTagPage.hasPrevious());
        model.addAttribute("hasNext",blogTagPage.hasNext());
        model.addAttribute("pc",blogTagPage.getCurrent());
        model.addAttribute("blogSize",blogService.count() == 0 ? 0 : blogService.count());
        model.addAttribute("commentSize",commentService.count() == 0 ? 0 : commentService.count());

        return "tags";
    }

    @GetMapping("/blogs")
    public String blog(Long blogId,Model model){
        model.addAttribute("user",userService.getUser());
        Blog blog = blogService.getBlog(blogId);
        UpdateWrapper<Blog> blogUpdateWrapper = new UpdateWrapper<>();
        blogUpdateWrapper.eq("id",blogId);
        blogUpdateWrapper.set("views",blog.getViews()+1);
        blogService.update(blogUpdateWrapper);

        model.addAttribute("blog",blogService.getBlog(blogId));
        model.addAttribute("blogSize",blogService.count() == 0 ? 0 : blogService.count());
        model.addAttribute("commentSize",commentService.count() == 0 ? 0 : commentService.count());
        return "blog";
    }

    @PostMapping(value = "/comment")
    public String commentSave(Comment comment,HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user != null) {
            comment.setAvatar(user.getAvatar());
            comment.setIfAdmin(true);
            comment.setNickname(user.getUsername());
        }
        commentService.save(comment);
        return "redirect:/view/commentList?blogId="+comment.getBlogId();
    }

    @GetMapping("/commentList")
    public String commentList(Model model, Long blogId, HttpSession session){
        List<Comment> comments = commentService.getAllReply(blogId);
        if (comments == null){
            return "";
        }
        if (session.getAttribute("user") != null){
            model.addAttribute("ifRemove",true);
        }
        model.addAttribute("comments",comments);
        model.addAttribute("blogSize",blogService.count() == 0 ? 0 : blogService.count());
        model.addAttribute("commentSize",commentService.count() == 0 ? 0 : commentService.count());
        return "blog::commentList";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "pc",required = false,defaultValue = "1") Integer pc,
                         @RequestParam(value = "query",required = false,defaultValue = "") String query,
                         Model model){
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq("published",true).and(i -> i.like("content",query).or()
                .like("introduction",query)).orderByDesc("create_time");
        Page<Blog> blogPage = new Page<>(pc,5);
        blogService.page(blogPage,blogQueryWrapper);
        List<Blog> blogList = blogPage.getRecords();

        //查询每一次blog的tag和type
        blogTagService.viewTagAndTypeQuery(blogList);

        model.addAttribute("blogList",blogList);
        model.addAttribute("hasPrevious",blogPage.hasPrevious());
        model.addAttribute("hasNext",blogPage.hasNext());
        model.addAttribute("count",blogPage.getTotal());
        model.addAttribute("query",query);
        model.addAttribute("pc",pc);
        model.addAttribute("user",userService.getUser());
        model.addAttribute("blogSize",blogService.count() == 0 ? 0 : blogService.count());
        model.addAttribute("commentSize",commentService.count() == 0 ? 0 : commentService.count());
        return "search";
    }

    @GetMapping("/archives")
    public String archives(Model model){
        model.addAttribute("count",blogService.count());
        model.addAttribute("archiveMap",blogService.archivesQuery());
        model.addAttribute("blogSize",blogService.count() == 0 ? 0 : blogService.count());
        model.addAttribute("commentSize",commentService.count() == 0 ? 0 : commentService.count());
        return "archives";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("blogSize",blogService.count() == 0 ? 0 : blogService.count());
        model.addAttribute("commentSize",commentService.count() == 0 ? 0 : commentService.count());
        return "about";
    }

    @PostMapping("/removeComment")
    public String removeComment(Long commentId,Long blogId){
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("id",commentId).eq("blog_id",blogId);
        commentService.remove(commentQueryWrapper);
        return "redirect:/view/commentList?blogId="+blogId;
    }
}
