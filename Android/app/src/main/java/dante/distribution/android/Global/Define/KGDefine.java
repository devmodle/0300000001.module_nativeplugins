package dante.distribution.android.Global.Define;

import android.os.Build;

//! 전역 상수
public abstract class KGDefine {
	// 기타
	public static final String TAG = "Plugin";
	public static final String EMPTY_STRING = "";
	public static final String INVALID_ANDROID_ID = "9774d56d682e549c";
	
	// 값
	public static final int VALUE_INT_0 = 0;
	public static final int VALUE_INT_1 = 1;
	
	// 유닛
	public static final int UNIT_SEC_TO_MILLISEC = 1000;
	public static final int UNIT_NORM_VALUE_TO_BYTE = 255;
	
	// 버전
	public static final int MIN_VERSION_APP_UPDATE_MANAGER = Build.VERSION_CODES.LOLLIPOP;
	public static final int MIN_VERSION_FEEDBACK_GENERATOR = Build.VERSION_CODES.O;
	
	// 비율
	public static final float SCALE_PROGRESS_BAR = 0.25f;
	public static final float SCALE_PROGRESS_BAR_OFFSET = 0.05f;
	
	// 결과
	public static final String RESULT_TRUE = "True";
	public static final String RESULT_FALSE = "False";
	
	// 빌드 모드
	public static final String BUILD_MODE_DEBUG = "Debug";
	public static final String BUILD_MODE_RELEASE = "Release";
	
	// 명령어 {
	public static final String CMD_INIT = "Init";
	
	public static final String CMD_GET_DEVICE_ID = "GetDeviceID";
	public static final String CMD_GET_COUNTRY_CODE = "GetCountryCode";
	public static final String CMD_GET_STORE_VERSION = "GetStoreVersion";
	
	public static final String CMD_SHOW_ALERT = "ShowAlert";
	public static final String CMD_SHOW_TOAST = "ShowToast";
	
	public static final String CMD_VIBRATE = "Vibrate";
	public static final String CMD_TRACKING = "Tracking";
	public static final String CMD_ACTIVITY_INDICATOR = "ActivityIndicator";
	
	public static final String CMD_INIT_ADS = "InitAds";
	public static final String CMD_LOAD_RESUME_ADS = "LoadResumeAds";
	public static final String CMD_SHOW_RESUME_ADS = "ShowResumeAds";
	// 명령어 }
	
	// 키 {
	public static final String KEY_CMD = "Cmd";
	public static final String KEY_MSG = "Msg";

	public static final String KEY_BUILD_MODE = "BuildMode";
	public static final String KEY_ORIENTATION = "Orientation";
	
	public static final String KEY_VERSION = "Version";
	
	public static final String KEY_ALERT_TITLE = "Title";
	public static final String KEY_ALERT_MSG = "Msg";
	public static final String KEY_ALERT_OK_BTN_TEXT = "OKBtnText";
	public static final String KEY_ALERT_CANCEL_BTN_TEXT = "CancelBtnText";
	
	public static final String KEY_VIBRATE_DURATION = "Duration";
	public static final String KEY_VIBRATE_INTENSITY = "Intensity";
	
	public static final String KEY_TRACKING_NAME = "Name";
	public static final String KEY_TRACKING_DATAS = "Datas";
	public static final String KEY_TRACKING_IS_START = "IsStart";
	
	public static final String KEY_ADMOB_IDS = "AdmobIDs";
	public static final String KEY_RESUME_ADS_ID = "ResumeAdsID";
	
	public static final String KEY_DEVICE_MS_RESULT = "Result";
	public static final String KEY_DEVICE_MS_VERSION = "Version";
	// 키 }
	
	// 이름
	public static final String OBJ_NAME_DEVICE_MSG_RECEIVER = "CDeviceMsgReceiver";
	public static final String FUNC_NAME_DEVICE_MSG_HANDLER = "HandleDeviceMsg";
}
