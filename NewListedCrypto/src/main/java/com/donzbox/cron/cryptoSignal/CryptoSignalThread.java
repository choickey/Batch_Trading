package com.donzbox.cron.cryptoSignal;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.donzbox.domain.cryptoSignal.both.CryptoSignalEntity;
import com.donzbox.service.cryptoSignal.CryptoSignalService;

/****************************************
 *
 * 할일 2 : sudo kill -9 homepage___________ 이거 sh로 만들기
 *
 * @author donz
 *
 */


@Component
public class CryptoSignalThread {

	@Value("${spring.profiles.active}")
	private String springProfilesActive;

	// 이전 데이터
	private double [] candleAccTradePriceBefore;         // 이전 거래금액(누적)
	private String [] candleDateTimeKstBefore;        // 이전 거래시간

	@Autowired
	CryptoSignalService cryptoSignalService;

	// 애플리케이션 시작 후 1초 후에 첫 실행, 그 후 매 10초마다 주기적으로 실행 (SchedulingConfiguration에서 재설정 하므로 이하의 설정값 무시됨)
	@Scheduled(initialDelay = 1000, fixedDelay = 10000)
	public void execute() throws Exception {

		// 로컬에서 돌릴 경우 DB에 저장하는 로직은 수행하지 않음. AWS에서 하고있음
		if(springProfilesActive.contains("local")) {
			// System.out.println("### [01]~[04]");

			final String marketCd = "UPBIT";
			final String [] coinCdArray = {"KRW-BTC","KRW-BCH","KRW-ETH","KRW-ZEC","KRW-XMR","KRW-DASH","KRW-LTC","KRW-NEO","KRW-REP","KRW-BTG","KRW-QTUM","KRW-OMG","KRW-LSK","KRW-ETC","KRW-EOS","KRW-STRAT","KRW-PIVX","KRW-WAVES","KRW-MTL","KRW-ARK","KRW-ICX","KRW-SBD","KRW-KMD","KRW-STEEM","KRW-VTC","KRW-STORJ","KRW-GRS","KRW-XRP","KRW-GNT","KRW-TIX","KRW-POWR","KRW-ARDR","KRW-XEM","KRW-XLM","KRW-EMC2","KRW-MER","KRW-ADA","KRW-SNT","KRW-TRX","KRW-STORM"};
			candleAccTradePriceBefore = new double[coinCdArray.length];
			candleDateTimeKstBefore = new String[coinCdArray.length];

			// [01] CTRIX.UPBIT API에서 가져올 데이터 설정
			for(int i=0; i<coinCdArray.length; i++) {
				final String coinCd = coinCdArray[i];
				insertCryptoSignal(marketCd, coinCd, i);
			}
		}

		// [05] DB에서 이전 360개 데이터 읽어오기
		if(springProfilesActive.contains("local")) {
			// System.out.println("### [05]~[06]");

			final String marketCd = "UPBIT";
			final String [] coinCdArray = {"KRW-BTC","KRW-BCH","KRW-ETH","KRW-ZEC","KRW-XMR","KRW-DASH","KRW-LTC","KRW-NEO","KRW-REP","KRW-BTG","KRW-QTUM","KRW-OMG","KRW-LSK","KRW-ETC","KRW-EOS","KRW-STRAT","KRW-PIVX","KRW-WAVES","KRW-MTL","KRW-ARK","KRW-ICX","KRW-SBD","KRW-KMD","KRW-STEEM","KRW-VTC","KRW-STORJ","KRW-GRS","KRW-XRP","KRW-GNT","KRW-TIX","KRW-POWR","KRW-ARDR","KRW-XEM","KRW-XLM","KRW-EMC2","KRW-MER","KRW-ADA","KRW-SNT","KRW-TRX","KRW-STORM"};
			final CryptoSignalEntity cryptoSignalEntity = new CryptoSignalEntity();
			for(int i=0; i<coinCdArray.length; i++) {
				final String coinCd = coinCdArray[i];
				cryptoSignalEntity.setMarketCd(marketCd);
				cryptoSignalEntity.setCoinCd(coinCd);
				final List<CryptoSignalEntity> list = cryptoSignalService.selectCryptoSignal(cryptoSignalEntity);
				// [06] 퍼센트 계산하여 Console출력
				displayData(list);
			}
		}
	}

	public void displayData(final List<CryptoSignalEntity> list) {

		// 계산 데이터( S=초, M=분, H=시간, I=1, II=2, III=3,...V=5, X=10 )
		double tradePricePerXS = 0;                 // 거래단가 10초 퍼센트
		double tradePricePerIM = 0;                 // 거래단가 1분 퍼센트
		double tradePriceCalIM = 0;                 // 거래단가 1분 계산용
		double tradePricePerXM = 0;                 // 거래단가 10분 퍼센트
		double tradePriceCalXM = 0;                 // 거래단가 10분 계산용
		double tradePricePerIH = 0;                 // 거래단가 1시간 퍼센트
		double tradePriceCalIH = 0;                 // 거래단가 1시간 계산용
		double candleAccTradePricePerXS = 0;        // 거래금액 10초 퍼센트
		double candleAccTradePricePerIM = 0;        // 거래금액 1분 퍼센트
		double candleAccTradePriceCalIM = 0;        // 거래금액 1분 계산용
		double candleAccTradePricePerXM = 0;        // 거래금액 10분 퍼센트
		double candleAccTradePriceCalXM = 0;        // 거래금액 10분 계산용
		double candleAccTradePricePerIH = 0;        // 거래금액 1시간 퍼센트
		double candleAccTradePriceCalIH = 0;        // 거래금액 1시간 계산용

		// 10초간 거래단가 퍼센트
		tradePricePerXS = (list.get(0).getTradePrice() - list.get(1).getTradePrice()) / list.get(1).getTradePrice() * 100;

		// 10초간 거래금액 퍼센트
		candleAccTradePricePerXS = (list.get(0).getCandleAccTradePriceCal() - list.get(1).getCandleAccTradePriceCal()) / list.get(1).getCandleAccTradePriceCal() * 100;

		int i = 1;
		// 각 시간대별 데이터 합
		for(final CryptoSignalEntity c : list) {
			// 1분 데이터 합
			if(i <= 6 && list.size() >= 6) {
				tradePriceCalIM = tradePriceCalIM + c.getTradePrice();
				candleAccTradePriceCalIM = candleAccTradePriceCalIM + c.getCandleAccTradePriceCal();
			}
			// 10분 데이터 합
			if(i <= 60 && list.size() >= 60) {
				tradePriceCalXM = tradePriceCalXM + c.getTradePrice();
				candleAccTradePriceCalXM = candleAccTradePriceCalXM + c.getCandleAccTradePriceCal();
			}
			// 1시간 데이터 합
			if(i <= 360 && list.size() >= 360) {
				tradePriceCalIH = tradePriceCalIH + c.getTradePrice();
				candleAccTradePriceCalIH = candleAccTradePriceCalIH + c.getCandleAccTradePriceCal();
			}
			i++;
		}

		// 각 시간대별 데이터 평균
		tradePriceCalIM          = tradePriceCalIM / 6;
		tradePriceCalXM          = tradePriceCalXM / 60;
		tradePriceCalIH          = tradePriceCalIH / 360;
		candleAccTradePriceCalIM = candleAccTradePriceCalIM / 6;
		candleAccTradePriceCalXM = candleAccTradePriceCalXM / 60;
		candleAccTradePriceCalIH = candleAccTradePriceCalIH / 360;

		// 각 시간대별 데이터 퍼센트
		tradePricePerIM          = (list.get(0).getTradePrice() - tradePriceCalIM) / tradePriceCalIM * 100;
		tradePricePerXM          = (list.get(0).getTradePrice() - tradePriceCalXM) / tradePriceCalXM * 100;
		tradePricePerIH          = (list.get(0).getTradePrice() - tradePriceCalIH) / tradePriceCalIH * 100;
		candleAccTradePricePerIM = (list.get(0).getCandleAccTradePriceCal() - candleAccTradePriceCalIM) / candleAccTradePriceCalIM * 100;
		candleAccTradePricePerXM = (list.get(0).getCandleAccTradePriceCal() - candleAccTradePriceCalXM) / candleAccTradePriceCalXM * 100;
		candleAccTradePricePerIH = (list.get(0).getCandleAccTradePriceCal() - candleAccTradePriceCalIH) / candleAccTradePriceCalIH * 100;


		// 화면에 보여주기
		System.out.printf(Thread.currentThread().getName()
				+ "\n\t   코             인 : \t" + list.get(0).getCoinCd()
				+ "\n\tⓐ 단             가 : \t%f"
				+ "\n\tⓑ 거   래   금   액 : \t%f"
				+ "\n\tⓒ  10초 단    가 ％ : \t%f"
				+ "\n\tⓓ  10초 거래금액 ％ : \t%f"
				+ "\n\tⓔ   1분 단    가 ％ : \t%f"
				+ "\n\tⓕ   1분 거래금액 ％ : \t%f"
				+ "\n\tⓖ  10분 단    가 ％ : \t%f"
				+ "\n\tⓗ  10분 거래금액 ％ : \t%f"
				+ "\n\tⓘ 1시간 단    가 ％ : \t%f"
				+ "\n\tⓙ 1시간 거래금액 ％ : \t%f\n\n",
				list.get(0).getTradePrice(),
				list.get(0).getCandleAccTradePriceCal(),
				tradePricePerXS,
				candleAccTradePricePerXS,
				tradePricePerIM,
				candleAccTradePricePerIM,
				tradePricePerXM,
				candleAccTradePricePerXM,
				tradePricePerIH,
				candleAccTradePricePerIH);
	}

	private void insertCryptoSignal(final String marketCd, final String coinCd, final int i) throws Exception {

		try {
			final URL url = new URL("https://crix-api-endpoint.upbit.com/v1/crix/candles/minutes/1?code=CRIX." + marketCd + "." + coinCd + "&count=2");
			final InputStreamReader isr = new InputStreamReader(url.openConnection().getInputStream(), "UTF-8");

			// [02] 긁어온 데이터 List넣기(2묶음)
			final List<CryptoSignalEntity> list = setData(isr);

			// [03] 긁어온 데이터에서 현재 거래대금 계산
			final CryptoSignalEntity c = calData(list, i);

			// [04] DB에 쓰기
			c.setMarketCd(marketCd);
			c.setCoinCd(coinCd);
			cryptoSignalService.insertCryptoSignal(c);
		}
		// java.io.IOException: Server returned HTTP response code: 502
		catch(final Exception e) {
			// 호출했는데 502 에러가 나면 본 메소드를 한번 더 호출하게 함
			System.out.println("### 재호출 [" + marketCd + ", " + coinCd + ", " + i + "] " + e);
			Thread.sleep(1000);
			insertCryptoSignal(marketCd, coinCd, i);
		}

	}

	public List<CryptoSignalEntity> setData(final InputStreamReader isr) {

		final JSONArray jsonArray = (JSONArray)JSONValue.parse(isr);
		final List<CryptoSignalEntity> list = new ArrayList<>();
		for(int i = 0; i < jsonArray.size(); i++) {
			final JSONObject jsonObject = (JSONObject)jsonArray.get(i);
			final CryptoSignalEntity c = new CryptoSignalEntity();
			c.setCode(                          (String)jsonObject.get("code"));                  // "CRIX.UPBIT.KRW-BTC"
			c.setMarketCd(                     ((String)jsonObject.get("code")).split("\\.")[1]); // "UPBIT"
			c.setCoinCd(                       ((String)jsonObject.get("code")).split("\\.")[2]); // "KRW-BTC"
			c.setCandleDateTime(                (String)jsonObject.get("candleDateTime"));        // "2018-04-24T07:04:00+00:00"
			c.setCandleDateTimeKst(             (String)jsonObject.get("candleDateTimeKst"));     // "2018-04-24T16:04:00+09:00"
			c.setOpeningPrice(                  (double)jsonObject.get("openingPrice"));          // 9981000
			c.setHighPrice(                     (double)jsonObject.get("highPrice"));             // 9984000
			c.setLowPrice(                      (double)jsonObject.get("lowPrice"));              // 9969000
			c.setTradePrice(                    (double)jsonObject.get("tradePrice"));            // 9970000
			c.setCandleAccTradeVolume(          (double)jsonObject.get("candleAccTradeVolume"));  // 26.92197636
			c.setCandleAccTradePrice(           (double)jsonObject.get("candleAccTradePrice"));   // 268571662.78924
			c.setTimestampReg(                    (long)jsonObject.get("timestamp"));             // 1524553475785
			c.setUnit(                            (long)jsonObject.get("unit"));                  // 1

			//			System.out.printf("Original Data [" + i + "] => "
			//			                 + c.getCode() + ", "
			//			                 + c.getCandleDateTimeKst() + "\t"
			//			                 + "%f\t"
			//			                 + "%f"
			//			                 + "\n"
			//			                 , c.getTradePrice()
			//			                 , c.getCandleAccTradePrice()
			//			                 );
			list.add(c);
		}
		return list;
	}

	public CryptoSignalEntity calData(final List<CryptoSignalEntity> list, final int i) throws Exception {

		// 현재 데이터
		final double candleAccTradePriceCurrent   = list.get(0).getCandleAccTradePrice();           // 현재 거래금액(누적)
		final String candleDateTimeKstCurrent     = list.get(0).getCandleDateTimeKst();             // 현재 거래시간
		final double candleAccTradePriceSecond    = list.get(1).getCandleAccTradePrice();           // 현재 두번째 거래금액(완료)
		double candleAccTradePriceCal       = 0;                                              // 계산된 거래금액

		// 현재 거래시간과 이전 거래시간이 같을때, 첫 실행시
		if(candleDateTimeKstCurrent.equals(candleDateTimeKstBefore[i]) || candleDateTimeKstBefore[i] == null) {
			// 현재 거래금액(누적) - 이전 거래금액(누적)
			candleAccTradePriceCal = candleAccTradePriceCurrent - candleAccTradePriceBefore[i];
			// System.out.print("[C] " + candleDateTimeKstCurrent + " == [B] " + candleDateTimeKstBefore[i] + "\t");
		}
		// 현재 거래시간이 이전 거래시간보다 클 때
		else if(candleDateTimeKstCurrent.compareTo(candleDateTimeKstBefore[i]) > 0) {
			// 현재 거래금액(누적) + (현재 두번째 거래금액(완료) - 이전 거래금액(누적))
			candleAccTradePriceCal = candleAccTradePriceCurrent + (candleAccTradePriceSecond - candleAccTradePriceBefore[i]);
			//System.out.print("[C] " + candleDateTimeKstCurrent + " != [B] " + candleDateTimeKstBefore[i] + "\t");
		}
		else {
			// System.out.println(candleDateTimeKstCurrent + " ? " + candleDateTimeKstBefore[i]);
			throw new Exception();
		}

		// 다음 턴에 필요한 현재 데이터
		candleAccTradePriceBefore[i] = candleAccTradePriceCurrent;       // 현재 거래금액(누적)
		candleDateTimeKstBefore[i]   = candleDateTimeKstCurrent;         // 현재 거래시간

		final CryptoSignalEntity c = new CryptoSignalEntity();
		c.setTimestampReg          (list.get(0).getTimestampReg());
		c.setMarketCd              (list.get(0).getMarketCd());
		c.setCoinCd                (list.get(0).getCoinCd());
		c.setOpeningPrice          (list.get(0).getOpeningPrice());
		c.setHighPrice             (list.get(0).getHighPrice());
		c.setLowPrice              (list.get(0).getLowPrice());
		c.setCandleDateTimeKst     (list.get(0).getCandleDateTimeKst());
		c.setTradePrice            (list.get(0).getTradePrice());
		//c.setTradePricePer         (tradePricePer);
		c.setCandleAccTradeVolume  (list.get(0).getCandleAccTradeVolume());
		c.setCandleAccTradePrice   (list.get(0).getCandleAccTradePrice());
		//c.setCandleAccTradePricePer(candleAccTradePricePer);
		c.setCandleAccTradePriceCal(candleAccTradePriceCal);

		return c;
	}
}
