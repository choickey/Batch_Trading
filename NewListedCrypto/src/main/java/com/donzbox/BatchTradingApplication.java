package com.donzbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

// http://jsonobject.tistory.com/236 의거하여 구현됨
@SpringBootApplication
@EnableScheduling
public class BatchTradingApplication {

	public static void main(final String[] args) {
		SpringApplication.run(BatchTradingApplication.class, args);
	}

	// 단일 쓰레드
	//	@Bean
	//	public TaskScheduler taskScheduler() {
	//		return new ConcurrentTaskScheduler();
	//	}

	// 멀티 쓰레드
	@Bean
	public TaskScheduler taskScheduler() {
		final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(10);
		return taskScheduler;
	}
}