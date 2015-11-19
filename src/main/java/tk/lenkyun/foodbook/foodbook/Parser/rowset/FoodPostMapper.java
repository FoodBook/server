package tk.lenkyun.foodbook.foodbook.Parser.rowset;

import org.springframework.jdbc.core.RowMapper;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPostDetail;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Location;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoItem;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Tag;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;

import java.lang.reflect.Array;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by lenkyun on 17/11/2558.
 */
public class FoodPostMapper implements RowMapper<FoodPost> {
    public static final String ID = "id",                     // Int
                            OWNER = "uid",                    // Int
                            CREATED_DATE = "create_date",     // Date
                            TAG = "tags",                     // String
                            LOCATION_NAME = "loc_name",       // String
                            LOCATION_LAT = "loc_lat",         // Float
                            LOCATION_LNG = "loc_lng",         // Float
                            PHOTOITEM = "photo_uri",          // String
                            CAPTION = "caption"               // String
    ;

    public static String[] mapList = new String[]{
        ID, OWNER, CREATED_DATE, LOCATION_NAME, LOCATION_LAT, LOCATION_LNG, TAG, PHOTOITEM, CAPTION
    };

    public static String generateInsertQuery(String db){
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(mapList));
        list.remove(0);

        return RowsetUtils.generateInsertQuery(db, list.toArray(new String[list.size()]));
    };

    public static Object[] generateInsertQueryValue(FoodPost foodPost){
        Object[] values = new Object[]{
                foodPost.getOwner().getId(),                                      // Owner
                foodPost.getPostDetail().getCreatedDate(),                        // Created Date
                foodPost.getPostDetail().getLocation().getName(),                 // Location name
                foodPost.getPostDetail().getLocation().getCoordinate().latitude,  // Latitude
                foodPost.getPostDetail().getLocation().getCoordinate().longitude, // Longitude
                "",                                                               // Tags
                foodPost.getPostDetail().getPhoto(0).getReferal().toString(),     // Photo Item
                foodPost.getPostDetail().getCaption()                             // Caption
        };

        return values;
    }

    @Override
    public FoodPost mapRow(ResultSet rs, int rowNum) throws SQLException {
        Location location = new Location(
                rs.getString(LOCATION_NAME),
                new Location.LatLng(
                    rs.getDouble(LOCATION_LAT),
                    rs.getDouble(LOCATION_LNG)
                )
        );

        FoodPostDetail detail = new FoodPostDetail(rs.getString(CAPTION), location);
        detail.setCreatedDate(rs.getTimestamp(CREATED_DATE));
        detail.addPhoto(new PhotoItem(URI.create(rs.getString(PHOTOITEM)), 0, 0));

        FoodPost foodPost = new FoodPost(
                String.valueOf(rs.getInt(ID)),
                detail,
                new User(
                        String.valueOf(rs.getInt(OWNER)),
                        null, null
                )
            );


        FoodPostDetail foodPostDetail = foodPost.getPostDetail();
        String tag = rs.getString(TAG);
        for(String str : tag.split(",")){
            foodPostDetail.pushTag(new Tag(str, null));
        }

        try{
            foodPost.setAverageRate(rs.getFloat("rate"));
        }catch (Exception ignored){}

        return foodPost;
    }
}
