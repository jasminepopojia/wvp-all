package com.genersoft.iot.vmp.storager.dao;

import com.genersoft.iot.vmp.storager.dao.dto.AlarmInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : JiangJiamin
 * @create 2024-07-16 16:17
 */
@Mapper
@Repository
public interface AlarmInfoMapper {
    @Select("SELECT * FROM alarm_info WHERE id = #{id}")
    AlarmInfo getById(@Param("id") Long id);

    @Insert("INSERT INTO alarm_info (video_id, device_id, channel_id, alarm_time, location, create_time, update_time, is_delete, status, transactor,result,stream_url) " +
            "VALUES (#{videoId}, #{deviceId}, #{channelId}, #{alarmTime}, #{location}, #{createTime}, #{updateTime}, #{isDelete}, #{status}, #{transactor},#{result},#{streamUrl})")
    int add(AlarmInfo alarmInfo);



    @Select("SELECT * FROM alarm_info")
    List<AlarmInfo> getAll();

    @Update("UPDATE alarm_info SET update_time = #{updateTime}, transactor = #{transactor}, result = #{result}, status = 1 WHERE id = #{id}")
    int updateAlarmInfo(@Param("id") Long id, @Param("updateTime") Date updateTime, @Param("transactor") String transactor, @Param("result") String result);

    @Select("SELECT * FROM alarm_info WHERE create_time BETWEEN #{start} AND #{end}")
    List<AlarmInfo> getAlarmInfoBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
//, location #{location},
    @Insert("INSERT INTO alarm_info (video_id, device_id, channel_id, alarm_time, create_time, update_time,is_delete,status) " +
            "VALUES (#{videoId}, #{deviceId}, #{channelId}, #{alarmTime}, #{createTime}, #{updateTime}, #{isDelete}, #{status})")
    int insertAlarmInfo(AlarmInfo alarmInfo);
}
