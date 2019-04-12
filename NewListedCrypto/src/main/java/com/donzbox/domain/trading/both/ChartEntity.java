package com.donzbox.domain.trading.both;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

public @Data class ChartEntity implements Serializable {

	private static final long serialVersionUID = -7991591472377954525L;

	private String marketCd;			// 마켓CD
	private String coinCd;				// 코인CD
	private String timeFrame;			// 캔들단위 (분:MIN, 시:HOUR, 일:DAY)
	private String endPrices;			// 종가
	private int candleCount;			// 가져올 캔들갯수
	private Date lastTransactionDate;	// 최신의 캔들생성 일시
}
