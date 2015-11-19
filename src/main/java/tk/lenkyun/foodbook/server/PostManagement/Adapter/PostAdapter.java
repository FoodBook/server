package tk.lenkyun.foodbook.server.PostManagement.Adapter;

import tk.lenkyun.foodbook.foodbook.Domain.Data.Comment;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Location;
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

    Float getRate(FoodPost foodPost);

    Float getRate(FoodPost foodPost, User userById);

    Float setRate(FoodPost foodPost, User user, float rate);
    Collection<FoodPost> getPostByKeyword(String keyword);
    Collection<FoodPost> getPostNearLocation(Location.LatLng latLng, double range);
    Collection<Location> getLocationNearLocation(Location.LatLng latLng, double range);
}
