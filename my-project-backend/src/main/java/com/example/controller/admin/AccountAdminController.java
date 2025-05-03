package com.example.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.RestBean;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountDetailsService;
import com.example.service.AccountPrivacyService;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user")
public class AccountAdminController {

    @Resource
    AccountService service;

    @Resource
    AccountDetailsService detailsService;

    @Resource
    AccountPrivacyService privacyService;

    @Resource
    StringRedisTemplate template;

    @Value("${spring.security.jwt.expire}")
    private int expire;

    @GetMapping("/list")
    public RestBean<JSONObject> accountList(int page, int size) {
        JSONObject object = new JSONObject();
        List<AccountVO> list = service.page(Page.of(page, size))
                .getRecords()
                .stream()
                .map(a -> a.asViewObject(AccountVO.class))
                .toList();
        object.put("total", service.count());
        object.put("list", list);
        return RestBean.success(object);
    }

    @GetMapping("/detail")
    public RestBean<JSONObject> accountDetail(int id) {
        JSONObject object = new JSONObject();
        object.put("detail", detailsService.findAccountDetailsById(id));
        object.put("privacy", privacyService.accountPrivacy(id));
        return RestBean.success(object);
    }

//    @PostMapping("/save")
//    public RestBean<Void> saveAccount(@RequestBody JSONObject object) {
//        int id = object.getInteger("id");
//        Account account = service.findAccountById(id);
//        Account save = object.toJavaObject(Account.class);
//        handleBanned(account, save);
//        BeanUtils.copyProperties(save, account, "password", "registerTime");
//        service.saveOrUpdate(account);
//        AccountDetails details = detailsService.findAccountDetailsById(id);
//        AccountDetails saveDetails = object.getJSONObject("detail").toJavaObject(AccountDetails.class);
//        BeanUtils.copyProperties(saveDetails, details);
//        detailsService.saveOrUpdate(details);
//        AccountPrivacy privacy = privacyService.accountPrivacy(id);
//        AccountPrivacy savePrivacy = object.getJSONObject("privacy").toJavaObject(AccountPrivacy.class);
//        BeanUtils.copyProperties(savePrivacy, privacy);
//        privacyService.saveOrUpdate(savePrivacy);
//        return RestBean.success();
//    }

//    private void handleBanned(Account old, Account current) {
//        String key = Const.BANNED_BLOCK + old.getId();
//        if(!old.isBanned() && current.isBanned()) {
//            template.opsForValue().set(key, "true", expire, TimeUnit.HOURS);
//        } else if(old.isBanned() && !current.isBanned()) {
//            template.delete(key);
//        }
//    }
}
