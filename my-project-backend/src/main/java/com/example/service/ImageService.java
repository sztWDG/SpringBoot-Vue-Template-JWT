package com.example.service;

import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ImageService {
    String uploadAvatar(MultipartFile file, int id) throws IOException;
    void fetchImageFromMinio(OutputStream stream, String image) throws  Exception;

}
