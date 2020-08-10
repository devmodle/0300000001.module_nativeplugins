package dante.distribution.plugin.Global.Utility.Platform;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import dante.distribution.plugin.Global.Define.KGlobalDefine;
import dante.distribution.plugin.Global.Function.GlobalFunc;

//! 디바이스 메세지 전송자
public class CDeviceMsgSender {
	private static CDeviceMsgSender m_pInstance = null;
	
	//! 인스턴스를 반환한다
	public static CDeviceMsgSender getInstance() {
		if(CDeviceMsgSender.m_pInstance == null) {
			CDeviceMsgSender.m_pInstance = new CDeviceMsgSender();
		}
		
		return CDeviceMsgSender.m_pInstance;
	}
	
	//! 디바이스 식별자 반환 메세지를 전송한다
	public void sendGetDeviceIDMsg(String a_oDeviceID) {
		try {
			this.sendDeviceMsg(KGlobalDefine.CMD_GET_DEVICE_ID, a_oDeviceID);
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGlobalDefine.TAG, String.format("CAndroidPlugin.sendGetDeviceIDMsg Exception: %s", oException.getMessage()));
		}
	}
	
	//! 국가 코드 반환 메세지를 전송한다
	public void sendGetCountryCodeMsg(String a_oCountryCode) {
		try {
			this.sendDeviceMsg(KGlobalDefine.CMD_GET_COUNTRY_CODE, a_oCountryCode);
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGlobalDefine.TAG, String.format("CAndroidPlugin.sendGetCountryCodeMsg Exception: %s", oException.getMessage()));
		}
	}
	
	//! 스토어 버전 반환 메세지를 전송한다
	public void sendGetStoreVersionMsg(String a_oVersion, boolean a_bIsSuccess) {
		try {
			String oResult = GlobalFunc.convertBoolToString(a_bIsSuccess);
			
			JSONObject oJSONObj = new JSONObject();
			oJSONObj.put(KGlobalDefine.KEY_DEVICE_MS_VERSION, a_oVersion);
			oJSONObj.put(KGlobalDefine.KEY_DEVICE_MS_RESULT, oResult);
			
			this.sendDeviceMsg(KGlobalDefine.CMD_GET_STORE_VERSION, oJSONObj.toString());
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGlobalDefine.TAG, String.format("CAndroidPlugin.sendGetStoreVersionMsg Exception: %s", oException.getMessage()));
		}
	}
	
	//! 알림 창 출력 메세지를 전송한다
	public void sendShowAlertMsg(boolean a_bIsTrue) {
		try {
			String oResult = GlobalFunc.convertBoolToString(a_bIsTrue);
			this.sendDeviceMsg(KGlobalDefine.CMD_SHOW_ALERT, oResult);
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGlobalDefine.TAG, String.format("CAndroidPlugin.sendShowAlertMsg Exception: %s", oException.getMessage()));
		}
	}
	
	//! 디바이스 메세지를 전송한다
	private void sendDeviceMsg(String a_oCmd, String a_oMsg) throws Exception {
		JSONObject oJSONObj = new JSONObject();
		oJSONObj.put(KGlobalDefine.KEY_CMD, a_oCmd);
		oJSONObj.put(KGlobalDefine.KEY_MSG, a_oMsg);
		
		UnityPlayer.UnitySendMessage(KGlobalDefine.OBJ_NAME_DEVICE_MSG_RECEIVER,
				KGlobalDefine.FUNC_NAME_DEVICE_MSG_HANDLER, oJSONObj.toString());
	}
}
