package com.donzbox.cron.cryptoSignal;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.donzbox.domain.cryptoSignal.both.CryptoBotEntity;
import com.donzbox.domain.cryptoSignal.both.CryptoListEntity;
import com.donzbox.service.cryptoSignal.CryptoListService;

/****************************************
 * 
 * 할일 2 : sudo kill -9 homepage___________ 이거 sh로 만들기
 * 
 * @author donz
 *
 */


@Component
public class CryptoListThread {

    @Value("${spring.profiles.active}")
    private String springProfilesActive;
    
    private int timeFlag = 0;
    
	@Autowired
	CryptoListService cryptoListService;
	
	// 애플리케이션 시작 후 1초 후에 첫 실행, 그 후 매 5초마다 주기적으로 실행 (SchedulingConfiguration에서 재설정 하므로 이하의 설정값 무시됨)
	@Scheduled(initialDelay = 1000, fixedDelay = 5000)
	public void execute() throws Exception {
		
		// [01] 데이터 긁어오기 (대략 400묶음)
		JSONArray jsonArray = crawler("https://s3.ap-northeast-2.amazonaws.com/crix-production/crix_master");
	    
		// [02] 긁어온 데이터 List넣기(현재 데이터)
		List<CryptoListEntity> cList = setData(jsonArray);
		
		// [03] DB에서 불러온 데이터(과거 데이터)
		// Timestamp가 최근인 순으로 SELECT
		List<CryptoListEntity> pList = cryptoListService.selectCryptoList();
		
		// [04] 현재 데이터와 과거 데이터 비교해서 새로 추가된 데이터와 마켓상태가 바뀐 데이터만 리스트에 담는다
		cList = listCompare(cList, pList);
		
		// [05] DB에 쓰기
		// 추가된 데이터가 존재 하면 INSERT, 마켓상태가 바뀌었으면 UPDATE
		for(CryptoListEntity c : cList) {
			cryptoListService.mergeCryptoList(c);
		}
		
		// [06] DB에서 불러온 데이터(과거 데이터)
		// Timestamp가 최근인 순으로 SELECT
		pList = cryptoListService.selectCryptoList();
		
		// [07] 콘솔창(화면)에 표시
		displayList(pList);
		
	}
	
	private JSONArray crawler(String domain) throws Exception {

		TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {}
			}
		};
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		Elements body = null;
		try {
			Document doc = Jsoup.connect(domain).ignoreContentType(true).get();
			body = doc.select("body");
			// org.json 라이브러리를 사용해 결과를 파싱한다.
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray(body.text());
	}
    
	public List<CryptoListEntity> setData(JSONArray jsonArray) throws JSONException {
		
		List<CryptoListEntity> list = new ArrayList<>();
		if(jsonArray != null) {
			for(int i = 0; i < jsonArray.length(); i++) {
				
				JSONObject jsonObject = (JSONObject)jsonArray.get(i);
				if(jsonObject != null) {
					CryptoListEntity c = new CryptoListEntity();
					c.setCode(                         (String)jsonObject.get("code"));                               // "CRIX.UPBIT.KRW-BTC"
					c.setKoreanName(                   (String)jsonObject.get("koreanName"));                         // "비트코인"
					c.setEnglishName(                  (String)jsonObject.get("englishName"));                        // "Bitcoin"
					c.setPair(                         (String)jsonObject.get("pair"));                               // "BTC/KRW"
					c.setBaseCurrencyCode(             (String)jsonObject.get("baseCurrencyCode"));                   // "BTC"
					c.setQuoteCurrencyCode(            (String)jsonObject.get("quoteCurrencyCode"));                  // "KRW"
					c.setExchange(                     (String)jsonObject.get("exchange"));                           // "UPBIT"
					c.setTradeStatus(                  (String)jsonObject.get("tradeStatus"));                        // "ACTIVE"
					c.setMarketState(                  (String)jsonObject.get("marketState"));                        // "ACTIVE"
					c.setMarketStateForIOS(            (String)jsonObject.get("marketStateForIOS"));                  // "ACTIVE"
					c.setIsTradingSuspended(          (Boolean)jsonObject.get("isTradingSuspended"));                 // false
					c.setBaseCurrencyDecimalPlace(    (Integer)jsonObject.get("baseCurrencyDecimalPlace"));           // 8
					c.setQuoteCurrencyDecimalPlace(   (Integer)jsonObject.get("quoteCurrencyDecimalPlace"));          // 0
					try {
						c.setDelistingDate(            (String)jsonObject.get("delistingDate"));                      // "2017-10-17"
					} catch(Exception e) {
						c.setDelistingDate(null);                                                                     // key값 없음
					}
					try {
						c.setTimestampReg(Long.valueOf(String.valueOf(jsonObject.get("timestamp")).substring(0,10))); // 1501485295 + 000 <-- 3자리삭제
					} catch(Exception e) {
						c.setTimestampReg(0);                                                                         // key값 없음
					}
					list.add(c);
				}
			}
		}
		return list;
	}
	
	public List<CryptoListEntity> listCompare(List<CryptoListEntity> cList, List<CryptoListEntity> pList) throws Exception {
		
		// 과거 데이터와 현재 데이터 비교
		for(int i = 0; i < pList.size(); i++) {
			for(int j = 0; j < cList.size(); j++) {
				// 이미 존재하는 코인 데이터는 삭제 (현제 데이터 중 새로 추가된 데이터만 남김)
				// AND 마켓상태의 변화가 없을 것 (마켓상태가 변하면 알림    예:골렘)
				if(pList.get(i).getCode().equals(cList.get(j).getCode()) && pList.get(i).getMarketState().equals(cList.get(j).getMarketState())) {
					cList.remove(j);
					break;
				}
			}
		}
		return cList;
	}
	
	
	public void displayList(List<CryptoListEntity> pList) throws Exception {
		
		// 현재시간
		long time = System.currentTimeMillis();
		// 유닉스 시간 --> 한국사람 시간으로 변경
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
		
		// 동작 중인지 멈췄는지 판단용(현재시간 표시)
		timeFlag++;
		if(timeFlag > 11) {
			System.out.println("현재  시간 : " + sdf.format(new Date(time)));
			timeFlag = 0;
		}
		

	    // 본 프로그램 실행시간 DB에 등록
		CryptoBotEntity cryptoBotEntity = new CryptoBotEntity();
		cryptoBotEntity.setBotCd("CryptoList");
		cryptoBotEntity.setBotNm("기습상장 감지봇");
		cryptoListService.insertBotRuntime(cryptoBotEntity);
		
		// 알람 플래그
		int alarmFlag = 0;
		// 현재 데이터 표시 (새로운 데이터)
		for(CryptoListEntity c : pList) {
			// 추가되거나 변경된 데이터는 20초간 알람(CRE_TIME, UPD_TIME를 현재시간과 비교)
			if((time / 1000) - (c.getCreTime().getTime() / 1000) < 20 || (c.getUpdTime() != null && (time / 1000) - (c.getUpdTime().getTime() / 1000) < 20)) {
				alarmFlag++;
				System.out.println("\n현재  시간 : " + sdf.format(new Date(time)));
				System.out.println("타임스탬프 : " + sdf.format(new Date(c.getTimestampReg()*1000L)) +
						           "    거래소 : " + c.getExchange() +
								   "    마켓 : " + c.getQuoteCurrencyCode() +
				                   "    종목 : " + c.getKoreanName() + " ( " + c.getBaseCurrencyCode() + " )" +
				                   "    마켓상태 : " + c.getMarketState() +
				                   "\n");
			}
		}
		
		// 추가되거나 변경된 데이터가 있으면 알람
		if(alarmFlag > 0) {
			if("!awsLinux".equals(springProfilesActive)) {
				File file = new File("../homepage-batch/src/main/resources/wav/wow.wav");
				if(!file.exists()) file = new File("../../wav/wow.wav");
		        try {
		            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
		            Clip clip = AudioSystem.getClip();
		            clip.open(stream);
		            clip.start();
		        } catch(Exception e) {
		            e.printStackTrace();
		        }
			}
		}
	}
	
//	public void displayList(List<CryptoListEntity> nList, List<CryptoListEntity> pList) throws Exception {
//		
//		// 유닉스 시간 --> 한국사람 시간으로 변경
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
//		
//		// 현재 데이터에 새로운 데이터가 있으면 알람 재생
//		if(nList.size() > 0) {
//			File file = new File("../homepage-batch/src/main/resources/wav/wow.wav");
//	        try {
//	            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
//	            Clip clip = AudioSystem.getClip();
//	            clip.open(stream);
//	            clip.start();
//	        } catch(Exception e) {
//	            e.printStackTrace();
//	        }
//		} else {
//			// 현재 데이터에 새로운 데이터가 없으면 현재시간 표시
//			// 동작 중인지 멈췄는지 판단용
//			System.out.println("현재  시간 : " + sdf.format(new Date(System.currentTimeMillis())));
//		}
//		
//		// 현재 데이터 표시 (새로운 데이터)
//		for(CryptoListEntity c : nList) {
//			System.out.println("\n현재  시간 : " + sdf.format(new Date(System.currentTimeMillis())) +
//						  	   "\n타임스탬프 : " + sdf.format(new Date(c.getTimestampReg()*1000L)) +
//					           "    거래소 : " + c.getExchange() +
//							   "    마켓 : " + c.getQuoteCurrencyCode() +
//			                   "    종목 : " + c.getKoreanName() + " ( " + c.getBaseCurrencyCode() + " )" +
//			                   "    마켓상태 : " + c.getMarketState() +
//			                   "\n");
//		}
//	}
}
