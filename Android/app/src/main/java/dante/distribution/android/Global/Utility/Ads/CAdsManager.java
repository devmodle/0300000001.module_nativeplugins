package dante.distribution.android.Global.Utility.Ads;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;

import dante.distribution.android.CAndroidPlugin;
import dante.distribution.android.Global.Define.KGDefine;
import dante.distribution.android.Global.Utility.Platform.CDeviceMsgSender;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

//! 광고 관리자
public class CAdsManager implements LifecycleObserver,
		Application.ActivityLifecycleCallbacks
{
	private boolean m_bIsInit = false;
	private boolean m_bIsLoadResumeAds = false;
	
	private String m_oResumeAdsID = "";
	private AppOpenAd m_oResumeAds = null;
	private AdRequest.Builder m_oRequestBuilder = null;
	
	private static CAdsManager m_oInstance = null;
	
	//! 액티비티가 생성 되었을 경우
	@Override
	public void onActivityCreated(Activity a_oActivity, Bundle a_oBundle) {
		Log.d(KGDefine.TAG, "CAdsManager.onActivityCreated");
	}
	
	//! 액티비티가 시작 되었을 경우
	@Override
	public void onActivityStarted(Activity a_oActivity) {
		Log.d(KGDefine.TAG, "CAdsManager.onActivityStarted");
	}
	
	//! 액티비티가 재개 되었을 경
	@Override
	public void onActivityResumed(Activity a_oActivity) {
		Log.d(KGDefine.TAG, "CAdsManager.onActivityResumed");
	}
	
	//! 액티비티가 정지 되었을 경우
	@Override
	public void onActivityPaused(Activity a_oActivity) {
		Log.d(KGDefine.TAG, "CAdsManager.onActivityPaused");
	}
	
	//! 액티비티가 중지 되었을 경우
	@Override
	public void onActivityStopped(Activity a_oActivity) {
		Log.d(KGDefine.TAG, "CAdsManager.onActivityStopped");
	}
	
	//! 액티비티 상태가 변경 되었을 경우
	@Override
	public void onActivitySaveInstanceState(Activity a_oActivity, Bundle a_oBundle) {
		Log.d(KGDefine.TAG, "CAdsManager.onActivitySaveInstanceState");
	}
	
	//! 액티비티가 제거 되었을 경우
	@Override
	public void onActivityDestroyed(Activity a_oActivity) {
		Log.d(KGDefine.TAG, "CAdsManager.onActivityDestroyed");
	}
	
	//! 앱이 시작 되었을 경우
	@OnLifecycleEvent(ON_START)
	public void onStart() {
		Log.d(KGDefine.TAG, "CAdsManager.onStart");
	}
	
	//! 인스턴스를 반환한다
	public static CAdsManager getInstance() {
		// 인스턴스가 없을 경우
		if(CAdsManager.m_oInstance == null) {
			CAdsManager.m_oInstance = new CAdsManager();
		}
		
		return CAdsManager.m_oInstance;
	}
	
	//! 초기화
	public void init(String a_oResumeAdsID, ArrayList<String> a_oDeviceIDList) {
		m_oResumeAdsID = a_oResumeAdsID;
		m_oRequestBuilder = new AdRequest.Builder();
		
		for(int i = 0; i < a_oDeviceIDList.size(); ++i) {
			m_oRequestBuilder.addTestDevice(a_oDeviceIDList.get(i));
		}
		
		UnityPlayer.currentActivity.getApplication().registerActivityLifecycleCallbacks(this);
		ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
		
		m_bIsInit = true;
		CDeviceMsgSender.getInstance().sendInitAdsMsg(true);
	}
	
	// 재개 광고를 로드한다
	public void loadResumeAds() {
		Log.d(KGDefine.TAG, "CAdsManager.loadResumeAds");
		
		// 초기화 되었을 경우
		if(m_bIsInit && !m_bIsLoadResumeAds && m_oResumeAds == null) {
			int nOrientation = CAndroidPlugin.getInstance().getOrientation();
			
			AppOpenAd.AppOpenAdLoadCallback oCallback = new AppOpenAd.AppOpenAdLoadCallback() {
				// 광고가 로드 되었을 경우
				@Override
				public void onAppOpenAdLoaded(AppOpenAd a_oAds) {
					Log.d(KGDefine.TAG, "CAdsManager.onAppOpenAdLoaded");
					
					CAdsManager.getInstance().m_oResumeAds = a_oAds;
					CAdsManager.getInstance().m_bIsLoadResumeAds = true;
					
					CDeviceMsgSender.getInstance().sendLoadResumeAdsMsg(true);
				}
				
				// 광고 로드에 실패했을 경우
				@Override
				public void onAppOpenAdFailedToLoad(LoadAdError a_oError) {
					Log.d(KGDefine.TAG, String.format("CAdsManager.onAppOpenAdFailedToLoad: %s", a_oError.getMessage()));
					CDeviceMsgSender.getInstance().sendLoadResumeAdsMsg(false);
				}
			};
			
			AppOpenAd.load(UnityPlayer.currentActivity.getApplicationContext(),
					m_oResumeAdsID, m_oRequestBuilder.build(), nOrientation, oCallback);
		} else {
			CDeviceMsgSender.getInstance().sendLoadResumeAdsMsg(false);
		}
	}
	
	// 재개 광고를 출력한다
	public void showResumeAds() {
		Log.d(KGDefine.TAG, "CAdsManager.showResumeAds");
		
		// 재개 광고가 로드 되었을 경우
		if(m_bIsInit && m_bIsLoadResumeAds && m_oResumeAds != null) {
			FullScreenContentCallback oCallback = new FullScreenContentCallback() {
				// 전면 광고를 출력했을 경우
				@Override
				public void onAdShowedFullScreenContent() {
					Log.d(KGDefine.TAG, "CAdsManager.onAdShowedFullScreenContent");
				}
				
				// 전면 광고 출력에 실패했을 경우
				@Override
				public void onAdFailedToShowFullScreenContent(AdError a_oError) {
					Log.d(KGDefine.TAG, String.format("CAdsManager.onAdFailedToShowFullScreenContent: %s", a_oError.getMessage()));
					CDeviceMsgSender.getInstance().sendShowResumeAdsMsg(false);
				}
				
				// 전면 광고가 닫혔을 경우
				@Override
				public void onAdDismissedFullScreenContent() {
					Log.d(KGDefine.TAG, "CAdsManager.onAdDismissedFullScreenContent");
					
					CAdsManager.getInstance().m_oResumeAds = null;
					CAdsManager.getInstance().m_bIsLoadResumeAds = false;
					
					CDeviceMsgSender.getInstance().sendShowResumeAdsMsg(true);
				}
			};
			
			m_oResumeAds.show(UnityPlayer.currentActivity, oCallback);
		} else {
			CDeviceMsgSender.getInstance().sendShowResumeAdsMsg(false);
		}
	}
}
