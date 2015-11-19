package tk.lenkyun.foodbook.server.UserManagement.Utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import tk.lenkyun.foodbook.foodbook.Domain.Data.User.User;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by lenkyun on 5/11/2558.
 */
@Repository
public class TokenProvider {
    SecretKey secret;

    public TokenProvider(){
        try {
            KeyGenerator generator = KeyGenerator.getInstance("DES");
            secret = generator.generateKey();
        } catch (NoSuchAlgorithmException ignored) {}
    }

    public Token decodeToken(String token){
        byte[] bytes = Base64.decodeBase64(token);
        ObjectMapper mapper = new ObjectMapper();

        try {
            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.DECRYPT_MODE, secret);

            byte[] decrypted = desCipher.doFinal(bytes);
            return mapper.readValue(decrypted, Token.class);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | IOException ignored) {}

        return null;
    }

    @Deprecated
    public String getUserId(String token){
        Token tokenObj = decodeToken(token);

        return tokenObj != null ? tokenObj.getUid() : null;
    }

    public boolean isTokenTimeout(Token token){
        return (System.currentTimeMillis() / 1000.0) > (token != null ? token.getLimit() : 0);
    }

    @Deprecated
    public boolean isTokenTimeout(String token){
        Token tokenObj = decodeToken(token);
        return (System.currentTimeMillis() / 1000.0) > (tokenObj != null ? tokenObj.getLimit() : 0);
    }

    public String getToken(User user, long tokenLive){
        ObjectMapper mapper = new ObjectMapper();
        Token token = new Token();

        token.setUid(user.getId());
        token.setLimit((System.currentTimeMillis() / 1000) + tokenLive);

        UUID salt = UUID.randomUUID();
        token.setSalt(salt.toString());


        try {
            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            byte[] bytes = mapper.writeValueAsBytes(token);
            desCipher.init(Cipher.ENCRYPT_MODE, secret);

            byte[] encrypted = desCipher.doFinal(bytes);
            return Base64.encodeBase64String(encrypted);
        }
        catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException | JsonProcessingException ignored) {}

            return null;
    }
}
