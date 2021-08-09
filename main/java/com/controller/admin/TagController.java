package com.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.domain.BlogTag;
import com.domain.Tag;
import com.service.BlogTagService;
import com.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author 47550
 */
@Controller
@RequestMapping("/admin")
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private BlogTagService blogtagService;

    @GetMapping("/tags")
    public String tag(@RequestParam(value = "pc",defaultValue = "1",required = false) Integer pc, Model model){
        Page<Tag> page = new  Page<>(pc,5);
        Page<Tag> result = tagService.page(page);

        model.addAttribute("taglist", result.getRecords());
        model.addAttribute("countpage", result.getPages());
        model.addAttribute("pc", result.getCurrent());
        model.addAttribute("hasPrevious",result.hasPrevious());
        model.addAttribute("hasNext",result.hasNext());
        return "admin/tags";
    }

    @GetMapping("/addTag")
    public String addTag() {
        return "admin/tags-input";
    }

    @PostMapping("/saveTag")
    public String saveTag(Tag tag, RedirectAttributes attributes){
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<Tag>();
        queryWrapper.eq("name",tag.getName());
        int count = tagService.count(queryWrapper);
        if(count != 0){
            attributes.addFlashAttribute("massages", "标签已存在");
            return "redirect:/admin/addTag";
        }
        boolean b = tagService.save(tag);
        if(b) {
            attributes.addFlashAttribute("messages","新增分类成功");
        }else {
            attributes.addFlashAttribute("messages","新增分类失败");
        }
        return "redirect:/admin/tags" ;
    }

    @GetMapping("/updateTag")
    public String updateTag(Long tagId,String tagName,RedirectAttributes attributes){
        attributes.addFlashAttribute("name",tagName);
        attributes.addFlashAttribute("tagId",tagId);
        return "redirect:/admin/addTag";
    }

    @PostMapping("/updateTag")
    public String updateTag(RedirectAttributes attributes,String name,String tagId){
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        int count = tagService.count(queryWrapper);
        if(count != 0){
            attributes.addFlashAttribute("messages","标签已存在");
            attributes.addFlashAttribute("name",name);
            attributes.addFlashAttribute("tagId",tagId);
            return "redirect:/admin/addTag";
        }

        UpdateWrapper<Tag> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("name",name).eq("id",tagId);
        boolean b = tagService.update(updateWrapper);
        if(b){
            attributes.addFlashAttribute("messages","标签修改成功");
        }else {
            attributes.addFlashAttribute("messages","标签修改失败");
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/removeTag")
    public String removeTag(RedirectAttributes attributes,Long tagId){
        QueryWrapper<BlogTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tags_id",tagId);
        int count = blogtagService.count(queryWrapper);
        if (count != 0){
            attributes.addFlashAttribute("messages","删除失败，有文章引用该分类");
            return "redirect:/admin/tags";
        }

        UpdateWrapper<Tag> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",tagId);
        tagService.remove(updateWrapper);
        attributes.addFlashAttribute("messages","删除成功");
        return "redirect:/admin/tags";

    }

}
