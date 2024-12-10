package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;

@Data
@TableName("db_topic_type")
//实现BaseData
public class TopicType implements BaseData {
    int id;
    String name;
    String description;

}
