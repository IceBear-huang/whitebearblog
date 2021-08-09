package com.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.domain.Blog;
import com.domain.Type;
import com.service.BlogService;
import com.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author 47550
 */
@Controller
@RequestMapping("/admin")
public class TypeController {
    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/types")
    public String types(@RequestParam(value = "pc",defaultValue = "1",required = false) Integer pc, Model model){
        Page<Type> page = new  Page<>(pc,5);
        Page<Type> result = typeService.page(page);

        model.addAttribute("typelist", result.getRecords());
        model.addAttribute("countpage", result.getPages());
        model.addAttribute("pc", result.getCurrent());
        model.addAttribute("hasPrevious",result.hasPrevious());
        model.addAttribute("hasNext",result.hasNext());
        return "admin/types";
    }

    @GetMapping("/addType")
    public String addType(){
        return "admin/types-input";
    }

    @PostMapping("/saveType")
    public String saveType(Type type, RedirectAttributes attributes){
        QueryWrapper<Type> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",type.getName());
        int count = typeService.count(queryWrapper);
        if (count != 0){
            attributes.addFlashAttribute("messages","分类已存在");
            return "redirect:/admin/addType";
        }
        boolean b = typeService.save(type);
        if(b) {
            attributes.addFlashAttribute("messages","新增分类成功");
        }else {
            attributes.addFlashAttribute("messages","新增分类失败");
        }
        return "redirect:/admin/types";
    }


    @GetMapping("/updateType")
    public String updateType(Long typeId,String typeName,RedirectAttributes attributes){
        attributes.addFlashAttribute("name",typeName);
        attributes.addFlashAttribute("typeId",typeId);
        return "redirect:/admin/addType";
    }

    @PostMapping("/updateType")
    public String updateType(String name,Long typeId,RedirectAttributes attributes){
        QueryWrapper<Type> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        int count = typeService.count(queryWrapper);
        if (count != 0){
            attributes.addFlashAttribute("messages","分类已存在");
            attributes.addFlashAttribute("name",name);
            attributes.addFlashAttribute("typeId",typeId);
            return "redirect:/admin/addType";
        }
        UpdateWrapper<Type> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("name",name).eq("id",typeId);
        boolean b = typeService.update(updateWrapper);
        if(b) {
            attributes.addFlashAttribute("messages","修改分类成功");
        }else {
            attributes.addFlashAttribute("messages","修改分类失败");
        }
        return "redirect:/admin/types";

    }

    @GetMapping("/removeType")
    public String delete(Long typeId,RedirectAttributes attributes){
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_id",typeId);
        int count = blogService.count(queryWrapper);
        if (count != 0){
            attributes.addFlashAttribute("messages","删除失败，有文章引用该分类");
            return "redirect:/admin/types";
        }

        UpdateWrapper<Type> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",typeId);
        typeService.remove(updateWrapper);
        attributes.addFlashAttribute("messages","删除成功");
        return "redirect:/admin/types";
    }
}
