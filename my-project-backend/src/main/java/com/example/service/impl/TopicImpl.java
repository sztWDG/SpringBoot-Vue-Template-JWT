package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.*;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

    @Resource
    TopicCommentMapper commentMapper;

    @Resource
    StringRedisTemplate RedisTemplate;

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

        if (!textLimitCheck(vo.getContent(),20000))
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
            cacheUtils.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
            return null;
        } else {
            return "内部错误，请联系管理员！";
        }
    }

    @Override
    public String updateTopic(int uid, TopicUpdateVO vo) {
        //校验
        if (!textLimitCheck(vo.getContent(),20000))
            return "文章内容字数超出限制，发文失败！";
        if (!types.contains(vo.getType()))
            return "文章类型非法！";
        //更新
        baseMapper.update(null, Wrappers.<Topic>update()
                .eq("uid", uid)
                .eq("id", vo.getId())
                .set("title", vo.getTitle())
                .set("content", vo.getContent().toString())
                .set("type", vo.getType())
        );
        return null;
    }

    //评论楼中楼来了！
    @Override
    public String createComment(int uid, AddCommentVO vo) {
        if(!textLimitCheck(JSONObject.parseObject(vo.getContent()), 2000))
            return "评论内容太多，发表失败！";
        String key = Const.FORUM_TOPIC_COMMENT_COUNTER + uid;
        //评论需要限制一分钟只能发两次
        if(!flowUtils.limitPeriodCounterCheck(key, 2, 60))
            return "发表评论频繁，请稍后再试！";
        //创建一个评论对象,准备进行操作
        TopicComment comment = new TopicComment();
        comment.setUid(uid);
        BeanUtils.copyProperties(vo, comment);
        comment.setTime(new Date());
        commentMapper.insert(comment);

//        Topic topic = baseMapper.selectById(vo.getTid());
//        Account account = accountMapper.selectById(uid);
//        if(vo.getQuote() > 0) {
//            TopicComment com = commentMapper.selectById(vo.getQuote());
//            if(!Objects.equals(account.getId(), com.getUid())) {
//                notificationService.addNotification(
//                        com.getUid(),
//                        "您有新的帖子评论回复",
//                        account.getUsername()+" 回复了你发表的评论，快去看看吧！",
//                        "success", "/index/topic-detail/"+com.getTid()
//                );
//            }
//        } else if (!Objects.equals(account.getId(), topic.getUid())) {
//            notificationService.addNotification(
//                    topic.getUid(),
//                    "您有新的帖子回复",
//                    account.getUsername()+" 回复了你发表主题: "+topic.getTitle()+"，快去看看吧！",
//                    "success", "/index/topic-detail/"+topic.getId()
//            );
//        }
        return null;
    }

    @Override
    public List<TopicPreviewVO> listTopicCollects(int uid) {
        return baseMapper.collectTopics(uid)
                .stream()
                .map(topic -> {
                    TopicPreviewVO vo = new TopicPreviewVO();
                    BeanUtils.copyProperties(topic, vo);
                    return vo;
        })
                .toList();
    }

    @Override
    public List<TopicPreviewVO> listTopicByPage(int pageNumber, int type) {
        //造一个key
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + pageNumber + ":" + type;
        List<TopicPreviewVO> list = cacheUtils.takeListFromCache(key, TopicPreviewVO.class);
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
            baseMapper.selectPage(page, Wrappers.<Topic>query().eq("type", type).orderByDesc("time"));

        List<Topic> topics = page.getRecords();
        if (topics.isEmpty()) return null;
        list = topics.stream().map(this::resolveToPreview).toList();
        //调用缓存工具类，对List类进行保存，过期时间给60秒
        cacheUtils.savaListToCache(key, list, 60);
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
    public TopicDetailVO getTopic(int tid, int uid) {
        TopicDetailVO vo = new TopicDetailVO();
        Topic topic = baseMapper.selectById(tid);
        BeanUtils.copyProperties(topic, vo);

        TopicDetailVO.Interact interact = new TopicDetailVO.Interact(
                //！这边有个问题，是获得当前用户ID而非帖子主人id
                hasInteract(tid, uid, "like"),
                hasInteract(tid, uid, "collect")
        );
        vo.setInteract(interact);

        TopicDetailVO.User user = new TopicDetailVO.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user, topic.getUid()));
        return vo;
    }

    /**
     * 由于论坛交互数据（如点赞、收藏等）更新可能会非常频繁
     * 更新信息实时到MYSQL不太现实，所以需要用Redis做换从并在合适的实际一次性入库一段时间的全部数据
     * 当数据更新到来时，会创建一个新的定时任务，此任务会在一段时间后执行
     * 将全部Redis暂时缓存的信息一次性加入到数据库，从而缓解MYSQL压力，如果
     * 在定时任务已经设定期间又有新的更新到来，进更新Redis不创建新的演示任务
     */
    @Override
    public void interact(Interact interact, boolean state) {
        //思路：频繁交互的数据，需要做一个缓冲，否则对数据库的压力很大
        String type = interact.getType();
        synchronized (type.intern()) {
            //不能直接存，而要存到哈希表里面，因为需要去重，相同的只留下一个数据 //这里的state要用Boolean封装一下
            RedisTemplate.opsForHash().put(type, interact.toKey(), Boolean.toString(state));
        }
        //有的话，创建定时任务，否则不管
        this.saveInteractSchedule(type);
    }


    /**
     * 清除缓存，干净
     */
    private boolean hasInteract(int tid, int uid, String type) {
        String key = tid + ":" + uid;
        if (RedisTemplate.opsForHash().hasKey(type, key)) {
            return Boolean.parseBoolean(RedisTemplate.opsForHash().entries(type).get(key).toString());
        }
        return baseMapper.userInteractCount(tid, uid, type) > 0;
    }

    /**
     * //定时任务：定期保存
     * 创建一个名为 state 的 Map，用于存储不同类型的状态，
     * 键是 String 类型（代表不同的 type），
     * 值是 Boolean 类型（表示该类型是否正在处理）。
     */
    private final Map<String, Boolean> state = new HashMap<>();
    //创建一个调度线程池 service，最多可以同时运行两个任务。这个线程池可以用于定时执行任务。
    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    private void saveInteractSchedule(String type) {
        /*
         检查 state 中是否已存在 type 这个键。
         如果不存在，则返回 false。如果对应的值为 false，说明该类型的交互尚未在处理。
         */
        if (!state.getOrDefault(type, false)) {
            //若没有开始任务，则设置其开始
            state.put(type, true);
            //开始之后，给一个定时任务
            service.schedule(() -> {
                //保存最终数据
                this.saveInteract(type);
                //保存之后，又设置为false
                state.put(type, false);
            }, 3, TimeUnit.SECONDS);
        }
    }

    //保存数据成功之前，要保证不受影响，所以这里要加一把锁
    private void saveInteract(String type) {
        synchronized (type.intern()) {
            //点赞的话，保存一条点赞记录，取消点赞则是从数据库删除该用户点赞数据
            List<Interact> check = new LinkedList<>();
            List<Interact> uncheck = new LinkedList<>();
            RedisTemplate.opsForHash().entries(type).forEach((k, v) -> {
                if (Boolean.parseBoolean(v.toString()))
                    check.add(Interact.parseInteract(k.toString(), type));
                else
                    uncheck.add(Interact.parseInteract(k.toString(), type));
            });
            if (!check.isEmpty())
                baseMapper.addInteract(check, type);
            if (!uncheck.isEmpty())
                baseMapper.deleteInteract(uncheck, type);

            RedisTemplate.delete(type);
        }

    }

    private <T> T fillUserDetailsByPrivacy(T target, int uid) {
        AccountDetails accountDetails = accountDetailsMapper.selectById(uid);
        Account account = accountMapper.selectById(uid);
        AccountPrivacy accountPrivacy = accountPrivacyMapper.selectById(uid);
        String[] ignores = accountPrivacy.hiddenFields();
        BeanUtils.copyProperties(account, target, ignores);
        BeanUtils.copyProperties(accountDetails, target, ignores);

        return target;
    }

    private TopicPreviewVO resolveToPreview(Topic topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        //由于细分表，这边需要再查询一下用户表
        BeanUtils.copyProperties(accountMapper.selectById(topic.getUid()), vo);
        BeanUtils.copyProperties(topic, vo);

        //预览界面，获取点赞情况
        vo.setLike(baseMapper.interactCount(topic.getId(), "like"));
        vo.setCollect(baseMapper.interactCount(topic.getId(), "collect"));

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
    private boolean textLimitCheck(JSONObject object, int max) {
        if (object == null) return false;
        //字数统计
        long length = 0;
        for (Object op : object.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if (length > max) return false;
        }
        return true;
    }
}
