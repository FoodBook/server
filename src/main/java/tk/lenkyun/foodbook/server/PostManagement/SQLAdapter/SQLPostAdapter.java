package tk.lenkyun.foodbook.server.PostManagement.SQLAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Comment;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Tag;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;
import tk.lenkyun.foodbook.foodbook.Parser.rowset.CommentMapper;
import tk.lenkyun.foodbook.foodbook.Parser.rowset.FoodPostMapper;
import tk.lenkyun.foodbook.server.PostManagement.Adapter.PostAdapter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by lenkyun on 17/11/2558.
 */
@Repository
@PropertySource("classpath:database.properties")
public class SQLPostAdapter extends JdbcTemplate implements PostAdapter {
    @Autowired
    Environment env;

    @Autowired
    public SQLPostAdapter(DataSource mainDataSource){
        super();
        this.setDataSource(mainDataSource);
    }

    @Override
    public synchronized FoodPost insertPost(FoodPost foodPost) {
        String querer = FoodPostMapper.generateInsertQuery(env.getProperty("database.table.post"));
        Object[] values = FoodPostMapper.generateInsertQueryValue(foodPost);
        Integer id = update(
                querer,
                values
        );

        Integer i = queryForObject("select last_insert_id()", new Object[]{}, Integer.class);
        foodPost.setId(String.valueOf(i));

        return foodPost;
    }

    @Override
    public FoodPost getPost(String id) {
        try {
            return queryForObject(String.format("select * from %s where %s = ?",
                            env.getProperty("database.table.post"),
                            FoodPostMapper.ID),
                    new Object[]{id}, new FoodPostMapper());
        }catch(EmptyResultDataAccessException ignored){
            return null;
        }
    }

    @Override
    public Collection<FoodPost> getPost(User user) {
        try {
            return query(String.format("select * from %s where %s = ?",
                    env.getProperty("database.table.post"),
                    FoodPostMapper.OWNER
            ), new FoodPostMapper(), Integer.parseInt(user.getId()));
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public FoodPost updatePost(FoodPost foodPost) {
        String querer = String.format("update %s SET %s = ?, %s = ? WHERE %s = ?",
                env.getProperty("database.table.post"),
                FoodPostMapper.CAPTION,
                FoodPostMapper.TAG,
                FoodPostMapper.ID
        );

        ArrayList<String> strs = new ArrayList<>();
        for(Tag tag : foodPost.getPostDetail().getTags()){
            if(tag != null && tag.getTagName() != null) {
                strs.add(tag.getTagName().trim());
            }
        }

        Object[] value = new Object[]{
                foodPost.getPostDetail().getCaption(),
                String.join(",", strs),
                foodPost.getId()
        };

        update(querer, value);
        return foodPost;
    }

    @Override
    public void removePost(FoodPost foodPost) {
        update(
                String.format("delete from %s where %s = ?",
                        env.getProperty("database.table.post"),
                        FoodPostMapper.ID
                ), foodPost.getId()
        );
    }

    @Override
    public Comment getComment(String id) {
        try {
            return queryForObject(String.format("select * from %s where %s = ?",
                            env.getProperty("database.table.comment"),
                            CommentMapper.ASSOC),
                    new Object[]{id}, new CommentMapper());
        }catch(EmptyResultDataAccessException ignored){
            return null;
        }
    }

    @Override
    public void removeComment(Comment comment) {
        update(
                String.format("delete from %s where %s = ?",
                        env.getProperty("database.table.comment"),
                        CommentMapper.ID
                ), comment.getId()
        );
    }

    @Override
    public Comment updateComment(Comment comment) {
        String querer = String.format("update %s SET %s = ? WHERE %s = ?",
                    env.getProperty("database.table.comment"),
                    CommentMapper.MESSAGE,
                    CommentMapper.ID
                );
        Object[] value = new Object[]{
                comment.getMessage(),
                comment.getId()
        };

        update(querer, value);
        return comment;
    }

    @Override
    public Comment createComment(Comment comment) {
        comment.setCreatedDate(new Date());

        String querer = CommentMapper.generateInsertQuery(env.getProperty("database.table.comment"));
        Object[] values = CommentMapper.generateInsertQueryValue(comment);
        Integer id = update(
                querer,
                values
        );

        Integer i = queryForObject("select last_insert_id()", new Object[]{}, Integer.class);
        comment.setId(String.valueOf(i));

        return comment;
    }

    @Override
    public Float getRate(FoodPost foodPost) {
        try {
            Float i = queryForObject(String.format("select AVG(%s) from %s where %s = ?",
                            "score",
                            env.getProperty("database.table.rate"),
                            "pid"),
                    new Object[]{foodPost.getId()}, Float.class);

            return i == null ? 0 : i;
        }catch(EmptyResultDataAccessException ignored){
            return 0F;
        }
    }

    @Override
    public Float getRate(FoodPost foodPost, User user) {
        try {
            Float i = queryForObject(String.format("select %s from %s where %s = ? and %s = ?",
                            "score",
                            env.getProperty("database.table.rate"),
                            "pid", "uid"),
                    new Object[]{foodPost.getId(), user.getId()}, Float.class);

            return i == null ? 0 : i;
        }catch(EmptyResultDataAccessException ignored){
            return 0F;
        }
    }

    @Override
    public Float setRate(FoodPost foodPost, User user, int rate) {
        String querer = "insert into %s (%s, %s, %s) values (?, ?, ?) on duplicate key update " +
                "%s = ?";
        querer = String.format(querer,
                env.getProperty("database.table.rate"),
                "pid", "uid", "score",
                "score"
                );

        Object[] values = new Object[]{
                foodPost.getId(),
                user.getId(),
                rate, rate
        };

        Integer id = update(
                querer,
                values
        );

        return getRate(foodPost);
    }

    @Override
    public Collection<Comment> getComments(FoodPost post) {
        try {
            return query(String.format("select * from %s where %s = ?",
                            env.getProperty("database.table.comment"),
                            CommentMapper.ASSOC
                            ),
                    new Object[]{post.getId()}, new CommentMapper());
        }catch(EmptyResultDataAccessException ignored){
            return null;
        }
    }
}
