package com.donzbox.service.cryptoSignal;

import java.util.List;

import com.donzbox.domain.cryptoSignal.both.CryptoSignalEntity;

public interface CryptoSignalService {

	/*----------------------------------------------------------
	 * 10초간 거래내역
	 *---------------------------------------------------------*/
    // 10초간 거래내역 DB 조회
    public List<CryptoSignalEntity> selectCryptoSignal(CryptoSignalEntity cryptoSignalEntity);

    // 10초간 거래내역 DB 입력
    public void insertCryptoSignal(CryptoSignalEntity cryptoSignalEntity);
}
