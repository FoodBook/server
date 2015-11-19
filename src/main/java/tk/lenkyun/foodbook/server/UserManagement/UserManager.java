package tk.lenkyun.foodbook.server.UserManagement;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.FacebookAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.SessionAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.UserAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoContent;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoItem;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.Profile;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.Domain.Operation.RegistrationBuilder;
import tk.lenkyun.foodbook.server.Exception.NoPermissionException;
import tk.lenkyun.foodbook.server.PhotoManagement.Adapter.PhotoAdapter;
import tk.lenkyun.foodbook.server.Config;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.SessionAdapter;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.Exception.DuplicateUserException;
import tk.lenkyun.foodbook.server.UserManagement.Utils.Token;

import java.net.URI;
import java.util.Collection;

/**
 * Created by lenkyun on 5/11/2558.
 */
@Service
@PropertySource("classpath:database.properties")
public class UserManager {
    @Autowired
    private UserAdapter userAdapter;
    @Autowired
    private SessionAdapter sessionAdapter;
    @Autowired
    private PhotoAdapter photoAdapter;
    @Autowired
    Environment env;

    public SessionAuthenticationInfo login(UserAuthenticationInfo authenticationInfo){
        User user = userAdapter.getUser(authenticationInfo);
        if(user == null)
            return null;

        SessionAuthenticationInfo session = sessionAdapter.createSession(user, Config.SESSION_SHORT);
        return session;
    }

    SessionAuthenticationInfo login(User user){
        return sessionAdapter.createSession(user, Config.SESSION_SHORT);
    }

    public User register(RegistrationBuilder helper) throws DuplicateUserException {
        if(userAdapter.getUserByUsername(helper.getUsername()) == null){

            String fbid = null;
            if(helper.getFacebookToken() != null) {
                FacebookClient facebookClient = new DefaultFacebookClient(helper.getFacebookToken(), Version.VERSION_2_4);
                com.restfb.types.User fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class);

                if (fbUser == null){
                    throw new NoPermissionException();
                }
                fbid = fbUser.getId();
            }

            PhotoItem profilePicture = new PhotoItem(photoAdapter.postPhoto(helper.getProfilePicture()), 0, 0);
            return userAdapter.createUser(helper, fbid, profilePicture, null);
        }else{
            throw new DuplicateUserException(helper.getUsername());
        }
    }

    public SessionAuthenticationInfo login(FacebookAuthenticationInfo user) throws NoPermissionException{
        try {
            FacebookClient facebookClient = new DefaultFacebookClient(user.getInfo(), Version.VERSION_2_4);
            com.restfb.types.User fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class);

            User userObj = userAdapter.getUserByFacebookId(fbUser.getId());
            return sessionAdapter.createSession(userObj, Config.SESSION_SHORT);
        }catch (Exception e){
            throw new NoPermissionException();
        }
    }

    public Boolean getFollowingUserStatus(Token token, User following) throws NoPermissionException {
        if(token.isTimedOut())
            throw new NoPermissionException();

        return userAdapter.getIsFollowing(
                userAdapter.getUserById(token.getUid()),
                following
        );
    }

    public Boolean setFollowingUser(Token token, User user) throws NoPermissionException{
        if(token.isTimedOut())
            throw new NoPermissionException();

        return userAdapter.setFollowing(
                userAdapter.getUserById(token.getUid()),
                user
        );
    }

    public Boolean unsetFollowingUser(Token token, User user) throws NoPermissionException{
        if(token.isTimedOut())
            throw new NoPermissionException();

        return userAdapter.unsetFollowing(
                userAdapter.getUserById(token.getUid()),
                user
        );
    }

    public Collection<User> getAllFollowingUser(Token token) throws NoPermissionException{
        if(token.isTimedOut())
            throw new NoPermissionException();

        Collection<User> users = userAdapter.getAllFollowingUser(
                userAdapter.getUserById(token.getUid())
        );

        for(User user : users){
            user.setUserAuthenticationInfo(null);
            user.setSocialId(null);
        }

        return users;
    }

    public User updateUser(Token token, UserAuthenticationInfo authen) throws NoPermissionException{
        if(token.isTimedOut())
            throw new NoPermissionException();

        User user = userAdapter.updateUserAuthen(userAdapter.getUserById(token.getUid()), authen);
        user.setUserAuthenticationInfo(null);

        return user;
    }

    public User updateProfilePhoto(Token token, PhotoContent photoContent) {
        if (token.isTimedOut()) {
            return null;
        }

        URI uri = photoAdapter.postPhoto(photoContent);
        PhotoItem photoItem = new PhotoItem(uri, 0, 0);

        return userAdapter.updateUserProfilePicture(userAdapter.getUserById(token.getUid()), photoItem);
    }

    public User updateUserInfo(Token token, User inputUser) {
        if(token.isTimedOut())
            throw new NoPermissionException();

        User user = userAdapter.getUserById(token.getUid());

        if(inputUser.getProfile() != null) {
            Profile profile = user.getProfile();
            Profile profileI = inputUser.getProfile();

            profile.setFirstname(profileI.getFirstname() != null ? profileI.getFirstname() : profile.getFirstname());
            profile.setLastname(profileI.getLastname() != null ? profileI.getLastname() : profile.getLastname());
        }

        User userO = userAdapter.updateUser(user);
        userO.setUserAuthenticationInfo(null);

        return userO;
    }
}
