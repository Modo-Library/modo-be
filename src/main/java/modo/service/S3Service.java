package modo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Service
@Log4j2
@PropertySource("classpath:application.properties")
public class S3Service {

    @Value("${aws.s3.bucket.name}")
    private String S3_BUCKET_NAME;

//      bucketName - The name of the Amazon S3 bucket.
//      keyName - A key name that represents a text file.

    public String createPreUrl(String keyName) throws IOException {

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.AP_NORTHEAST_2;

        S3Presigner presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(S3_BUCKET_NAME)
                .key(keyName)
                .contentType("image/webp")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        String myURL = presignedRequest.url().toString();

        log.info("Presigned URL to upload a file to : {}", myURL);
        log.info("Which HTTP method needs to be used when uploading a file: {}", presignedRequest.httpRequest().method());

        presigner.close();

        return myURL;
    }
}
