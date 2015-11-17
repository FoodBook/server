package tk.lenkyun.foodbook.server.PostManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.SessionAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Comment;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoItem;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.Domain.Operation.FoodPostBuilder;
import tk.lenkyun.foodbook.server.PhotoManagement.Adapter.PhotoAdapter;
import tk.lenkyun.foodbook.server.PostManagement.Adapter.PostAdapter;
import tk.lenkyun.foodbook.server.PostManagement.Exception.NoPermissionException;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.Utils.Token;
import tk.lenkyun.foodbook.server.UserManagement.Utils.TokenProvider;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lenkyun on 5/11/2558.
 */
@Service
public class PostFeed {
    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    UserAdapter userAdapter;

    @Autowired
    PostAdapter postAdapter;

    @Autowired
    PhotoAdapter photoAdapter;

    public Comment insertComment(Token token, Comment comment) throws NoPermissionException {
        if(token.getUid().equals(comment.getUser().getId()) && !token.isTimedOut()){
            return postAdapter.createComment(comment);
        }else{
            throw new NoPermissionException();
        }
    }

    public Comment updateComment(Token token, Comment comment) throws NoPermissionException {
        if(token.getUid().equals(comment.getUser().getId()) && !token.isTimedOut()){
            return postAdapter.updateComment(comment);
        }else{
            throw new NoPermissionException();
        }
    }

    public void removeComment(Token token, Comment comment) throws NoPermissionException {
        if(token.getUid().equals(comment.getUser().getId()) && !token.isTimedOut()){
            postAdapter.removeComment(comment);
        }else{
            throw new NoPermissionException();
        }
    }

    public Comment getComment(Token token, String id){
        return postAdapter.getComment(id);
    }

    public void removePost(Token token, FoodPost foodPost) throws NoPermissionException {
        if(token.getUid().equals(foodPost.getOwner().getId()) && !token.isTimedOut()){
            postAdapter.removePost(foodPost);
        }else{
            throw new NoPermissionException();
        }
    }

    public Collection<Comment> getComments(Token token, FoodPost foodPost){
        ArrayList<Comment> comments = new ArrayList<>();
        return postAdapter.getComments(foodPost);
    }

    public FoodPost updatePost(Token token, FoodPost foodPost){
        if(token.getUid().equals(foodPost.getOwner().getId()) && !token.isTimedOut()){
            return postAdapter.updatePost(foodPost);
        }else{
            throw new NoPermissionException();
        }
    }

    public Collection<FoodPost> getPost(Token token, User id){
        if(token.isTimedOut()) {
            throw new NoPermissionException();
        }
        return postAdapter.getPost(id);
    }

    public FoodPost getPost(Token token, String id){
        if(token.isTimedOut()) {
            throw new NoPermissionException();
        }
        return postAdapter.getPost(id);
    }

    public FoodPost publistPost(Token token, FoodPostBuilder builder){
        if(token.isTimedOut()){
            return null;
        }

        User user = userAdapter.getUserById(token.getUid());
        user.setUserAuthenticationInfo(null);

        FoodPost foodPost = builder.build();
        foodPost.setOwner(user);

        URI uri = photoAdapter.postPhoto(builder.getBundle().get(0));
        foodPost.getPostDetail().addPhoto(
                new PhotoItem(uri, 0, 0)
        );

        return postAdapter.insertPost(foodPost);
    }
}
