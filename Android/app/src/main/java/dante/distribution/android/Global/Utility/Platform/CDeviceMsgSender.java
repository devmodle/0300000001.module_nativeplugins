package dante.distribution.android.Global.Utility.Platform;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import dante.distribution.android.Global.Define.KGDefine;
import dante.distribution.android.Global.Function.GlobalFunc;

//! 디바이스 메세지 전송자
public class CDeviceMsgSender {
	private static CDeviceMsgSender m_oInstance = null;
	
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
			oException.printStackTrace();
			Log.e(KGDefine.TAG, String.format("CAndroidPlugin.sendGetDeviceIDMsg Exception: %s", oException.getMessage()));
		}
	}
	
	//! 국가 코드 반환 메세지를 전송한다
	public void sendGetCountryCodeMsg(String a_oCountryCode) {
		try {
			this.sendDeviceMsg(KGDefine.CMD_GET_COUNTRY_CODE, a_oCountryCode);
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGDefine.TAG, String.format("CAndroidPlugin.sendGetCountryCodeMsg Exception: %s", oException.getMessage()));
		}
	}
	
	//! 스토어 버전 반환 메세지를 전송한다
	public void sendGetStoreVersionMsg(String a_oVersion, boolean a_bIsSuccess) {
		try {
			String oResult = GlobalFunc.convertBoolToString(a_bIsSuccess);
			
			JSONObject oJSONObj = new JSONObject();
			oJSONObj.put(KGDefine.KEY_DEVICE_MS_VERSION, a_oVersion);
			oJSONObj.put(KGDefine.KEY_DEVICE_MS_RESULT, oResult);
			
			this.sendDeviceMsg(KGDefine.CMD_GET_STORE_VERSION, oJSONObj.toString());
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGDefine.TAG, String.format("CAndroidPlugin.sendGetStoreVersionMsg Exception: %s", oException.getMessage()));
		}
	}
	
	//! 경고 창 출력 메세지를 전송한다
	public void sendShowAlertMsg(boolean a_bIsTrue) {
		try {
			String oResult = GlobalFunc.convertBoolToString(a_bIsTrue);
			this.sendDeviceMsg(KGDefine.CMD_SHOW_ALERT, oResult);
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGDefine.TAG, String.format("CAndroidPlugin.sendShowAlertMsg Exception: %s", oException.getMessage()));
		}
	}
	
	//! 디바이스 메세지를 전송한다
	private void sendDeviceMsg(String a_oCmd, String a_oMsg) throws Exception {
		JSONObject oJSONObj = new JSONObject();
		oJSONObj.put(KGDefine.KEY_CMD, a_oCmd);
		oJSONObj.put(KGDefine.KEY_MSG, a_oMsg);
		
		UnityPlayer.UnitySendMessage(KGDefine.OBJ_NAME_DEVICE_MSG_RECEIVER,
				KGDefine.FUNC_NAME_DEVICE_MSG_HANDLER, oJSONObj.toString());
	}
}
