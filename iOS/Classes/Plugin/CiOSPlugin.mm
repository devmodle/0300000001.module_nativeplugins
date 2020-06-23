//
//  CiOSPlugin.m
//  Unity-iPhone
//
//  Created by 이상동 on 2020/02/26.
//

#import "CiOSPlugin.h"
#import "Global/Function/Func+Global.h"
#import "Global/Utility/Platform/CDeviceMessageSender.h"

//! 전역 변수
static CiOSPlugin *g_pInstance = nil;

//! iOS 플러그인 - Private
@interface CiOSPlugin (Private) {
	// Nothing
}

//! 디바이스 식별자 반환 메세지를 처리한다
- (void)handleGetDeviceIDMessage:(const char *)a_pszMessage;

//! 국가 코드 반환 메세지를 처리한다
- (void)handleGetCountryCodeMessage:(const char *)a_pszMessage;

//! 스토어 버전 반환 메세지를 처리한다
- (void)handleGetStoreVersionMessage:(const char *)a_pszMessage;

//! 빌드 모드 변경 메세지를 처리한다
- (void)handleSetBuildModeMessage:(const char *)a_pszMessage;

//! 알림 창 출력 메세지를 처리한다
- (void)handleShowAlertMessage:(const char *)a_pszMessage;

//! 진동 메세지를 처리한다
- (void)handleVibrateMessage:(const char *)a_pszMessage;

//! 액티비티 인디게이터 메세지를 처리한다
- (void)handleActivityIndicatorMessage:(const char *)a_pszMessage;

@end			// CiOSPlugin (Private)

extern "C" {
	//! 유니티 메세지를 처리한다
	void HandleUnityMessage(const char *a_pszCommand, const char *a_pszMessage) {
		NSLog(@"CiOSPlugin.HandleUnityMessage: %@, %@", @(a_pszCommand), @(a_pszMessage));
		
		if(strcmp(a_pszCommand, COMMAND_GET_DEVICE_ID) == 0) {
			[CiOSPlugin.sharedInstance handleGetDeviceIDMessage:a_pszMessage];
		} else if(strcmp(a_pszCommand, COMMAND_GET_COUNTRY_CODE) == 0) {
			[CiOSPlugin.sharedInstance handleGetCountryCodeMessage:a_pszMessage];
		} else if(strcmp(a_pszCommand, COMMAND_GET_STORE_VERSION) == 0) {
			[CiOSPlugin.sharedInstance handleGetStoreVersionMessage:a_pszMessage];
		} else if(strcmp(a_pszCommand, COMMAND_SET_BUILD_MODE) == 0) {
			[CiOSPlugin.sharedInstance handleSetBuildModeMessage:a_pszMessage];
		} else if(strcmp(a_pszCommand, COMMAND_SHOW_ALERT) == 0) {
			[CiOSPlugin.sharedInstance handleShowAlertMessage:a_pszMessage];
		} else if(strcmp(a_pszCommand, COMMAND_VIBRATE) == 0) {
			[CiOSPlugin.sharedInstance handleVibrateMessage:a_pszMessage];
		} else if(strcmp(a_pszCommand, COMMAND_ACTIVITY_INDICATOR) == 0) {
			[CiOSPlugin.sharedInstance handleActivityIndicatorMessage:a_pszMessage];
		}
	}
}

//! iOS 플러그인
@implementation CiOSPlugin

// 프로퍼티 {
@synthesize buildMode;

@synthesize deviceID = m_pDeviceID;
@synthesize keychainItemWrapper = m_pKeychainItemWrapper;
@synthesize activityIndicatorView = m_pActivityIndicatorView;

@synthesize impactGeneratorList = m_pImpactGeneratorList;
@synthesize selectionGenerator = m_pSelectionGenerator;
@synthesize notificationGenerator = m_pNotificationGenerator;
// 프로퍼티 }

#pragma mark - init
//! 객체를 생성한다
+ (id)alloc {
	@synchronized(CiOSPlugin.class) {
		if(g_pInstance == nil) {
			g_pInstance = [[super alloc] init];
		}
	}
	
	return g_pInstance;
}

#pragma mark - instance method
//! 디바이스 식별자를 반환한다
- (NSString *)deviceID {
	if(!Func::IsValidString(m_pDeviceID)) {
		m_pDeviceID = (NSString *)[self.keychainItemWrapper objectForKey:(__bridge id)kSecAttrAccount];
	}
	
	return m_pDeviceID;
}

//! 키체인 아이템 래퍼를 반환한다
- (KeychainItemWrapper *)keychainItemWrapper {
	if(m_pKeychainItemWrapper == nil) {
		m_pKeychainItemWrapper = [[KeychainItemWrapper alloc] initWithIdentifier:@(ID_KEYCHAIN_DEVICE)
																	 accessGroup:nil];
	}
	
	return m_pKeychainItemWrapper;
}

//! 액티비티 인디게이터 뷰를 반환한다
- (UIActivityIndicatorView *)activityIndicatorView {
	if(m_pActivityIndicatorView == nil) {
		auto eIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
		
		if(@available(iOS MIN_VERSION_ACTIVITY_INDICATOR, *)) {
			eIndicatorViewStyle = UIActivityIndicatorViewStyleLarge;
		}
		
		m_pActivityIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:eIndicatorViewStyle];
		m_pActivityIndicatorView.color = [UIColor colorWithWhite:1.0f alpha:1.0f];
		m_pActivityIndicatorView.center = self.rootViewController.view.center;
		m_pActivityIndicatorView.hidesWhenStopped = YES;
		
		// 크기를 설정한다 {
		float fSize = MIN(self.rootViewController.view.bounds.size.width, self.rootViewController.view.bounds.size.height);
		fSize *= SCALE_ACTIVITY_INDICATOR;
		
		float fScaleX = fSize / m_pActivityIndicatorView.bounds.size.width;
		float fScaleY = fSize / m_pActivityIndicatorView.bounds.size.height;
		
		auto stTransform = m_pActivityIndicatorView.transform;
		m_pActivityIndicatorView.transform = CGAffineTransformScale(stTransform, fScaleX, fScaleY);
		// 크기를 설정한다 }
		
		// 위치를 설정한다 {
		float fOffset = MIN(self.rootViewController.view.bounds.size.width, self.rootViewController.view.bounds.size.height);
		fOffset *= SCALE_ACTIVITY_INDICATOR_OFFSET;
		
		stTransform = m_pActivityIndicatorView.transform;
		m_pActivityIndicatorView.transform = CGAffineTransformTranslate(stTransform, 0.0f, -fOffset);
		// 위치를 설정한다 }
		
		[self.rootViewController.view addSubview:m_pActivityIndicatorView];
	}
	
	return m_pActivityIndicatorView;
}

//! 충격 피드백 생성자 리스트를 반환한다
- (NSArray *)impactGeneratorList {
	if(m_pImpactGeneratorList == nil) {
		auto pLightGenerator = [[UIImpactFeedbackGenerator alloc] initWithStyle:UIImpactFeedbackStyleLight];
		auto pMediumGenerator = [[UIImpactFeedbackGenerator alloc] initWithStyle:UIImpactFeedbackStyleMedium];
		auto pHeavyGenerator = [[UIImpactFeedbackGenerator alloc] initWithStyle:UIImpactFeedbackStyleHeavy];
		
		m_pImpactGeneratorList = [NSArray arrayWithObjects:pLightGenerator, pMediumGenerator, pHeavyGenerator, nil];
	}
	
	return m_pImpactGeneratorList;
}

//! 선택 피드백 생성자를 반환한다
- (UISelectionFeedbackGenerator *)selectionGenerator {
	if(m_pSelectionGenerator == nil) {
		m_pSelectionGenerator = [[UISelectionFeedbackGenerator alloc] init];
	}
	
	return m_pSelectionGenerator;
}

//! 알림 피드백 생성자를 반환한다
- (UINotificationFeedbackGenerator *)notificationGenerator {
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

//! 디바이스 식별자 반환 메세지를 처리한다
- (void)handleGetDeviceIDMessage:(const char *)a_pszMessage {
	NSLog(@"CiOSPlugin.handleGetDeviceIDMessage: %@", @(a_pszMessage));
	
	if(!Func::IsValidString(self.deviceID)) {
		if(@available(iOS MIN_VERSION_DEVICE_ID_FOR_VENDOR, *)) {
			self.deviceID = UIDevice.currentDevice.identifierForVendor.UUIDString;
		} else {
			auto pUUID = CFUUIDCreate(kCFAllocatorDefault);
			self.deviceID = (__bridge NSString *)CFUUIDCreateString(kCFAllocatorDefault, pUUID);
		}
		
		[self.keychainItemWrapper setObject:self.deviceID forKey:(__bridge id)kSecAttrAccount];
	}
	
	[CDeviceMessageSender.sharedInstance sendGetDeviceIDMessage:self.deviceID];
}

//! 국가 코드 반환 메세지를 처리한다
- (void)handleGetCountryCodeMessage:(const char *)a_pszMessage {
	NSLog(@"CiOSPlugin.handleGetCountryCodeMessage: %@", @(a_pszMessage));
	
	auto pLocale = NSLocale.currentLocale;
	[CDeviceMessageSender.sharedInstance sendGetCountryCodeMessage:pLocale.countryCode];
}

//! 스토어 버전 반환 메세지를 처리한다
- (void)handleGetStoreVersionMessage:(const char *)a_pszMessage {
	NSLog(@"CiOSPlugin.handleGetStoreVersionMessage: %@", @(a_pszMessage));
	auto pDataList = (NSDictionary *)Func::ConvertJSONStringToObject(@(a_pszMessage), NULL);
	
	auto pAppID = (NSString *)[pDataList objectForKey:@(KEY_APP_ID)];
	auto pVersion = (NSString *)[pDataList objectForKey:@(KEY_VERSION)];
	auto pTimeout = (NSString *)[pDataList objectForKey:@(KEY_TIMEOUT)];
	
	auto pURL = [NSString stringWithFormat:@(URL_FORMAT_STORE_VERSION), pAppID];
	auto pRequest = Func::CreateURLRequest(pURL, @(HTTP_METHOD_GET), pTimeout.doubleValue);
	
	// 데이터를 수신했을 경우
	Func::SendURLRequest(pRequest, ^void(NSData *a_pData, NSURLResponse *a_pResponse, NSError *a_pError) {
		NSLog(@"CiOSPlugin.onHandleGetStoreVersionMessage: %@", a_pData);
		
		// 디버그 모드 일 경우
		if([self.buildMode isEqualToString:@(BUILD_MODE_DEBUG)]) {
			[CDeviceMessageSender.sharedInstance sendGetStoreVersionMessage:pVersion withResult:YES];
		} else {
			if(a_pError != nil || (a_pData == nil || a_pResponse == nil)) {
				NSLog(@"CiOSPlugin.onHandleGetStoreVersionMessage Fail: %@", a_pError);
				[CDeviceMessageSender.sharedInstance sendGetStoreVersionMessage:pVersion withResult:NO];
			} else {
				auto pString = [[NSString alloc] initWithData:a_pData encoding:NSUTF8StringEncoding];
				auto pResponseDataList = (NSDictionary *)Func::ConvertJSONStringToObject(pString, NULL);
				
				auto pVersionInfoList = (NSArray *)[pResponseDataList objectForKey:@(KEY_STORE_VERSION_RESULT)];
				auto pVersionInfo = (NSDictionary *)[pVersionInfoList lastObject];
				
				auto pStoreVersion = (NSString *)[pVersionInfo objectForKey:@(KEY_STORE_VERSION)];
				NSLog(@"CiOSPlugin.onHandleGetStoreVersionMessage Success: %@", pStoreVersion);
				
				if(Func::IsValidString(pStoreVersion)) {
					[CDeviceMessageSender.sharedInstance sendGetStoreVersionMessage:pStoreVersion withResult:YES];
				} else {
					[CDeviceMessageSender.sharedInstance sendGetStoreVersionMessage:pVersion withResult:NO];
				}
			}
		}
	});
}

//! 빌드 모드 변경 메세지를 처리한다
- (void)handleSetBuildModeMessage:(const char *)a_pszMessage {
	NSLog(@"CiOSPlugin.handleSetBuildModeMessage: %@", @(a_pszMessage));
	self.buildMode = @(a_pszMessage);
}

//! 알림 창 출력 메세지를 처리한다
- (void)handleShowAlertMessage:(const char *)a_pszMessage {
	NSLog(@"CiOSPlugin.handleShowAlertMessage: %@", @(a_pszMessage));
	auto pDataList = (NSDictionary *)Func::ConvertJSONStringToObject(@(a_pszMessage), NULL);
	
	auto pTitle = (NSString *)[pDataList objectForKey:@(KEY_ALERT_TITLE)];
	auto pMessage = (NSString *)[pDataList objectForKey:@(KEY_ALERT_MESSAGE)];
	auto pOKButtonTitle = (NSString *)[pDataList objectForKey:@(KEY_ALERT_OK_BUTTON_TEXT)];
	auto pCancelButtonTitle = (NSString *)[pDataList objectForKey:@(KEY_ALERT_CANCEL_BUTTON_TEXT)];
	
	auto pAlertController = [UIAlertController alertControllerWithTitle:Func::IsValidString(pTitle) ? pTitle : nil
																message:pMessage
														 preferredStyle:UIAlertControllerStyleAlert];
	
	// 확인 버튼을 눌렀을 경우
	[pAlertController addAction:[UIAlertAction actionWithTitle:pOKButtonTitle
														 style:UIAlertActionStyleDefault
													   handler:^void(UIAlertAction *a_pSender)
	{
		[CDeviceMessageSender.sharedInstance sendShowAlertMessage:YES];
	}]];
	
	if(Func::IsValidString(pCancelButtonTitle)) {
		// 취소 버튼을 눌렀을 경우
		[pAlertController addAction:[UIAlertAction actionWithTitle:pCancelButtonTitle
															 style:UIAlertActionStyleCancel
														   handler:^void(UIAlertAction *a_pSender)
		{
			[CDeviceMessageSender.sharedInstance sendShowAlertMessage:NO];
		}]];
	}
	
	// 알림 창을 출력한다
	[self.rootViewController presentViewController:pAlertController animated:YES completion:NULL];
}

//! 진동 메세지를 처리한다
- (void)handleVibrateMessage:(const char *)a_pszMessage {
	NSLog(@"CiOSPlugin.handleVibrateMessage: %@", @(a_pszMessage));
	auto pDataList = (NSDictionary *)Func::ConvertJSONStringToObject(@(a_pszMessage), NULL);
	
	auto pType = (NSString *)[pDataList objectForKey:@(KEY_VIBRATE_TYPE)];
	auto pStyle = (NSString *)[pDataList objectForKey:@(KEY_VIBRATE_STYLE)];
	
	auto eVibrateType = (EVibrateType)pType.intValue;
	auto eVibrateStyle = (EVibrateStyle)pStyle.intValue;
	
	if(Func::IsValidVibrateType(eVibrateType)) {
		// 햅틱 진동을 지원 할 경우
		if(@available(iOS MIN_VERSION_FEEDBACK_GENERATOR, *)) {
			if(eVibrateType == EVibrateType::SELECTION) {
				[self.selectionGenerator prepare];
				[self.selectionGenerator selectionChanged];
			} else if(eVibrateType == EVibrateType::NOTIFICATION) {
				[self.notificationGenerator prepare];
				[self.notificationGenerator notificationOccurred:(UINotificationFeedbackType)eVibrateStyle];
			} else {
				auto eFeedbackStyle = (UIImpactFeedbackStyle)eVibrateStyle;
				auto pImpactGenerator = (UIImpactFeedbackGenerator *)[self.impactGeneratorList objectAtIndex:eFeedbackStyle];
				
				[pImpactGenerator prepare];
				
				// 진동 세기를 지원 할 경우
				if(@available(iOS MIN_VERSION_IMPACT_INTENSITY, *)) {
					auto pIntensity = (NSString *)[pDataList objectForKey:@(KEY_VIBRATE_INTENSITY)];
					[pImpactGenerator impactOccurredWithIntensity:pIntensity.floatValue];
				} else {
					[pImpactGenerator impactOccurred];
				}
			}
		} else {
			SystemSoundID nSoundID = SYSTEM_SOUND_ID_LIGHT;
			
			if(eVibrateStyle == EVibrateStyle::MEDIUM) {
				nSoundID = SYSTEM_SOUND_ID_MEDIUM;
			} else if(eVibrateStyle == EVibrateStyle::HEAVY) {
				nSoundID = SYSTEM_SOUND_ID_HEAVY;
			}
			
			AudioServicesPlaySystemSound(nSoundID);
		}
	}
}

//! 액티비티 인디게이터 메세지를 처리한다
- (void)handleActivityIndicatorMessage:(const char *)a_pszMessage {
	NSLog(@"CiOSPlugin.handleStartActivityIndicatorMessage: %@", @(a_pszMessage));
	
	if(Func::ConvertStringToBool(@(a_pszMessage))) {
		[self.activityIndicatorView startAnimating];
	} else {
		[self.activityIndicatorView stopAnimating];
	}
}

#pragma mark - class method
//! 인스턴스를 반환한다
+ (instancetype)sharedInstance {
	@synchronized(CiOSPlugin.class) {
		if(g_pInstance == nil) {
			g_pInstance = [[CiOSPlugin alloc] init];
		}
	}
	
	return g_pInstance;
}

@end			// CiOSPlugin
