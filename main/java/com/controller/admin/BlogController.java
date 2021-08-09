package com.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.domain.*;
import com.service.BlogService;
import com.service.BlogTagService;
import com.service.TagService;
import com.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.RedirectViewControllerRegistration;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ClassName: BlogController
 * Description: admin的blog控制层
 * date: 2021/7/13 16:42
 *
 * @author WhiteBear
 */
@Controller
@RequestMapping("/admin")
public class BlogController {
    
    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @Autowired
    private BlogTagService blogTagService;

    /**
    * Description:
    * @author: WhiteBear
    * @date: 2021/7/13 17:00
    * @param:[model]
    * @return:java.lang.String
    */
    @GetMapping("/blogs")
    public String blogs(Model model){
        List<Type> typeList = typeService.list();

        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.orderByDesc("create_time");
        Page<Blog> blogPage = new Page<>(1,5);
        List<SimplifyBlog> simplifyBlogList = blogService.adminBlogPageQuery(blogPage,blogQueryWrapper);

        model.addAttribute("typeList",typeList);
        model.addAttribute("blogList",simplifyBlogList);
        model.addAttribute("pc", blogPage.getCurrent());
        model.addAttribute("hasPrevious",blogPage.hasPrevious());
        model.addAttribute("hasNext",blogPage.hasNext());

        return "admin/blogs";
    }


    @PostMapping("/findBlog")
    public String findBlog(@RequestParam(required = false) String title,
                           @RequestParam(required = false) Long typeId,
                           @RequestParam(required = false) Boolean recommend,
                           @RequestParam(required = false) Boolean published , Model model,
                           @RequestParam(required = false,defaultValue = "1") Integer pc){
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        Map<String,Object> map = new HashMap<>();
        map.put("title", title);
        map.put("type_id", typeId);
        map.put("recommend", recommend);
        map.put("published",published);
        blogQueryWrapper.select("id", "title", "type_id", "recommend", "update_time","published")
                .allEq(map,false).orderByDesc("create_time");

        Page<Blog> blogPage = new Page<>(pc,5);
        List<SimplifyBlog> simplifyBlogList = blogService.adminBlogPageQuery(blogPage,blogQueryWrapper);

        model.addAttribute("blogList",simplifyBlogList);
        model.addAttribute("pc", blogPage.getCurrent());
        model.addAttribute("hasPrevious",blogPage.hasPrevious());
        model.addAttribute("hasNext",blogPage.hasNext());

        return "admin/blogs :: blogList";
    }

    @GetMapping("/blogs-input")
    public String blogsInput(Model model){

        List<Type> typeList = typeService.list();
        List<Tag> tagList = tagService.list();
        model.addAttribute("typeList",typeList);
        model.addAttribute("tagList",tagList);

        return "admin/blogs-input";
    }

    @PostMapping("/blogsInput")
    public String blogsInput(Blog blog,Long[] tags,RedirectAttributes attributes) {
        if (!blogService.save(blog)) {
            attributes.addFlashAttribute("messages", "新增文章失败");
            return "redirect:/admin/blogs";
        }

        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.orderByDesc("create_time").last("LIMIT 1");
        Blog newBlog = blogService.getOne(blogQueryWrapper);
        for (Long tag : tags) {
            BlogTag blogTag = new BlogTag();
            blogTag.setBlogsId(newBlog.getId());
            blogTag.setTagsId(tag);
            blogTagService.save(blogTag);
        }

        if (newBlog.getPublished()) {
            attributes.addFlashAttribute("messages", "发布成功");
        } else {
            attributes.addFlashAttribute("messages", "发布失败");
        }

        return "redirect:/admin/blogs";
    }

    @GetMapping("/updateBlog")
    public String updateBlog(Long blogId,Model model){
        //获取当前id的blog实体类
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq("id",blogId);
        Blog blog = blogService.getOne(blogQueryWrapper);

        //获取所有的type
        List<Type> typeList = typeService.list();

        //获取所有的tag
        List<Tag> tagList = tagService.list();

        //获取这个文章所有的tag字符串
        QueryWrapper<BlogTag> blogTagQueryWrapper = new QueryWrapper<>();
        blogTagQueryWrapper.eq("blogs_id",blogId);
        List<BlogTag> blogTagList = blogTagService.list(blogTagQueryWrapper);
        StringBuilder tagIdBuilder = new StringBuilder();
        blogTagList.forEach(blogTag -> {
            tagIdBuilder.append(blogTag.getTagsId()).append(",");
        });

        //把所有的获取的数据存储到model
        if (tagIdBuilder.length() >= 1){
            model.addAttribute("blogTags",tagIdBuilder.substring(0,tagIdBuilder.length()-1));
        }
        model.addAttribute("blog",blog);
        model.addAttribute("typeList",typeList);
        model.addAttribute("tagList",tagList);

        return "admin/blogs-update";
    }

    @PostMapping("/updateBlog")
    public String updateBlog(Blog blog,Long[] tags,RedirectAttributes attributes){
        //更新blog
        blog.setUpdateTime(LocalDateTime.now());
        if (!blogService.updateById(blog)) {
            attributes.addFlashAttribute("messages", "新增文章失败");
            return "redirect:/admin/blogs";
        }

        //对blog_tag表对应blog的数据删除
        QueryWrapper<BlogTag> blogTagQueryWrapper = new QueryWrapper<>();
        blogTagQueryWrapper.eq("blogs_id",blog.getId());
        blogTagService.remove(blogTagQueryWrapper);

        //再插入
        for (Long tag : tags) {
            BlogTag blogTag = new BlogTag();
            blogTag.setBlogsId(blog.getId());
            blogTag.setTagsId(tag);
            blogTagService.save(blogTag);
        }

        if (blog.getPublished()) {
            attributes.addFlashAttribute("messages", "发布成功");
        } else {
            attributes.addFlashAttribute("messages", "发布失败");
        }
        return "redirect:/admin/blogs";
    }

    @PostMapping("/removeBlog")
    public String removeBlog(@RequestParam(required = false) String title,
                                   @RequestParam(required = false) Long typeId,
                                   @RequestParam(required = false) Boolean recommend, Long blogId,
                                   @RequestParam(required = false) Boolean published , Model model,
                                   @RequestParam(required = false,defaultValue = "1") Integer pc){
        blogService.removeById(blogId);

        QueryWrapper<BlogTag> blogTagQueryWrapper = new QueryWrapper<>();
        blogTagQueryWrapper.eq("blogs_id",blogId);
        blogTagService.remove(blogTagQueryWrapper);

        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        Map<String,Object> map = new HashMap<>();
        map.put("title", title);
        map.put("type_id", typeId);
        map.put("recommend", recommend);
        map.put("published",published);
        blogQueryWrapper.select("id", "title", "type_id", "recommend", "update_time","published")
                .allEq(map,false).orderByDesc("create_time");

        Page<Blog> blogPage = new Page<>();
        List<SimplifyBlog> simplifyBlogList = blogService.adminBlogPageQuery(blogPage,blogQueryWrapper);

        model.addAttribute("blogList",simplifyBlogList);
        model.addAttribute("pc", blogPage.getCurrent());
        model.addAttribute("hasPrevious",blogPage.hasPrevious());
        model.addAttribute("hasNext",blogPage.hasNext());

        return "admin/blogs :: blogList";
    }


}
