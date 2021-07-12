//
//  KGDefine.h
//  Unity-iPhone
//
//  Created by 이상동 on 2020/08/24.
//

#ifndef KGDefine_h
#define KGDefine_h

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <StoreKit/StoreKit.h>
#import <AudioToolbox/AudioToolbox.h>

#import "UnityInterface.h"

#ifdef ADMOB_ENABLE
#import <GoogleMobileAds/GoogleMobileAds.h>
#endif			// #ifdef ADMOB_ENABLE

#if defined IRON_SRC_ENABLE || defined APP_LOVIN_ENABLE
#import <FBAudienceNetwork/FBAdSettings.h>
#endif			// #if defined IRON_SRC_ENABLE || defined APP_LOVIN_ENABLE

#ifdef FIREBASE_MODULE_ENABLE
#import <Firebase/Firebase.h>
#endif			// #ifdef FIREBASE_MODULE_ENABLE

// 기타
#define G_EMPTY_STRING			("")
#define G_INDEX_INVALID			(-1)

// 값 {
#define G_VALUE_INT_0			(0)
#define G_VALUE_INT_1			(1)

#define G_VALUE_FLOAT_0			(0.0f)
#define G_VALUE_FLOAT_1			(1.0f)
// 값 }

// 방향
#define G_ORIENTATION_PORTRAIT			(0)
#define G_ORIENTATION_LANDSCAPE			(1)

// 결과
#define G_RESULT_TRUE			("True")
#define G_RESULT_FALSE			("False")

// 빌드 모드
#define G_BUILD_MODE_DEBUG				("Debug")
#define G_BUILD_MODE_RELEASE			("Release")

// 비율
#define G_SCALE_ACTIVITY_INDICATOR					(0.25f)
#define G_OFFSET_SCALE_ACTIVITY_INDICATOR			(0.01f)

// 버전
#define G_MIN_VERSION_DEVICE_ID_FOR_VENDOR			6.0
#define G_MIN_VERSION_FEEDBACK_GENERATOR			10.0
#define G_MIN_VERSION_IMPACT_INTENSITY				13.0
#define G_MIN_VERSION_INDICATOR						13.0

// 식별자 {
#define G_ID_KEYCHAIN_DEVICE			("KeychainDeviceID")

#define G_KEY_CMD			("Cmd")
#define G_KEY_MSG			("Msg")

#define G_KEY_BUILD_MODE			("BuildMode")
#define G_KEY_ORIENTATION			("Orientation")

#define G_KEY_APP_ID			("AppID")
#define G_KEY_VERSION			("Version")
#define G_KEY_TIMEOUT			("Timeout")

#define G_KEY_ALERT_TITLE					("Title")
#define G_KEY_ALERT_MSG						("Msg")
#define G_KEY_ALERT_OK_BTN_TEXT				("OKBtnText")
#define G_KEY_ALERT_CANCEL_BTN_TEXT			("CancelBtnText")

#define G_KEY_STORE_VERSION					("version")
#define G_KEY_STORE_VERSION_RESULT			("results")

#define G_KEY_VIBRATE_TYPE				("Type")
#define G_KEY_VIBRATE_STYLE				("Style")
#define G_KEY_VIBRATE_INTENSITY			("Intensity")

#define G_KEY_ADMOB_IDS 			("AdmobIDs")
#define G_KEY_RESUME_ADS_ID			("ResumeAdsID")

#define G_KEY_DEVICE_MS_RESULT			("Result")
#define G_KEY_DEVICE_MS_VERSION			("Version")
// 식별자 }

// 명령어 {
#define G_CMD_INIT			("Init")

#define G_CMD_GET_DEVICE_ID				("GetDeviceID")
#define G_CMD_GET_COUNTRY_CODE			("GetCountryCode")
#define G_CMD_GET_STORE_VERSION			("GetStoreVersion")

#define G_CMD_SET_ADS_TRACKING_ENABLE			("SetAdsTrackingEnable")
#define G_CMD_SHOW_ALERT						("ShowAlert")

#define G_CMD_VIBRATE			("Vibrate")
#define G_CMD_INDICATOR			("Indicator")

#define G_CMD_INIT_ADS					("InitAds")
#define G_CMD_LOAD_RESUME_ADS			("LoadResumeAds")
#define G_CMD_SHOW_RESUME_ADS			("ShowResumeAds")
// 명령어 }

// 네트워크 {
#define G_HTTP_METHOD_GET			("GET")
#define G_HTTP_METHOD_POST			("POST")

#define G_URL_FMT_STORE_VERSION			("http://itunes.apple.com/lookup?bundleId=%@")
// 네트워크 }

// 이름
#define G_OBJ_N_DEVICE_MSG_RECEIVER					("CDeviceMsgReceiver")
#define G_FUNC_N_DEVICE_MSG_HANDLE_METHOD			("HandleDeviceMsg")

//! 진동 타입
enum class EVibrateType {
	NONE = -1,
	SELECTION,
	NOTIFICATION,
	IMPACT,
	MAX_VALUE
};

//! 진동 스타일
enum class EVibrateStyle {
	NONE = -1,
	LIGHT,
	MEDIUM,
	HEAVY,
	MAX_VALUE
};

#endif /* KGDefine_h */
