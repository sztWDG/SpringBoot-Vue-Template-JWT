package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.DetailsSaveVO;

public interface AccountDetailsService extends IService<AccountDetails> {
    //查找用户信息（通过id）
    AccountDetails findeAccountDetailsById(int id);
    //保存信息
    boolean saveAccountDetails(int id, DetailsSaveVO vo);
}
