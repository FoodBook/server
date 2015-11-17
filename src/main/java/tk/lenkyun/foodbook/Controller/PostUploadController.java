package tk.lenkyun.foodbook.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Authentication.SessionAuthenticationInfo;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Location;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoContent;
import tk.lenkyun.foodbook.foodbook.Domain.Operation.FoodPostBuilder;
import tk.lenkyun.foodbook.foodbook.Domain.Operation.PhotoBundle;
import tk.lenkyun.foodbook.foodbook.ResponseWrapper;
import tk.lenkyun.foodbook.server.PhotoManagement.Adapter.PhotoAdapter;
import tk.lenkyun.foodbook.server.PostManagement.PostFeed;
import tk.lenkyun.foodbook.server.UserManagement.Utils.Token;
import tk.lenkyun.foodbook.server.UserManagement.Utils.TokenProvider;

import java.net.URI;

/**
 * Created by lenkyun on 17/11/2558.
 */
@RestController
public class PostUploadController {
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private PhotoAdapter photoAdapter;

    @Autowired
    private PostFeed postFeed;

    @RequestMapping(method = RequestMethod.GET, value = "/post/test/builder")
    public @ResponseBody
    FoodPostBuilder foodPostBuilder(){
        FoodPostBuilder builder = new FoodPostBuilder("test caption", null, null);

        Location location = new Location("Tester", new Location.LatLng(13.5, 100.0));

        PhotoContent content = new PhotoContent(new byte[]{0x30, 0x32, 0x34, 0x36, 0x38});
        PhotoBundle bundle = new PhotoBundle();
        bundle.put(content);

        builder.setBundle(bundle);
        builder.setLocation(location);

        return builder;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/post")
    public @ResponseBody
    ResponseWrapper<FoodPost> foodPostCreate(
            @RequestParam(value = "token") String token,
            @RequestBody FoodPostBuilder builder){

        Token tokenObj = tokenProvider.decodeToken(token);

        ResponseWrapper<FoodPost> responseWrapper = new ResponseWrapper<>();

        if(tokenObj != null){
            responseWrapper.setError(0);
            responseWrapper.setResult(postFeed.publistPost(
                tokenObj, builder
            ));
        }else{
            responseWrapper.setError(1);
            responseWrapper.setDetail("Token is invalid.");
        }
        return responseWrapper;
    }
}
