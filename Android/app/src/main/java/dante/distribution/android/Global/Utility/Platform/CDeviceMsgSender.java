package dante.distribution.android.Global.Utility.Platform;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import dante.distribution.android.Global.Define.KGDefine;
import dante.distribution.android.Global.Function.GFunc;

//! 디바이스 메세지 전송자
public class CDeviceMsgSender {
	private static CDeviceMsgSender m_oInstance = null;
	
	//! 생성자
	private CDeviceMsgSender() {
		// Do Nothing
	}
	
	//! 인스턴스를 반환한다
	public static CDeviceMsgSender getInstance() {
		// 인스턴스가 없을 경우
		if(CDeviceMsgSender.m_oInstance == null) {
			CDeviceMsgSender.m_oInstance = new CDeviceMsgSender();
		}
		
		return CDeviceMsgSender.m_oInstance;
	}
	
	//! 디바이스 식별자 반환 메세지를 전송한다
	public void sendGetDeviceIDMsg(String a_oDeviceID) {
		try {
			this.sendDeviceMsg(KGDefine.CMD_GET_DEVICE_ID, a_oDeviceID);
		} catch(Exception oException) {
			Log.e(KGDefine.TAG, String.format("CDeviceMsgSender.sendGetDeviceIDMsg Exception: %s, %s",
					a_oDeviceID, oException.getMessage()));
			
			oException.printStackTrace();
		}
	}
	
	//! 국가 코드 반환 메세지를 전송한다
	public void sendGetCountryCodeMsg(String a_oCountryCode) {
		try {
			this.sendDeviceMsg(KGDefine.CMD_GET_COUNTRY_CODE, a_oCountryCode);
		} catch(Exception oException) {
			Log.e(KGDefine.TAG, String.format("CDeviceMsgSender.sendGetCountryCodeMsg Exception: %s, %s",
					a_oCountryCode, oException.getMessage()));
			
			oException.printStackTrace();
		}
	}
	
	//! 스토어 버전 반환 메세지를 전송한다
	public void sendGetStoreVersionMsg(String a_oVersion, boolean a_bIsSuccess) {
		String oResult = GFunc.convertBoolToString(a_bIsSuccess);
		
		try {
			JSONObject oJSONObj = new JSONObject();
			oJSONObj.put(KGDefine.KEY_DEVICE_MS_VERSION, a_oVersion);
			oJSONObj.put(KGDefine.KEY_DEVICE_MS_RESULT, oResult);
			
			this.sendDeviceMsg(KGDefine.CMD_GET_STORE_VERSION, oJSONObj.toString());
		} catch(Exception oException) {
			Log.e(KGDefine.TAG, String.format("CDeviceMsgSender.sendGetStoreVersionMsg Exception: %s, %s, %s",
					a_oVersion, oResult, oException.getMessage()));
			
			oException.printStackTrace();
		}
	}
	
	//! 경고 창 출력 메세지를 전송한다
	public void sendShowAlertMsg(boolean a_bIsOK) {
		String oResult = GFunc.convertBoolToString(a_bIsOK);
		
		try {
			this.sendDeviceMsg(KGDefine.CMD_SHOW_ALERT, oResult);
		} catch(Exception oException) {
			Log.e(KGDefine.TAG, String.format("CDeviceMsgSender.sendShowAlertMsg Exception: %s, %s",
					oResult, oException.getMessage()));
			
			oException.printStackTrace();
		}
	}
	
	//! 광고 초기화 메세지를 전송한다
	public void sendInitAdsMsg(boolean a_bIsSuccess) {
		String oResult = GFunc.convertBoolToString(a_bIsSuccess);
		
		try {
			this.sendDeviceMsg(KGDefine.CMD_INIT_ADS, oResult);
		} catch(Exception oException) {
			Log.e(KGDefine.TAG, String.format("CDeviceMsgSender.sendInitAdsMsg Exception: %s, %s",
					oResult, oException.getMessage()));
			
			oException.printStackTrace();
		}
	}
	
	//! 재개 광고 로드 메세지를 전송한다
	public void sendLoadResumeAdsMsg(boolean a_bIsSuccess) {
		String oResult = GFunc.convertBoolToString(a_bIsSuccess);
		
		try {
			this.sendDeviceMsg(KGDefine.CMD_LOAD_RESUME_ADS, oResult);
		} catch(Exception oException) {
			Log.e(KGDefine.TAG, String.format("CDeviceMsgSender.sendLoadResumeAdsMsg Exception: %s, %s",
					oResult, oException.getMessage()));
			
			oException.printStackTrace();
		}
	}
	
	//! 재개 광고 출력 메세지를 전송한다
	public void sendShowResumeAdsMsg(boolean a_bIsSuccess) {
		String oResult = GFunc.convertBoolToString(a_bIsSuccess);
		
		try {
			this.sendDeviceMsg(KGDefine.CMD_SHOW_RESUME_ADS, oResult);
		} catch(Exception oException) {
			Log.e(KGDefine.TAG, String.format("CDeviceMsgSender.sendShowResumeAdsMsg Exception: %s, %s",
					oResult, oException.getMessage()));
			
			oException.printStackTrace();
		}
	}
	
	//! 디바이스 메세지를 전송한다
	private void sendDeviceMsg(String a_oCmd, String a_oMsg) throws Exception {
		JSONObject oJSONObj = new JSONObject();
		oJSONObj.put(KGDefine.KEY_CMD, a_oCmd);
		oJSONObj.put(KGDefine.KEY_MSG, a_oMsg);
		
		UnityPlayer.UnitySendMessage(KGDefine.OBJ_NAME_DEVICE_MSG_RECEIVER,
				KGDefine.FUNC_NAME_DEVICE_MSG_HANDLER,
				oJSONObj.toString());
	}
}
