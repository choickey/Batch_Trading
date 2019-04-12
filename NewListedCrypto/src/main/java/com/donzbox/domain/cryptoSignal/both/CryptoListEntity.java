package com.donzbox.domain.cryptoSignal.both;

import java.util.Date;

import lombok.Data;

public @Data class CryptoListEntity {

	private String  code;                      // "CRIX.UPBIT.KRW-BTC"
	private String  koreanName;                // "비트코인"
	private String  englishName;               // "Bitcoin"
	private String  pair;                      // "BTC/KRW"
	private String  baseCurrencyCode;          // "BTC"
	private String  quoteCurrencyCode;         // "KRW"
	private String  exchange;                  // "UPBIT"
	private String  tradeStatus;               // "ACTIVE"
	private String  marketState;               // "ACTIVE"
	private String  marketStateForIOS;         // "ACTIVE"
	private Boolean isTradingSuspended;        // false
	private Integer baseCurrencyDecimalPlace;  // 8
	private Integer quoteCurrencyDecimalPlace; // 0
	private String  delistingDate;             // "2017-10-11"
	private long    timestampReg;              // 1501485295
	private Date    updTime;                   // 갱신시간
	private Date    creTime;                   // 생성시간
	
	@Override
	public String toString() {
		return "CryptoListEntity ["
				+ "code=" + code + ", "
				+ "koreanName=" + koreanName + ", "
				+ "englishName=" + englishName	+ ", "
				+ "pair=" + pair + ", "
				+ "baseCurrencyCode=" + baseCurrencyCode + ", "
				+ "quoteCurrencyCode="	+ quoteCurrencyCode + ", "
				+ "exchange=" + exchange + ", "
				+ "tradeStatus=" + tradeStatus + ", "
				+ "marketState=" + marketState	+ ", "
				+ "marketStateForIOS=" + marketStateForIOS + ", "
				+ "isTradingSuspended=" + isTradingSuspended + ", "
				+ "baseCurrencyDecimalPlace=" + baseCurrencyDecimalPlace + ", "
				+ "quoteCurrencyDecimalPlace=" + quoteCurrencyDecimalPlace + ", "
				+ "delistingDate=" + delistingDate + ", "
				+ "timestampReg=" + timestampReg
				+ "]";
	}
}