package com.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.domain.User;
import com.service.UserService;
import com.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 47550
 */
@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String loginPage () {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login (String username, String password, HttpSession session, RedirectAttributes attributes) {
        User user;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", MD5Util.getMD5String(password));
        queryWrapper.allEq(map);
        user = userService.getOne(queryWrapper);
        if (user == null) {
            attributes.addFlashAttribute("messages", "账号或者密码错误");
            return "redirect:/admin/";
        } else {
            user.setPassword(null);
            session.setAttribute("user", user);
            return "admin/index";
        }
    }

    @RequestMapping("/logout")
    public String logout (HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/admin/";
    }
}
