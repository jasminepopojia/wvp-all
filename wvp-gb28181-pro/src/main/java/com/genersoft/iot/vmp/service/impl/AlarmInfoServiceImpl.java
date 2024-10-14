package com.genersoft.iot.vmp.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.genersoft.iot.vmp.service.IAlarmInfoService;
import com.genersoft.iot.vmp.storager.dao.AlarmInfoMapper;
import com.genersoft.iot.vmp.storager.dao.dto.AlarmInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : JiangJiamin
 * @create 2024-07-16 16:34
 */
@Service
@DS("master")
public class AlarmInfoServiceImpl implements IAlarmInfoService {
    @Autowired
    private  AlarmInfoMapper alarmInfoMapper;

    @Override
    public AlarmInfo getById(Long id) {
        return alarmInfoMapper.getById(id);
    }

    @Override
    public int insert(AlarmInfo alarmInfo) {
        return alarmInfoMapper.add(alarmInfo);
    }
    @Override
    public List<AlarmInfo> getAll() {
        return alarmInfoMapper.getAll();
    }
    @Override
    public int updateAlarmInfo(Long id, Date updateTime, String transactor, String result) {
        return alarmInfoMapper.updateAlarmInfo(id, updateTime, transactor, result);
    }

    public List<AlarmInfo> getAlarmInfoBetweenDates(LocalDateTime start, LocalDateTime end) {
        return alarmInfoMapper.getAlarmInfoBetweenDates(start, end);
    }

// , String location
    @Override
    public int addAlarmInfo(Long videoId, String deviceId, String channelId, String alarmTime) {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setVideoId(videoId);
        alarmInfo.setDeviceId(deviceId);
        alarmInfo.setChannelId(channelId);
        alarmInfo.setAlarmTime(alarmTime);
//        alarmInfo.setLocation(location);
        Date currentTime = new Date();
        alarmInfo.setCreateTime(currentTime);
        alarmInfo.setUpdateTime(currentTime);
        alarmInfo.setIsDelete(0);
        alarmInfo.setStatus(0);

        return alarmInfoMapper.insertAlarmInfo(alarmInfo);
    }



}
