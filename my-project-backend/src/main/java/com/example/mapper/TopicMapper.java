package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Topic;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
    //由于帖子信息和用户信息分离，所以这里的连表查询去除掉
    //start：从这里开始，往后多少条。
//    @Select("""
//            select * from db_topic left join db_account on uid = db_account.id
//            order by `time` desc limit ${start},10
//            """)
//    List<Topic> topicList(int start);
//
//
//    @Select("""
//            select * from db_topic left join db_account on uid = db_account.id
//            where type = #{type}
//            order by `time` desc limit ${start},10
//            """)
//    List<Topic> topicListByType(int start, int type);

}
