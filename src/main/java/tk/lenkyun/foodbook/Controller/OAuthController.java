package tk.lenkyun.foodbook.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.SessionAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.UserAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.ResponseWrapper;
import tk.lenkyun.foodbook.server.Config;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.SessionManager;
import tk.lenkyun.foodbook.server.UserManagement.UserAuthentication;
import tk.lenkyun.foodbook.server.UserManagement.Utils.TokenProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by lenkyun on 6/11/2558.
 */
@RestController
public class OAuthController {
    @Autowired
    UserAuthentication userAuthentication;
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    SessionManager sessionManager;
    @Autowired
    UserAdapter userAdapter;


    @RequestMapping(method = RequestMethod.POST, value = "/oauth/test")
    public @ResponseBody UserAuthenticationInfo test(){
        return new UserAuthenticationInfo("lenkyun", "1234");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/oauth/login")
    public @ResponseBody
    ResponseWrapper<SessionAuthenticationInfo> sessionLogin(@RequestBody UserAuthenticationInfo user) {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseWrapper<SessionAuthenticationInfo> wrapper = new ResponseWrapper<>();
        SessionAuthenticationInfo session = userAuthentication.login(user);

        if (session == null) {
            wrapper.setError(1);
            wrapper.setDetail("Invalid credential.");
            wrapper.setResult(null);

            return wrapper;
        } else {
            wrapper.setError(0);
            wrapper.setDetail(null);
            wrapper.setResult(session);

            return wrapper;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/oauth/renew")
    public @ResponseBody ResponseWrapper<SessionAuthenticationInfo> sessionRenew(@RequestParam(value = "token") String token){
        ResponseWrapper<SessionAuthenticationInfo> wrapper = new ResponseWrapper<>();

        if(sessionManager.isTokenValid(token)){
            String id = tokenProvider.getUserId(token);
            User user = userAdapter.getUserById(id);
            SessionAuthenticationInfo session = new SessionAuthenticationInfo(id, user.getUsername(),
                    tokenProvider.getToken(userAdapter.getUserById(id), Config.SESSION_SHORT));

            wrapper.setError(0);
            wrapper.setDetail(null);
            wrapper.setResult(session);
        }else{
            wrapper.setError(1);
            wrapper.setDetail("Result invalid");
            wrapper.setResult(null);
        }

        return wrapper;
    }
}
