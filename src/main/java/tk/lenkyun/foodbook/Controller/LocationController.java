package tk.lenkyun.foodbook.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodPost;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Location;
import tk.lenkyun.foodbook.foodbook.ResponseWrapper;
import tk.lenkyun.foodbook.server.PostManagement.PostFeed;
import tk.lenkyun.foodbook.server.UserManagement.Utils.Token;
import tk.lenkyun.foodbook.server.UserManagement.Utils.TokenProvider;

import java.util.Collection;

/**
 * Created by lenkyun on 19/11/2558.
 */
@RestController
public class LocationController {
    @Autowired
    PostFeed postFeed;

    @Autowired
    TokenProvider tokenProvider;

    @RequestMapping(method = RequestMethod.GET, value = "/search/{keyword}")
    public @ResponseBody
    ResponseWrapper<Collection<FoodPost>> getPostByKeyword(@PathVariable(value = "keyword") String keyword, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<Collection<FoodPost>> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        responseWrapper.setResult(postFeed.getPostByKeyword(token, keyword));
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{lat}/{lon}")
    public @ResponseBody
    ResponseWrapper<Collection<FoodPost>> getPostNear(@PathVariable(value = "lat") float lat, @PathVariable(value = "lon") float lon, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<Collection<FoodPost>> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        responseWrapper.setResult(postFeed.getPostNearLocation(token, new Location.LatLng(lat, lon)));
        return responseWrapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/location/near/{lat}/{lon}")
    public @ResponseBody
    ResponseWrapper<Collection<Location>> getLocationNear(@PathVariable(value = "lat") float lat, @PathVariable(value = "lon") float lon, @RequestParam(value = "token") String tokenString){
        ResponseWrapper<Collection<Location>> responseWrapper = new ResponseWrapper<>();
        Token token = tokenProvider.decodeToken(tokenString);

        if(token == null){
            responseWrapper.setError(1);
            responseWrapper.setDetail("Invalid token.");
            return responseWrapper;
        }

        responseWrapper.setResult(postFeed.getLocation(token, new Location.LatLng(lat, lon)));
        return responseWrapper;
    }

}
