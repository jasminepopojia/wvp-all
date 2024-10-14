package com.genersoft.iot.vmp.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.genersoft.iot.vmp.service.IAlarmVideoService;
import com.genersoft.iot.vmp.service.bean.CloudRecordItem;
import com.genersoft.iot.vmp.storager.dao.AlarmVideoMapper;
import com.genersoft.iot.vmp.storager.dao.CloudRecordServiceMapper;
import com.genersoft.iot.vmp.storager.dao.DeviceChannelMapper;
import com.genersoft.iot.vmp.storager.dao.DeviceMapper;
import com.genersoft.iot.vmp.storager.dao.dto.AlarmVideo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : JiangJiamin
 * @create 2024-07-18 15:45
 */
@Service
@DS("master")
public class AlarmVideoServiceImpl implements IAlarmVideoService {
    @Autowired
    private AlarmVideoMapper alarmVideoMapper;


    @Autowired
    private CloudRecordServiceMapper cloudRecordServiceMapper;

    @Override
    public AlarmVideo getById(Long id) {
        System.out.println("111："+ alarmVideoMapper.getById(id));
        return alarmVideoMapper.getById(id);
    }

    @Override
    public Long getId(String stream) {
        return alarmVideoMapper.getIdByStream(stream);
    }

    @Override
    public int addVideo(AlarmVideo alarmVideo) {
        return alarmVideoMapper.addVideo(alarmVideo);
    }

    @Override
    public List<AlarmVideo> getAllVideo() {
        return alarmVideoMapper.getAllVideo();
    }

    @Override
    public int addAlarmVideo(String stream, String startTime, String endTime) {
//        CloudRecordItem record = cloudRecordServiceMapper.findRecordByStream(stream);
//        if (record != null) {
        AlarmVideo alarmVideo = new AlarmVideo();
        alarmVideo.setStream(stream);
        alarmVideo.setStartTime(startTime);
        alarmVideo.setEndTime(endTime);
//            alarmVideo.setFileName(record.getFileName());
//            alarmVideo.setFilePath(record.getFilePath());
//        alarmVideo.setFileName(fileName);
//        alarmVideo.setFilePath(filePath);
        Date currentTime = new Date();
        alarmVideo.setCreateTime(currentTime);
        alarmVideo.setUpdateTime(currentTime);
        alarmVideo.setIsDelete(0); // Assuming 0 means not deleted

        return alarmVideoMapper.insertAlarmVideo(alarmVideo);
//        } else {
//            throw new RuntimeException("No matching record found in wvp_cloud_record for stream: " + stream);
//        }
    }

//    @Override
//    public int addAlarmVideo(String stream, String startTime, String endTime,String fileName,String filePath) {
////        CloudRecordItem record = cloudRecordServiceMapper.findRecordByStream(stream);
////        if (record != null) {
//            AlarmVideo alarmVideo = new AlarmVideo();
//            alarmVideo.setStream(stream);
//            alarmVideo.setStartTime(startTime);
//            alarmVideo.setEndTime(endTime);
////            alarmVideo.setFileName(record.getFileName());
////            alarmVideo.setFilePath(record.getFilePath());
//            alarmVideo.setFileName(fileName);
//            alarmVideo.setFilePath(filePath);
//            Date currentTime = new Date();
//            alarmVideo.setCreateTime(currentTime);
//            alarmVideo.setUpdateTime(currentTime);
//            alarmVideo.setIsDelete(0); // Assuming 0 means not deleted
//
//            return alarmVideoMapper.insertAlarmVideo(alarmVideo);
////        } else {
////            throw new RuntimeException("No matching record found in wvp_cloud_record for stream: " + stream);
////        }
//    }
}
