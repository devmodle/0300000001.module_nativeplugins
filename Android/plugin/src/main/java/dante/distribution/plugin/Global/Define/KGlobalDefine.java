package dante.distribution.plugin.Global.Define;

import android.os.Build;

//! 전역 상수
public abstract class KGlobalDefine {
	public static final String TAG = "Plugin";
	public static final String EMPTY_STRING = "";
	public static final String INVALID_ANDROID_ID = "9774d56d682e549c";
	
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
	
	// 명령어
	public static final String COMMAND_GET_DEVICE_ID = "GetDeviceID";
	public static final String COMMAND_GET_COUNTRY_CODE = "GetCountryCode";
	public static final String COMMAND_GET_STORE_VERSION = "GetStoreVersion";
	public static final String COMMAND_SET_BUILD_MODE = "SetBuildMode";
	public static final String COMMAND_SHOW_ALERT = "ShowAlert";
	public static final String COMMAND_SHOW_TOAST = "ShowToast";
	public static final String COMMAND_VIBRATE = "Vibrate";
	public static final String COMMAND_ACTIVITY_INDICATOR = "ActivityIndicator";
	
	// 키 {
	public static final String KEY_COMMAND = "Command";
	public static final String KEY_MESSAGE = "Message";
	
	public static final String KEY_VERSION = "Version";
	
	public static final String KEY_ALERT_TITLE = "Title";
	public static final String KEY_ALERT_MESSAGE = "Message";
	public static final String KEY_ALERT_OK_BUTTON_TEXT = "OKButtonText";
	public static final String KEY_ALERT_CANCEL_BUTTON_TEXT = "CancelButtonText";
	
	public static final String KEY_VIBRATE_DURATION = "Duration";
	public static final String KEY_VIBRATE_INTENSITY = "Intensity";
	
	public static final String KEY_DEVICE_MS_RESULT = "Result";
	public static final String KEY_DEVICE_MS_VERSION = "Version";
	// 키 }
	
	// 이름
	public static final String OBJ_NAME_DEVICE_MESSAGE_RECEIVER = "CDeviceMessageReceiver";
	public static final String FUNC_NAME_DEVICE_MESSAGE_HANDLER = "HandleDeviceMessage";
}
