package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.entity.dto.Account;
import com.example.mapper.AccountMapper;
import com.example.service.ImageService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
@Service
public class ImageImpl implements ImageService {

    @Resource
    MinioClient minioClient;

    @Resource
    AccountMapper accountMapper;

    @Override
    public void fetchImageFromMinio(OutputStream stream, String image) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("forum")
                .object(image)
                .build();
        GetObjectResponse response = minioClient.getObject(args);
        IOUtils.copy(response,stream);
    }

    @Override
    public String uploadAvatar(MultipartFile file, int id) throws IOException{
        //replace去掉UUID自带的-，美观
        String imageName = UUID.randomUUID().toString().replace("-","");
        imageName = "/avatar/"+imageName;

        //MinIO， partSize：-1 让其自动分配分块大小
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("forum")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();

        try {
            //传入参数
            minioClient.putObject(args);
            //没问题，表示图片已经成功保存，同步图片地址到数据库中
            if (accountMapper.update(null, Wrappers.<Account>update()
                    .eq("id",id).set("avatar",imageName)) > 0) {
                return imageName;
            }else {
                return null;
            }
        }catch (Exception e){
            log.error("图片上传出现问题："+e.getMessage(), e);
            return null;
        }

    }
}
