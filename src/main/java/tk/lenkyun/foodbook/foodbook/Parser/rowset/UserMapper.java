package tk.lenkyun.foodbook.foodbook.Parser.rowset;

import org.springframework.jdbc.core.RowMapper;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.UserAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoItem;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.Profile;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lenkyun on 18/11/2558.
 */
public class UserMapper implements RowMapper<User> {
    public static final String UID = "uid",                             // Int
                                USERNAME = "username",                  // String
                                PASSWORD = "password",                  // String
                                SOCIALID = "socialid",                  // String
                                FIRSTNAME = "firstname",                // String
                                LASTNAME = "lastname",                  // String
                                PROFILE_IMAGE = "profile_image";        // String

    public static String[] mapList = new String[]{
            UID, USERNAME, PASSWORD, SOCIALID, FIRSTNAME, LASTNAME, PROFILE_IMAGE
    };

    public static String generateInsertQuery(String db){
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(mapList));
        list.remove(0);

        return RowsetUtils.generateInsertQuery(db, list.toArray(new String[list.size()]));
    };

    public static Object[] generateInsertQueryValue(User user){
        Object[] values = new Object[]{
                user.getUsername(),
                user.getUserAuthenticationInfo().getInfo(),
                user.getSocialId(),
                user.getProfile().getFirstname(),
                user.getProfile().getLastname(),
                user.getProfile().getProfilePicture().getReferal().toString()
        };

        return values;
    }

    @Override
    public User mapRow(ResultSet rowSet, int rowNum) throws SQLException {
        URI imageURI = null;
        if(rowSet.getString(PROFILE_IMAGE) != null){
            try {
                imageURI = new URI(rowSet.getString(PROFILE_IMAGE));
            } catch (URISyntaxException ignored) {}
        }

        PhotoItem photoItem = new PhotoItem(imageURI, 0, 0);
        Profile profile = new Profile(rowSet.getString(FIRSTNAME),
                rowSet.getString(LASTNAME),
                photoItem);

        User user = new User(String.valueOf(rowSet.getLong(UID)), rowSet.getString(USERNAME), profile);
        user.setUserAuthenticationInfo(
                new UserAuthenticationInfo(user.getId(), rowSet.getString(PASSWORD))
        );

        return user;
    }
}
