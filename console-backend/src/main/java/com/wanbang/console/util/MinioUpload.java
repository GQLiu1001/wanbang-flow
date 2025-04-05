package com.wanbang.console.util;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static com.google.common.io.Files.getFileExtension;

public class MinioUpload {
    final static String BUCKET_NAME = "wanbang";
    final  static String ACCESS_KEY = "hP3zArCnQzJvCGblFkb4";
    final static   String SECRET_KEY = "XevaAMNhoPjmIbIRKqncuTscmeITfxBWDP19MlrM";
    final  static String ENDPOINT = "http://localhost:9000";
    public static  String uploadFile(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InputStream inputStream = file.getInputStream();
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(ENDPOINT)
                        .credentials(ACCESS_KEY, SECRET_KEY)
                        .build();
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);
        String fileName = shortUuid + "." + fileExtension;
        // Upload known sized input stream.
        minioClient.putObject(
                PutObjectArgs.builder().bucket(BUCKET_NAME)
                        .object(fileName)
                        .stream(inputStream, file.getSize(), -1)
                        .build());

        return ENDPOINT + "/" + BUCKET_NAME + "/" + fileName;
    }

}
