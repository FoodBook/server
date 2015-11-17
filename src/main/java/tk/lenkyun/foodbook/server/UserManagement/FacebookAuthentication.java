package tk.lenkyun.foodbook.server.UserManagement;

import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.SessionAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.FacebookAdapter;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.Exception.InvalidTokenException;

/**
 * Created by lenkyun on 5/11/2558.
 */
public class FacebookAuthentication {
    private UserManager userManager;
    private FacebookAdapter facebookAdapter;
    private UserAdapter userAdapter;

    public FacebookAuthentication(UserManager userManager, FacebookAdapter facebookAdapter, UserAdapter userAdapter){
        this.userManager = userManager;
        this.facebookAdapter = facebookAdapter;
        this.userAdapter = userAdapter;
    }

    public SessionAuthenticationInfo login(String accessToken) throws InvalidTokenException {
        String fid = facebookAdapter.getFacebookIdFromToken(accessToken);
        if(fid == null)
            throw new InvalidTokenException("");

        if(fid.length() == 0)
            throw new InvalidTokenException("");

        User user = userAdapter.getUserByFacebookId(fid);
        if(user == null)
            return null;

        return userManager.login(user);
    }
}
