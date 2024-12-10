package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ImageService;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ObjectController {

    @Resource
    ImageService imageService;

    @GetMapping("/images/**")
    //优化：相应图片，设置特殊响应头，为一些比较固定的参数设置一个缓存周期，避免频繁访问后端，优化程序
    public void imageFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.fetchImage(request, response);
        response.setHeader("Content-Type", "image/jpg");

    }

    //优化：相应图片，设置特殊响应头，为一些比较固定的参数设置一个缓存周期，避免频繁访问后端，优化程序
    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取用户要请求的图片，去掉前七个：/images，剩下的就是minio里面存储的路径
        String imagePath = request.getServletPath().substring(7);
        ServletOutputStream stream = response.getOutputStream();

        if (imagePath.length() <= 13){
            //UUID自动生成绝对大于13，小于的直接扔掉
            stream.println(RestBean.failure(404, "Not Found").toString());
        }else {
            try {
                imageService.fetchImageFromMinio(stream, imagePath);
                response.setHeader("Cache-Control", "max-age=2592000");

            }catch (ErrorResponseException e){//Minio的错误检测
                if (e.response().code() == 404){
                    response.setStatus(404);
                    stream.println(RestBean.failure(404, "Not Found").toString());
                } else {
                    log.error("从Minio获取图片出现异常："+e.getMessage(), e);
                }
            }
        }
    }
}
