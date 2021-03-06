package tk.lenkyun.foodbook.server.UserManagement.Adapter;

import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.UserAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoItem;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.Domain.Operation.RegistrationBuilder;
import tk.lenkyun.foodbook.server.UserManagement.Utils.Token;

import java.util.Collection;

/**
 * Created by lenkyun on 5/11/2558.
 */
public interface UserAdapter {
//    public abstract Administrator getAdminUserById(String id);
//    public abstract Supporter getSupporterUserById(String id);
    User getUserById(String id);
    User getUserByUsername(String name);
    User getUserByFacebookId(String fid);
    User getUser(UserAuthenticationInfo authenticationInfo);
    User updateUser(User user);
    User createUser(RegistrationBuilder registrationBuilder, String facebookId, PhotoItem profile_picture, PhotoItem profile_cover);
    void removeUser(String id);

    Boolean getIsFollowing(User user, User following);
    Boolean setFollowing(User user, User following);
    Boolean unsetFollowing(User user, User following);
    Collection<User> getAllFollowingUser(User user);

    User updateUserAuthen(User user, UserAuthenticationInfo authen);

    User updateUserProfilePicture(User user, PhotoItem photoItem);
}
