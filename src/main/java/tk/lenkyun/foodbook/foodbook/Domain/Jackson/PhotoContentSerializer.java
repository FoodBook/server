package tk.lenkyun.foodbook.foodbook.Domain.Jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by lenkyun on 6/11/2558.
 */
public class PhotoContentSerializer extends JsonSerializer<byte[]> {
    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        gen.writeString(Base64.encodeBase64String(value));
    }
}
