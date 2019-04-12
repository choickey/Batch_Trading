package com.donzbox.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.donzbox.cron.cryptoSignal.CryptoListThread;
import com.donzbox.cron.cryptoSignal.CryptoSignalThread;
import com.donzbox.cron.trading.TradingThread;

@Configuration
@EnableScheduling
public class SchedulingConfiguration implements SchedulingConfigurer {

	@Autowired
	CryptoSignalThread cryptoSignalThread;

	@Autowired
	CryptoListThread cryptoListThread;
	
	@Autowired
	TradingThread tradingThread;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		
		// 개발하는 배치를 제외하고는 돌릴 수 없거나(환경부재) 돌리면 안되는 경우
		// 개발서버에선 해당 잡을 받아줄 연계장치가 없거나 쓸대없이 돌면 안되는 경우
		if(true) {
			
			// set 을통해 기존 설정을 모두 날려버리기
			// taskRegistrar.setCronTasksList(Collections.emptyList());
						
			// 리스트 생성
			List<CronTask> list = new ArrayList<>();
	
			// @Autowired cryptoSignalThread 를 직접 넣어주고 시간도 재설정
			// 물론 리플렉션을 통해 시간을 가져올 수도있지만 보통 로컬에서
			// 따로 돌릴 정도면 기존의 시간을 쓰지않는 경우가 대부분이라
			list.add(new CronTask(() -> {
				try {
//					cryptoSignalThread.execute();
//					cryptoListThread.execute();
					tradingThread.execute();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, "0/5 * * * * ?"));
	
			// 모든 스케줄 덮어쓰기
			taskRegistrar.setCronTasksList(list);
		}
	}
}
