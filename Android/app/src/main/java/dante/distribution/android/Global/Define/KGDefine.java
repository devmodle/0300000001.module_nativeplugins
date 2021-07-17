package dante.distribution.android.Global.Define;

import android.os.Build;

//! 전역 상수
public abstract class KGDefine {
	// 기타 {
	public static final String TAG = "Plugin";
	public static final String EMPTY_STR = "";
	public static final String INVALID_ANDROID_ID = "9774d56d682e549c";
	
	public static final String MAIL_DATA = "mailto:";
	public static final String MAIL_TYPE = "message/rfc822";
	// 기타 }
	
	// 값 {
	public static final int VAL_0_INT = 0;
	public static final int VAL_1_INT = 1;
	public static final int VAL_2_INT = 2;
	public static final int VAL_3_INT = 3;
	public static final int VAL_4_INT = 4;
	public static final int VAL_5_INT = 5;
	public static final int VAL_6_INT = 6;
	public static final int VAL_7_INT = 7;
	public static final int VAL_8_INT = 8;
	public static final int VAL_9_INT = 9;
	
	public static final float VAL_0_FLT = 0.0f;
	public static final float VAL_1_FLT = 1.0f;
	public static final float VAL_2_FLT = 2.0f;
	public static final float VAL_3_FLT = 3.0f;
	public static final float VAL_4_FLT = 4.0f;
	public static final float VAL_5_FLT = 5.0f;
	public static final float VAL_6_FLT = 6.0f;
	public static final float VAL_7_FLT = 7.0f;
	public static final float VAL_8_FLT = 8.0f;
	public static final float VAL_9_FLT = 9.0f;
	// 값 }
	
	// 유닛
	public static final int UNIT_SEC_TO_MILLISEC = 1000;
	public static final int UNIT_NORM_VAL_TO_BYTE = 255;
	
	// 버전
	public static final int MIN_VER_FEEDBACK_GENERATOR = Build.VERSION_CODES.O;
	
	// 비율
	public static final float SCALE_PROGRESS_BAR = 0.25f;
	public static final float OFFSET_SCALE_PROGRESS_BAR = 0.05f;
	
	// 결과
	public static final String RESULT_TRUE = "True";
	public static final String RESULT_FALSE = "False";
	
	// 명령어 {
	public static final String CMD_GET_DEVICE_ID = "GetDeviceID";
	public static final String CMD_GET_COUNTRY_CODE = "GetCountryCode";
	
	public static final String CMD_SHOW_ALERT = "ShowAlert";
	public static final String CMD_SHOW_TOAST = "ShowToast";
	
	public static final String CMD_MAIL = "Mail";
	public static final String CMD_VIBRATE = "Vibrate";
	public static final String CMD_INDICATOR = "Indicator";
	// 명령어 }
	
	// 식별자 {
	public static final String KEY_CMD = "Cmd";
	public static final String KEY_MSG = "Msg";

	public static final String KEY_ALERT_TITLE = "Title";
	public static final String KEY_ALERT_MSG = "Msg";
	public static final String KEY_ALERT_OK_BTN_TEXT = "OKBtnText";
	public static final String KEY_ALERT_CANCEL_BTN_TEXT = "CancelBtnText";
	
	public static final String KEY_MAIL_RECIPIENT = "Recipient";
	public static final String KEY_MAIL_TITLE = "Title";
	public static final String KEY_MAIL_MSG = "Msg";
	
	public static final String KEY_VIBRATE_DURATION = "Duration";
	public static final String KEY_VIBRATE_INTENSITY = "Intensity";
	// 식별자 }
	
	// 이름
	public static final String OBJ_N_DEVICE_MSG_RECEIVER = "CDeviceMsgReceiver";
	public static final String FUNC_N_DEVICE_MSG_HANDLER = "HandleDeviceMsg";
}
