package cn.damon.session;

import cn.damon.utils.CookieBasedSession;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MyRequestWrapper extends HttpServletRequestWrapper {

    //if ignore
    private volatile boolean committed = false;

    //sessionId
    private String sessionId = null;

    //custom session
    private MySession session;

    //redis utils
    private RedisTemplate redisTemplate;

    /**
     *  wrapper the request,redirect before arrive controller
     * @param request request
     * @param redisTemplate redisTemplate
     */
    public MyRequestWrapper(HttpServletRequest request,RedisTemplate redisTemplate) {
        super(request);
        this.redisTemplate = redisTemplate;
    }

    /**
     * submit session to redis
     */
    public void commitSession() {
        //if ignore ,not commit to redis ,commit as default
        if (committed) {
            return;
        }
        //change it to ignore
        committed = true;

        MySession session = this.getSession();
        if (session != null && null != session.getAttrs()) {
            redisTemplate.opsForHash().putAll(session.getId(),session.getAttrs());
        }
    }

    /**
     * create new session
     * @return
     */
    public MySession createSession() {
        //if sessionId is null,get form request ,otherwise get sessionId
        String psessionId = (sessionId != null)?sessionId: CookieBasedSession.getRequestedSessionId(this);
        //properties
        Map<String,Object> attr ;
        if (!StringUtils.isEmpty(psessionId)){
            //get userInfo from redis throw sessionId
            attr = redisTemplate.opsForHash().entries(psessionId);
        } else {
            //if sessionId is null
            System.out.println("create session by rId:"+sessionId);
            //we create a new sessionId
            psessionId = UUID.randomUUID().toString();
            attr = new HashMap<>();
        }

        session = new MySession();
        //set sessionId
        session.setId(psessionId);
        //set attributes
        session.setAttrs(attr);

        return session;
    }

    /**
     * get session
     * @return
     */
    @Override
    public MySession getSession() {
        return this.getSession(true);
    }

    /**
     * get session
     * @return
     */
    @Override
    public MySession getSession(boolean create) {
        //if null, create
        if (null != session){
            return session;
        }
        return this.createSession();
    }

    public void removeSession(String sessionId) {
        if(StringUtils.isEmpty(sessionId)){
            return;
        }
        if(sessionId.equals(this.session.getId())){
            this.session = null;
        }
    }

    /**
     * charge if it is logined
     * @return
     */
    public boolean isLogin(){
        //if userInfo is null, not login ,otherwise ,logined
        Object user = getSession().getAttribute(CasFilter.USER_INFO);
        return null != user;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
