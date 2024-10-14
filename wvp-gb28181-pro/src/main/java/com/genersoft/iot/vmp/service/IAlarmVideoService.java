package com.genersoft.iot.vmp.service;

import com.genersoft.iot.vmp.storager.dao.dto.AlarmVideo;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : JiangJiamin
 * @create 2024-07-18 15:43
 */
public interface IAlarmVideoService {
    public AlarmVideo getById(Long id);

    public int addVideo(AlarmVideo alarmVideo);

    public List<AlarmVideo> getAllVideo();

    //    int addAlarmVideo(String stream, String startTime, String endTime,String fileName,String filePath);
    int addAlarmVideo(String stream, String startTime, String endTime);

    public Long getId(String stream);
}

