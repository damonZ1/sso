package cn.damon.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CookieBasedSession{

    //session name
    public static final String COOKIE_NAME_SESSION = "psession";

    //ge sessionId from request
    public static String getRequestedSessionId(HttpServletRequest request) {
        //get all cookie
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            //if null continue
            if (cookie == null) {
                continue;
            }
            // if it is not psession ,continue filter
            if (!COOKIE_NAME_SESSION.equalsIgnoreCase(cookie.getName())) {
                continue;
            }
            //return
            return cookie.getValue();
        }
        return null;
    }

    //create a session
    public static void onNewSession(HttpServletRequest request,
                             HttpServletResponse response) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        Cookie cookie = new Cookie(COOKIE_NAME_SESSION, sessionId);
        //set http only,it can defence
        cookie.setHttpOnly(true);
        //get relative path
        cookie.setPath(request.getContextPath() + "/");
//        cookie.setDomain("cas.com");
        //set max age
        cookie.setMaxAge(Integer.MAX_VALUE);
        //set cookie in response
        response.addCookie(cookie);
    }

}
