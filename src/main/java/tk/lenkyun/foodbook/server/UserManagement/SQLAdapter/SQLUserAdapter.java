package tk.lenkyun.foodbook.server.UserManagement.SQLAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.UserAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoItem;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.Profile;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.Domain.Operation.RegistrationBuilder;
import tk.lenkyun.foodbook.foodbook.Parser.rowset.RowsetParser;
import tk.lenkyun.foodbook.foodbook.Parser.rowset.UserMapper;
import tk.lenkyun.foodbook.foodbook.Parser.rowset.UserParser;
import tk.lenkyun.foodbook.server.UserManagement.Adapter.UserAdapter;
import tk.lenkyun.foodbook.server.UserManagement.Exception.DuplicateUserException;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by lenkyun on 5/11/2558.
 */
@Repository
@PropertySource("classpath:database.properties")
public class SQLUserAdapter extends JdbcTemplate implements UserAdapter {
    @Autowired
    Environment env;

    @Autowired
    public SQLUserAdapter(DataSource mainDataSource){
        super();
        this.setDataSource(mainDataSource);
    }

    @Override
    public User getUserById(String id) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM ")
                .append(env.getProperty("database.table.user"))
                .append(" WHERE " + UserParser.UID + " = ?");

        Object[] parameters = {Long.parseLong(id)};
        SqlRowSet result = queryForRowSet(query.toString(), parameters);

        if(result.next()){
            return new UserParser().from(result);
        }
        return null;
    }

    @Override
    public User getUserByUsername(String name) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM ")
                .append(env.getProperty("database.table.user"))
                .append(" WHERE " + UserParser.USERNAME + " = ?");

        Object[] parameters = {name};
        SqlRowSet result = queryForRowSet(query.toString(), parameters);

        if(result.next()){
            return new UserParser().from(result);
        }
        return null;
    }

    @Override
    public User getUserByFacebookId(String fid) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM ")
                .append(env.getProperty("database.table.user"))
                .append(" WHERE " + UserParser.SOCIALID + " = ?");

        Object[] parameters = {fid};
        SqlRowSet result = queryForRowSet(query.toString(), parameters);

        if(result.next()){
            return new UserParser().from(result);
        }

        return null;
    }

    @Override
    public User getUser(UserAuthenticationInfo authenticationInfo) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM ")
                .append(env.getProperty("database.table.user"))
                .append(" WHERE " + UserParser.USERNAME + " = ?")
                .append(" AND " + UserParser.PASSWORD + " = ? ");

        Object[] parameters = {authenticationInfo.getId(), authenticationInfo.getInfo()};
        SqlRowSet result = queryForRowSet(query.toString(), parameters);

        if(result.next()){
            return new UserParser().from(result);
        }

        return null;
    }

    @Override
    public User updateUser(User user) {
        if(getUserById(user.getId()) == null)
            return null;

        StringBuilder query = new StringBuilder();
        Map<String, Object> list = new UserParser().parse(user);

        query.append("UPDATE ")
                .append(env.getProperty("database.table.user"))
                .append(" SET " + RowsetParser.getNameToValue(list))
                .append(" WHERE " + UserParser.UID + " = ? ");

        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.addAll(Arrays.asList(RowsetParser.getValueList(list)));
        parameters.add(Long.parseLong(user.getId()));

        SqlRowSet result = queryForRowSet(query.toString(), parameters.toArray());

        return getUserById(user.getId());
    }

    @Override
    public User createUser(RegistrationBuilder registrationBuilder, PhotoItem profile_picture, PhotoItem profile_cover) {
        if(getUserByUsername(registrationBuilder.getUsername()) != null)
            throw new DuplicateUserException("");

        StringBuilder query = new StringBuilder();
        User user = new User(null, registrationBuilder.getUsername(),
                new Profile(registrationBuilder.getFirstname(), registrationBuilder.getLastname(),
                    profile_picture));
        Map<String, Object> list = new UserParser().parse(user);

        query.append("INSERT INTO ")
                .append(env.getProperty("database.table.user"))
                .append(" (" + RowsetParser.getNameList(list) + ")")
                .append(" VALUES (" + RowsetParser.getValueBlanker(list) + ")");

        SqlRowSet result = queryForRowSet(query.toString(), RowsetParser.getValueList(list));

        return getUserByUsername(registrationBuilder.getUsername());
    }

    @Override
    public void removeUser(String id) {
        StringBuilder query = new StringBuilder();

        query.append("DELETE FROM ")
                .append(env.getProperty("database.table.user"))
                .append(" WHERE " + UserParser.UID + " = ?");

        Object[] parameters = {Long.parseLong(id)};
        SqlRowSet result = queryForRowSet(query.toString(), parameters);
    }

    @Override
    public Boolean getIsFollowing(User user, User following) {
        String querer = "select count(*) from %s where %s = ? AND %s = ?";
        querer = String.format(querer,
                env.getProperty("database.table.follow"),
                "uid", "follow_uid"
                );
        Object[] value = new Object[]{
                user.getId(), following.getId()
        };

        return queryForObject(querer, value, Integer.class) > 0;
    }

    @Override
    public Boolean setFollowing(User user, User following) {
        String querer = "insert ignore into %s (%s, %s) values (?, ?)";

        querer = String.format(querer,
                env.getProperty("database.table.follow"),
                "uid", "follow_uid"
        );

        Object[] values = new Object[]{
                user.getId(),
                following.getId()
        };

        Integer id = update(
                querer,
                values
        );

        return true;
    }

    @Override
    public Boolean unsetFollowing(User user, User following) {
        String querer = "delete from %s where %s = ? and %s = ?";

        querer = String.format(querer,
                env.getProperty("database.table.follow"),
                "uid", "follow_uid"
        );

        Object[] values = new Object[]{
                user.getId(),
                following.getId()
        };

        Integer id = update(
                querer,
                values
        );

        return false;
    }

    @Override
    public Collection<User> getAllFollowingUser(User user) {
        String querer = "select %s.* from %s, %s where %s.%s = %s.%s and %s.%s = ?";
        querer = String.format(querer,
                env.getProperty("database.table.user"),
                env.getProperty("database.table.follow"),
                env.getProperty("database.table.user"),
                env.getProperty("database.table.user"), "uid",
                env.getProperty("database.table.follow"), "follow_uid",
                env.getProperty("database.table.follow"), "uid"
            );

        Object[] values = new Object[]{
                user.getId()
        };

        return query(querer, new UserMapper(), user.getId());
    }

    @Override
    public User updateUserAuthen(User user, UserAuthenticationInfo authen) {
        String querer = String.format("update %s SET %s = ? WHERE %s = ?",
                env.getProperty("database.table.user"),
                UserMapper.PASSWORD,
                UserMapper.UID
        );
        Object[] value = new Object[]{
                authen.getInfo(),
                user.getId()
        };

        update(querer, value);
        return getUserById(user.getId());
    }
}
