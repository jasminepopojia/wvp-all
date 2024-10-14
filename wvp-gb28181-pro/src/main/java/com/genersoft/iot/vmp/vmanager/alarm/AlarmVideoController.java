package com.genersoft.iot.vmp.vmanager.alarm;

import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.conf.security.JwtUtils;
import com.genersoft.iot.vmp.media.zlm.ZLMMediaServerStatusManger;
import com.genersoft.iot.vmp.service.IAlarmInfoService;
import com.genersoft.iot.vmp.service.IAlarmVideoService;
import com.genersoft.iot.vmp.service.impl.SmsService;
import com.genersoft.iot.vmp.storager.dao.DeviceChannelMapper;
import com.genersoft.iot.vmp.storager.dao.DeviceMapper;
import com.genersoft.iot.vmp.storager.dao.dto.AlarmInfo;
import com.genersoft.iot.vmp.storager.dao.dto.AlarmVideo;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import com.genersoft.iot.vmp.vmanager.gb28181.sse.SseEmitterStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.flogger.Flogger;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : JiangJiamin
 * @create 2024-07-18 15:24
 */
@Tag(name  = "报警视频管理")
@RestController
@RequestMapping("/api/alarmVideo")
public class AlarmVideoController {
    @Autowired
    private IAlarmVideoService alarmVideoService;

    @Autowired
    private IAlarmInfoService alarmInfoService;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceChannelMapper deviceChannelMapper;

    @Autowired
    private SmsService smsService;

    private final SseEmitterStore sseEmitterStore;

    private final static Logger logger = LoggerFactory.getLogger(ZLMMediaServerStatusManger.class);

    public AlarmVideoController(SseEmitterStore sseEmitterStore) {
        this.sseEmitterStore = sseEmitterStore;
    }

    @ResponseBody
    @GetMapping("/path")
    @Parameter(name = "id", description = "视频id", required = true)
    @Operation(summary = "查询视频信息", security = @SecurityRequirement(name = JwtUtils.HEADER))
    public AlarmVideo getById(@RequestParam Long id) {
        AlarmVideo alarmVideo = alarmVideoService.getById(id);
        if (alarmVideo == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "视频不存在");
        }
        System.out.println("输出结果为："+alarmVideo);
        return alarmVideo;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addAlarmVideo(@RequestParam String stream,
                                                             @RequestParam String startTime,
                                                             @RequestParam String endTime) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 创建一个新的 Date 对象
            Date now = new Date();
            String deviceId = "";
            String channelId = "";

            // 根据下划线分割
            String[] parts = stream.split("_");

            // 找到第一个字符的开端
            int startIndex1 = stream.indexOf("rtp_") + 4; //跳过 "rtp_"
            // 找到第一个字符的末尾
            int endIndex1 = stream.indexOf("_", startIndex1);

            // 找到第二个字符的开端
            int startIndex2 = endIndex1 + 1; // Skip the underscore
            // 找到第二个字符的末尾
            int endIndex2 = stream.indexOf("/", startIndex2);

            if (!stream.equals("")) {
                deviceId = stream.substring(startIndex1, endIndex1);
                channelId = stream.substring(startIndex2, endIndex2);
                smsService.sendSmsToUsers(deviceId);
                logger.info("设备通道号：" + deviceId + channelId);

                // 定义日期格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");

                // 格式化日期
                String formattedDate = sdf.format(now);

                String msg = "<strong>设备编号：</strong> <i>" + deviceId + "</i>"
                        + "<br><strong>通道编号：</strong> <i>" + channelId + "</i>"
                        + "<br><strong>报警描述：</strong> <i>" + "识别到霸凌行为，请及时关注处理！" + "</i>"
                        + "<br><strong>报警时间：</strong> <i>" + formattedDate + "</i>"
                        + "<br><strong>报警视频流地址：</strong><a href=\"" + stream + "\">点击查看相关视频</a>";

                // 告诉前端报警信息
                Map<String, SseEmitter> emitters = sseEmitterStore.getAllEmitters();
                logger.info("是否有emmiter?" + !emitters.isEmpty());
                emitters.forEach((browserId, emitter) -> {
                    try {
                        logger.info("emitter数据发送成功给对应的浏览器了");
                        emitter.send(SseEmitter.event().name("message").data(msg));
                    } catch (Exception e) {
                        e.printStackTrace();
                        sseEmitterStore.removeEmitter(browserId);
                    }
                });

                int addResult = alarmVideoService.addAlarmVideo(stream, startTime, endTime);
                if (addResult <= 0) {
                    response.put("code", 1);
                    response.put("msg", "添加报警视频失败");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }

                // 定义日期格式
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String alarmTime = sdf2.format(now);
                Long video_id = alarmVideoService.getId(stream);

                AlarmInfo alarmInfo = new AlarmInfo();
                alarmInfo.setStatus(0);
                alarmInfo.setIsDelete(0);
                alarmInfo.setChannelId(channelId);
                alarmInfo.setDeviceId(deviceId);
                alarmInfo.setLocation("报警地点");
                alarmInfo.setVideoId(video_id);
                alarmInfo.setCreateTime(new Date());
                alarmInfo.setUpdateTime(new Date());
                alarmInfo.setAlarmTime(alarmTime);
                alarmInfo.setStreamUrl(stream);

                int addResult2 = alarmInfoService.insert(alarmInfo);
                if (addResult2 <= 0) {
                    response.put("code", 1);
                    response.put("msg", "添加报警信息失败");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }

                response.put("code", 0);
                response.put("msg", "Alarm video added successfully");
                return ResponseEntity.ok(response);
            }

        } catch (TooManyResultsException e) {
            logger.error("查询返回多个结果: " + e.getMessage());
            response.put("code", 1);
            response.put("msg", "查询返回多个结果，请检查查询条件");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (RuntimeException e) {
            logger.error("运行时异常: " + e.getMessage());
            response.put("code", 1);
            response.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("code", 1);
        response.put("msg", "请求参数无效");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    //存本地视频
    private String saveVideoSegment(String stream, String startTime, String endTime) {
        try {
            // Get current year and month (e.g., 2024-08)
            SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
            String yearMonth = yearMonthFormat.format(new Date());

            // Create year-month folder if it doesn't exist
            File yearMonthFolder = new File("video/" + yearMonth);
            if (!yearMonthFolder.exists()) {
                yearMonthFolder.mkdirs();
            }
            // Generate timestamp for file name
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = dateFormat.format(new Date());
            String fileName = timestamp + ".mp4";

            // Execute FFmpeg command to save video segment
            String ffmpegCommand = "ffmpeg -i " + stream + " -ss " + startTime + " -to " + endTime + " -c copy video/"+yearMonthFolder+"/" + fileName;
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", ffmpegCommand);
            Process process = processBuilder.start();
            process.waitFor();

            return fileName;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error saving video segment: " + e.getMessage());
        }
    }

    //


}
