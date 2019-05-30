package cn.damon.controller;
import cn.damon.session.CasFilter;
import cn.damon.session.MyRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
@RequestMapping(value="/")
public class IndexController{

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/index")
    public ModelAndView index(MyRequestWrapper request) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("index");
        Object user = request.getSession().getAttribute(CasFilter.USER_INFO);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/logout")
    @ResponseBody
    public void logout(MyRequestWrapper request, HttpServletResponse response) throws IOException {
        String redisKey = request.getSession().getId();
        if(redisKey == null){
            throw new RuntimeException("未登录，不需要退出");
        }
        //clear psession from redis
        redisTemplate.opsForHash().delete(redisKey, CasFilter.USER_INFO);
        //clear pssion from request
        request.removeSession(redisKey);
        //redirect to index
        response.sendRedirect(request.getContextPath()+"/index");
    }

}
