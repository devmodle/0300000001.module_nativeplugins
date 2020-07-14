//
//  CDeviceMsgSender.m
//  Unity-iPhone
//
//  Created by 이상동 on 2020/01/11.
//

#import "CDeviceMsgSender.h"
#import "../../Function/Func+Global.h"

//! 전역 변수
static CDeviceMsgSender *g_pInstance = nil;

//! 디바이스 메세지 전송자
@implementation CDeviceMsgSender

#pragma mark - init
//! 객체를 생성한다
+ (id)alloc {
	@synchronized(CDeviceMsgSender.class) {
		if(g_pInstance == nil) {
			g_pInstance = [[super alloc] init];
		}
	}
	
	return g_pInstance;
}

#pragma mark - instance method
//! 디바이스 식별자 반환 메세지를 전송한다
- (void)sendGetDeviceIDMsg:(NSString *)a_oDeviceID {
	[self send:@(CMD_GET_DEVICE_ID) withMsg:a_oDeviceID];
}

//! 국가 코드 반환 메세지를 전송한다
- (void)sendGetCountryCodeMsg:(NSString *)a_pCountryCode {
	[self send:@(CMD_GET_COUNTRY_CODE) withMsg:a_pCountryCode];
}

//! 스토어 버전 반환 메세지를 전송한다
- (void)sendGetStoreVersionMsg:(NSString *)a_pVersion withResult:(BOOL)a_bIsSuccess {
	auto pResultString = Func::ConvertBoolToString(a_bIsSuccess);
	
	auto pDataList = [NSDictionary dictionaryWithObjectsAndKeys:a_pVersion, @(KEY_DEVICE_MS_VERSION),
					  pResultString, @(KEY_DEVICE_MS_RESULT), nil];
	
	auto pMsg = Func::ConvertObjectToJSONString(pDataList, NULL);
	[self send:@(CMD_GET_STORE_VERSION) withMsg:pMsg];
}

//! 알림 창 출력 메세지를 전송한다
- (void)sendShowAlertMsg:(BOOL)a_bIsTrue {
	auto pMsg = Func::ConvertBoolToString(a_bIsTrue);
	[self send:@(CMD_SHOW_ALERT) withMsg:pMsg];
}

//! 메세지를 전송한다
- (void)send:(NSString *)a_pCmd withMsg:(NSString *)a_pMsg {
	auto pDictionary = [NSDictionary dictionaryWithObjectsAndKeys:a_pCmd, @(KEY_CMD),
						a_pMsg, @(KEY_MSG), nil];
	
	NSString *pString = Func::ConvertObjectToJSONString(pDictionary, NULL);
	UnitySendMessage(OBJ_NAME_DEVICE_MSG_RECEIVER, FUNC_NAME_DEVICE_MSG_HANDLE_METHOD, pString.UTF8String);
}

#pragma mark - class method
//! 인스턴스를 반환한다
+ (instancetype)sharedInstance {
	@synchronized(CDeviceMsgSender.class) {
		if(g_pInstance == nil) {
			g_pInstance = [[CDeviceMsgSender alloc] init];
		}
	}
	
	return g_pInstance;
}

@end			// CDeviceMsgSender
