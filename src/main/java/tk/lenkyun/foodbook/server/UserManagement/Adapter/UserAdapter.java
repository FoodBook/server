package tk.lenkyun.foodbook.server.UserManagement.Adapter;

import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.UserAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoItem;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.Domain.Operation.RegistrationBuilder;

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
    User createUser(RegistrationBuilder registrationBuilder, PhotoItem profile_picture, PhotoItem profile_cover);
    void removeUser(String id);
}
