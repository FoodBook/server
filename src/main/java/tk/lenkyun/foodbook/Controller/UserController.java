package tk.lenkyun.foodbook.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.ResponseWrapper;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.SessionManager;
import tk.lenkyun.foodbook.server.UserManagement.Utils.TokenProvider;

/**
 * Created by lenkyun on 6/11/2558.
 */
@RestController
public class UserController {
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    SessionManager sessionManager;
    @Autowired
    UserAdapter userAdapter;

    @RequestMapping(method = RequestMethod.GET, value = "/user/me")
    public ResponseWrapper<User> requestMe(@RequestParam(value = "token", required = false) String token){
        ResponseWrapper<User> wrapper = new ResponseWrapper<>();

        if(token != null && sessionManager.isTokenValid(token)){
            wrapper.setError(0);
            wrapper.setDetail(null);

            User user = userAdapter.getUserById(tokenProvider.getUserId(token));
            user.setUserAuthenticationInfo(null);

            wrapper.setResult(user);

            return wrapper;
        }else{
            wrapper.setError(1);
            wrapper.setDetail("Invalid token.");
            wrapper.setResult(null);

            return wrapper;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{uid}")
    public ResponseWrapper<User> requestUserInfo(@PathVariable(value = "uid") String uid, @RequestParam(value = "token") String token){
        ResponseWrapper<User> wrapper = new ResponseWrapper<>();
        if(sessionManager.isTokenValid(token)){
            if(tokenProvider.getUserId(token).equals(uid)){
                return requestMe(token);
            }else{
                try {
                    Long.parseLong(uid);
                } catch(NumberFormatException e){
                    wrapper.setError(1);
                    wrapper.setDetail("Invalid user id.");
                    wrapper.setResult(null);

                    return wrapper;
                }

                User user = userAdapter.getUserById(uid);
                if(user != null) {
                    user.setUserAuthenticationInfo(null);
                    user.setSocialId(null);

                    wrapper.setError(0);
                    wrapper.setDetail(null);
                    wrapper.setResult(user);
                }else{
                    wrapper.setError(1);
                    wrapper.setDetail("Invalid user id.");
                    wrapper.setResult(null);
                }
                return wrapper;
            }
        }else{
            wrapper.setError(1);
            wrapper.setDetail("Invalid token.");
            wrapper.setResult(null);

            return wrapper;
        }
    }
}
