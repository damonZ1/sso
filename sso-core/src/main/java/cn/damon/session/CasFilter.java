package cn.damon.session;

import cn.damon.utils.CookieBasedSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class CasFilter implements Filter {
    public static final String USER_INFO = "user";

    private RedisTemplate redisTemplate;

    @Value(value = "${cas.serverUrl}")
    private String serverUrl;

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {


        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //create own wrapper
        MyRequestWrapper myRequestWrapper = new MyRequestWrapper(request,redisTemplate);

        //if not login do follow
        String requestUrl = request.getServletPath();
        if (!"/toLogin".equals(requestUrl)
                //if it isn't login url
                && !requestUrl.startsWith("/login")
                //if not start with 'login'
                && !myRequestWrapper.isLogin()
                //if not login
        ) {

            /**
             *
             * ticket is null or sessionId not exists indicate that this it not auto
             * login request.force to login page
             */
            String ticket = request.getParameter("ticket");
            if (null == ticket || null == redisTemplate.opsForValue().get(ticket)){
                HttpServletResponse response = (HttpServletResponse)servletResponse;
                //redirect to login page
                response.sendRedirect(serverUrl+"?url="+request.getRequestURL().toString());
                return ;
            }

            /**
             *
             * is login request , set cookie, this request is 302 redirect
             * the request after redirect with cookie,is logined
             */

            //ticket not null and sessionId not null
            // find ticket by sessionId
            myRequestWrapper.setSessionId((String) redisTemplate.opsForValue().get(ticket));

            //
            myRequestWrapper.createSession();
            //set cookie under the relative path of the project
            CookieBasedSession.onNewSession(myRequestWrapper,(HttpServletResponse)servletResponse);

            //重定向自流转一次，原地跳转重向一次
            HttpServletResponse response = (HttpServletResponse)servletResponse;
            response.sendRedirect(request.getRequestURL().toString());
            return;
        }

        try {
            filterChain.doFilter(myRequestWrapper,servletResponse);
        } finally {
            myRequestWrapper.commitSession();
        }
    }

    @Override
    public void destroy() {

    }
}
