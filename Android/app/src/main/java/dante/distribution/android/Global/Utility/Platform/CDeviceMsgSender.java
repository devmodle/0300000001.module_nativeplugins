package dante.distribution.android.Global.Utility.Platform;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import dante.distribution.android.Global.Define.KGDefine;
import dante.distribution.android.Global.Function.GFunc;

//! 디바이스 메세지 전송자
public class CDeviceMsgSender {
	private static CDeviceMsgSender m_oInst = null;
	
	//! 생성자
	private CDeviceMsgSender() {
		// Do Nothing
	}
	
	//! 인스턴스를 반환한다
	public static CDeviceMsgSender getInst() {
		// 인스턴스가 없을 경우
		if(CDeviceMsgSender.m_oInst == null) {
			CDeviceMsgSender.m_oInst = new CDeviceMsgSender();
		}
		
		return CDeviceMsgSender.m_oInst;
	}
	
	//! 디바이스 식별자 반환 메세지를 전송한다
	public void sendGetDeviceIDMsg(String a_oDeviceID) {
		try {
			this.sendDeviceMsg(KGDefine.CMD_GET_DEVICE_ID, a_oDeviceID);
		} catch(Exception oException) {
			Log.e(KGDefine.TAG, String.format("CDeviceMsgSender.sendGetDeviceIDMsg Exception: %s, %s", a_oDeviceID, oException.getMessage()));
			oException.printStackTrace();
		}
	}
	
	//! 국가 코드 반환 메세지를 전송한다
	public void sendGetCountryCodeMsg(String a_oCountryCode) {
		try {
			this.sendDeviceMsg(KGDefine.CMD_GET_COUNTRY_CODE, a_oCountryCode);
		} catch(Exception oException) {
			Log.e(KGDefine.TAG, String.format("CDeviceMsgSender.sendGetCountryCodeMsg Exception: %s, %s", a_oCountryCode, oException.getMessage()));
			oException.printStackTrace();
		}
	}
	
	//! 경고 창 출력 메세지를 전송한다
	public void sendShowAlertMsg(boolean a_bIsOK) {
		String oResult = GFunc.convertBoolToStr(a_bIsOK);
		
		try {
			this.sendDeviceMsg(KGDefine.CMD_SHOW_ALERT, oResult);
		} catch(Exception oException) {
			Log.e(KGDefine.TAG, String.format("CDeviceMsgSender.sendShowAlertMsg Exception: %s, %s", oResult, oException.getMessage()));
			oException.printStackTrace();
		}
	}
	
	//! 디바이스 메세지를 전송한다
	private void sendDeviceMsg(String a_oCmd, String a_oMsg) throws Exception {
		JSONObject oJSONObj = new JSONObject();
		oJSONObj.put(KGDefine.KEY_CMD, a_oCmd);
		oJSONObj.put(KGDefine.KEY_MSG, a_oMsg);
		
		UnityPlayer.UnitySendMessage(KGDefine.OBJ_N_DEVICE_MSG_RECEIVER, KGDefine.FUNC_N_DEVICE_MSG_HANDLER, oJSONObj.toString());
	}
}
