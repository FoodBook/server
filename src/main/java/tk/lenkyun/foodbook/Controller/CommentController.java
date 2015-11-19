package tk.lenkyun.foodbook.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Comment;
import tk.lenkyun.foodbook.foodbook.ResponseWrapper;
import tk.lenkyun.foodbook.server.Exception.NoPermissionException;
import tk.lenkyun.foodbook.server.PostManagement.PostFeed;
import tk.lenkyun.foodbook.server.UserManagement.Utils.Token;
import tk.lenkyun.foodbook.server.UserManagement.Utils.TokenProvider;

/**
 * Created by lenkyun on 17/11/2558.
 */
@RestController
public class CommentController {
    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    PostFeed postFeed;

    @RequestMapping(method = RequestMethod.POST, value = "/comment/{id}/{token}")
    public @ResponseBody
    ResponseWrapper<Comment> updateComment(@PathVariable(value = "id") String id, @PathVariable(value = "token") String tokenString,
        @RequestBody Comment inputComment)
    {
        ResponseWrapper<Comment> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        if(inputComment.getMessage() == null){
            responseWrapper.setError(2);
            responseWrapper.setDetail("Message can't be null.");
            return responseWrapper;
        }

        ResponseWrapper<Comment> comment = getComment(id, tokenString);
        if(comment.getError() != 0){
            responseWrapper.setError(comment.getError());
            responseWrapper.setDetail(comment.getDetail());
            return responseWrapper;
        }

        inputComment.setId(comment.getResult().getId());
        inputComment.setCreatedDate(comment.getResult().getCreatedDate());
        inputComment.setUser(comment.getResult().getUser());

        try {
            responseWrapper.setResult(
                postFeed.updateComment(token, inputComment)
            );
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/comment/{id}/delete/{token}")
    public @ResponseBody
    ResponseWrapper<Comment> deleteComment(@PathVariable(value = "id") String id, @PathVariable(value = "token")  String tokenString){
        ResponseWrapper<Comment> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        ResponseWrapper<Comment> comment = getComment(id, tokenString);
        if(comment.getError() != 0){
            responseWrapper.setError(comment.getError());
            responseWrapper.setDetail(comment.getDetail());
            return responseWrapper;
        }

        try {
            postFeed.removeComment(token, comment.getResult());
        }catch (NoPermissionException e){
            responseWrapper.setError(403);
            responseWrapper.setDetail("No permission.");
        }
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/comment/{id}/{token}")
    public @ResponseBody
    ResponseWrapper<Comment> getComment(@PathVariable(value = "id") String id, @PathVariable(value = "token")  String tokenString){
        ResponseWrapper<Comment> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        Comment comment = postFeed.getComment(token, id);

        if(comment != null){
            responseWrapper.setResult(comment);
        }else{
            responseWrapper.setError(404);
            responseWrapper.setDetail("No comment.");
        }

        return responseWrapper;
    }
}
