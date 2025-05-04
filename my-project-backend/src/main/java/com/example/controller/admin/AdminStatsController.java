package com.example.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.RestBean;
import com.example.entity.dto.Topic;
import com.example.mapper.AccountMapper;
import com.example.mapper.TopicMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;

/**
 * 管理员统计数据接口
 */
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Resource
    AccountMapper accountMapper;

    @Resource
    TopicMapper topicMapper;

    /**
     * 获取统计数据，包括用户数、帖子数和今日发帖数
     * @return 统计信息的JSON
     */
    @GetMapping
    public RestBean<JSONObject> getStats() {
        JSONObject data = new JSONObject();
        
        // 统计用户总数
        int userCount = Math.toIntExact(accountMapper.selectCount(null));
        data.put("userCount", userCount);
        
        // 统计帖子总数
        int topicCount = Math.toIntExact(topicMapper.selectCount(null));
        data.put("topicCount", topicCount);
        
        // 统计今日发帖数
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        
        QueryWrapper<Topic> wrapper = new QueryWrapper<>();
        wrapper.ge("time", today);
        int todayTopicCount = Math.toIntExact(topicMapper.selectCount(wrapper));
        data.put("todayTopicCount", todayTopicCount);
        
        return RestBean.success(data);
    }
} 