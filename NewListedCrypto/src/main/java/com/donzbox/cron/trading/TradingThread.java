package com.donzbox.cron.trading;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.donzbox.domain.trading.both.ChartEntity;
import com.donzbox.service.trading.TradingService;

@Component
public class TradingThread {

	final static private int CANDLE_COUNT = 60;
	final static private String BITHUMB = "BIT";
	final static private String TIME_FRAME_MIN = "MIN";
	
    @Value("${spring.profiles.active}")
    private String springProfilesActive;
    
	@Autowired
	TradingService tradingService;
	
	// 애플리케이션 시작 후 1초 후에 첫 실행, 그 후 매 5초마다 주기적으로 실행 (SchedulingConfiguration에서 재설정 하므로 이하의 설정값 무시됨)
	// @Scheduled(initialDelay = 1000, fixedDelay = 15000)
	public void execute() throws Exception {
		
		// 거래소별 코인의 현시점으로부터 일정기간 이전 까지의 종가를 모아 코인별 리스트로 가져옴
		String marketCd = BITHUMB; // 빚덤
		ChartEntity chartEntity = new ChartEntity();
		chartEntity.setMarketCd(marketCd);
		chartEntity.setTimeFrame(TIME_FRAME_MIN); // 분단위 캔들수집
		chartEntity.setCandleCount(CANDLE_COUNT); // 현시점으로부터 과거까지 봉 60개를 가져오겠음 
		List<ChartEntity> chartEntityList = tradingService.selectMiniChart(chartEntity);

		// 거래소별 코인의 현시점으로부터 일정기간 이전 까지의 종가를 모아 코인별 리스트로 가져옴
		for(ChartEntity c : chartEntityList) {
			tradingService.mergeMiniChart(c);
		}
	}
}