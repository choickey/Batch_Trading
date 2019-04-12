package com.donzbox.domain.cryptoSignal.both;

import lombok.Data;

public @Data class CryptoSignalEntity {

	private String code;                   // "CRIX.UPBIT.KRW-BTC"
	private String marketCd;               // "UPBIT"
	private String coinCd;                 // "KRW-BTC"
	private String candleDateTime;         // "2018-04-24T07:04:00+00:00"
	private String candleDateTimeKst;      // "2018-04-24T16:04:00+09:00"
	private double openingPrice;           // 9981000
	private double highPrice;              // 9984000
	private double lowPrice;               // 9969000
	private double tradePrice;             // 9970000
	private double tradePricePer;          // 현재 거래단가와 이전 거래단가의 차이의 퍼센트 비율
	private double candleAccTradeVolume;   // 26.92197636
	private double candleAccTradePrice;    // 268571662.78924
	private double candleAccTradePricePer; // 현재의 총 거래금액과 이전 총 거래금액의 차이의 퍼센트 비율
	private double candleAccTradePriceCal; // 현재의 총 거래금액과 이전 총 거래금액의 차
	private long   timestampReg;           // 1524553475785
	private long   unit;                   // 1
	
	@Override
	public String toString() {
		return "CryptoSignalEntity [code=" + code + ", marketCd=" + marketCd + ", coinCd=" + coinCd
				+ ", candleDateTime=" + candleDateTime + ", candleDateTimeKst=" + candleDateTimeKst + ", openingPrice="
				+ openingPrice + ", highPrice=" + highPrice + ", lowPrice=" + lowPrice + ", tradePrice=" + tradePrice
				+ ", tradePricePer=" + tradePricePer + ", candleAccTradeVolume=" + candleAccTradeVolume
				+ ", candleAccTradePrice=" + candleAccTradePrice + ", candleAccTradePricePer=" + candleAccTradePricePer
				+ ", candleAccTradePriceCal=" + candleAccTradePriceCal + ", timestampReg=" + timestampReg + ", unit="
				+ unit + "]";
	}
}