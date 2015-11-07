package tk.lenkyun.foodbook.foodbook.Domain.Jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.tomcat.util.codec.binary.Base64;
import tk.lenkyun.foodbook.foodbook.Utils.Base64Utils;

import java.io.IOException;

/**
 * Created by lenkyun on 6/11/2558.
 */
public class PhotoContentDeserializer extends JsonDeserializer<byte[]> {
    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return Base64Utils.decode(p.getText());
    }
}
