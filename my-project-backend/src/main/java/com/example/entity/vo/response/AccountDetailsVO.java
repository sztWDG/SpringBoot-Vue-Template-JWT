package com.example.entity.vo.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class AccountDetailsVO {
    int gender;
    String phone;
    String qq;
    String wx;


    String description;
}
