package com.example.entity.vo.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountVO {
    int id;
    String username;
    String email;
    String role;
    String avatar;
    LocalDateTime registerTime;
}
