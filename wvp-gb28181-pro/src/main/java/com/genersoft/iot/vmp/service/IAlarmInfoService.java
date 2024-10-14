package com.genersoft.iot.vmp.service;

import com.genersoft.iot.vmp.storager.dao.dto.AlarmInfo;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : JiangJiamin
 * @create 2024-07-16 16:30
 */
public interface IAlarmInfoService {


    public AlarmInfo getById(Long id);

    public int insert(AlarmInfo alarmInfo);

    public List<AlarmInfo> getAll();

    public int updateAlarmInfo(Long id, Date updateTime, String transactor, String result);

    public List<AlarmInfo> getAlarmInfoBetweenDates(LocalDateTime start, LocalDateTime end);
//    , String location
    public int addAlarmInfo(Long videoId, String deviceId, String channelId, String alarmTime);

}
