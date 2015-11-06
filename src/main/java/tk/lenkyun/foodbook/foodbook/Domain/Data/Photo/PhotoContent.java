package tk.lenkyun.foodbook.foodbook.Domain.Data.Photo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Content;
import tk.lenkyun.foodbook.foodbook.Domain.Data.FoodbookType;
import tk.lenkyun.foodbook.foodbook.Domain.Jackson.PhotoContentDeserializer;
import tk.lenkyun.foodbook.foodbook.Domain.Jackson.PhotoContentSerializer;

public class PhotoContent implements FoodbookType, Content<byte[]> {
    @JsonSerialize(using = PhotoContentSerializer.class)
    @JsonDeserialize(using = PhotoContentDeserializer.class)
    protected byte[] content;

    public PhotoContent(byte[] content) {
        this.content = content;
    }

    @Override
    public byte[] getContent() {
        return this.content;
    }

    @Override
    public void setContent(byte[] content) {
        throw new UnsupportedOperationException("No support for PhotoContent to set its content.");
    }
}
