package com.donzbox.service.cryptoSignal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donzbox.domain.cryptoSignal.both.CryptoBotEntity;
import com.donzbox.domain.cryptoSignal.both.CryptoListEntity;
import com.donzbox.mapper.cryptoSignal.CryptoListMapper;

@Service
public class CryptoListServiceImpl implements CryptoListService {

	// private static final Logger logger = LoggerFactory.getLogger(CryptoListServiceImpl.class);

	@Autowired
	CryptoListMapper cryptoListMapper;

	/*----------------------------------------------------------
	 * 신규코인
	 *---------------------------------------------------------*/
	// 신규코인 DB 조회
	public List<CryptoListEntity> selectCryptoList() {
		return cryptoListMapper.selectCryptoList();
	}

	// 신규코인 DB 등록
	public void mergeCryptoList(CryptoListEntity cryptoListEntity) {
		cryptoListMapper.mergeCryptoList(cryptoListEntity);
	}
	
    // 본 프로그램 실행시간 DB에 등록
    public void insertBotRuntime(CryptoBotEntity cryptoBotEntity) {
		cryptoListMapper.insertBotRuntime(cryptoBotEntity);
	}
}