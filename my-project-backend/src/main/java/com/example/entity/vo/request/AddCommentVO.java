package com.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AddCommentVO {
    @Min(1)
    int tid;
    String content;
    //额外增加一个字段，引用：指向其他评论的ID
    @Min(-1)
    int quote;

}
