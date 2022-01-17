package dante.distribution.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

import dante.distribution.android.Global.Define.KGDefine;
import dante.distribution.android.Global.Function.GFunc;
import dante.distribution.android.Global.Utility.Platform.CDeviceMsgSender;

/** 안드로이드 플러그인 */
public class CAndroidPlugin {
	private ProgressBar m_oProgressBar = null;
	@SuppressLint("StaticFieldLeak") private static CAndroidPlugin m_oInst = null;
	
	/** 생성자 */
	private CAndroidPlugin() {
		Point oPoint = new Point();
		UnityPlayer.currentActivity.getWindowManager().getDefaultDisplay().getSize(oPoint);
		
		// 프로그레스 바를 설정한다
		m_oProgressBar = new ProgressBar(UnityPlayer.currentActivity, null, android.R.attr.progressBarStyleLargeInverse);
		m_oProgressBar.setIndeterminate(true);
		m_oProgressBar.setVisibility(View.GONE);
		
		// 레이아웃을 설정한다 {
		int nSize = (int)(Math.min(oPoint.x, oPoint.y) * KGDefine.SCALE_PROGRESS_BAR);
		int nOffset = (int)(Math.min(oPoint.x, oPoint.y) * KGDefine.OFFSET_SCALE_PROGRESS_BAR);
		
		RelativeLayout.LayoutParams oParams = new RelativeLayout.LayoutParams(nSize, nSize);
		oParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		RelativeLayout oLayout = new RelativeLayout(UnityPlayer.currentActivity);
		oLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		oLayout.setPadding(KGDefine.VAL_0_INT, KGDefine.VAL_0_INT, KGDefine.VAL_0_INT, nOffset);
		oLayout.addView(m_oProgressBar, oParams);
		
		RelativeLayout.LayoutParams oLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		UnityPlayer.currentActivity.addContentView(oLayout, oLayoutParams);
		// 레이아웃을 설정한다 }
	}
	
	/** 인스턴스를 반환한다 */
	public static CAndroidPlugin getInst() {
		// 인스턴스가 없을 경우
		if(CAndroidPlugin.m_oInst == null) {
			CAndroidPlugin.m_oInst = new CAndroidPlugin();
		}
		
		return CAndroidPlugin.m_oInst;
	}
	
	/** 유니티 메세지를 처리한다 */
	public static void handleUnityMsg(final String a_oCmd, final String a_oMsg) {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleUnityMsg: %s, %s", a_oCmd, a_oMsg));
		
		// UI 쓰레드가 시작 되었을 경우
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					switch(a_oCmd) {
						case KGDefine.CMD_GET_DEVICE_ID: CAndroidPlugin.getInst().handleGetDeviceIDMsg(a_oMsg); break;
						case KGDefine.CMD_GET_COUNTRY_CODE: CAndroidPlugin.getInst().handleGetCountryCodeMsg(a_oMsg); break;
						case KGDefine.CMD_SHOW_ALERT: CAndroidPlugin.getInst().handleShowAlertMsg(a_oMsg); break;
						case KGDefine.CMD_MAIL: CAndroidPlugin.getInst().handleMailMsg(a_oMsg); break;
						case KGDefine.CMD_VIBRATE: CAndroidPlugin.getInst().handleVibrateMsg(a_oMsg); break;
						case KGDefine.CMD_INDICATOR: CAndroidPlugin.getInst().handleIndicatorMsg(a_oMsg); break;
					}
				} catch(Exception oException) {
					Log.e(KGDefine.TAG, String.format("CAndroidPlugin.handleUnityMsg Exception: %s, %s", a_oCmd, oException.getMessage()));
					oException.printStackTrace();
				}
			}
		});
	}
	
	/** 디바이스 식별자 반환 메세지를 처리한다 */
	private void handleGetDeviceIDMsg(String a_oMsg) {
		@SuppressLint("HardwareIds") String oDeviceID = Settings.Secure.getString(UnityPlayer.currentActivity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
		UUID oUUID = oDeviceID.equals(KGDefine.INVALID_ANDROID_ID) ? UUID.randomUUID() : UUID.nameUUIDFromBytes(oDeviceID.getBytes(StandardCharsets.UTF_8));
		
		CDeviceMsgSender.getInst().sendGetDeviceIDMsg(oUUID.toString());
	}
	
	/** 국가 코드 반환 메세지를 처리한다 */
	private void handleGetCountryCodeMsg(String a_oMsg) {
		CDeviceMsgSender.getInst().sendGetCountryCodeMsg(Locale.getDefault().getCountry());
	}
	
	/** 경고 창 출력 메세지를 처리한다 */
	private void handleShowAlertMsg(String a_oMsg) throws Exception {
		JSONObject oJSONObj = new JSONObject(a_oMsg);
		
		String oTitle = oJSONObj.getString(KGDefine.KEY_ALERT_TITLE);
		String oMsg = oJSONObj.getString(KGDefine.KEY_ALERT_MSG);
		String oOKBtnText = oJSONObj.getString(KGDefine.KEY_ALERT_OK_BTN_TEXT);
		String oCancelBtnText = oJSONObj.getString(KGDefine.KEY_ALERT_CANCEL_BTN_TEXT);
		
		AlertDialog.Builder oBuilder = new AlertDialog.Builder(UnityPlayer.currentActivity);
		oBuilder.setTitle(GFunc.isValid(oTitle) ? oTitle : null);
		oBuilder.setMessage(oMsg);
		
		// 확인 버튼을 눌렀을 경우
		oBuilder.setPositiveButton(oOKBtnText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface a_oSender, int a_nIndex) {
				CDeviceMsgSender.getInst().sendShowAlertMsg(true);
			}
		});
		
		// 취소 버튼 텍스트가 유효 할 경우
		if(GFunc.isValid(oCancelBtnText)) {
			// 취소 버튼을 눌렀을 경우
			oBuilder.setNegativeButton(oCancelBtnText, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface a_oSender, int a_nIndex) {
					CDeviceMsgSender.getInst().sendShowAlertMsg(false);
				}
			});
		}
		
		oBuilder.create().show();
	}
	
	/** 메일 메세지를 처리한다 */
	@SuppressLint("IntentReset")
	private void handleMailMsg(String a_oMsg) throws Exception {
		JSONObject oJSONObj = new JSONObject(a_oMsg);
		
		String oRecipient = oJSONObj.getString(KGDefine.KEY_MAIL_RECIPIENT);
		String oTitle = oJSONObj.getString(KGDefine.KEY_MAIL_TITLE);
		String oMsg = oJSONObj.getString(KGDefine.KEY_MAIL_MSG);
		
		Intent oIntent = new Intent(Intent.ACTION_SENDTO);
		oIntent.setType(KGDefine.MAIL_TYPE);
		oIntent.setData(Uri.parse(KGDefine.MAIL_DATA));
		
		oIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { oRecipient });
		oIntent.putExtra(Intent.EXTRA_SUBJECT, oTitle);
		oIntent.putExtra(Intent.EXTRA_TEXT, oMsg);
		
		UnityPlayer.currentActivity.startActivity(oIntent);
	}
	
	/** 진동 메세지를 처리한다 */
	private void handleVibrateMsg(String a_oMsg) throws Exception {
		JSONObject oJSONObj = new JSONObject(a_oMsg);
		
		String oDuration = oJSONObj.getString(KGDefine.KEY_VIBRATE_DURATION);
		String oIntensity = oJSONObj.getString(KGDefine.KEY_VIBRATE_INTENSITY);
		
		Context oContext = UnityPlayer.currentActivity.getApplicationContext();
		Vibrator oVibrator = (Vibrator)oContext.getSystemService(Context.VIBRATOR_SERVICE);
		
		// 햅틱 진동을 지원하지 않을 경우
		if(Build.VERSION.SDK_INT < KGDefine.MIN_VER_FEEDBACK_GENERATOR) {
			oVibrator.vibrate((int)(Math.abs(Float.parseFloat(oDuration)) * KGDefine.UNIT_SEC_TO_MILLISEC));
		} else {
			oVibrator.vibrate(VibrationEffect.createOneShot((int)(Math.abs(Float.parseFloat(oDuration)) * KGDefine.UNIT_SEC_TO_MILLISEC), (int)(Math.abs(Float.parseFloat(oIntensity)) * KGDefine.UNIT_NORM_VAL_TO_BYTE)));
		}
	}
	
	/** 인디케이터 메세지를 처리한다 */
	private void handleIndicatorMsg(String a_oMsg) {
		// 출력 모드 일 경우
		if(GFunc.convertStrToBool(a_oMsg)) {
			m_oProgressBar.setVisibility(View.VISIBLE);
		} else {
			m_oProgressBar.setVisibility(View.GONE);
		}
	}
}
