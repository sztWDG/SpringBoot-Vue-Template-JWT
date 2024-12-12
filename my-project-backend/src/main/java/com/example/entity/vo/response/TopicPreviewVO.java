package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
//预览对象：帖子
public class TopicPreviewVO {
    int id;
    int type;
    String title;
    String text;
    List<String> images;
    Date time;
    Integer uid;
    String username;
    String avatar;
    int like;
    int collect;
}
