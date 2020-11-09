//
//  CiOSPlugin.m
//  Unity-iPhone
//
//  Created by 이상동 on 2020/02/26.
//

#import "CiOSPlugin.h"
#import "Global/Function/GFunc.h"
#import "Global/Utility/Ads/CAdsManager.h"
#import "Global/Utility/Platform/CDeviceMsgSender.h"

//! 전역 변수
static CiOSPlugin *g_pInstance = nil;

//! iOS 플러그인 - Private
@interface CiOSPlugin (Private) {
	// Nothing
}

//! 초기화 메세지를 처리한다
- (void)handleInitMsg:(const char *)a_pszMsg;

//! 디바이스 식별자 반환 메세지를 처리한다
- (void)handleGetDeviceIDMsg:(const char *)a_pszMsg;

//! 국가 코드 반환 메세지를 처리한다
- (void)handleGetCountryCodeMsg:(const char *)a_pszMsg;

//! 스토어 버전 반환 메세지를 처리한다
- (void)handleGetStoreVersionMsg:(const char *)a_pszMsg;

//! 경고 창 출력 메세지를 처리한다
- (void)handleShowAlertMsg:(const char *)a_pszMsg;

//! 진동 메세지를 처리한다
- (void)handleVibrateMsg:(const char *)a_pszMsg;

//! 추적 메세지를 처리한다
- (void)handleTrackingMsg:(const char *)a_pszMsg;

//! 액티비티 인디케이터 메세지를 처리한다
- (void)handleActivityIndicatorMsg:(const char *)a_pszMsg;

//! 광고 초기화 메세지를 처리한다
- (void)handleInitAdsMsg:(const char *)a_pszMsg;

//! 재개 광고 로드 메세지를 처리한다
- (void)handleLoadResumeAdsMsg:(const char *)a_pszMsg;

//! 재개 광고 출력 메세지를 처리한다
- (void)handleShowResumeAdsMsg:(const char *)a_pszMsg;
@end			// CiOSPlugin (Private)

extern "C" {
	//! 유니티 메세지를 처리한다
	void HandleUnityMsg(const char *a_pszCmd, const char *a_pszMsg) {
		NSLog(@"CiOSPlugin.HandleUnityMsg: %@, %@", @(a_pszCmd), @(a_pszMsg));
		
		// 초기화 메세지 일 경우
		if(strcmp(a_pszCmd, G_CMD_INIT) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleInitMsg:a_pszMsg];
		}
		// 디바이스 식별자 반환 메세지 일 경우
		else if(strcmp(a_pszCmd, G_CMD_GET_DEVICE_ID) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleGetDeviceIDMsg:a_pszMsg];
		}
		// 국가 코드 반환 메세지 일 경우
		else if(strcmp(a_pszCmd, G_CMD_GET_COUNTRY_CODE) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleGetCountryCodeMsg:a_pszMsg];
		}
		// 스토어 버전 반환 메세지 일 경우
		else if(strcmp(a_pszCmd, G_CMD_GET_STORE_VERSION) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleGetStoreVersionMsg:a_pszMsg];
		}
		// 경고 창 출력 메세지 일 경우
		else if(strcmp(a_pszCmd, G_CMD_SHOW_ALERT) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleShowAlertMsg:a_pszMsg];
		}
		// 진동 메세지 일 경우
		else if(strcmp(a_pszCmd, G_CMD_VIBRATE) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleVibrateMsg:a_pszMsg];
		}
		// 추적 메세지 일 경우
		else if(strcmp(a_pszCmd, G_CMD_TRACKING) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleTrackingMsg:a_pszMsg];
		}
		// 액티비티 인디케이터 메세지 일 경우
		else if(strcmp(a_pszCmd, G_CMD_ACTIVITY_INDICATOR) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleActivityIndicatorMsg:a_pszMsg];
		}
		// 광고 초기화 메세지 일 경우
		else if(strcmp(a_pszCmd, G_CMD_INIT_ADS) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleInitAdsMsg:a_pszMsg];
		}
		// 재개 광고 로드 메세지 일 경우
		else if(strcmp(a_pszCmd, G_CMD_LOAD_RESUME_ADS) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleLoadResumeAdsMsg:a_pszMsg];
		}
		// 재개 광고 출력 메세지 일 경우
		else if(strcmp(a_pszCmd, G_CMD_SHOW_RESUME_ADS) == G_VALUE_INT_0) {
			[CiOSPlugin.sharedInstance handleShowResumeAdsMsg:a_pszMsg];
		}
	}
}

//! iOS 플러그인
@implementation CiOSPlugin
#pragma mark - 프로퍼티
@synthesize deviceID = m_pDeviceID;

@synthesize keychainItemWrapper = m_pKeychainItemWrapper;
@synthesize activityIndicatorView = m_pActivityIndicatorView;

@synthesize impactGeneratorList = m_pImpactGeneratorList;
@synthesize selectionGenerator = m_pSelectionGenerator;
@synthesize notificationGenerator = m_pNotificationGenerator;

#ifdef FIREBASE_MODULE_ENABLE
@synthesize trackingList = m_pTrackingList;
#endif			// #ifdef FIREBASE_MODULE_ENABLE

#pragma mark - 초기화
//! 객체를 생성한다
+ (id)alloc {
	@synchronized(CiOSPlugin.class) {
		// 인스턴스가 없을 경우
		if(g_pInstance == nil) {
			g_pInstance = [[super alloc] init];
		}
	}
	
	return g_pInstance;
}

#pragma mark - 인스턴스 메서드
//! 디바이스 식별자를 반환한다
- (NSString *)deviceID {
	// 디바이스 식별자가 유효하지 않을 경우
	if(!GFunc::IsValid(m_pDeviceID)) {
		m_pDeviceID = (NSString *)[self.keychainItemWrapper objectForKey:(__bridge id)kSecAttrAccount];
	}
	
	return m_pDeviceID;
}

//! 키체인 아이템 래퍼를 반환한다
- (KeychainItemWrapper *)keychainItemWrapper {
	// 키체인 아이템 래퍼가 없을 경우
	if(m_pKeychainItemWrapper == nil) {
		m_pKeychainItemWrapper = [[KeychainItemWrapper alloc] initWithIdentifier:@(G_ID_KEYCHAIN_DEVICE)
																	 accessGroup:nil];
	}
	
	return m_pKeychainItemWrapper;
}

//! 액티비티 인디케이터 뷰를 반환한다
- (UIActivityIndicatorView *)activityIndicatorView {
	// 액티비티 인디케이터가 없을 경우
	if(m_pActivityIndicatorView == nil) {
		UIActivityIndicatorViewStyle eIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
		
		// 새로운 액티비티 인디케이터를 지원 할 경우
		if(@available(iOS G_MIN_VERSION_ACTIVITY_INDICATOR, *)) {
			eIndicatorViewStyle = UIActivityIndicatorViewStyleLarge;
		}
		
		m_pActivityIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:eIndicatorViewStyle];
		m_pActivityIndicatorView.color = [UIColor colorWithWhite:G_VALUE_FLOAT_1 alpha:G_VALUE_FLOAT_1];
		m_pActivityIndicatorView.center = self.rootViewController.view.center;
		m_pActivityIndicatorView.hidesWhenStopped = YES;
		
		// 크기를 설정한다 {
		float fSize = MIN(self.rootViewController.view.bounds.size.width, self.rootViewController.view.bounds.size.height);
		fSize *= G_SCALE_ACTIVITY_INDICATOR;
		
		float fScaleX = fSize / m_pActivityIndicatorView.bounds.size.width;
		float fScaleY = fSize / m_pActivityIndicatorView.bounds.size.height;
		
		CGAffineTransform stTransform = m_pActivityIndicatorView.transform;
		m_pActivityIndicatorView.transform = CGAffineTransformScale(stTransform, fScaleX, fScaleY);
		// 크기를 설정한다 }
		
		// 위치를 설정한다 {
		float fOffset = MIN(self.rootViewController.view.bounds.size.width, self.rootViewController.view.bounds.size.height);
		fOffset *= G_SCALE_ACTIVITY_INDICATOR_OFFSET;
		
		stTransform = m_pActivityIndicatorView.transform;
		m_pActivityIndicatorView.transform = CGAffineTransformTranslate(stTransform, G_VALUE_FLOAT_0, -fOffset);
		// 위치를 설정한다 }
		
		[self.rootViewController.view addSubview:m_pActivityIndicatorView];
	}
	
	return m_pActivityIndicatorView;
}

//! 충격 피드백 생성자 리스트를 반환한다
- (NSArray *)impactGeneratorList {
	// 충격 피드백 생성자 리스트가 없을 경우
	if(m_pImpactGeneratorList == nil) {
		UIImpactFeedbackGenerator *pLightGenerator = [[UIImpactFeedbackGenerator alloc] initWithStyle:UIImpactFeedbackStyleLight];
		UIImpactFeedbackGenerator *pMediumGenerator = [[UIImpactFeedbackGenerator alloc] initWithStyle:UIImpactFeedbackStyleMedium];
		UIImpactFeedbackGenerator *pHeavyGenerator = [[UIImpactFeedbackGenerator alloc] initWithStyle:UIImpactFeedbackStyleHeavy];
		
		m_pImpactGeneratorList = [NSArray arrayWithObjects:pLightGenerator, pMediumGenerator, pHeavyGenerator, nil];
	}
	
	return m_pImpactGeneratorList;
}

//! 선택 피드백 생성자를 반환한다
- (UISelectionFeedbackGenerator *)selectionGenerator {
	// 선택 피드백 생성자가 없을 경우
	if(m_pSelectionGenerator == nil) {
		m_pSelectionGenerator = [[UISelectionFeedbackGenerator alloc] init];
	}
	
	return m_pSelectionGenerator;
}

//! 경고 피드백 생성자를 반환한다
- (UINotificationFeedbackGenerator *)notificationGenerator {
	// 경고 피드백 생성자가 없을 경우
	if(m_pNotificationGenerator == nil) {
		m_pNotificationGenerator = [[UINotificationFeedbackGenerator alloc] init];
	}
	
	return m_pNotificationGenerator;
}

//! 루트 뷰 컨트롤러를 반환한다
- (UIViewController *)rootViewController {
	return self.unityAppController.rootViewController;
}

//! 유니티 앱 컨트롤러를 반환한다
- (UnityAppController *)unityAppController {
	return (UnityAppController *)UIApplication.sharedApplication.delegate;
}

//! 초기화 메세지를 처리한다
- (void)handleInitMsg:(const char *)a_pszMsg {
	NSDictionary *pDataList = (NSDictionary *)GFunc::ConvertJSONStringToObj(@(a_pszMsg), NULL);
	
	NSString *pBuildMode = (NSString *)[pDataList objectForKey:@(G_KEY_BUILD_MODE)];
	NSString *pOrientation = (NSString *)[pDataList objectForKey:@(G_KEY_ORIENTATION)];
	
	self.buildMode = pBuildMode;
	
	// 세로 모드 일 경우
	if(pOrientation.intValue == G_ORIENTATION_PORTRAIT) {
		self.orientation = UIInterfaceOrientationPortrait;
	} else {
		self.orientation = UIApplication.sharedApplication.statusBarOrientation;
	}
}

//! 디바이스 식별자 반환 메세지를 처리한다
- (void)handleGetDeviceIDMsg:(const char *)a_pszMsg {
	// 디바이스 식별자가 유효하지 않을 경우
	if(!GFunc::IsValid(self.deviceID)) {
		// UUID 를 지원 할 경우
		if(@available(iOS G_MIN_VERSION_DEVICE_ID_FOR_VENDOR, *)) {
			self.deviceID = UIDevice.currentDevice.identifierForVendor.UUIDString;
		} else {
			CFUUIDRef pUUID = CFUUIDCreate(kCFAllocatorDefault);
			self.deviceID = (__bridge NSString *)CFUUIDCreateString(kCFAllocatorDefault, pUUID);
		}
		
		[self.keychainItemWrapper setObject:self.deviceID forKey:(__bridge id)kSecAttrAccount];
	}
	
	[CDeviceMsgSender.sharedInstance sendGetDeviceIDMsg:self.deviceID];
}

//! 국가 코드 반환 메세지를 처리한다
- (void)handleGetCountryCodeMsg:(const char *)a_pszMsg {
	NSLocale *pLocale = NSLocale.currentLocale;
	[CDeviceMsgSender.sharedInstance sendGetCountryCodeMsg:pLocale.countryCode];
}

//! 스토어 버전 반환 메세지를 처리한다
- (void)handleGetStoreVersionMsg:(const char *)a_pszMsg {
	NSDictionary *pDataList = (NSDictionary *)GFunc::ConvertJSONStringToObj(@(a_pszMsg), NULL);
	
	NSString *pAppID = (NSString *)[pDataList objectForKey:@(G_KEY_APP_ID)];
	NSString *pVersion = (NSString *)[pDataList objectForKey:@(G_KEY_VERSION)];
	NSString *pTimeout = (NSString *)[pDataList objectForKey:@(G_KEY_TIMEOUT)];
	
	// 디버그 모드 일 경우
	if([self.buildMode isEqualToString:@(G_BUILD_MODE_DEBUG)]) {
		[CDeviceMsgSender.sharedInstance sendGetStoreVersionMsg:pVersion withResult:YES];
	} else {
		NSString *pURL = [NSString stringWithFormat:@(G_URL_FORMAT_STORE_VERSION), pAppID];
		NSMutableURLRequest * pURLRequest = GFunc::MakeURLRequest(pURL, @(G_HTTP_METHOD_GET), pTimeout.doubleValue);
		
		// 데이터를 수신했을 경우
		[NSURLSession.sharedSession dataTaskWithRequest:pURLRequest completionHandler:^void(NSData *a_pData, NSURLResponse *a_pResponse, NSError *a_pError) {
			NSLog(@"CiOSPlugin.onHandleGetStoreVersionMsg: %@", a_pData);
			
			// 스토어 버전 로드에 실패했을 경우
			if(a_pError != nil || (a_pData == nil || a_pResponse == nil)) {
				NSLog(@"CiOSPlugin.onHandleGetStoreVersionMsg Fail: %@", a_pError);
				[CDeviceMsgSender.sharedInstance sendGetStoreVersionMsg:pVersion withResult:NO];
			} else {
				NSString *pString = [[NSString alloc] initWithData:a_pData encoding:NSUTF8StringEncoding];
				NSDictionary *pResponseDataList = (NSDictionary *)GFunc::ConvertJSONStringToObj(pString, NULL);
				
				NSArray *pVersionInfoList = (NSArray *)[pResponseDataList objectForKey:@(G_KEY_STORE_VERSION_RESULT)];
				NSDictionary *pVersionInfo = (NSDictionary *)[pVersionInfoList lastObject];
				
				NSString *pStoreVersion = (NSString *)[pVersionInfo objectForKey:@(G_KEY_STORE_VERSION)];
				NSLog(@"CiOSPlugin.onHandleGetStoreVersionMsg Success: %@", pStoreVersion);
				
				// 스토어 버전이 유효 할 경우
				if(GFunc::IsValid(pStoreVersion)) {
					[CDeviceMsgSender.sharedInstance sendGetStoreVersionMsg:pStoreVersion withResult:YES];
				} else {
					[CDeviceMsgSender.sharedInstance sendGetStoreVersionMsg:pVersion withResult:NO];
				}
			}
		}];
	}
}

//! 경고 창 출력 메세지를 처리한다
- (void)handleShowAlertMsg:(const char *)a_pszMsg {
	NSDictionary *pDataList = (NSDictionary *)GFunc::ConvertJSONStringToObj(@(a_pszMsg), NULL);
	
	NSString *pTitle = (NSString *)[pDataList objectForKey:@(G_KEY_ALERT_TITLE)];
	NSString *pMsg = (NSString *)[pDataList objectForKey:@(G_KEY_ALERT_MSG)];
	NSString *pOKBtnText = (NSString *)[pDataList objectForKey:@(G_KEY_ALERT_OK_BTN_TEXT)];
	NSString *pCancelBtnText = (NSString *)[pDataList objectForKey:@(G_KEY_ALERT_CANCEL_BTN_TEXT)];
	
	UIAlertController *pAlertController = [UIAlertController alertControllerWithTitle:GFunc::IsValid(pTitle) ? pTitle : nil
																			  message:pMsg
																	   preferredStyle:UIAlertControllerStyleAlert];
	
	// 확인 버튼을 눌렀을 경우
	[pAlertController addAction:[UIAlertAction actionWithTitle:pOKBtnText
														 style:UIAlertActionStyleDefault
													   handler:^void(UIAlertAction *a_pSender)
	{
		[CDeviceMsgSender.sharedInstance sendShowAlertMsg:YES];
	}]];
	
	// 취소 버튼 텍스트가 유효 할 경우
	if(GFunc::IsValid(pCancelBtnText)) {
		// 취소 버튼을 눌렀을 경우
		[pAlertController addAction:[UIAlertAction actionWithTitle:pCancelBtnText
															 style:UIAlertActionStyleCancel
														   handler:^void(UIAlertAction *a_pSender)
		{
			[CDeviceMsgSender.sharedInstance sendShowAlertMsg:NO];
		}]];
	}
	
	// 경고 창을 출력한다
	[self.rootViewController presentViewController:pAlertController animated:YES completion:NULL];
}

//! 진동 메세지를 처리한다
- (void)handleVibrateMsg:(const char *)a_pszMsg {
	NSDictionary *pDataList = (NSDictionary *)GFunc::ConvertJSONStringToObj(@(a_pszMsg), NULL);
	
	NSString *pType = (NSString *)[pDataList objectForKey:@(G_KEY_VIBRATE_TYPE)];
	NSString *pStyle = (NSString *)[pDataList objectForKey:@(G_KEY_VIBRATE_STYLE)];
	
	EVibrateType eVibrateType = (EVibrateType)pType.intValue;
	EVibrateStyle eVibrateStyle = (EVibrateStyle)pStyle.intValue;
	
	// 진동 타입이 유효 할 경우
	if(GFunc::IsValid(eVibrateType)) {
		// 햅틱 진동을 지원 할 경우
		if(@available(iOS G_MIN_VERSION_FEEDBACK_GENERATOR, *)) {
			// 선택 진동 모드 일 경우
			if(eVibrateType == EVibrateType::SELECTION) {
				[self.selectionGenerator prepare];
				[self.selectionGenerator selectionChanged];
			}
			// 경고 진동 모드 일 경우
			else if(eVibrateType == EVibrateType::NOTIFICATION) {
				[self.notificationGenerator prepare];
				[self.notificationGenerator notificationOccurred:(UINotificationFeedbackType)eVibrateStyle];
			} else {
				UIImpactFeedbackStyle eFeedbackStyle = (UIImpactFeedbackStyle)eVibrateStyle;
				UIImpactFeedbackGenerator *pImpactGenerator = (UIImpactFeedbackGenerator *)[self.impactGeneratorList objectAtIndex:eFeedbackStyle];
				
				[pImpactGenerator prepare];
				
				// 진동 세기를 지원 할 경우
				if(@available(iOS G_MIN_VERSION_IMPACT_INTENSITY, *)) {
					NSString *pIntensity = (NSString *)[pDataList objectForKey:@(G_KEY_VIBRATE_INTENSITY)];
					[pImpactGenerator impactOccurredWithIntensity:pIntensity.floatValue];
				} else {
					[pImpactGenerator impactOccurred];
				}
			}
		} else {
			AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
		}
	}
}

//! 추적 메세지를 처리한다
- (void)handleTrackingMsg:(const char *)a_pszMsg {
#ifdef FIREBASE_MODULE_ENABLE
	NSDictionary *pDataList = (NSDictionary *)GFunc::ConvertJSONStringToObj(@(a_pszMsg), NULL);
	
	NSString *pName = (NSString *)[pDataList objectForKey:@(G_KEY_TRACKING_NAME)];
	NSString *pIsStartString = (NSString *)[pDataList objectForKey:@(G_KEY_TRACKING_IS_START)];
	
	BOOL bIsStart = GFunc::ConvertStringToBool(pIsStartString);
	BOOL bIsContainsTracking = [self.trackingList objectForKey:pName] != nil;
	
	// 시작 모드 일 경우
	if(bIsStart && !bIsContainsTracking) {
		FIRTrace *pTracking = [FIRPerformance startTraceWithName:pName];
		NSString *pDatasString = (NSString *)[pDataList objectForKey:@(G_KEY_TRACKING_DATAS)];
		
		// 데이터가 존재 할 경우
		if(pDatasString != nil) {
			NSDictionary *pTrackingDataList = (NSDictionary *)GFunc::ConvertJSONStringToObj(pDatasString, NULL);
			NSArray *pKeyList = pTrackingDataList.allKeys;
			
			for(int i = 0; i < pKeyList.count; ++i) {
				NSString *pKey = (NSString *)[pKeyList objectAtIndex:i];
				NSString *pValue = (NSString *)[pTrackingDataList objectForKey:pKey];
				
				[pTracking setValue:pKey forAttribute:pValue];
			}
		}
		
		[pTracking start];
		[self.trackingList setObject:pName forKey:pTracking];
	}
	// 중지 모드 일 경우
	if(!bIsStart && bIsContainsTracking) {
		FIRTrace *pTracking = (FIRTrace *)[self.trackingList objectForKey:pName];
		
		[pTracking stop];
		[self.trackingList removeObjectForKey:pName];
	}
#endif			// #ifdef FIREBASE_MODULE_ENABLE
}

//! 액티비티 인디케이터 메세지를 처리한다
- (void)handleActivityIndicatorMsg:(const char *)a_pszMsg {
	// 출력 모드 일 경우
	if(GFunc::ConvertStringToBool(@(a_pszMsg))) {
		[self.activityIndicatorView startAnimating];
	} else {
		[self.activityIndicatorView stopAnimating];
	}
}

//! 광고 초기화 메세지를 처리한다
- (void)handleInitAdsMsg:(const char *)a_pszMsg {
	NSDictionary *pDataList = (NSDictionary *)GFunc::ConvertJSONStringToObj(@(a_pszMsg), NULL);
	
	NSString *pResumeAdsID = (NSString *)[pDataList objectForKey:@(G_KEY_RESUME_ADS_ID)];
	NSString *pAdmobIDsString = (NSString *)[pDataList objectForKey:@(G_KEY_ADMOB_IDS)];
	
	NSArray *pAdmobIDList = (NSArray *)GFunc::ConvertJSONStringToObj(pAdmobIDsString, NULL);
	[CAdsManager.sharedInstance init:pResumeAdsID withDeviceIDList:pAdmobIDList];
}

//! 재개 광고 로드 메세지를 처리한다
- (void)handleLoadResumeAdsMsg:(const char *)a_pszMsg {
	[CAdsManager.sharedInstance loadResumeAds];
}

//! 재개 광고 출력 메세지를 처리한다
- (void)handleShowResumeAdsMsg:(const char *)a_pszMsg {
	[CAdsManager.sharedInstance showResumeAds];
}

#ifdef FIREBASE_MODULE_ENABLE
- (NSMutableDictionary *)trackingList {
	// 추적 리스트가 없을 경우
	if(m_pTrackingList == nil) {
		m_pTrackingList = [[NSMutableDictionary alloc] init];
	}
	
	return m_pTrackingList;
}
#endif			// #ifdef FIREBASE_MODULE_ENABLE

#pragma mark - 클래스 메서드
//! 인스턴스를 반환한다
+ (instancetype)sharedInstance {
	@synchronized(CiOSPlugin.class) {
		// 인스턴스가 없을 경우
		if(g_pInstance == nil) {
			g_pInstance = [[CiOSPlugin alloc] init];
		}
	}
	
	return g_pInstance;
}
@end			// CiOSPlugin
