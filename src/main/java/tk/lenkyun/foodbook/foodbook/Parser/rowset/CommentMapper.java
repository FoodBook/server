package tk.lenkyun.foodbook.foodbook.Parser.rowset;

import org.springframework.jdbc.core.RowMapper;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Comment;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lenkyun on 17/11/2558.
 */
public class CommentMapper implements RowMapper<Comment> {
    public static final String ID = "id",                         // Int
                                ASSOC = "pid",                    // Int
                                OWNER = "uid",                    // Int
                                MESSAGE = "message",              // String
                                CREATED_DATE = "created_date";    // DateTime

    public static String[] mapList = new String[]{
            ID, ASSOC, OWNER, MESSAGE, CREATED_DATE
    };

    public static String generateInsertQuery(String db){
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(mapList));
        list.remove(0);

        return RowsetUtils.generateInsertQuery(db, list.toArray(new String[list.size()]));
    };

    public static Object[] generateInsertQueryValue(Comment comment){
        Object[] values = new Object[]{
                comment.getId(),             // Id
                comment.getAssoc(),          // Assoc
                comment.getUser().getId(),   // Owner
                comment.getMessage(),        // Message
                comment.getCreatedDate()     // Created Date
        };

        return values;
    }

    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();

        comment.setId(String.valueOf(rs.getInt(ID)));
        comment.setCreatedDate(rs.getDate(CREATED_DATE));
        comment.setMessage(rs.getString(MESSAGE));
        comment.setAssoc(new FoodPost(
                String.valueOf(rs.getInt(ASSOC)), null, null));
        comment.setUser(new User(String.valueOf(rs.getInt(OWNER)), null, null));

        return comment;
    }
}
