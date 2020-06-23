//
//  CDeviceMessageSender.m
//  Unity-iPhone
//
//  Created by 이상동 on 2020/01/11.
//

#import "CDeviceMessageSender.h"
#import "../../Function/Func+Global.h"

//! 전역 변수
static CDeviceMessageSender *g_pInstance = nil;

//! 디바이스 메세지 전송자
@implementation CDeviceMessageSender

#pragma mark - init
//! 객체를 생성한다
+ (id)alloc {
	@synchronized(CDeviceMessageSender.class) {
		if(g_pInstance == nil) {
			g_pInstance = [[super alloc] init];
		}
	}
	
	return g_pInstance;
}

#pragma mark - instance method
//! 디바이스 식별자 반환 메세지를 전송한다
- (void)sendGetDeviceIDMessage:(NSString *)a_oDeviceID {
	[self send:@(COMMAND_GET_DEVICE_ID) withMessage:a_oDeviceID];
}

//! 국가 코드 반환 메세지를 전송한다
- (void)sendGetCountryCodeMessage:(NSString *)a_pCountryCode {
	[self send:@(COMMAND_GET_COUNTRY_CODE) withMessage:a_pCountryCode];
}

//! 스토어 버전 반환 메세지를 전송한다
- (void)sendGetStoreVersionMessage:(NSString *)a_pVersion withResult:(BOOL)a_bIsSuccess {
	auto pDataList = [NSDictionary dictionaryWithObjectsAndKeys:a_pVersion, @(KEY_DEVICE_MS_VERSION),
					  Func::ConvertBoolToString(a_bIsSuccess), @(KEY_DEVICE_MS_RESULT), nil];
	
	auto pMessage = Func::ConvertObjectToJSONString(pDataList, NULL);
	[self send:@(COMMAND_GET_STORE_VERSION) withMessage:pMessage];
}

//! 알림 창 출력 메세지를 전송한다
- (void)sendShowAlertMessage:(BOOL)a_bIsTrue {
	auto pMessage = Func::ConvertBoolToString(a_bIsTrue);
	[self send:@(COMMAND_SHOW_ALERT) withMessage:pMessage];
}

//! 메세지를 전송한다
- (void)send:(NSString *)a_pCommand withMessage:(NSString *)a_pMessage {
	auto pDictionary = [NSDictionary dictionaryWithObjectsAndKeys:a_pCommand, @(KEY_COMMAND),
						a_pMessage, @(KEY_MESSAGE), nil];
	
	NSString *pString = Func::ConvertObjectToJSONString(pDictionary, NULL);
	UnitySendMessage(OBJ_NAME_DEVICE_MESSAGE_RECEIVER, FUNC_NAME_DEVICE_MESSAGE_HANDLE_METHOD, pString.UTF8String);
}

#pragma mark - class method
//! 인스턴스를 반환한다
+ (instancetype)sharedInstance {
	@synchronized(CDeviceMessageSender.class) {
		if(g_pInstance == nil) {
			g_pInstance = [[CDeviceMessageSender alloc] init];
		}
	}
	
	return g_pInstance;
}

@end			// CDeviceMessageSender
