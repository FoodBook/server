package tk.lenkyun.foodbook.server.UserManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.Utils.TokenProvider;

/**
 * Created by lenkyun on 6/11/2558.
 */
@Service
public class SessionManager {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserAdapter userAdapter;

    public boolean isTokenValid(String token){
        if(!tokenProvider.isTokenTimeout(token))
            return false;

        String id = tokenProvider.getUserId(token);
        User user = userAdapter.getUserById(id);
        if(user.getId().equals(id)){
            return true;
        }

        return false;
    }
}
