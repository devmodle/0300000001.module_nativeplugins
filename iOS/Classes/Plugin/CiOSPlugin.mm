//
//  CiOSPlugin.m
//  Unity-iPhone
//
//  Created by 이상동 on 2020/02/26.
//

#import "CiOSPlugin.h"
#import "Global/Function/GFunc.h"
#import "Global/Utility/Platform/CDeviceMsgSender.h"

//! 전역 변수
static CiOSPlugin *g_pInst = nil;

//! iOS 플러그인 - Private
@interface CiOSPlugin (Private) {
	// Do Something
}

//! 디바이스 식별자 반환 메세지를 처리한다
- (void)handleGetDeviceIDMsg:(NSString *)a_pMsg;

//! 국가 코드 반환 메세지를 처리한다
- (void)handleGetCountryCodeMsg:(NSString *)a_pMsg;

//! 스토어 버전 반환 메세지를 처리한다
- (void)handleGetStoreVerMsg:(NSString *)a_pMsg;

//! 광고 추적 여부 변경 메세지를 처리한다
- (void)handleSetEnableAdsTrackingMsg:(NSString *)a_pMsg;

//! 경고 창 출력 메세지를 처리한다
- (void)handleShowAlertMsg:(NSString *)a_pMsg;

//! 메일 메세지를 처리한다
- (void)handleMailMsg:(NSString *)a_pMsg;

//! 진동 메세지를 처리한다
- (void)handleVibrateMsg:(NSString *)a_pMsg;

//! 인디케이터 메세지를 처리한다
- (void)handleIndicatorMsg:(NSString *)a_pMsg;
@end			// CiOSPlugin (Private)

extern "C" {
	//! 유니티 메세지를 처리한다
	void HandleUnityMsg(const char *a_pszCmd, const char *a_pszMsg) {
		NSLog(@"CiOSPlugin.HandleUnityMsg: %@, %@", @(a_pszCmd), @(a_pszMsg));
		
		NSString *pMsg = @(a_pszMsg);
		NSString *pSelectorName = (NSString *)[CiOSPlugin.sharedInst.unityMsgHandlerList objectForKey:@(a_pszCmd)];
		
		// 유니티 메세지 처리자가 존재 할 경우
		if(GFunc::IsValid(pSelectorName)) {
			SEL pSelector = NSSelectorFromString(pSelectorName);
			NSMethodSignature *pSignature = [CiOSPlugin.sharedInst methodSignatureForSelector:pSelector];
			
			NSInvocation *pInvocation = [NSInvocation invocationWithMethodSignature:pSignature];
			pInvocation.selector = pSelector;
			
			[pInvocation setArgument:&pMsg atIndex:G_VAL_2_INT];
			[pInvocation invokeWithTarget:CiOSPlugin.sharedInst];
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
@synthesize unityMsgHandlerList = m_pUnityMsgHandlerList;

@synthesize selectionGenerator = m_pSelectionGenerator;
@synthesize notificationGenerator = m_pNotificationGenerator;

#pragma mark - 인터페이스
//! 메일이 완료 되었을 경우
- (void)mailComposeController:(MFMailComposeViewController *)a_pSender didFinishWithResult:(MFMailComposeResult)a_eResult error:(NSError *)a_pError {
	[a_pSender dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - 초기화
//! 객체를 생성한다
+ (id)alloc {
	@synchronized(CiOSPlugin.class) {
		// 인스턴스가 없을 경우
		if(g_pInst == nil) {
			g_pInst = [[super alloc] init];
		}
	}
	
	return g_pInst;
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

//! 유니티 메세지 처리자 리스트를 반환한다
- (NSDictionary *)unityMsgHandlerList {
	// 처리자 리스트가 없을 경우
	if(m_pUnityMsgHandlerList == nil) {
		NSMutableDictionary *pMsgHandlerList = [[NSMutableDictionary alloc] init];
		[pMsgHandlerList setObject:NSStringFromSelector(@selector(handleGetDeviceIDMsg:)) forKey:@(G_CMD_GET_DEVICE_ID)];
		[pMsgHandlerList setObject:NSStringFromSelector(@selector(handleGetCountryCodeMsg:)) forKey:@(G_CMD_GET_COUNTRY_CODE)];
		[pMsgHandlerList setObject:NSStringFromSelector(@selector(handleGetStoreVerMsg:)) forKey:@(G_CMD_GET_STORE_VER)];
		[pMsgHandlerList setObject:NSStringFromSelector(@selector(handleSetEnableAdsTrackingMsg:)) forKey:@(G_CMD_SET_ENABLE_ADS_TRACKING)];
		[pMsgHandlerList setObject:NSStringFromSelector(@selector(handleShowAlertMsg:)) forKey:@(G_CMD_SHOW_ALERT)];
		[pMsgHandlerList setObject:NSStringFromSelector(@selector(handleMailMsg:)) forKey:@(G_CMD_MAIL)];
		[pMsgHandlerList setObject:NSStringFromSelector(@selector(handleVibrateMsg:)) forKey:@(G_CMD_VIBRATE)];
		[pMsgHandlerList setObject:NSStringFromSelector(@selector(handleIndicatorMsg:)) forKey:@(G_CMD_INDICATOR)];
		
		m_pUnityMsgHandlerList = pMsgHandlerList;
	}
	
	return m_pUnityMsgHandlerList;
}

//! 키체인 아이템 래퍼를 반환한다
- (KeychainItemWrapper *)keychainItemWrapper {
	// 키체인 아이템 래퍼가 없을 경우
	if(m_pKeychainItemWrapper == nil) {
		m_pKeychainItemWrapper = [[KeychainItemWrapper alloc] initWithIdentifier:@(G_ID_KEYCHAIN_DEVICE) accessGroup:nil];
	}
	
	return m_pKeychainItemWrapper;
}

//! 액티비티 인디케이터 뷰를 반환한다
- (UIActivityIndicatorView *)activityIndicatorView {
	// 인디케이터가 없을 경우
	if(m_pActivityIndicatorView == nil) {
		UIActivityIndicatorViewStyle eIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
		
		// 신규 버전 인디케이터를 지원 할 경우
		if(@available(iOS G_MIN_VER_INDICATOR, *)) {
			eIndicatorViewStyle = UIActivityIndicatorViewStyleLarge;
		}
		
		m_pActivityIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:eIndicatorViewStyle];
		m_pActivityIndicatorView.color = [UIColor colorWithWhite:G_VAL_1_FLT alpha:G_VAL_1_FLT];
		m_pActivityIndicatorView.center = self.rootViewController.view.center;
		m_pActivityIndicatorView.hidesWhenStopped = YES;
		
		// 크기를 설정한다 {
		float fSize = MIN(self.rootViewController.view.bounds.size.width, self.rootViewController.view.bounds.size.height);
		fSize *= G_SCALE_ACTIVITY_INDICATOR;
		
		float fScaleX = fSize / m_pActivityIndicatorView.bounds.size.width;
		float fScaleY = fSize / m_pActivityIndicatorView.bounds.size.height;
		
		CGAffineTransform stTransform = m_pActivityIndicatorView.transform;
		stTransform = CGAffineTransformScale(stTransform, fScaleX, fScaleY);
		// 크기를 설정한다 }
		
		// 위치를 설정한다 {
		float fOffset = MIN(self.rootViewController.view.bounds.size.width, self.rootViewController.view.bounds.size.height);
		fOffset *= G_OFFSET_SCALE_ACTIVITY_INDICATOR;
		
		stTransform = CGAffineTransformTranslate(stTransform, G_VAL_0_FLT, -fOffset);
		m_pActivityIndicatorView.transform = stTransform;
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
		
		m_pImpactGeneratorList = [[NSArray alloc] initWithObjects:pLightGenerator, pMediumGenerator, pHeavyGenerator, nil];
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

//! 유니티 앱 컨트롤러를 반환한다
- (UnityAppController *)unityAppController {
	return (UnityAppController *)UIApplication.sharedApplication.delegate;
}

//! 루트 뷰 컨트롤러를 반환한다
- (UIViewController *)rootViewController {
	return self.unityAppController.rootViewController;
}

//! 디바이스 식별자 반환 메세지를 처리한다
- (void)handleGetDeviceIDMsg:(NSString *)a_pMsg {
	// 디바이스 식별자가 유효하지 않을 경우
	if(!GFunc::IsValid(self.deviceID)) {
		self.deviceID = UIDevice.currentDevice.identifierForVendor.UUIDString;
		[self.keychainItemWrapper setObject:self.deviceID forKey:(__bridge id)kSecAttrAccount];
	}
	
	[CDeviceMsgSender.sharedInst sendGetDeviceIDMsg:self.deviceID];
}

//! 국가 코드 반환 메세지를 처리한다
- (void)handleGetCountryCodeMsg:(NSString *)a_pMsg {
	NSLocale *pLocale = NSLocale.currentLocale;
	[CDeviceMsgSender.sharedInst sendGetCountryCodeMsg:pLocale.countryCode];
}

//! 스토어 버전 반환 메세지를 처리한다
- (void)handleGetStoreVerMsg:(NSString *)a_pMsg {
	NSDictionary *pDataList = (NSDictionary *)GFunc::ConvertJSONStrToObj(a_pMsg, NULL);
	
	NSString *pAppID = (NSString *)[pDataList objectForKey:@(G_KEY_APP_ID)];
	NSString *pVer = (NSString *)[pDataList objectForKey:@(G_KEY_VER)];
	NSString *pTimeout = (NSString *)[pDataList objectForKey:@(G_KEY_TIMEOUT)];
	
	NSString *pURL = [NSString stringWithFormat:@(G_URL_FMT_STORE_VER), pAppID];
	NSMutableURLRequest * pURLRequest = GFunc::MakeURLRequest(pURL, @(G_HTTP_METHOD_GET), pTimeout.doubleValue);
	
	// 데이터를 수신했을 경우
	[NSURLSession.sharedSession dataTaskWithRequest:pURLRequest completionHandler:^void(NSData *a_pData, NSURLResponse *a_pResponse, NSError *a_pError) {
		NSLog(@"CiOSPlugin.onHandleGetStoreVerMsg: %@", a_pData);
		
		// 스토어 버전 로드에 실패했을 경우
		if(a_pError != nil || (a_pData == nil || a_pResponse == nil)) {
			NSLog(@"CiOSPlugin.onHandleGetStoreVerMsg Fail: %@", a_pError);
			[CDeviceMsgSender.sharedInst sendGetStoreVerMsg:pVer withResult:NO];
		} else {
			NSString *pStr = [[NSString alloc] initWithData:a_pData encoding:NSUTF8StringEncoding];
			NSDictionary *pResponseDataList = (NSDictionary *)GFunc::ConvertJSONStrToObj(pStr, NULL);
			
			NSArray *pVerInfoList = (NSArray *)[pResponseDataList objectForKey:@(G_KEY_STORE_VER_RESULT)];
			NSDictionary *pVerInfo = (NSDictionary *)[pVerInfoList lastObject];
			
			NSString *pStoreVer = (NSString *)[pVerInfo objectForKey:@(G_KEY_STORE_VER)];
			NSLog(@"CiOSPlugin.onHandleGetStoreVerMsg Success: %@", pStoreVer);
			
			// 스토어 버전이 유효 할 경우
			if(GFunc::IsValid(pStoreVer)) {
				[CDeviceMsgSender.sharedInst sendGetStoreVerMsg:pStoreVer withResult:YES];
			} else {
				[CDeviceMsgSender.sharedInst sendGetStoreVerMsg:pVer withResult:NO];
			}
		}
	}];
}

//! 광고 추적 여부 변경 메세지를 처리한다
- (void)handleSetEnableAdsTrackingMsg:(NSString *)a_pMsg {
#if defined IRON_SRC_ENABLE || defined APP_LOVIN_ENABLE
	BOOL bIsEnable = GFunc::ConvertStrToBool(a_pMsg);
	[FBAdSettings setAdvertiserTrackingEnabled:bIsEnable];
#endif			// #if defined IRON_SRC_ENABLE || defined APP_LOVIN_ENABLE
}

//! 경고 창 출력 메세지를 처리한다
- (void)handleShowAlertMsg:(NSString *)a_pMsg {
	NSDictionary *pDataList = (NSDictionary *)GFunc::ConvertJSONStrToObj(a_pMsg, NULL);
	
	NSString *pTitle = (NSString *)[pDataList objectForKey:@(G_KEY_ALERT_TITLE)];
	NSString *pMsg = (NSString *)[pDataList objectForKey:@(G_KEY_ALERT_MSG)];
	NSString *pOKBtnText = (NSString *)[pDataList objectForKey:@(G_KEY_ALERT_OK_BTN_TEXT)];
	NSString *pCancelBtnText = (NSString *)[pDataList objectForKey:@(G_KEY_ALERT_CANCEL_BTN_TEXT)];
	
	NSString *pAlertTitle = GFunc::IsValid(pTitle) ? pTitle : nil;
	UIAlertController *pAlertController = [UIAlertController alertControllerWithTitle:pAlertTitle message:pMsg preferredStyle:UIAlertControllerStyleAlert];
	
	// 확인 버튼을 눌렀을 경우
	[pAlertController addAction:[UIAlertAction actionWithTitle:pOKBtnText style:UIAlertActionStyleDefault handler:^void(UIAlertAction *a_pSender) {
		[CDeviceMsgSender.sharedInst sendShowAlertMsg:YES];
	}]];
	
	// 취소 버튼 텍스트가 유효 할 경우
	if(GFunc::IsValid(pCancelBtnText)) {
		// 취소 버튼을 눌렀을 경우
		[pAlertController addAction:[UIAlertAction actionWithTitle:pCancelBtnText style:UIAlertActionStyleCancel handler:^void(UIAlertAction *a_pSender) {
			[CDeviceMsgSender.sharedInst sendShowAlertMsg:NO];
		}]];
	}
	
	[self.rootViewController presentViewController:pAlertController animated:YES completion:NULL];
}

//! 메일 메세지를 처리한다
- (void)handleMailMsg:(NSString *)a_pMsg {
	NSDictionary *pDataList = (NSDictionary *)GFunc::ConvertJSONStrToObj(a_pMsg, NULL);
	
	NSString *pRecipient = (NSString *)[pDataList objectForKey:@(G_KEY_MAIL_RECIPIENT)];
	NSString *pTitle = (NSString *)[pDataList objectForKey:@(G_KEY_MAIL_TITLE)];
	NSString *pMsg = (NSString *)[pDataList objectForKey:@(G_KEY_MAIL_MSG)];
	
	// 메일 전송이 가능 할 경우
	if([MFMailComposeViewController canSendMail]) {
		MFMailComposeViewController *pMailViewController = [[MFMailComposeViewController alloc] init];
		pMailViewController.mailComposeDelegate = self;
		
		[pMailViewController setToRecipients:[NSArray arrayWithObjects:pRecipient, nil]];
		[pMailViewController setSubject:pTitle];
		[pMailViewController setMessageBody:pMsg isHTML:NO];
		
		[self.rootViewController presentViewController:pMailViewController animated:YES completion:NULL];
	} else {
		pTitle = [pTitle stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLUserAllowedCharacterSet];
		pMsg = [pMsg stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLUserAllowedCharacterSet];
		
		NSString *pURL = [NSString stringWithFormat:@(G_URL_FMT_MAIL), pRecipient, pTitle, pMsg, nil];
		[UIApplication.sharedApplication openURL:[NSURL URLWithString:pURL] options:G_EMPTY_DICT completionHandler:nil];
	}
}

//! 진동 메세지를 처리한다
- (void)handleVibrateMsg:(NSString *)a_pMsg {
	NSDictionary *pDataList = (NSDictionary *)GFunc::ConvertJSONStrToObj(a_pMsg, NULL);
	
	NSString *pType = (NSString *)[pDataList objectForKey:@(G_KEY_VIBRATE_TYPE)];
	NSString *pStyle = (NSString *)[pDataList objectForKey:@(G_KEY_VIBRATE_STYLE)];
	
	EVibrateType eVibrateType = (EVibrateType)pType.intValue;
	EVibrateStyle eVibrateStyle = (EVibrateStyle)pStyle.intValue;
	
	// 진동 타입이 유효 할 경우
	if(GFunc::IsValid(eVibrateType)) {
		// 햅틱 진동을 지원 할 경우
		if(@available(iOS G_MIN_VER_FEEDBACK_GENERATOR, *)) {
			// 선택 진동 모드 일 경우
			if(eVibrateType == EVibrateType::SELECTION) {
				[self.selectionGenerator selectionChanged];
			}
			// 경고 진동 모드 일 경우
			else if(eVibrateType == EVibrateType::NOTIFICATION) {
				[self.notificationGenerator notificationOccurred:(UINotificationFeedbackType)eVibrateStyle];
			} else {
				UIImpactFeedbackStyle eFeedbackStyle = (UIImpactFeedbackStyle)eVibrateStyle;
				UIImpactFeedbackGenerator *pImpactGenerator = (UIImpactFeedbackGenerator *)[self.impactGeneratorList objectAtIndex:eFeedbackStyle];
				
				// 진동 세기를 지원 할 경우
				if(@available(iOS G_MIN_VER_IMPACT_INTENSITY, *)) {
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

//! 인디케이터 메세지를 처리한다
- (void)handleIndicatorMsg:(NSString *)a_pMsg {
	// 출력 모드 일 경우
	if(GFunc::ConvertStrToBool(a_pMsg)) {
		[self.activityIndicatorView startAnimating];
	} else {
		[self.activityIndicatorView stopAnimating];
	}
}

#pragma mark - 클래스 메서드
//! 인스턴스를 반환한다
+ (instancetype)sharedInst {
	@synchronized(CiOSPlugin.class) {
		// 인스턴스가 없을 경우
		if(g_pInst == nil) {
			g_pInst = [[CiOSPlugin alloc] init];
		}
	}
	
	return g_pInst;
}
@end			// CiOSPlugin
