package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.*;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.response.TopicDetailVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.entity.vo.response.TopicTopVO;
import com.example.mapper.*;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Resource
    TopicTypeMapper mapper;

    @Resource
    FlowUtils flowUtils;

    @Resource
    CacheUtils cacheUtils;

    @Resource
    AccountMapper accountMapper;

    @Resource
    AccountDetailsMapper accountDetailsMapper;

    @Resource
    AccountPrivacyMapper accountPrivacyMapper;

    //???判断所有合法typeID
    private Set<Integer> types = null;

    @PostConstruct
    private void initTypes() {
        types = this.listTypes()
                .stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());

    }


    @Override
    public List<TopicType> listTypes() {
        return mapper.selectList(null);
    }

    @Override
    public String createTopic(int uid, TopicCreateVO vo) {

        if (!textLimitCheck(vo.getContent()))
            return "文章内容字数超出限制，发文失败！";

        if (!types.contains(vo.getType()))
            return "文章类型非法！";

        //限制用户发文频率
        String key = Const.FORUM_TOPIC_CREATE_COUNTER + uid;
        if (!flowUtils.limitPeriodCounterCheck(key, 3, 3600))
            return "发文频繁，请稍后再试！";

        //没问题，对发文进行保存
        Topic topic = new Topic();
        BeanUtils.copyProperties(vo, topic);
        topic.setContent(vo.getContent().toJSONString());
        topic.setUid(uid);
        topic.setTime(new Date());

        if (this.save(topic)) {
            //保存之后全部清除
            cacheUtils.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE+"*");
            return null;
        } else {
            return "内部错误，请联系管理员！";
        }

    }

    @Override
    public List<TopicPreviewVO> listTopicByPage(int pageNumber, int type) {
        //造一个key
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + pageNumber + ":" + type;
        List<TopicPreviewVO> list = cacheUtils.takeListFromCache(key,TopicPreviewVO.class);
        //如果list不为空，则直接返回，为空则重新构造一次
        if (list != null) return list;
        //接收
        //List<Topic> topics;
        Page<Topic> page = Page.of(pageNumber, 10);
        //控制每页的帖子数量
        if (type == 0)
            //topics = baseMapper.topicList(page * 10);
            baseMapper.selectPage(page, Wrappers.<Topic>query().orderByDesc("time"));
        else
            //topics = baseMapper.topicListByType(page, type);
            baseMapper.selectPage(page, Wrappers.<Topic>query().eq("type",type).orderByDesc("time"));

        List<Topic> topics = page.getRecords();
        if (topics.isEmpty()) return null;
        list = topics.stream().map(this::resolveToPreview).toList();
        //调用缓存工具类，对List类进行保存，过期时间给60秒
        cacheUtils.savaListToCache(key,list,60);
        return list;
    }

    //置顶帖子
    @Override
    public List<TopicTopVO> listTopTopics() {
        List<Topic> topics = baseMapper.selectList(Wrappers.<Topic>query()
                .select("id", "title", "time")
                .eq("top", 1));
        return topics.stream().map(topic -> {
            TopicTopVO vo = new TopicTopVO();
            BeanUtils.copyProperties(topic, vo);
            return vo;
        }).toList();
    }

    @Override
    public TopicDetailVO getTopic(int tid) {
        TopicDetailVO vo = new TopicDetailVO();
        Topic topic = baseMapper.selectById(tid);
        BeanUtils.copyProperties(topic, vo);
        TopicDetailVO.User user = new TopicDetailVO.User();

        vo.setUser(this.fillUserDetailsByPrivacy(user,topic.getUid()));
        return vo;
    }

    private <T> T fillUserDetailsByPrivacy(T target, int uid){
        AccountDetails accountDetails = accountDetailsMapper.selectById(uid);
        Account account = accountMapper.selectById(uid);
        AccountPrivacy accountPrivacy = accountPrivacyMapper.selectById(uid);
        String[] ignores = accountPrivacy.hiddenFields();
        BeanUtils.copyProperties(account,target,ignores);
        BeanUtils.copyProperties(accountDetails,target,ignores);

        return target;
    }

    private TopicPreviewVO resolveToPreview(Topic topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        //由于细分表，这边需要再查询一下用户表
        BeanUtils.copyProperties(accountMapper.selectById(topic.getUid()), vo);
        BeanUtils.copyProperties(topic, vo);
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            //判断insert是不是普通的字符串（文本）
            if (insert instanceof String text) {
                if (previewText.length() >= 300) continue;
                previewText.append(text);
            } else if (insert instanceof Map<?, ?> map) {
                //！！！认真学习优雅的写法
                Optional.ofNullable(map.get("image"))
                        //Lambda表达式
                        /*
                         * 1. obj ->：这是 lambda 表达式的参数部分，表示传入了一个名为 obj 的变量。
                         * 2. images.add(obj.toString())：这是 lambda 表达式的函数体部分，
                         * 表示对参数 obj 调用 toString() 方法，将其字符串表示形式添加到集合 images 中。*/
                        .ifPresent(obj -> images.add(obj.toString()));
            }
        }
        vo.setText(previewText.length() > 300
                ? previewText.substring(0, 300)
                : previewText.toString());
        vo.setImages(images);
        return vo;
    }

    //校验content
    private boolean textLimitCheck(JSONObject object) {
        if (object == null) return false;
        //字数统计
        long length = 0;
        for (Object op : object.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if (length > 20000) return false;
        }
        return true;
    }
}
