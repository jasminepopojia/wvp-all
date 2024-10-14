package com.genersoft.iot.vmp.storager.dao;


import com.genersoft.iot.vmp.service.bean.CloudRecordItem;
import com.genersoft.iot.vmp.storager.dao.dto.AlarmVideo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : JiangJiamin
 * @create 2024-07-18 15:40
 */
@Mapper
@Repository
public interface AlarmVideoMapper {
    @Select("SELECT * FROM alarm_video WHERE id = #{id}")
    AlarmVideo getById(@Param("id") Long id);

    @Select("SELECT * FROM alarm_video WHERE stream = #{stream} LIMIT 1")
    Long getIdByStream(@Param("stream") String stream);


    @Insert("INSERT INTO alarm_video (stream, startTime, endTime, fileName, filePath, createTime, updateTime, isDelete) " +
            "VALUES (#{stream}, #{startTime}, #{endTime}, #{fileName}, #{filePath}, #{createTime}, #{updateTime}, #{isDelete})")
    int addVideo(AlarmVideo alarmVideo);

    @Select("SELECT * FROM alarm_video")
    List<AlarmVideo> getAllVideo();

//    @Insert("INSERT INTO alarm_video (stream, start_time, end_time, file_name, file_path, create_time, update_time, is_delete) " +
//            "VALUES (#{stream}, #{startTime}, #{endTime}, #{fileName}, #{filePath}, #{createTime}, #{updateTime}, #{isDelete})")
//    int insertAlarmVideo(AlarmVideo alarmVideo);

    @Insert("INSERT INTO alarm_video (stream, start_time, end_time,create_time, update_time, is_delete) " +
            "VALUES (#{stream}, #{startTime}, #{endTime}, #{createTime}, #{updateTime}, #{isDelete})")
    int insertAlarmVideo(AlarmVideo alarmVideo);
}




