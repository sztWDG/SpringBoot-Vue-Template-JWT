package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.request.PrivacySaveVO;
import com.example.mapper.AccountPrivacyMapper;
import com.example.service.AccountPrivacyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountPrivacyImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {

    @Override
    @Transactional
    public void savePrivacy(int id, PrivacySaveVO vo) {
        //QUESTION: Optional???
        AccountPrivacy privacy = Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
        boolean status = vo.isStatus();

        switch (vo.getType()){
            case "phone" -> privacy.setPhone(status);
            case "email" -> privacy.setEmail(status);
            case "gender" -> privacy.setGender(status);
            case "wx" -> privacy.setWx(status);
            case "qq" -> privacy.setQq(status);
        }
        this.saveOrUpdate(privacy);
    }

    public AccountPrivacy accountPrivacy(int id) {
        //return this.getById(id);
        //数据库没有东西时，使用默认数据
        return Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
    }
}
