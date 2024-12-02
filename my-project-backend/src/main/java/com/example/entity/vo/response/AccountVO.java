package com.example.entity.vo.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class AccountVO {
    String username;
    String email;
    String role;
    LocalDateTime registerTime;
}
