package com.donzbox.service.cryptoSignal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donzbox.domain.cryptoSignal.both.CryptoSignalEntity;
import com.donzbox.mapper.cryptoSignal.CryptoSignalMapper;

@Service
public class CryptoSignalServiceImpl implements CryptoSignalService {

	// private static final Logger logger = LoggerFactory.getLogger(CryptoSignalServiceImpl.class);

	@Autowired
	CryptoSignalMapper cryptoSignalMapper;

	/*----------------------------------------------------------
	 * 10초간 거래내역
	 *---------------------------------------------------------*/
	// 10초간 거래내역 DB 조회
	public List<CryptoSignalEntity> selectCryptoSignal(CryptoSignalEntity cryptoSignalEntity) {
		return cryptoSignalMapper.selectCryptoSignal(cryptoSignalEntity);
	}

	// 10초간 거래내역 DB 입력
	public void insertCryptoSignal(CryptoSignalEntity cryptoSignalEntity) {
		cryptoSignalMapper.insertCryptoSignal(cryptoSignalEntity);
	}
}