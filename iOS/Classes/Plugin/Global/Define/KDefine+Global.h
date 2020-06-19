//
//  KGlobalDefine.h
//  Unity-iPhone
//
//  Created by 이상동 on 2020/01/10.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <AudioToolbox/AudioToolbox.h>

#import "UnityInterface.h"

#define EMPTY_STRING			("")

// 결과
#define RESULT_TRUE				("True")
#define RESULT_FALSE			("False")

// 빌드 모드
#define BUILD_MODE_DEBUG			("Debug")
#define BUILD_MODE_RELEASE			("Release")

// 식별자 {
#define ID_KEYCHAIN_DEVICE			("KeychainDeviceID")

#define SYSTEM_SOUND_ID_LIGHT			(1519)
#define SYSTEM_SOUND_ID_MEDIUM			(1102)
#define SYSTEM_SOUND_ID_HEAVY			(1520)
// 식별자 }

// 비율
#define SCALE_ACTIVITY_INDICATOR				(0.25f)
#define SCALE_ACTIVITY_INDICATOR_OFFSET			(0.01f)

// 버전
#define MIN_VERSION_DEVICE_ID_FOR_VENDOR			6.0
#define MIN_VERSION_FEEDBACK_GENERATOR				10.0
#define MIN_VERSION_IMPACT_INTENSITY				13.0
#define MIN_VERSION_ACTIVITY_INDICATOR				13.0

// 명령어
#define COMMAND_GET_DEVICE_ID				("GetDeviceID")
#define COMMAND_GET_COUNTRY_CODE			("GetCountryCode")
#define COMMAND_GET_STORE_VERSION			("GetStoreVersion")
#define COMMAND_SET_BUILD_MODE				("SetBuildMode")
#define COMMAND_SHOW_ALERT					("ShowAlert")
#define COMMAND_VIBRATE						("Vibrate")
#define COMMAND_ACTIVITY_INDICATOR			("ActivityIndicator")

// 키 {
#define KEY_COMMAND			("Command")
#define KEY_MESSAGE			("Message")

#define KEY_APP_ID			("AppID")
#define KEY_VERSION			("Version")
#define KEY_TIMEOUT			("Timeout")

#define KEY_ALERT_TITLE							("Title")
#define KEY_ALERT_MESSAGE						("Message")
#define KEY_ALERT_OK_BUTTON_TEXT				("OKButtonText")
#define KEY_ALERT_CANCEL_BUTTON_TEXT			("CancelButtonText")

#define KEY_STORE_VERSION					("version")
#define KEY_STORE_VERSION_RESULT			("results")

#define KEY_VIBRATE_TYPE				("Type")
#define KEY_VIBRATE_STYLE				("Style")
#define KEY_VIBRATE_INTENSITY			("Intensity")

#define KEY_DEVICE_MS_RESULT			("Result")
#define KEY_DEVICE_MS_VERSION			("Version")
// 키 }

// 네트워크 {
#define HTTP_METHOD_GET				("GET")
#define HTTP_METHOD_POST			("POST")

#define URL_FORMAT_STORE_VERSION			("http://itunes.apple.com/lookup?bundleId=%@")
// 네트워크 }

// 이름
#define OBJ_NAME_DEVICE_MESSAGE_RECEIVER				("CDeviceMessageReceiver")
#define FUNC_NAME_DEVICE_MESSAGE_HANDLE_METHOD			("HandleDeviceMessage")

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
