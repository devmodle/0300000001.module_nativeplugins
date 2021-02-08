//
//  CDeviceMsgSender.m
//  Unity-iPhone
//
//  Created by 이상동 on 2020/01/11.
//

#import "CDeviceMsgSender.h"
#import "../../Function/GFunc.h"

//! 전역 변수
static CDeviceMsgSender *g_pInst = nil;

//! 디바이스 메세지 전송자
@implementation CDeviceMsgSender
#pragma mark - 초기화
//! 객체를 생성한다
+ (id)alloc {
	@synchronized(CDeviceMsgSender.class) {
		// 인스턴스가 없을 경우
		if(g_pInst == nil) {
			g_pInst = [[super alloc] init];
		}
	}
	
	return g_pInst;
}

#pragma mark - 인스턴스 메서드
//! 디바이스 식별자 반환 메세지를 전송한다
- (void)sendGetDeviceIDMsg:(NSString *)a_oDeviceID {
	[self send:@(G_CMD_GET_DEVICE_ID) withDeviceMsg:a_oDeviceID];
}

//! 국가 코드 반환 메세지를 전송한다
- (void)sendGetCountryCodeMsg:(NSString *)a_pCountryCode {
	[self send:@(G_CMD_GET_COUNTRY_CODE) withDeviceMsg:a_pCountryCode];
}

//! 스토어 버전 반환 메세지를 전송한다
- (void)sendGetStoreVersionMsg:(NSString *)a_pVersion withResult:(BOOL)a_bIsSuccess {
	NSString *pString = GFunc::ConvertBoolToString(a_bIsSuccess);
	NSDictionary *pDataList = [NSDictionary dictionaryWithObjectsAndKeys:a_pVersion, @(G_KEY_DEVICE_MS_VERSION), pString, @(G_KEY_DEVICE_MS_RESULT), nil];
	NSString *pMsg = GFunc::ConvertObjToJSONString(pDataList, NULL);
	
	[self send:@(G_CMD_GET_STORE_VERSION) withDeviceMsg:pMsg];
}

//! 경고 창 출력 메세지를 전송한다
- (void)sendShowAlertMsg:(BOOL)a_bIsOK {
	NSString *pMsg = GFunc::ConvertBoolToString(a_bIsOK);
	[self send:@(G_CMD_SHOW_ALERT) withDeviceMsg:pMsg];
}

//! 광고 초기화 메세지를 전송한다
- (void)sendInitAdsMsg:(BOOL)a_bIsSuccess {
	NSString *pMsg = GFunc::ConvertBoolToString(a_bIsSuccess);
	[self send:@(G_CMD_INIT_ADS) withDeviceMsg:pMsg];
}

//! 재개 광고 로드 메세지를 전송한다
- (void)sendLoadResumeAdsMsg:(BOOL)a_bIsSuccess {
	NSString *pMsg = GFunc::ConvertBoolToString(a_bIsSuccess);
	[self send:@(G_CMD_LOAD_RESUME_ADS) withDeviceMsg:pMsg];
}
		
//! 재개 광고 출력 메세지를 전송한다
- (void)sendShowResumeAdsMsg:(BOOL)a_bIsSuccess {
	NSString *pMsg = GFunc::ConvertBoolToString(a_bIsSuccess);
	[self send:@(G_CMD_SHOW_RESUME_ADS) withDeviceMsg:pMsg];
}

//! 디바이스 메세지를 전송한다
- (void)send:(NSString *)a_pCmd withDeviceMsg:(NSString *)a_pMsg {
	NSDictionary *pDictionary = [NSDictionary dictionaryWithObjectsAndKeys:a_pCmd, @(G_KEY_CMD), a_pMsg, @(G_KEY_MSG), nil];
	NSString *pString = GFunc::ConvertObjToJSONString(pDictionary, NULL);
	
	UnitySendMessage(G_OBJ_N_DEVICE_MSG_RECEIVER, G_FUNC_N_DEVICE_MSG_HANDLE_METHOD, pString.UTF8String);
}

#pragma mark - 클래스 메서드
//! 인스턴스를 반환한다
+ (instancetype)sharedInst {
	@synchronized(CDeviceMsgSender.class) {
		// 인스턴스가 없을 경우
		if(g_pInst == nil) {
			g_pInst = [[CDeviceMsgSender alloc] init];
		}
	}
	
	return g_pInst;
}
@end			// CDeviceMsgSender
