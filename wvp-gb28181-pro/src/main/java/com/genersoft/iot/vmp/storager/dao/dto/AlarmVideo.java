package com.genersoft.iot.vmp.storager.dao.dto;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : JiangJiamin
 * @create 2024-07-18 15:34
 */
public class AlarmVideo {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    /**
     * id
     */
    private Long id;

    /**
     * 报警视频流
     */
    private String stream;

    /**
     * 报警视频开始时间
     */
    private String startTime;

    /**
     * 报警视频结束时间
     */
    private String endTime;

    /**
     * 报警视频的文件名
     */
    private String fileName;

    /**
     * 报警视频的全路径名
     */
    private String filePath;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;
}
