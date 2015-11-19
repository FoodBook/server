package tk.lenkyun.foodbook.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.ResponseWrapper;
import tk.lenkyun.foodbook.server.Exception.NoPermissionException;
import tk.lenkyun.foodbook.server.PostManagement.PostFeed;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.SessionManager;
import tk.lenkyun.foodbook.server.UserManagement.UserManager;
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
    @Autowired
    UserManager userManager;

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

    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}/follow")
    public @ResponseBody
    ResponseWrapper<Boolean> getIsFollowUser(@PathVariable(value = "id") String id, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<Boolean> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<User> following = requestUserInfo(id, tokenString);
        if(following.getError() != 0){
            responseWrapper.setError(following.getError());
            responseWrapper.setDetail(following.getDetail());
            return responseWrapper;
        }

        if(token.getUid().equals(following.getResult().getId())){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Not able to follow yourself.");
            return responseWrapper;
        }

        try {
            responseWrapper.setResult(
                    userManager.getFollowingUserStatus(token, following.getResult()));
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/{id}/follow")
    public @ResponseBody
    ResponseWrapper<Boolean> setFollowUser(@PathVariable(value = "id") String id, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<Boolean> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<User> following = requestUserInfo(id, tokenString);
        if(following.getError() != 0){
            responseWrapper.setError(following.getError());
            responseWrapper.setDetail(following.getDetail());
            return responseWrapper;
        }

        if(token.getUid().equals(following.getResult().getId())){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Not able to follow yourself.");
            return responseWrapper;
        }

        try {
            responseWrapper.setResult(
                    userManager.setFollowingUser(token, following.getResult()));
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/{id}/unfollow")
    public @ResponseBody
    ResponseWrapper<Boolean> setUnFollowUser(@PathVariable(value = "id") String id, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<Boolean> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<User> following = requestUserInfo(id, tokenString);
        if(following.getError() != 0){
            responseWrapper.setError(following.getError());
            responseWrapper.setDetail(following.getDetail());
            return responseWrapper;
        }

        if(token.getUid().equals(following.getResult().getId())){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Not able to follow yourself.");
            return responseWrapper;
        }

        try {
            responseWrapper.setResult(
                    userManager.unsetFollowingUser(token, following.getResult()));
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/me/following")
    public @ResponseBody
    ResponseWrapper<Collection<User>> getAllFollowUser(@RequestParam(value = "token") String tokenString){
        ResponseWrapper<Collection<User>> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        try {
            responseWrapper.setResult(
                    userManager.getAllFollowingUser(token));
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }
        return responseWrapper;
    }

}
