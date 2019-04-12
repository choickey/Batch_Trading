package com.donzbox.service.trading;

import java.util.List;

import com.donzbox.domain.trading.both.ChartEntity;

public interface TradingService {

	// 거래소별 코인의 현시점으로부터 일정기간 이전 까지의 종가를 모아 코인별 리스트 가져옴
    public List<ChartEntity> selectMiniChart(ChartEntity chartEntity);
    
	// 거래소별 코인의 현시점으로부터 일정기간 이전 까지의 종가를 모아 코인별로 배열로 table에 merge
    public void mergeMiniChart(ChartEntity chartEntity);
}
