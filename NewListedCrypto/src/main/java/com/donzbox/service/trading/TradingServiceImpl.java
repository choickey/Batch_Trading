package com.donzbox.service.trading;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donzbox.domain.trading.both.ChartEntity;
import com.donzbox.mapper.trading.TradingMapper;

@Service
public class TradingServiceImpl implements TradingService {

	// private static final Logger logger = LoggerFactory.getLogger(CryptoListServiceImpl.class);

	@Autowired
	TradingMapper tradingMapper;

	// 거래소별 코인의 현시점으로부터 일정기간 이전 까지의 종가를 모아 코인별 리스트 가져옴
    public List<ChartEntity> selectMiniChart(ChartEntity chartEntity) {
    	return tradingMapper.selectMiniChart(chartEntity);
	}
    
	// 거래소별 코인의 현시점으로부터 일정기간 이전 까지의 종가를 모아 코인별로 배열로 table에 merge
    public void mergeMiniChart(ChartEntity chartEntity) {
    	tradingMapper.mergeMiniChart(chartEntity);
	}
}