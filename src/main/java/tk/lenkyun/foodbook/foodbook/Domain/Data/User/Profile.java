package tk.lenkyun.foodbook.foodbook.Domain.Data.User;

import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodbookType;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoItem;

import java.net.URI;

/**
 * Created by lenkyun on 15/10/2558.
 */
public class Profile implements FoodbookType {
    private String firstname = null, lastname = null;
    private PhotoItem profilePicture = null;
    private PhotoItem coverPicture = null;

    public Profile() {
    }

    public Profile(String firstname, String lastname, PhotoItem photoItem) {
        this.firstname = firstname == null ? "" : firstname;
        this.lastname = lastname == null ? "" : lastname;
        this.profilePicture = photoItem == null ? new PhotoItem(URI.create(""), 0, 0) : photoItem;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public PhotoItem getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(PhotoItem profilePicture) {
        this.profilePicture = profilePicture;
    }

    public PhotoItem getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(PhotoItem coverPicture) {
        this.coverPicture = coverPicture;
    }
}
