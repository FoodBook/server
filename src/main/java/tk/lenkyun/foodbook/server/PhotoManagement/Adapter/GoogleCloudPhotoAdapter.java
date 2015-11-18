package tk.lenkyun.foodbook.server.PhotoManagement.Adapter;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;
import org.springframework.stereotype.Repository;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Photo.PhotoContent;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by lenkyun on 5/11/2558.
 */
@Repository
public class GoogleCloudPhotoAdapter implements PhotoAdapter {
    private Storage storageService = null;
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private Storage getService() throws IOException, GeneralSecurityException {
        if (null == storageService) {
            GoogleCredential credential = GoogleCredential.getApplicationDefault();
            // Depending on the environment that provides the default credentials (e.g. Compute Engine,
            // App Engine), the credentials may require us to specify the scopes we need explicitly.
            // Check for this case, and inject the Cloud Storage scope if required.
            if (credential.createScopedRequired()) {
                credential = credential.createScoped(StorageScopes.all());
            }
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            storageService = new Storage.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName("Foodbook").build();
        }
        return storageService;
    }

    private Bucket getBucket() throws IOException, GeneralSecurityException {
        Storage client = getService();

        Storage.Buckets.Get bucketRequest = client.buckets().get("foodbook");
        // Fetch the full set of the bucket's properties (e.g. include the ACLs in the response)
        bucketRequest.setProjection("full");
        return bucketRequest.execute();
    }

    @Override
    public URI postPhoto(PhotoContent photoContent) {
        UUID uuid = UUID.randomUUID();
        try {
            Storage storage = getService();

            do {
                try {
                    Storage.Objects.Get get = storage.objects().get("foodbook", uuid.toString() + ".jpg");
                    get.execute();
                }catch (GoogleJsonResponseException ignored){
                    break;
                }
            }while(true);

            InputStreamContent inputStream = new InputStreamContent(
                    "image/jpg",
                    new ByteArrayInputStream(photoContent.getContent()));


            StorageObject storageObject = new StorageObject()
                    .setName(uuid.toString() + ".jpg")
                    .setAcl(Arrays.asList(
                            new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

            Storage.Objects.Insert insert = storage.objects().insert(
                    "foodbook", storageObject, inputStream);

            StorageObject result = insert.execute();
            try {
                return new URI(String.format("http://storage.googleapis.com/%s/%s", result.getBucket(), result.getName()));
            } catch (URISyntaxException e) {
                return null;
            }
        } catch (IOException | GeneralSecurityException ignored) {}

        return null;
    }

    @Override
    public void getPhoto(URI uri) {
        return;
    }

    @Override
    public void removePhoto(URI uri) {
        return;
    }
}
