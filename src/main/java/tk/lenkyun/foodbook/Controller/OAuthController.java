package tk.lenkyun.foodbook.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.FacebookAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.SessionAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.UserAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.ResponseWrapper;
import tk.lenkyun.foodbook.server.Config;
import tk.lenkyun.foodbook.server.Exception.NoPermissionException;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.SessionManager;
import tk.lenkyun.foodbook.server.UserManagement.UserManager;
import tk.lenkyun.foodbook.server.UserManagement.Utils.TokenProvider;

/**
 * Created by lenkyun on 6/11/2558.
 */
@RestController
public class OAuthController {
    @Autowired
    UserManager userManager;
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    SessionManager sessionManager;
    @Autowired
    UserAdapter userAdapter;


    @RequestMapping(method = RequestMethod.POST, value = "/oauth/test")
    public @ResponseBody
    ResponseWrapper<SessionAuthenticationInfo> test(){
        return sessionLogin(new UserAuthenticationInfo("lenkyun", "1234"));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/oauth/login")
    public @ResponseBody
    ResponseWrapper<SessionAuthenticationInfo> sessionLogin(@RequestBody UserAuthenticationInfo user) {
        ResponseWrapper<SessionAuthenticationInfo> wrapper = new ResponseWrapper<>();
        SessionAuthenticationInfo session = userManager.login(user);

        if (session == null) {
            wrapper.setError(1);
            wrapper.setDetail("Invalid credential.");
        } else {
            wrapper.setResult(session);
        }
        return wrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/oauth/login/facebook")
    public @ResponseBody
    ResponseWrapper<SessionAuthenticationInfo> sessionLoginFacebook(@RequestBody FacebookAuthenticationInfo user) {
        ResponseWrapper<SessionAuthenticationInfo> wrapper = new ResponseWrapper<>();

        try {
            SessionAuthenticationInfo session = userManager.login(user);

            if (session == null) {
                wrapper.setError(1);
                wrapper.setDetail("Invalid credential.");
            } else {
                wrapper.setResult(session);
            }
        }catch (NoPermissionException e){
            wrapper.setError(1);
            wrapper.setDetail("Invalid credential.");
        }
        return wrapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/oauth/renew")
    public @ResponseBody ResponseWrapper<SessionAuthenticationInfo> sessionRenew(@RequestParam(value = "token") String token){
        ResponseWrapper<SessionAuthenticationInfo> wrapper = new ResponseWrapper<>();

        if(sessionManager.isTokenValid(token)){
            String id = tokenProvider.getUserId(token);
            User user = userAdapter.getUserById(id);
            SessionAuthenticationInfo session = new SessionAuthenticationInfo(id,
                    tokenProvider.getToken(userAdapter.getUserById(id), Config.SESSION_SHORT));

            wrapper.setResult(session);
        }else{
            wrapper.setError(1);
            wrapper.setDetail("Result invalid");
        }

        return wrapper;
    }
}
