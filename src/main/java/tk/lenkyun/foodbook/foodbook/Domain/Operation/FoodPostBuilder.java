package tk.lenkyun.foodbook.foodbook.Domain.Operation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.AuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPostDetail;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodbookType;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Location;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoContent;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;

import java.util.Date;
import java.util.List;

/**
 * Created by lenkyun on 19/10/2558.
 */
public class FoodPostBuilder implements FoodbookType {
    private Location location;
    private String caption;
    private PhotoBundle bundle;

    public FoodPostBuilder(){}

    public FoodPostBuilder(String caption, Location location, PhotoBundle photos) {
        this.location = location;
        this.caption = caption;
        this.bundle = photos;
    }

    public Location getLocation() {
        return location;
    }

    public FoodPostBuilder setLocation(Location location) {
        this.location = location;
        return this;
    }

    public String getCaption() {
        return caption;
    }

    public FoodPostBuilder setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public PhotoBundle getBundle() {
        return bundle;
    }

    public FoodPostBuilder setBundle(Iterable<PhotoContent> bundle) {
        this.bundle = new PhotoBundle();
        for(PhotoContent photoContent : bundle){
            this.bundle.put(photoContent);
        }

        return this;
    }

    @JsonIgnore
    public FoodPost build(){
        Location location = getLocation();

        FoodPostDetail detail = new FoodPostDetail(getCaption(), location);
        FoodPost foodPost = new FoodPost(
                null,
                detail,
                null
        );

        foodPost.getPostDetail().setCreatedDate(new Date());

        return foodPost;
    }
}
