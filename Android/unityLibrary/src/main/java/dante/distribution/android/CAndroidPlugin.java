package dante.distribution.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import dante.distribution.android.Global.Define.KGDefine;
import dante.distribution.android.Global.Function.GFunc;
import dante.distribution.android.Global.Utility.Platform.CDeviceMsgSender;

/** 안드로이드 플러그인 */
public class CAndroidPlugin {
	/** 유니티 메세지 정보 */
	private static class CUnityMsgInfo {
		public String m_oCmd = "";
		public String m_oMsg = "";
		
		/** 생성자 */
		public CUnityMsgInfo(String a_oCmd, String a_oMsg) {
			m_oCmd = a_oCmd;
			m_oMsg = a_oMsg;
		}
	}
	
	private ImageView m_oIndicatorImgView = null;
	private RotateAnimation m_oIndicatorImgViewAni = null;
	@SuppressLint("StaticFieldLeak") private static ArrayList<CUnityMsgInfo> m_oUnityMsgInfoList = new ArrayList<CUnityMsgInfo>();
	@SuppressLint("StaticFieldLeak") private static CAndroidPlugin m_oInst = null;
	
	/** 생성자 */
	private CAndroidPlugin() {
		Point oPoint = new Point();
		Bitmap oBitmap = BitmapFactory.decodeResource(UnityPlayer.currentActivity.getResources(), R.drawable.indicator);
		BitmapDrawable oBitmapDrawable = new BitmapDrawable(UnityPlayer.currentActivity.getResources(), oBitmap);
		
		UnityPlayer.currentActivity.getWindowManager().getDefaultDisplay().getSize(oPoint);
		
		// 이미지 뷰를 설정한다
		m_oIndicatorImgView = new ImageView(UnityPlayer.currentActivity, null, android.R.attr.animatedImageDrawable);
		m_oIndicatorImgView.setImageDrawable(oBitmapDrawable);
		m_oIndicatorImgView.setVisibility(View.GONE);
		
		// 애니메이션을 설정한다
		m_oIndicatorImgViewAni = new RotateAnimation(KGDefine.VAL_0_INT, KGDefine.ANGLE_360_DEG, Animation.RELATIVE_TO_SELF, KGDefine.PIVOT_CENTER, Animation.RELATIVE_TO_SELF, KGDefine.PIVOT_CENTER);
		m_oIndicatorImgViewAni.setDuration(KGDefine.VAL_1_INT * KGDefine.UNIT_MILLI_SECS_PER_SEC);
		m_oIndicatorImgViewAni.setRepeatCount(Animation.INFINITE);
		m_oIndicatorImgViewAni.setInterpolator(UnityPlayer.currentActivity, android.R.anim.linear_interpolator);
		
		// 레이아웃을 설정한다 {
		int nSize = (int)(Math.min(oPoint.x, oPoint.y) * KGDefine.SCALE_IMG_VIEW);
		int nOffset = (int)(Math.min(oPoint.x, oPoint.y) * KGDefine.OFFSET_SCALE_IMG_VIEW);
		
		RelativeLayout.LayoutParams oParams = new RelativeLayout.LayoutParams(nSize, nSize);
		oParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		RelativeLayout oLayout = new RelativeLayout(UnityPlayer.currentActivity);
		oLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		oLayout.setPadding(KGDefine.VAL_0_INT, KGDefine.VAL_0_INT, KGDefine.VAL_0_INT, nOffset);
		oLayout.addView(m_oIndicatorImgView, oParams);
		
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
	public static void handleUnityMsg(String a_oCmd, String a_oMsg) {
		Log.d(KGDefine.TAG, String.format("CAndroidPlugin.handleUnityMsg: %s, %s", a_oCmd, a_oMsg));
		CAndroidPlugin.m_oUnityMsgInfoList.add(new CUnityMsgInfo(a_oCmd, a_oMsg));
		
		// UI 쓰레드가 시작 되었을 경우
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					// 유니티 메세지 정보가 존재 할 경우
					if(!CAndroidPlugin.m_oUnityMsgInfoList.isEmpty()) {
						switch(CAndroidPlugin.m_oUnityMsgInfoList.get(KGDefine.VAL_0_INT).m_oCmd) {
							case KGDefine.CMD_GET_DEVICE_ID: CAndroidPlugin.getInst().handleGetDeviceIDMsg(CAndroidPlugin.m_oUnityMsgInfoList.get(KGDefine.VAL_0_INT).m_oMsg); break;
							case KGDefine.CMD_GET_COUNTRY_CODE: CAndroidPlugin.getInst().handleGetCountryCodeMsg(CAndroidPlugin.m_oUnityMsgInfoList.get(KGDefine.VAL_0_INT).m_oMsg); break;
							case KGDefine.CMD_SHOW_ALERT: CAndroidPlugin.getInst().handleShowAlertMsg(CAndroidPlugin.m_oUnityMsgInfoList.get(KGDefine.VAL_0_INT).m_oMsg); break;
							case KGDefine.CMD_MAIL: CAndroidPlugin.getInst().handleMailMsg(CAndroidPlugin.m_oUnityMsgInfoList.get(KGDefine.VAL_0_INT).m_oMsg); break;
							case KGDefine.CMD_VIBRATE: CAndroidPlugin.getInst().handleVibrateMsg(CAndroidPlugin.m_oUnityMsgInfoList.get(KGDefine.VAL_0_INT).m_oMsg); break;
							case KGDefine.CMD_INDICATOR: CAndroidPlugin.getInst().handleIndicatorMsg(CAndroidPlugin.m_oUnityMsgInfoList.get(KGDefine.VAL_0_INT).m_oMsg); break;
						}
						
						CAndroidPlugin.m_oUnityMsgInfoList.remove(KGDefine.VAL_0_INT);
					}
				} catch(Exception oException) {
					Log.e(KGDefine.TAG, String.format("CAndroidPlugin.handleUnityMsg Exception: %s", oException.getMessage()));
					oException.printStackTrace();
				}
			}
		});
	}
	
	/** 디바이스 식별자 반환 메세지를 처리한다 */
	private void handleGetDeviceIDMsg(String a_oMsg) {
		@SuppressLint("HardwareIds") String oDeviceID = Settings.Secure.getString(UnityPlayer.currentActivity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
		CDeviceMsgSender.getInst().sendGetDeviceIDMsg((oDeviceID.equals(KGDefine.INVALID_ANDROID_ID) ? UUID.randomUUID() : UUID.nameUUIDFromBytes(oDeviceID.getBytes(StandardCharsets.UTF_8))).toString());
	}
	
	/** 국가 코드 반환 메세지를 처리한다 */
	private void handleGetCountryCodeMsg(String a_oMsg) {
		CDeviceMsgSender.getInst().sendGetCountryCodeMsg(Locale.getDefault().getCountry());
	}
	
	/** 경고 창 출력 메세지를 처리한다 */
	private void handleShowAlertMsg(String a_oMsg) throws Exception {
		JSONObject oJSONObj = new JSONObject(a_oMsg);
		
		AlertDialog.Builder oBuilder = new AlertDialog.Builder(UnityPlayer.currentActivity);
		oBuilder.setTitle(oJSONObj.has(KGDefine.KEY_ALERT_TITLE) ? oJSONObj.getString(KGDefine.KEY_ALERT_TITLE) : null);
		oBuilder.setMessage(oJSONObj.getString(KGDefine.KEY_ALERT_MSG));
		
		// 확인 버튼을 눌렀을 경우
		oBuilder.setPositiveButton(oJSONObj.getString(KGDefine.KEY_ALERT_OK_BTN_TEXT), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface a_oSender, int a_nIndex) {
				CDeviceMsgSender.getInst().sendShowAlertMsg(true);
			}
		});
		
		// 취소 버튼 텍스트가 유효 할 경우
		if(GFunc.isValid(oJSONObj.getString(KGDefine.KEY_ALERT_CANCEL_BTN_TEXT))) {
			// 취소 버튼을 눌렀을 경우
			oBuilder.setNegativeButton(oJSONObj.getString(KGDefine.KEY_ALERT_CANCEL_BTN_TEXT), new DialogInterface.OnClickListener() {
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
		
		Intent oIntent = new Intent(Intent.ACTION_SENDTO);
		oIntent.setType(KGDefine.MAIL_TYPE);
		oIntent.setData(Uri.parse(KGDefine.MAIL_DATA));
		
		oIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { oJSONObj.getString(KGDefine.KEY_MAIL_RECIPIENT) });
		oIntent.putExtra(Intent.EXTRA_SUBJECT, oJSONObj.getString(KGDefine.KEY_MAIL_TITLE));
		oIntent.putExtra(Intent.EXTRA_TEXT, oJSONObj.getString(KGDefine.KEY_MAIL_MSG));
		
		UnityPlayer.currentActivity.startActivity(oIntent);
	}
	
	/** 진동 메세지를 처리한다 */
	private void handleVibrateMsg(String a_oMsg) throws Exception {
		JSONObject oJSONObj = new JSONObject(a_oMsg);
		
		String oDuration = oJSONObj.getString(KGDefine.KEY_VIBRATE_DURATION);
		String oIntensity = oJSONObj.getString(KGDefine.KEY_VIBRATE_INTENSITY);
		
		float fDuration = Math.abs(Float.parseFloat(oDuration));
		Vibrator oVibrator = (Vibrator)UnityPlayer.currentActivity.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		
		// 햅틱 진동을 지원하지 않을 경우
		if(Build.VERSION.SDK_INT < KGDefine.MIN_VER_FEEDBACK_GENERATOR) {
			oVibrator.vibrate((int)(fDuration * KGDefine.UNIT_MILLI_SECS_PER_SEC));
		} else {
			float fIntensity = Math.abs(Float.parseFloat(oIntensity));
			oVibrator.vibrate(VibrationEffect.createOneShot((int)(fDuration * KGDefine.UNIT_MILLI_SECS_PER_SEC), (int)(fIntensity * KGDefine.UNIT_NORM_VAL_TO_BYTE)));
		}
	}
	
	/** 인디케이터 메세지를 처리한다 */
	private void handleIndicatorMsg(String a_oMsg) {
		// 출력 모드 일 경우
		if(GFunc.convertStrToBool(a_oMsg)) {
			m_oIndicatorImgView.startAnimation(m_oIndicatorImgViewAni);
		} else {
			m_oIndicatorImgView.clearAnimation();
		}
		
		m_oIndicatorImgView.setVisibility(GFunc.convertStrToBool(a_oMsg) ? View.VISIBLE : View.GONE);
	}
}
