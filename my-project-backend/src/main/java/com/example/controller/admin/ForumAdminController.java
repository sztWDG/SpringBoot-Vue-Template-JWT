package com.example.controller.admin;

import com.example.entity.RestBean;
import com.example.entity.vo.request.AddTopicTypeVO;
import com.example.entity.vo.request.UpdateTopicTypeVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.entity.vo.response.TopicTopVO;
import com.example.entity.vo.response.TopicTypeVO;
import com.example.service.TopicService;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/admin/forum")
public class ForumAdminController {

    @Resource
    TopicService topicService;

    @Resource
    ControllerUtils utils;

    /**
     * 获取所有帖子类型
     * @return 帖子类型列表
     */
    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>> getTopicTypes() {
        return RestBean.success(topicService
                .listTypes()
                .stream()
                .map(type -> type.asViewObject(TopicTypeVO.class))
                .toList());
    }

    /**
     * 添加新的帖子类型
     * @param vo 帖子类型数据
     * @return 添加结果
     */
    @PostMapping("/type/add")
    public RestBean<Void> addTopicType(@Valid @RequestBody AddTopicTypeVO vo) {
        return utils.messageHandle(() -> topicService.createTopicType(vo));
    }

    /**
     * 更新帖子类型
     * @param vo 帖子类型数据
     * @return 更新结果
     */
    @PostMapping("/type/update")
    public RestBean<Void> updateTopicType(@Valid @RequestBody UpdateTopicTypeVO vo) {
        return utils.messageHandle(() -> topicService.updateTopicType(vo));
    }

    /**
     * 删除帖子类型
     * @param id 帖子类型ID
     * @return 删除结果
     */
    @DeleteMapping("/type/delete")
    public RestBean<Void> deleteTopicType(@RequestParam @Min(1) int id) {
        return utils.messageHandle(() -> topicService.deleteTopicType(id));
    }
    
    /**
     * 获取所有帖子列表
     * @return 帖子列表
     */
    @GetMapping("/topics")
    public RestBean<List<TopicPreviewVO>> getAllTopics() {
        return RestBean.success(topicService.listAllTopics());
    }
    
    /**
     * 获取当前所有置顶帖子
     * @return 置顶帖子列表
     */
    @GetMapping("/top-topics")
    public RestBean<List<TopicTopVO>> getTopTopics() {
        return RestBean.success(topicService.listTopTopics());
    }
    
    /**
     * 设置帖子为置顶状态
     * @param tid 帖子ID
     * @return 设置结果
     */
    @PostMapping("/topic/set-top")
    public RestBean<Void> setTopicTop(@RequestParam @Min(1) int tid) {
        return utils.messageHandle(() -> topicService.setTopicTop(tid, true));
    }
    
    /**
     * 取消帖子的置顶状态
     * @param tid 帖子ID
     * @return 取消结果
     */
    @PostMapping("/topic/cancel-top")
    public RestBean<Void> cancelTopicTop(@RequestParam @Min(1) int tid) {
        return utils.messageHandle(() -> topicService.setTopicTop(tid, false));
    }
}
