package tk.lenkyun.foodbook.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Comment;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.ResponseWrapper;
import tk.lenkyun.foodbook.server.Exception.NoPermissionException;
import tk.lenkyun.foodbook.server.PostManagement.PostFeed;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.Utils.Token;
import tk.lenkyun.foodbook.server.UserManagement.Utils.TokenProvider;

import java.util.Collection;

/**
 * Created by lenkyun on 17/11/2558.
 */
@RestController
public class PostController {
    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    PostFeed postFeed;

    @Autowired
    UserAdapter userAdapter;

    @RequestMapping(method = RequestMethod.GET, value = "/post/{id}/rate/me")
    public @ResponseBody
    ResponseWrapper<Integer> getRateMe(@PathVariable(value = "id") String id, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<Integer> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<FoodPost> foodPost = getFoodPost(id, tokenString);
        if(foodPost.getError() != 0){
            responseWrapper.setError(foodPost.getError());
            responseWrapper.setDetail(foodPost.getDetail());
            return responseWrapper;
        }

        try {
            postFeed.getRateMe(token, foodPost.getResult());
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/post/{id}/rate")
    public @ResponseBody
    ResponseWrapper<Integer> getRate(@PathVariable(value = "id") String id, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<Integer> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<FoodPost> foodPost = getFoodPost(id, tokenString);
        if(foodPost.getError() != 0){
            responseWrapper.setError(foodPost.getError());
            responseWrapper.setDetail(foodPost.getDetail());
            return responseWrapper;
        }

        try {
            postFeed.getRate(token, foodPost.getResult());
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/post/{id}/delete")
    public @ResponseBody
    ResponseWrapper<FoodPost> deletePost(@PathVariable(value = "id") String id, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<FoodPost> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<FoodPost> foodPost = getFoodPost(id, tokenString);
        if(foodPost.getError() != 0){
            responseWrapper.setError(foodPost.getError());
            responseWrapper.setDetail(foodPost.getDetail());
            return responseWrapper;
        }

        try {
            postFeed.removePost(token, foodPost.getResult());
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/post/{id}")
    public @ResponseBody
    ResponseWrapper<FoodPost> updatePost(@PathVariable(value = "id") String id, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<FoodPost> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<FoodPost> foodPost = getFoodPost(id, tokenString);
        if(foodPost.getError() != 0){
            responseWrapper.setError(foodPost.getError());
            responseWrapper.setDetail(foodPost.getDetail());
            return responseWrapper;
        }

        try {
            responseWrapper.setResult(postFeed.updatePost(token, foodPost.getResult()));
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/post/{id}/comment")
    public @ResponseBody
    ResponseWrapper<Comment> insertFoodPostComments(@PathVariable(value = "id") String id, @RequestParam(value="token") String tokenString,
                                                    @RequestBody Comment inputComment){
        ResponseWrapper<Comment> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<FoodPost> foodPost = getFoodPost(id, tokenString);
        if(foodPost.getError() != 0){
            responseWrapper.setError(foodPost.getError());
            responseWrapper.setDetail(foodPost.getDetail());
            return responseWrapper;
        }

        inputComment.setAssoc(foodPost.getResult());
        inputComment.setUser(userAdapter.getUserById(token.getUid()));

        Comment comment = postFeed.insertComment(token, inputComment);
        if(comment != null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Token expired.");
        }else {
            responseWrapper.setResult(comment);
        }

        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/post/{id}/rate")
    public @ResponseBody
    ResponseWrapper<Integer> setPostRate(@PathVariable(value = "id") String id, @RequestParam(value="token") String tokenString,
                                                    @RequestBody Integer inputRate){
        ResponseWrapper<Integer> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<FoodPost> foodPost = getFoodPost(id, tokenString);
        if(foodPost.getError() != 0){
            responseWrapper.setError(foodPost.getError());
            responseWrapper.setDetail(foodPost.getDetail());
            return responseWrapper;
        }


        try {
            responseWrapper.setResult(postFeed.setRate(token, foodPost.getResult(), inputRate));
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }

        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/post/{id}/comment")
    public @ResponseBody
    ResponseWrapper<Collection<Comment>> getFoodPostComments(@PathVariable(value = "id") String id, @RequestParam(value="token") String tokenString){
        ResponseWrapper<Collection<Comment>> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<FoodPost> foodPost = getFoodPost(id, tokenString);
        if(foodPost.getError() != 0){
            responseWrapper.setError(foodPost.getError());
            responseWrapper.setDetail(foodPost.getDetail());
            return responseWrapper;
        }

        Collection<Comment> comments = postFeed.getComments(token, new FoodPost(id, null, null));
        if(comments.size() == 0){
            responseWrapper.setError(404);
            responseWrapper.setDetail("No comments.");
        }else {
            responseWrapper.setResult(comments);
        }

        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/post/{id}")
    public @ResponseBody
    ResponseWrapper<FoodPost> getFoodPost(@PathVariable(value = "id") String id, @RequestParam(value="token") String tokenString){
        ResponseWrapper<FoodPost> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        FoodPost post = postFeed.getPost(token, id);
        if(post == null){
            responseWrapper.setError(404);
            responseWrapper.setDetail("No post.");
        }else {
            responseWrapper.setResult(post);
        }

        return responseWrapper;
    }
}
