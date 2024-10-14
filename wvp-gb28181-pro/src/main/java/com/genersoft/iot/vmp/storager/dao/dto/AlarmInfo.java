package com.genersoft.iot.vmp.storager.dao.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : JiangJiamin
 * @create 2024-07-16 16:12
 */
public class AlarmInfo {
    private Long id;

    /**
     * 报警信息相关的视频id
     */
    private Long videoId;

    /**
     * 报警信息相关的摄像头id
     */
    private String deviceId;

    /**
     * 报警信息相关的摄像头通道
     */
    private String channelId;

    /**
     * 报警的时间
     */
    private String alarmTime;

    /**
     * 报警地点
     */
    private String location;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    private String streamUrl;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**获取处理状态**/
    private Integer status;
    /**获取处理人**/
    private String transactor;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    /***处理结果**/
    private String result;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTransactor() {
        return transactor;
    }

    public void setTransactor(String transactor) {
        this.transactor = transactor;
    }

}
