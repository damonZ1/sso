package cn.damon.controller;
import cn.damon.session.CasFilter;
import cn.damon.session.MyRequestWrapper;
import cn.damon.session.UserForm;
import cn.damon.utils.CookieBasedSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller
public class IndexController {
    @Autowired
    private RedisTemplate redisTemplate;

    //all not login request ,force to this url
    @GetMapping("/toLogin")
    public String toLogin(Model model, MyRequestWrapper request, HttpServletResponse response) throws ServletException, IOException {
        //judge if it is logined
        if (request.isLogin()){
            //if it is logined,return to backurl with ticket
            String ticket = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(ticket,request.getSession().getId(),20, TimeUnit.SECONDS);
            return "redirect:"+request.getParameter("url")+"?ticket="+ticket;
        }
        //if it is not login,create a user entity
        UserForm user = new UserForm();
        user.setUsername("damon");
        user.setPassword("123456");
        user.setBackurl(request.getParameter("url"));
        //set user attributes,fill in login page
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping("/login")
    public void login(@ModelAttribute UserForm user,MyRequestWrapper request,HttpServletResponse response) throws IOException {
        //set user info to session
        request.getSession().setAttribute("user",user);
        //after login set 20s ticket
        String ticket = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(ticket,request.getSession().getId(),20, TimeUnit.SECONDS);
        //set a cookie in server
        CookieBasedSession.onNewSession(request,response);
        //redirect to backurl
        response.sendRedirect(user.getBackurl()+"?ticket="+ticket);

    }

    @GetMapping("/index")
    public ModelAndView index(MyRequestWrapper request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        modelAndView.addObject("user", request.getSession().getAttribute(CasFilter.USER_INFO));
        //if you want to request this page,you can get userInfo after login
        return modelAndView;
    }

    @GetMapping("/logout")
    @ResponseBody
    public void logout(MyRequestWrapper request, HttpServletResponse response) throws IOException {
        String redisKey = request.getSession().getId();
        if(redisKey == null){
            throw new RuntimeException("未登录，不需要退出");
        }
        //clear pession from redis
        redisTemplate.opsForHash().delete(redisKey, CasFilter.USER_INFO);
        //clear pession from request
        request.removeSession(redisKey);
        //redirect to front page
        response.sendRedirect(request.getContextPath()+"/index");
    }
}
