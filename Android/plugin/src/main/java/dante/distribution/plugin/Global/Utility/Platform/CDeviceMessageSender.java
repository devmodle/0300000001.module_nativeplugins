package dante.distribution.plugin.Global.Utility.Platform;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import dante.distribution.plugin.Global.Define.KGlobalDefine;
import dante.distribution.plugin.Global.Function.GlobalFunc;

//! 디바이스 메세지 전송자
public class CDeviceMessageSender {
	private static CDeviceMessageSender m_pInstance = null;
	
	//! 인스턴스를 반환한다
	public static CDeviceMessageSender getInstance() {
		if(CDeviceMessageSender.m_pInstance == null) {
			CDeviceMessageSender.m_pInstance = new CDeviceMessageSender();
		}
		
		return CDeviceMessageSender.m_pInstance;
	}
	
	//! 디바이스 식별자 반환 메세지를 전송한다
	public void sendGetDeviceIDMessage(String a_oDeviceID) {
		try {
			this.sendMessage(KGlobalDefine.COMMAND_GET_DEVICE_ID, a_oDeviceID);
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGlobalDefine.TAG, String.format("CAndroidPlugin.sendGetDeviceIDMessage Exception: %s", oException.getLocalizedMessage()));
		}
	}
	
	//! 국가 코드 반환 메세지를 전송한다
	public void sendGetCountryCodeMessage(String a_oCountryCode) {
		try {
			this.sendMessage(KGlobalDefine.COMMAND_GET_COUNTRY_CODE, a_oCountryCode);
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGlobalDefine.TAG, String.format("CAndroidPlugin.sendGetCountryCodeMessage Exception: %s", oException.getLocalizedMessage()));
		}
	}
	
	//! 스토어 버전 반환 메세지를 전송한다
	public void sendGetStoreVersionMessage(String a_oVersion, boolean a_bIsSuccess) {
		try {
			String oResult = GlobalFunc.convertBoolToString(a_bIsSuccess);
			
			JSONObject oJSONObject = new JSONObject();
			oJSONObject.put(KGlobalDefine.KEY_DEVICE_MS_VERSION, a_oVersion);
			oJSONObject.put(KGlobalDefine.KEY_DEVICE_MS_RESULT, oResult);
			
			this.sendMessage(KGlobalDefine.COMMAND_GET_STORE_VERSION, oJSONObject.toString());
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGlobalDefine.TAG, String.format("CAndroidPlugin.sendGetStoreVersionMessage Exception: %s", oException.getLocalizedMessage()));
		}
	}
	
	//! 알림 창 출력 메세지를 전송한다
	public void sendShowAlertMessage(boolean a_bIsTrue) {
		try {
			String oResult = GlobalFunc.convertBoolToString(a_bIsTrue);
			this.sendMessage(KGlobalDefine.COMMAND_SHOW_ALERT, oResult);
		} catch(Exception oException) {
			oException.printStackTrace();
			Log.e(KGlobalDefine.TAG, String.format("CAndroidPlugin.sendShowAlertMessage Exception: %s", oException.getLocalizedMessage()));
		}
	}
	
	//! 메세지를 전송한다
	private void sendMessage(String a_oCommand, String a_oMessage) throws Exception {
		JSONObject oJSONObject = new JSONObject();
		oJSONObject.put(KGlobalDefine.KEY_COMMAND, a_oCommand);
		oJSONObject.put(KGlobalDefine.KEY_MESSAGE, a_oMessage);
		
		UnityPlayer.UnitySendMessage(KGlobalDefine.OBJ_NAME_DEVICE_MESSAGE_RECEIVER,
				KGlobalDefine.FUNC_NAME_DEVICE_MESSAGE_HANDLER, oJSONObject.toString());
	}
}
