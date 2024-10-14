package com.genersoft.iot.vmp.vmanager.gb28181.sse;

import com.genersoft.iot.vmp.gb28181.event.alarm.AlarmEventListener;
import com.genersoft.iot.vmp.media.zlm.ZLMMediaServerStatusManger;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeEditor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * SSE 推送.
 *
 * @author lawrencehj
 * @author <a href="mailto:xiaoQQya@126.com">xiaoQQya</a>
 * @since 2021/01/20
 */
@Tag(name = "SSE 推送")
@RestController
@RequestMapping("/api")
public class SseController {

    @Resource
    private AlarmEventListener alarmEventListener;
    private final static Logger logger = LoggerFactory.getLogger(ZLMMediaServerStatusManger.class);

    private final SseEmitterStore sseEmitterStore;

    public SseController(SseEmitterStore sseEmitterStore) {
        this.sseEmitterStore = sseEmitterStore;
    }

    @GetMapping("/emit")
    public SseEmitter emit(@RequestParam String browserId) {
        SseEmitter emitter = new SseEmitter(0L);


        logger.info("传过来的浏览器id："+browserId);
        if (sseEmitterStore.getEmitter(browserId) == null)
        {
            logger.info("进来添加了");
            sseEmitterStore.addEmitter(browserId, emitter);
        }

        // 设置超时处理
        emitter.onTimeout(() -> {
            emitter.complete();
            System.out.println("SSE 连接超时");
        });

        // 设置关闭处理
        emitter.onCompletion(() -> {
            System.out.println("SSE 连接已完成");
        });

        emitter.onError(throwable -> {
            try {
                logger.info("连接异常", throwable.toString());
                emitter.send(SseEmitter.event()
                        .id(browserId)
                        .name("发生异常")
                        .data("发生异常请重试！")
                        .reconnectTime(3000));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

//        try {
//            emitter.send(SseEmitter.event().data("发送信息成功了!!"));
//            logger.info("数据已发送");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return emitter;
    }

//    /**
//     * SSE 推送.
//     *
//     * @param response  响应
//     * @param browserId 浏览器ID
//     * @throws IOException IOEXCEPTION
//     * @author <a href="mailto:xiaoQQya@126.com">xiaoQQya</a>
//     * @since 2023/11/06
//     */
//    @GetMapping("/emit")
//    public void emit(HttpServletResponse response, @RequestParam String browserId) throws IOException, InterruptedException {
//        response.setContentType("text/event-stream");
//        response.setCharacterEncoding("utf-8");
//        response.setHeader("Cache-Control", "no-cache");
//        response.setHeader("Connection", "keep-alive");
//        response.setHeader("Access-Control-Allow-Origin", "*");
//
//        logger.info("传过来的浏览器id："+browserId);
//
//        response.setBufferSize(1); // 设置较小的缓冲区大小
//
//        PrintWriter writer = response.getWriter();
////        SseEmitter emitter = new SseEmitter(0L);
////        sseEmitterStore.addEmitter(browserId, emitter);
//        alarmEventListener.addSseEmitter(browserId, writer);
//
//        while (!writer.checkError()) {
//            Thread.sleep(1000);
//            writer.write(":keep alive\n\n");
//            logger.info("进到这里来了，writer没错");
//            writer.flush();
//            response.flushBuffer(); // 强制刷新缓冲区
//        }
//
//        alarmEventListener.removeSseEmitter(browserId, writer);
//    }

//    /**
//     * SSE 推送.
//     *
//     * @param response  响应
//     * @param browserId 浏览器ID
//     * @throws IOException IOEXCEPTION
//     * @author <a href="mailto:xiaoQQya@126.com">xiaoQQya</a>
//     * @since 2023/11/06
//     */
//    @GetMapping("/emit")
//    public void emit(HttpServletResponse response, @RequestParam String browserId, HttpServletRequest request) throws IOException, InterruptedException {
//        response.setContentType("text/event-stream");
//        response.setCharacterEncoding("utf-8");
//        response.setHeader("Cache-Control", "no-cache");
//        response.setHeader("Connection", "keep-alive");
//        response.setHeader("Access-Control-Allow-Origin", "*");
//
//        logger.info("传过来的浏览器id："+browserId);
//
//        PrintWriter writer = response.getWriter();
////        alarmEventListener.addSseEmitter(browserId, writer);
//        SseEmitter emitter = new SseEmitter(0l);
//
//        // 设置超时处理
//        emitter.onTimeout(() -> {
//            emitter.complete();
//            System.out.println("SSE 连接超时");
//        });
//
//        // 设置关闭处理
//        emitter.onCompletion(() -> {
//            System.out.println("SSE 连接已完成");
//        });
//
//        emitter.onError(
//                throwable -> {
//                    try{
//                        logger.info("连接异常",throwable.toString());
//                        emitter.send(SseEmitter.event()
//                            .id(browserId)
//                            .name("发生异常")
//                            .data("发生异常请重试！")
//                            .reconnectTime(3000));
//                    }catch (IOException E){
//                        E.printStackTrace();
//                    }
//                }
//        );
//
//
//
//        try {
////            emitter.send(SseEmitter.event().comment("welcome"));
////            emitter.send("向前端传递的数据",MediaType.APPLICATION_JSON);
////            emitter.send(SseEmitter.event().reconnectTime(5000));
////            emitter.send(SseEmitter.event().id(browserId).reconnectTime(1*60*1000L).data("发送信息成功了!!"));
//            emitter.send(SseEmitter.event().data("发送信息成功了!!"));
//            logger.info("数据已发送");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        while (!writer.checkError()) {
//            Thread.sleep(1000);
//            writer.write(":keep alive\n\n");
//            writer.flush();
//        }
//        logger.error("writer有问题，alarmEventListener被移除");
//        alarmEventListener.removeSseEmitter(browserId, writer);
//    }



}
