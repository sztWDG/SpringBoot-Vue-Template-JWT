package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.StoreImage;
import com.example.mapper.AccountMapper;
import com.example.mapper.ImageStoreMapper;
import com.example.service.ImageService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ImageImpl extends ServiceImpl<ImageStoreMapper, StoreImage> implements ImageService {

    @Resource
    MinioClient minioClient;

    @Resource
    AccountMapper accountMapper;

    @Resource
    FlowUtils flowUtils;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

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
    public String uploadImage(MultipartFile file, int id) throws IOException {
        //限制用户上传图片的规格和频率
        String key = Const.FORUM_IMAGE_COUNTER + id;
        if (!flowUtils.limitPeriodCounterCheck(key, 20, 3600)){
            return null;
        }
        String imageName = UUID.randomUUID().toString().replace("-","");
        Date date = new Date();
        imageName = "/cache/" + format.format(date) + "/" +imageName;

        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("forum")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();

        try {
            minioClient.putObject(args);
            if (this.save(new StoreImage(id,imageName,date))){
                return imageName;
            }else {
                return null;
            }
        }catch (Exception e){
            log.error("图片上传出现问题："+e.getMessage(), e);
            return null;
        }

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
            //删除掉用户的历史头像
            String avatar = accountMapper.selectById(id).getAvatar();
            this.deleteOldAvatar(avatar);
            //没问题，表示图片已经成功保存，同步图片地址到数据库中
            if (accountMapper.update(null, Wrappers.<Account>update()
                    .eq("id", Optional.of(id)).set("avatar",imageName)) > 0) {
                return imageName;
            }else {
                return null;
            }
        }catch (Exception e){
            log.error("图片上传出现问题："+e.getMessage(), e);
            return null;
        }

    }

    //删除掉原有头像
    private void deleteOldAvatar(String avatar) throws Exception{
        if (avatar == null || avatar.isEmpty()) return;
        RemoveObjectArgs remove = RemoveObjectArgs.builder()
                .bucket("forum")
                .object(avatar)
                .build();

        minioClient.removeObject(remove);
    }


}
