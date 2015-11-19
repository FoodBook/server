package tk.lenkyun.foodbook.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.UserAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoContent;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.Domain.Operation.RegistrationBuilder;
import tk.lenkyun.foodbook.foodbook.ResponseWrapper;
import tk.lenkyun.foodbook.server.Exception.NoPermissionException;
import tk.lenkyun.foodbook.server.PostManagement.PostFeed;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.Exception.DuplicateUserException;
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

    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public @ResponseBody
    ResponseWrapper<Boolean> userChangeInfo(@RequestBody RegistrationBuilder register){
        ResponseWrapper<Boolean> responseWrapper = new ResponseWrapper<>();

        if(register.getAuthenticationInfo() == null ||
                register.getProfilePicture() == null ||
                register.getProfilePicture().getContent().length == 0 ||
                register.getFirstname() == null ||
                register.getLastname() == null ||
                register.getUsername() == null
                ){
            responseWrapper.setError(2);
            responseWrapper.setDetail("Missing required arguments.");
        }

        if(register.getUsername() == null || register.getUsername().trim().length() < 6){
            responseWrapper.setError(2);
            responseWrapper.setDetail("Username length too short.");
        }

        try {
            User userOut = userManager.register(register);
            responseWrapper.setResult(userOut != null);
        }catch (DuplicateUserException e){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Token expired.");
        }

        return responseWrapper;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/user/me/{token}")
    public @ResponseBody
    ResponseWrapper<User> userChangeInfo(@PathVariable(value = "token") String tokenString,
                                                   @RequestBody User inputUser){
        ResponseWrapper<User> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        try {
            User userOut = userManager.updateUserInfo(token, inputUser);
            if (userOut == null) {
                responseWrapper.setError(1);
                responseWrapper.setDetail("Token expired.");
            } else {
                responseWrapper.setResult(userOut);
            }
        }catch (NoPermissionException e){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Token expired.");
        }

        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/me/profile_picture/{token}")
    public @ResponseBody
    ResponseWrapper<User> userChangeProfilePicture(@PathVariable(value = "token") String tokenString,
                                             @RequestBody PhotoContent inputContent){
        ResponseWrapper<User> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        if(inputContent.getContent() == null || inputContent.getContent().length == 0){
            responseWrapper.setError(2);
            responseWrapper.setDetail("Invalid input.");
            return responseWrapper;
        }

        ResponseWrapper<User> user = requestUserInfo(token.getUid(), tokenString);
        if(user.getError() != 0){
            responseWrapper.setError(user.getError());
            responseWrapper.setDetail(user.getDetail());
            return responseWrapper;
        }

        try {
            User userOut = userManager.updateProfilePhoto(token, inputContent);
            if (userOut == null) {
                responseWrapper.setError(1);
                responseWrapper.setDetail("Token expired.");
            } else {
                responseWrapper.setResult(userOut);
            }
        }catch (NoPermissionException e){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Token expired.");
        }

        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/me/password/{token}")
    public @ResponseBody
    ResponseWrapper<User> userChangePassword(@PathVariable(value = "token") String tokenString,
                                                 @RequestBody UserAuthenticationInfo inputAuthen){
        ResponseWrapper<User> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        if(inputAuthen.getInfo() == null || inputAuthen.getInfo().length() < 7){
            responseWrapper.setError(2);
            responseWrapper.setDetail("Password must be longer than or equals 7.");
            return responseWrapper;
        }

        ResponseWrapper<User> user = requestUserInfo(token.getUid(), tokenString);
        if(user.getError() != 0){
            responseWrapper.setError(user.getError());
            responseWrapper.setDetail(user.getDetail());
            return responseWrapper;
        }

        user.getResult().setUserAuthenticationInfo(inputAuthen);

        try {
            User userOut = userManager.updateUser(token, inputAuthen);
            if (userOut == null) {
                responseWrapper.setError(1);
                responseWrapper.setDetail("Token expired.");
            } else {
                responseWrapper.setResult(userOut);
            }
        }catch (NoPermissionException e){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Token expired.");
        }

        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/me/{token}")
    public ResponseWrapper<User> requestMe(@PathVariable(value = "token") String token){
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

    @RequestMapping(method = RequestMethod.GET, value = "/user/{uid}/{token}")
    public ResponseWrapper<User> requestUserInfo(@PathVariable(value = "uid") String uid, @PathVariable(value = "token") String token){
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

    @RequestMapping(method = RequestMethod.GET, value = "/user/{uid}/post/{token}")
    public ResponseWrapper<Collection<FoodPost>> requestUserPostInfo(@PathVariable(value = "uid") String uid, @PathVariable(value = "token") String tokenString){
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

    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}/follow/{token}")
    public @ResponseBody
    ResponseWrapper<Boolean> getIsFollowUser(@PathVariable(value = "id") String id, @PathVariable(value = "token") String tokenString){
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

    @RequestMapping(method = RequestMethod.POST, value = "/user/{id}/follow/{token}")
    public @ResponseBody
    ResponseWrapper<Boolean> setFollowUser(@PathVariable(value = "id") String id, @PathVariable(value = "token") String tokenString){
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

    @RequestMapping(method = RequestMethod.POST, value = "/user/{id}/unfollow/{token}")
    public @ResponseBody
    ResponseWrapper<Boolean> setUnFollowUser(@PathVariable(value = "id") String id, @PathVariable(value = "token") String tokenString){
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

    @RequestMapping(method = RequestMethod.GET, value = "/user/me/following/{token}")
    public @ResponseBody
    ResponseWrapper<Collection<User>> getAllFollowUser(@PathVariable(value = "token") String tokenString){
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
