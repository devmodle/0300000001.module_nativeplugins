package dante.distribution.plugin;

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

import dante.distribution.plugin.Global.Define.KGlobalDefine;
import dante.distribution.plugin.Global.Function.GlobalFunction;
import dante.distribution.plugin.Global.Utility.Platform.CDeviceMessageSender;

//! 안드로이드 플러그인
public class CAndroidPlugin {
	private String m_oBuildMode = KGlobalDefine.EMPTY_STRING;
	private ProgressBar m_oProgressBar = null;
	
	private static CAndroidPlugin m_oInstance = null;
	
	//! 생성자
	private CAndroidPlugin() {
		Point oPoint = new Point();
		UnityPlayer.currentActivity.getWindowManager().getDefaultDisplay().getSize(oPoint);
		
		int nSize = Math.min(oPoint.x, oPoint.y);
		nSize = (int)(nSize * KGlobalDefine.SCALE_PROGRESS_BAR);
		
		int nOffset = Math.min(oPoint.x, oPoint.y);
		nOffset = (int)(nOffset * KGlobalDefine.SCALE_PROGRESS_BAR_OFFSET);
		
		// 프로그레스 바를 설정한다 {
		m_oProgressBar = new ProgressBar(UnityPlayer.currentActivity, null, android.R.attr.progressBarStyleLarge);
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
		if(CAndroidPlugin.m_oInstance == null) {
			CAndroidPlugin.m_oInstance = new CAndroidPlugin();
		}
		
		return CAndroidPlugin.m_oInstance;
	}
	
	//! 유니티 메세지를 처리한다
	public static void handleUnityMessage(final String a_oCommand, final String a_oMessage) {
		Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.handleUnityMessage: %s, %s", a_oCommand, a_oMessage));
		
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					switch(a_oCommand) {
						case KGlobalDefine.COMMAND_GET_DEVICE_ID: CAndroidPlugin.getInstance().handleGetDeviceIDMessage(a_oMessage); break;
						case KGlobalDefine.COMMAND_GET_COUNTRY_CODE: CAndroidPlugin.getInstance().handleGetCountryCodeMessage(a_oMessage); break;
						case KGlobalDefine.COMMAND_GET_STORE_VERSION: CAndroidPlugin.getInstance().handleGetStoreVersionMessage(a_oMessage); break;
						case KGlobalDefine.COMMAND_SET_BUILD_MODE: CAndroidPlugin.getInstance().handleSetBuildModeMessage(a_oMessage); break;
						case KGlobalDefine.COMMAND_SHOW_TOAST: CAndroidPlugin.getInstance().handleShowToastMessage(a_oMessage); break;
						case KGlobalDefine.COMMAND_SHOW_ALERT: CAndroidPlugin.getInstance().handleShowAlertMessage(a_oMessage); break;
						case KGlobalDefine.COMMAND_VIBRATE: CAndroidPlugin.getInstance().handleVibrateMessage(a_oMessage); break;
						case KGlobalDefine.COMMAND_ACTIVITY_INDICATOR: CAndroidPlugin.getInstance().handleActivityIndicatorMessage(a_oMessage); break;
					}
				} catch(Exception oException) {
					oException.printStackTrace();
					Log.e(KGlobalDefine.TAG, String.format("CAndroidPlugin.handleUnityMessage Exception: %s", oException.getMessage()));
				}
			}
		});
	}
	
	//! 디바이스 식별자 반환 메세지를 처리한다
	private void handleGetDeviceIDMessage(String a_oMessage) {
		Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.handleGetDeviceIDMessage: %s", a_oMessage));
		
		UUID oUUID = null;
		Context oAppContext = UnityPlayer.currentActivity.getApplicationContext();
		
		// 안드로이드 식별자가 유효하지 않을 경우
		if(Settings.Secure.ANDROID_ID.equals(KGlobalDefine.INVALID_ANDROID_ID)) {
			oUUID = UUID.randomUUID();
		} else {
			ContentResolver oResolver = oAppContext.getContentResolver();
			@SuppressLint("HardwareIds") String oDeviceID = Settings.Secure.getString(oResolver, Settings.Secure.ANDROID_ID);
			
			oUUID = UUID.nameUUIDFromBytes(oDeviceID.getBytes(StandardCharsets.UTF_8));
		}
		
		CDeviceMessageSender.getInstance().sendGetDeviceIDMessage(oUUID.toString());
	}
	
	//! 국가 코드 반환 메세지를 처리한다
	private void handleGetCountryCodeMessage(String a_oMessage) {
		Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.handleGetCountryCodeMessage: %s", a_oMessage));
		
		Locale oLocale = Locale.getDefault();
		CDeviceMessageSender.getInstance().sendGetCountryCodeMessage(oLocale.getCountry());
	}
	
	//! 스토어 버전 반환 메세지를 처리한다
	private void handleGetStoreVersionMessage(String a_oMessage) throws Exception {
		Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.handleGetStoreVersionMessage: %s", a_oMessage));
		
		JSONObject oJSONObject = new JSONObject(a_oMessage);
		final String oVersion = oJSONObject.getString(KGlobalDefine.KEY_VERSION);
		
		// 앱 업데이트 관리자를 지원하지 않을 경우
		if(Build.VERSION.SDK_INT < KGlobalDefine.MIN_VERSION_APP_UPDATE_MANAGER) {
			CDeviceMessageSender.getInstance().sendGetStoreVersionMessage(oVersion, false);
		} else {
			Task<AppUpdateInfo> oTask = null;
			
			// 디버그 모드 일 경우
			if(m_oBuildMode.equals(KGlobalDefine.BUILD_MODE_DEBUG)) {
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
					Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.onHandleGetStoreVersionMessage Success: %d", a_oUpdateInfo.updateAvailability()));
					
					int nVersion = Integer.parseInt(oVersion);
					boolean bIsSuccess = a_oUpdateInfo.updateAvailability() != UpdateAvailability.UNKNOWN;
					
					if(a_oUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
						nVersion = a_oUpdateInfo.availableVersionCode();
					}
					
					String oVersion = String.valueOf(nVersion);
					CDeviceMessageSender.getInstance().sendGetStoreVersionMessage(oVersion, bIsSuccess);
				}
			});
			
			// 업데이트 정보 로드에 실패했을 경우
			oTask.addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(Exception a_oException) {
					a_oException.printStackTrace();
					Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.onHandleGetStoreVersionMessage Fail: %s", a_oException.getMessage()));
					
					CDeviceMessageSender.getInstance().sendGetStoreVersionMessage(oVersion, false);
				}
			});
		}
	}
	
	//! 빌드 모드 변경 메세지를 처리한다
	private void handleSetBuildModeMessage(String a_oMessage) {
		Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.handleSetBuildModeMessage: %s", a_oMessage));
		m_oBuildMode = a_oMessage;
	}
	
	//! 토스트 출력 메세지를 처리한다
	private void handleShowToastMessage(String a_oMessage) {
		Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.handleShowToastMessage: %s", a_oMessage));
		Toast.makeText(UnityPlayer.currentActivity, a_oMessage, Toast.LENGTH_LONG);
	}
	
	//! 알림 창 출력 메세지를 처리한다
	private void handleShowAlertMessage(String a_oMessage) throws Exception {
		JSONObject oJSONObject = new JSONObject(a_oMessage);
		Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.handleShowAlertMessage: %s", a_oMessage));
		
		String oTitle = oJSONObject.getString(KGlobalDefine.KEY_ALERT_TITLE);
		String oMessage = oJSONObject.getString(KGlobalDefine.KEY_ALERT_MESSAGE);
		String oOKButtonTitle = oJSONObject.getString(KGlobalDefine.KEY_ALERT_OK_BUTTON_TEXT);
		String oCancelButtonTitle = oJSONObject.getString(KGlobalDefine.KEY_ALERT_CANCEL_BUTTON_TEXT);
		
		AlertDialog.Builder oBuilder = new AlertDialog.Builder(UnityPlayer.currentActivity);
		oBuilder.setTitle(GlobalFunction.isValidString(oTitle) ? oTitle : null);
		oBuilder.setMessage(oMessage);
		
		// 확인 버튼을 눌렀을 경우
		oBuilder.setPositiveButton(oOKButtonTitle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface a_oSender, int a_nIndex) {
				CDeviceMessageSender.getInstance().sendShowAlertMessage(true);
			}
		});
		
		if(GlobalFunction.isValidString(oCancelButtonTitle)) {
			// 취소 버튼을 눌렀을 경우
			oBuilder.setNegativeButton(oCancelButtonTitle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface a_oSender, int a_nIndex) {
					CDeviceMessageSender.getInstance().sendShowAlertMessage(false);
				}
			});
		}
		
		// 알림 창을 출력한다
		oBuilder.create().show();
	}
	
	//! 진동 메세지를 처리한다
	private void handleVibrateMessage(String a_oMessage) throws Exception {
		Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.handleVibrateMessage: %s", a_oMessage));
		JSONObject oJSONObject = new JSONObject(a_oMessage);
		
		String oDurationString = oJSONObject.getString(KGlobalDefine.KEY_VIBRATE_DURATION);
		String oIntensityString = oJSONObject.getString(KGlobalDefine.KEY_VIBRATE_INTENSITY);
		
		float fDuration = Math.abs(Float.parseFloat(oDurationString));
		float fIntensity = Math.abs(Float.parseFloat(oIntensityString));
		
		Context oAppContext = UnityPlayer.currentActivity.getApplicationContext();
		Vibrator oVibrator = (Vibrator)oAppContext.getSystemService(Context.VIBRATOR_SERVICE);
		
		// 햅틱 진동을 지원하지 않을 경우
		if(Build.VERSION.SDK_INT < KGlobalDefine.MIN_VERSION_FEEDBACK_GENERATOR) {
			oVibrator.vibrate((int)(fDuration * 1000.0f));
		} else {
			VibrationEffect oEffect = VibrationEffect.createOneShot((int)(fDuration * 1000.0f), (int)(fIntensity * 255.0f));
			oVibrator.vibrate(oEffect);
		}
	}
	
	//! 액티비티 인디게이터 메세지를 처리한다
	private void handleActivityIndicatorMessage(String a_oMessage) {
		Log.d(KGlobalDefine.TAG, String.format("CAndroidPlugin.handleActivityIndicatorMessage: %s", a_oMessage));
		
		if(GlobalFunction.convertStringToBool(a_oMessage)) {
			m_oProgressBar.setVisibility(View.VISIBLE);
		} else {
			m_oProgressBar.setVisibility(View.GONE);
		}
	}
}
