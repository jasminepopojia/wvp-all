package com.genersoft.iot.vmp.vmanager.alarm;


import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.conf.security.JwtUtils;
import com.genersoft.iot.vmp.service.IAlarmInfoService;
import com.genersoft.iot.vmp.storager.dao.dto.AlarmInfo;
import com.genersoft.iot.vmp.storager.dao.dto.Role;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : JiangJiamin
 * @create 2024-07-16 16:28
 */
@Tag(name  = "报警信息管理")
@RestController
@RequestMapping("/api/event")
public class AlarmInfoController {
    @Autowired
    private IAlarmInfoService alarmInfoService;

//    @RequestParam String location , location
    @PostMapping("/add")
    public String addAlarmInfo(@RequestParam Long videoId,
                               @RequestParam String deviceId,
                               @RequestParam String channelId,
                               @RequestParam String alarmTime
                              ) {
        int resultCount = alarmInfoService.addAlarmInfo(videoId, deviceId, channelId, alarmTime);
        if (resultCount > 0) {
            return "AlarmInfo added successfully!";
        } else {
            return "Failed to add AlarmInfo.";
        }
    }


    @GetMapping("/list")
    @Operation(summary = "查询信息", security = @SecurityRequirement(name = JwtUtils.HEADER))
    public List<AlarmInfo> getAllAlarmInfo() {
        return alarmInfoService.getAll();
    }


    @PostMapping("/update")
    @Parameter(name = "id", description = "事件id", required = true)
    @Parameter(name = "updateTime", description = "处理时间", required = true)
    @Parameter(name = "transactor", description = "处理人", required = true)
    @Parameter(name = "result", description = "处理结果", required = true)
    public String UpdateAlarmInfo(@RequestParam Long id, @RequestParam String updateTime, @RequestParam String transactor, @RequestParam String result) throws ParseException {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(updateTime) || ObjectUtils.isEmpty(transactor) || ObjectUtils.isEmpty(result)) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "参数不可为空");
        }
        AlarmInfo alarmInfo = alarmInfoService.getById(id);

        if (alarmInfo == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "事件不存在");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        Date updateTimeDate = formatter.parse(updateTime);

        int resultCount = alarmInfoService.updateAlarmInfo(id, updateTimeDate, transactor, result);
        if (resultCount > 0) {
            return "AlarmInfo updated successfully!";
        } else {
            return "Failed to update AlarmInfo.";
        }
    }

    @PostMapping("/searchList")
    @Parameter(name = "startTime", description = "开始时间", required = true)
    @Parameter(name = "endTime", description = "结束时间", required = true)
    @Parameter(name = "startDate", description = "开始日期", required = true)
    @Parameter(name = "endDate", description = "结束日期", required = true)
    public ResponseEntity<List<AlarmInfo>> getAlarmInfoBetweenDates(
            @RequestParam String startTime, @RequestParam String endTime, @RequestParam String startDate, @RequestParam String endDate) {
        // 将日期和时间拼接成 LocalDateTime 对象
        LocalDateTime start = LocalDateTime.of(
                LocalDate.parse(startDate),
                LocalTime.parse(startTime)
        );

        LocalDateTime end = LocalDateTime.of(
                LocalDate.parse(endDate),
                LocalTime.parse(endTime)
        );
        List<AlarmInfo> alarmInfoList = alarmInfoService.getAlarmInfoBetweenDates(start, end);
        return ResponseEntity.ok(alarmInfoList);
    }

}
