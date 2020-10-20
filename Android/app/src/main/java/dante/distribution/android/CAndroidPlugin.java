package dante.distribution.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

import dante.distribution.android.Global.Define.KGDefine;
import dante.distribution.android.Global.Function.GlobalFunc;
import dante.distribution.android.Global.Utility.Platform.CDeviceMsgSender;

//! 안드로이드 플러그인
public class CAndroidPlugin {
	private String m_oBuildMode = KGDefine.EMPTY_STRING;
	private ProgressBar m_oProgressBar = null;
	
	private static CAndroidPlugin m_oInstance = null;
	
	//! 생성자
	private CAndroidPlugin() {
		Point oPoint = new Point();
		UnityPlayer.currentActivity.getWindowManager().getDefaultDisplay().getSize(oPoint);
		
		int nSize = Math.min(oPoint.x, oPoint.y);
		nSize = (int)(nSize * KGDefine.SCALE_PROGRESS_BAR);
		
		int nOffset = Math.min(oPoint.x, oPoint.y);
		nOffset = (int)(nOffset * KGDefine.SCALE_PROGRESS_BAR_OFFSET);
		
		// 프로그레스 바를 설정한다 {
		m_oProgressBar = new ProgressBar(UnityPlayer.currentActivity,
				null, android.R.attr.progressBarStyleLarge);
		
		m_oProgressBar.setIndeterminate(true);
		m_oProgressBar.setVisibility(View.GONE);
		
		RelativeLayout.LayoutParams oParams = new RelativeLayout.LayoutParams(nSize, nSize);
		oParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		// 프로그레스 바를 설정한다 }
		
		// 레이아웃을 설정한다 {
		RelativeLayout oLayout = new RelativeLayout(UnityPlayer.currentActivity);
		oLayout.setPadding(0, 0, 0, nOffset);
		oLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		oLayout.addView(m_oProgressBar, oParams);
		
		RelativeLayout.LayoutParams oLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		
		UnityPlayer.currentActivity.addContentView(oLayout, oLayoutParams);
		// 레이아웃을 설정한다 }
	}
	
	//! 인스턴스를 반환한다
	public static CAndroidPlugin getInstance() {
		// 인스턴스가 없을 경우
		if(CAndroidPlugin.m_oInstance == null) {
			CAndroidPlugin.m_oInstance = new CAndroidPlugin();
		}
		
		return CAndroidPlugin.m_oInstance;
	}
	
	//! 유니티 메세지를 처리한다
	public static void handleUnityMsg(final String a_oCmd, final String a_oMsg) {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleUnityMsg: %s, %s", a_oCmd, a_oMsg));
		
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					switch(a_oCmd) {
						case KGDefine.CMD_GET_DEVICE_ID: CAndroidPlugin.getInstance().handleGetDeviceIDMsg(a_oMsg); break;
						case KGDefine.CMD_GET_DEVICE_TYPE: CAndroidPlugin.getInstance().handleGetDeviceTypeMsg(a_oMsg); break;
						case KGDefine.CMD_GET_COUNTRY_CODE: CAndroidPlugin.getInstance().handleGetCountryCodeMsg(a_oMsg); break;
						case KGDefine.CMD_GET_STORE_VERSION: CAndroidPlugin.getInstance().handleGetStoreVersionMsg(a_oMsg); break;
						case KGDefine.CMD_SET_BUILD_MODE: CAndroidPlugin.getInstance().handleSetBuildModeMsg(a_oMsg); break;
						case KGDefine.CMD_SHOW_TOAST: CAndroidPlugin.getInstance().handleShowToastMsg(a_oMsg); break;
						case KGDefine.CMD_SHOW_ALERT: CAndroidPlugin.getInstance().handleShowAlertMsg(a_oMsg); break;
						case KGDefine.CMD_VIBRATE: CAndroidPlugin.getInstance().handleVibrateMsg(a_oMsg); break;
						case KGDefine.CMD_ACTIVITY_INDICATOR: CAndroidPlugin.getInstance().handleActivityIndicatorMsg(a_oMsg); break;
					}
				} catch(Exception oException) {
					oException.printStackTrace();
					Log.e(KGDefine.TAG, String.format("CAndroidPlugin.handleUnityMsg Exception: %s", oException.getMessage()));
				}
			}
		});
	}
	
	//! 디바이스 식별자 반환 메세지를 처리한다
	private void handleGetDeviceIDMsg(String a_oMsg) {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleGetDeviceIDMsg: %s", a_oMsg));
		
		UUID oUUID = null;
		Context oAppContext = UnityPlayer.currentActivity.getApplicationContext();
		
		// 안드로이드 식별자가 유효하지 않을 경우
		if(Settings.Secure.ANDROID_ID.equals(KGDefine.INVALID_ANDROID_ID)) {
			oUUID = UUID.randomUUID();
		} else {
			ContentResolver oResolver = oAppContext.getContentResolver();
			
			@SuppressLint("HardwareIds") String oDeviceID = Settings.Secure.getString(oResolver,
					Settings.Secure.ANDROID_ID);
			
			oUUID = UUID.nameUUIDFromBytes(oDeviceID.getBytes(StandardCharsets.UTF_8));
		}
		
		CDeviceMsgSender.getInstance().sendGetDeviceIDMsg(oUUID.toString());
	}
	
	//! 디바이스 타입 반환 메세지를 처리한다
	private void handleGetDeviceTypeMsg(String a_oMsg) {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleGetDeviceTypeMsg: %s", a_oMsg));
	}
	
	//! 국가 코드 반환 메세지를 처리한다
	private void handleGetCountryCodeMsg(String a_oMsg) {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleGetCountryCodeMsg: %s", a_oMsg));
		
		Locale oLocale = Locale.getDefault();
		CDeviceMsgSender.getInstance().sendGetCountryCodeMsg(oLocale.getCountry());
	}
	
	//! 스토어 버전 반환 메세지를 처리한다
	private void handleGetStoreVersionMsg(String a_oMsg) throws Exception {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleGetStoreVersionMsg: %s", a_oMsg));
		
		JSONObject oJSONObj = new JSONObject(a_oMsg);
		final String oVersion = oJSONObj.getString(KGDefine.KEY_VERSION);
		
		// 앱 업데이트 관리자를 지원하지 않을 경우
		if(Build.VERSION.SDK_INT < KGDefine.MIN_VERSION_APP_UPDATE_MANAGER) {
			CDeviceMsgSender.getInstance().sendGetStoreVersionMsg(oVersion, false);
		} else {
			Task<AppUpdateInfo> oTask = null;
			
			// 디버그 모드 일 경우
			if(m_oBuildMode.equals(KGDefine.BUILD_MODE_DEBUG)) {
				FakeAppUpdateManager oFakeUpdateManager = new FakeAppUpdateManager(UnityPlayer.currentActivity);
				oFakeUpdateManager.setUpdateAvailable(Integer.parseInt(oVersion));
				
				oTask = oFakeUpdateManager.getAppUpdateInfo();
			} else {
				AppUpdateManager oUpdateManager = AppUpdateManagerFactory.create(UnityPlayer.currentActivity);
				oTask = oUpdateManager.getAppUpdateInfo();
			}
			
			// 업데이트 정보를 로드했을 경우
			oTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
				@Override
				public void onSuccess(AppUpdateInfo a_oUpdateInfo) {
					Log.d(KGDefine.TAG, String.format("CAndroidPlugin.onHandleGetStoreVersionMsg Success: %d",
							a_oUpdateInfo.updateAvailability()));
					
					int nVersion = Integer.parseInt(oVersion);
					boolean bIsSuccess = a_oUpdateInfo.updateAvailability() != UpdateAvailability.UNKNOWN;
					
					// 업데이트 가능 할 경우
					if(a_oUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
						nVersion = a_oUpdateInfo.availableVersionCode();
					}
					
					String oVersion = String.valueOf(nVersion);
					CDeviceMsgSender.getInstance().sendGetStoreVersionMsg(oVersion, bIsSuccess);
				}
			});
			
			// 업데이트 정보 로드에 실패했을 경우
			oTask.addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(Exception a_oException) {
					a_oException.printStackTrace();
					
					Log.d(KGDefine.TAG, String.format("CAndroidPlugin.onHandleGetStoreVersionMsg Fail: %s",
							a_oException.getMessage()));
					
					CDeviceMsgSender.getInstance().sendGetStoreVersionMsg(oVersion, false);
				}
			});
		}
	}
	
	//! 빌드 모드 변경 메세지를 처리한다
	private void handleSetBuildModeMsg(String a_oMsg) {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleSetBuildModeMsg: %s", a_oMsg));
		m_oBuildMode = a_oMsg;
	}
	
	//! 토스트 출력 메세지를 처리한다
	private void handleShowToastMsg(String a_oMsg) {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleShowToastMsg: %s", a_oMsg));
		Toast.makeText(UnityPlayer.currentActivity, a_oMsg, Toast.LENGTH_LONG);
	}
	
	//! 경고 창 출력 메세지를 처리한다
	private void handleShowAlertMsg(String a_oMsg) throws Exception {
		JSONObject oJSONObj = new JSONObject(a_oMsg);
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleShowAlertMsg: %s", a_oMsg));
		
		String oTitle = oJSONObj.getString(KGDefine.KEY_ALERT_TITLE);
		String oMsg = oJSONObj.getString(KGDefine.KEY_ALERT_MSG);
		String oOKBtnText = oJSONObj.getString(KGDefine.KEY_ALERT_OK_BTN_TEXT);
		String oCancelBtnText = oJSONObj.getString(KGDefine.KEY_ALERT_CANCEL_BTN_TEXT);
		
		AlertDialog.Builder oBuilder = new AlertDialog.Builder(UnityPlayer.currentActivity);
		oBuilder.setTitle(GlobalFunc.isValid(oTitle) ? oTitle : null);
		oBuilder.setMessage(oMsg);
		
		// 확인 버튼을 눌렀을 경우
		oBuilder.setPositiveButton(oOKBtnText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface a_oSender, int a_nIndex) {
				CDeviceMsgSender.getInstance().sendShowAlertMsg(true);
			}
		});
		
		// 취소 버튼 텍스트가 유효 할 경우
		if(GlobalFunc.isValid(oCancelBtnText)) {
			// 취소 버튼을 눌렀을 경우
			oBuilder.setNegativeButton(oCancelBtnText, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface a_oSender, int a_nIndex) {
					CDeviceMsgSender.getInstance().sendShowAlertMsg(false);
				}
			});
		}
		
		// 경고 창을 출력한다
		oBuilder.create().show();
	}
	
	//! 진동 메세지를 처리한다
	private void handleVibrateMsg(String a_oMsg) throws Exception {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleVibrateMsg: %s", a_oMsg));
		JSONObject oJSONObj = new JSONObject(a_oMsg);
		
		String oDuration = oJSONObj.getString(KGDefine.KEY_VIBRATE_DURATION);
		String oIntensity = oJSONObj.getString(KGDefine.KEY_VIBRATE_INTENSITY);
		
		float fDuration = Math.abs(Float.parseFloat(oDuration));
		float fIntensity = Math.abs(Float.parseFloat(oIntensity));
		
		Context oAppContext = UnityPlayer.currentActivity.getApplicationContext();
		Vibrator oVibrator = (Vibrator)oAppContext.getSystemService(Context.VIBRATOR_SERVICE);
		
		// 햅틱 진동을 지원하지 않을 경우
		if(Build.VERSION.SDK_INT < KGDefine.MIN_VERSION_FEEDBACK_GENERATOR) {
			oVibrator.vibrate((int)(fDuration * KGDefine.UNIT_SEC_TO_MILLISEC));
		} else {
			VibrationEffect oEffect = VibrationEffect.createOneShot((int)(fDuration * KGDefine.UNIT_SEC_TO_MILLISEC),
					(int)(fIntensity * KGDefine.UNIT_NORM_VALUE_TO_BYTE));
			
			oVibrator.vibrate(oEffect);
		}
	}
	
	//! 액티비티 인디케이터 메세지를 처리한다
	private void handleActivityIndicatorMsg(String a_oMsg) {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleActivityIndicatorMsg: %s", a_oMsg));
		
		// 출력 상태 일 경우
		if(GlobalFunc.convertStringToBool(a_oMsg)) {
			m_oProgressBar.setVisibility(View.VISIBLE);
		} else {
			m_oProgressBar.setVisibility(View.GONE);
		}
	}
}
