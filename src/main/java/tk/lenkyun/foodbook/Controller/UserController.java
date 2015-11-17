package tk.lenkyun.foodbook.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.ResponseWrapper;
import tk.lenkyun.foodbook.server.PostManagement.PostFeed;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.SessionManager;
import tk.lenkyun.foodbook.server.UserManagement.Utils.Token;
import tk.lenkyun.foodbook.server.UserManagement.Utils.TokenProvider;

import java.util.Collection;

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
    @Autowired
    PostFeed postFeed;

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

    @RequestMapping(method = RequestMethod.GET, value = "/user/{uid}/post")
    public ResponseWrapper<Collection<FoodPost>> requestUserPostInfo(@PathVariable(value = "uid") String uid, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<Collection<FoodPost>> wrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);
        if(token == null){
            wrapper.setError(1);
            wrapper.setDetail("Invalid token.");
            return wrapper;
        }

        ResponseWrapper<User> user = requestUserInfo(uid, tokenString);
        if(user.getError() != 0){
            wrapper.setError(user.getError());
            wrapper.setDetail(user.getDetail());
            return wrapper;
        }

        Collection<FoodPost> foodPosts = postFeed.getPost(token, userAdapter.getUserById(token.getUid()));
        if(foodPosts.size() != 0){
            wrapper.setResult(foodPosts);
        }else{
            wrapper.setError(404);
            wrapper.setDetail("No post.");
        }

        return wrapper;
    }
}
