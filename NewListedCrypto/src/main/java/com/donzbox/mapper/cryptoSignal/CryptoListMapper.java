package com.donzbox.mapper.cryptoSignal;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.donzbox.domain.cryptoSignal.both.CryptoBotEntity;
import com.donzbox.domain.cryptoSignal.both.CryptoListEntity;

@Mapper
public interface CryptoListMapper {

	/*----------------------------------------------------------
	 * 신규코인
	 *---------------------------------------------------------*/
	// 신규코인 DB 조회
    public List<CryptoListEntity> selectCryptoList();

    // 신규코인 DB 등록
    public void mergeCryptoList(CryptoListEntity cryptoListEntity);
    
    // 본 프로그램 실행시간 DB에 등록
    public void insertBotRuntime(CryptoBotEntity cryptoBotEntity);
}