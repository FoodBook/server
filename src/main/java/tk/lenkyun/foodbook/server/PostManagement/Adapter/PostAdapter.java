package tk.lenkyun.foodbook.server.PostManagement.Adapter;

import tk.lenkyun.foodbook.foodbook.Domain.Data.Comment;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;

import java.util.Collection;

/**
 * Created by lenkyun on 17/11/2558.
 */
public interface PostAdapter {
    FoodPost insertPost(FoodPost foodPost);
    FoodPost getPost(String id);
    Collection<FoodPost> getPost(User user);
    FoodPost updatePost(FoodPost foodPost);
    void removePost(FoodPost foodPost);

    Collection<Comment> getComments(FoodPost post);
    Comment getComment(String id);
    void removeComment(Comment comment);
    Comment updateComment(Comment comment);
    Comment createComment(Comment comment);

    Integer getRate(FoodPost foodPost);

    Integer getRateMe(FoodPost foodPost, User userById);

    Integer setRate(FoodPost foodPost, User user, int rate);
}