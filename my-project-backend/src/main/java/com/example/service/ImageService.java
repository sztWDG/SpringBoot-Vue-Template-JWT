package com.example.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String uploadAvatar(MultipartFile file, int id) throws IOException;
}
