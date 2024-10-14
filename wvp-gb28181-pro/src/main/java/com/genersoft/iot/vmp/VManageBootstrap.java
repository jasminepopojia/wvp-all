package com.genersoft.iot.vmp;

import com.genersoft.iot.vmp.utils.GitUtil;
import com.genersoft.iot.vmp.utils.SpringBeanFactory;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 启动类
 */
@ServletComponentScan("com.genersoft.iot.vmp.conf")
@MapperScan("com.genersoft.iot.vmp.storager.dao")
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class VManageBootstrap extends SpringBootServletInitializer {

	private final static Logger logger = LoggerFactory.getLogger(VManageBootstrap.class);

	private static String[] args;
	private static ConfigurableApplicationContext context;
	public static void main(String[] args) throws IOException {

		// 获取当前项目根目录
		String projectRoot = System.getProperty("user.dir");
		// 获取项目根目录的父目录
		File projectRootDir = new File(projectRoot);
		String parentDir = projectRootDir.getParent();

		// 启动Redis
		String redisPath = parentDir + "\\Redis-x64-3.0.504\\redis-server.exe";
		File redisFile = new File(redisPath);
		if (!redisFile.exists()) {
			System.out.println("Redis server executable not found at: " + redisPath);
			return;
		}
		ProcessBuilder redisProcessBuilder = new ProcessBuilder("cmd.exe", "/C", redisPath);
		redisProcessBuilder.start();
		System.out.println("Redis started successfully.");

		// 启动ZLMediaKit
		String zlmPath = parentDir + "\\ZLMediaKit\\release\\windows\\Debug\\MediaServer.exe";
		ProcessBuilder zlmProcessBuilder = new ProcessBuilder("cmd.exe", "/C", zlmPath);
		zlmProcessBuilder.start();
		System.out.println("ZLMediaKit started successfully.");

		VManageBootstrap.args = args;
		VManageBootstrap.context = SpringApplication.run(VManageBootstrap.class, args);
		GitUtil gitUtil1 = SpringBeanFactory.getBean("gitUtil");
		logger.info("构建版本： {}", gitUtil1.getBuildVersion());
		logger.info("构建时间： {}", gitUtil1.getBuildDate());
		logger.info("GIT最后提交时间： {}", gitUtil1.getCommitTime());

//		// 创建一个命令List集合，用于存放ffmpeg执行的命令参数
//		List<List<String>> commands = new ArrayList<>();
//
//		// 构建FFmpeg的相对路径
//		String ffmpegPath = parentDir + "\\ffmpeg-7.0.1-full_build\\bin\\ffmpeg.exe";
//		// 第一个输入流和输出流
//		List<String> command1 = new ArrayList<>();
//		command1.add(ffmpegPath);
//		command1.add("-i");
//		command1.add("rtsp://admin:zanyhat741@10.0.0.11:554/h264/ch1/main/av_stream1"); // 输入视频源1
//		command1.add("-c:v");
//		command1.add("libx264");
//		command1.add("-preset");
//		command1.add("veryfast");
//		command1.add("-b:v");
//		command1.add("500k");
//		command1.add("-maxrate");
//		command1.add("500k");
//		command1.add("-bufsize");
//		command1.add("1000k");
//		command1.add("-vf");
//		command1.add("scale=1280:720");
//		command1.add("-c:a");
//		command1.add("aac");
//		command1.add("-b:a");
//		command1.add("128k");
//		command1.add("-f");
//		command1.add("flv");
//		command1.add("rtmp://49.232.189.216:1935/live/test1"); // 输出流地址1
//		commands.add(command1);
//
//		// 第二个输入流和输出流
//		List<String> command2 = new ArrayList<>();
//		command2.add(ffmpegPath);
//		command2.add("-i");
//		command2.add("rtsp://admin:zanyhat741@10.0.0.12:554/h264/ch1/main/av_stream2"); // 输入视频源2
//		command2.add("-c:v");
//		command2.add("libx264");
//		command2.add("-preset");
//		command2.add("veryfast");
//		command2.add("-b:v");
//		command2.add("500k");
//		command2.add("-maxrate");
//		command2.add("500k");
//		command2.add("-bufsize");
//		command2.add("1000k");
//		command2.add("-vf");
//		command2.add("scale=1280:720");
//		command2.add("-c:a");
//		command2.add("aac");
//		command2.add("-b:a");
//		command2.add("128k");
//		command2.add("-f");
//		command2.add("flv");
//		command2.add("rtmp://49.232.189.216:1935/live/test2"); // 输出流地址2
//		commands.add(command2);
//
//		// 创建并启动每个ffmpeg进程
//		for (List<String> command : commands) {
//			ProcessBuilder builder = new ProcessBuilder(command);
//			Process process = builder.start();
//
//			// 创建log文件夹
//			File logDir = new File("log");
//			if (!logDir.exists()) {
//				logDir.mkdirs();
//			}
//
//			// 多线程读取正常输出信息 process.getInputStream()
//			new Thread(() -> {
//				try {
//					InputStream is = process.getInputStream();
//					// 将正常输出信息写入到log/normal.txt文件中
//					RandomAccessFile raf = new RandomAccessFile("log/normal.txt", "rw");
//
//					byte[] data = new byte[128];
//					int len = -1;
//					while ((len = is.read(data)) != -1) {
//						raf.write(data, 0, len);
//					}
//					raf.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}, "Thread-Normal").start();
//
//			// 多线程读取错误输出信息 process.getErrorStream()
//			new Thread(() -> {
//				try {
//					InputStream is = process.getErrorStream();
//					// 将错误输出信息写入到log/error.txt文件中
//					RandomAccessFile raf = new RandomAccessFile("log/error.txt", "rw");
//
//					byte[] data = new byte[128];
//					int len = -1;
//					while ((len = is.read(data)) != -1) {
//						raf.write(data, 0, len);
//					}
//					raf.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}, "Thread-Error").start();
//		}
	}

	// 项目重启
	public static void restart() {
		context.close();
		VManageBootstrap.context = SpringApplication.run(VManageBootstrap.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(VManageBootstrap.class);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);

		servletContext.setSessionTrackingModes(
				Collections.singleton(SessionTrackingMode.COOKIE)
		);
		SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
		sessionCookieConfig.setHttpOnly(true);

	}
}
