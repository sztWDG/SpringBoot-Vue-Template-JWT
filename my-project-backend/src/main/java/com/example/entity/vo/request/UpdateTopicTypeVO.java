package com.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateTopicTypeVO {
    @Min(value = 1, message = "类型ID必须大于0")
    int id;
    
    @NotEmpty(message = "类型名称不能为空")
    @Length(min = 1, max = 20, message = "类型名称长度必须在1-20之间")
    String name;
    
    @Length(max = 50, message = "类型描述长度不能超过50")
    String description;
    
    @NotEmpty(message = "类型颜色不能为空")
    String color;
} 