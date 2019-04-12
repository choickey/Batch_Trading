package com.donzbox.mapper.cryptoSignal;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.donzbox.domain.cryptoSignal.both.CryptoSignalEntity;

@Mapper
public interface CryptoSignalMapper {

	/*----------------------------------------------------------
	 * 10초간 거래내역
	 *---------------------------------------------------------*/
	// 10초간 거래내역 DB 조회
    public List<CryptoSignalEntity> selectCryptoSignal(CryptoSignalEntity cryptoSignalEntity);
	
	// 10초간 거래내역 DB 입력
    public void insertCryptoSignal(CryptoSignalEntity cryptoSignalEntity);
}