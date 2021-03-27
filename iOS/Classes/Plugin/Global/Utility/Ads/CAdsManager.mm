//
//  CAdsManager.m
//  Unity-iPhone
//
//  Created by 이상동 on 2020/10/30.
//

#import "CAdsManager.h"
#import "../../../CiOSPlugin.h"
#import "../Platform/CDeviceMsgSender.h"

//! 전역 변수
static CAdsManager *g_pInst = nil;

//! 광고 관리자
@implementation CAdsManager
#pragma mark - 초기화
//! 객체를 생성한다
+ (id)alloc {
	@synchronized(CAdsManager.class) {
		// 인스턴스가 없을 경우
		if(g_pInst == nil) {
			g_pInst = [[super alloc] init];
		}
	}
	
	return g_pInst;
}

#pragma mark - 인스턴스 메서드
//! 초기화
- (void)init:(NSString *)a_pResumeAdsID withDeviceIDList:(NSArray *)a_pDeviceIDList {
	NSLog(@"CAdsManager.initWithDeviceIDList: %@, %@", a_pResumeAdsID, a_pDeviceIDList);
	
#ifdef ADMOB_ENABLE
	self.resumeAdsID = a_pResumeAdsID;
	self.deviceIDList = a_pDeviceIDList;
	
	self.isInit = YES;
	[CDeviceMsgSender.sharedInst sendInitAdsMsg:YES];
#endif			// #ifdef ADMOB_ENABLE
}

//! 재개 광고를 로드한다
- (void)loadResumeAds {
	NSLog(@"CAdsManager.loadResumeAds");
	
#ifdef ADMOB_ENABLE
	// 초기화 되었을 경우
	if(self.isInit && !self.isLoadResumeAds && self.resumeAds == nil) {
		[GADAppOpenAd loadWithAdUnitID:self.resumeAdsID request:self.request orientation:CiOSPlugin.sharedInst.orientation completionHandler:^void(GADAppOpenAd *a_pAds, NSError *a_pError) {
			NSLog(@"CAdsManager.onLoadResumeAds: %@", a_pError);
			
			// 광고 로드에 실패했을 경우
			if(a_pError != nil) {
				[CDeviceMsgSender.sharedInst sendLoadResumeAdsMsg:NO];
			} else {
				self.resumeAds = a_pAds;
				self.resumeAds.fullScreenContentDelegate = self;
				
				self.isLoadResumeAds = YES;
				[CDeviceMsgSender.sharedInst sendLoadResumeAdsMsg:YES];
			}
		}];
	} else {
		[CDeviceMsgSender.sharedInst sendLoadResumeAdsMsg:NO];
	}
#else
	[CDeviceMsgSender.sharedInst sendLoadResumeAdsMsg:NO];
#endif			// #ifdef ADMOB_ENABLE
}

//! 재개 광고를 출력한다
- (void)showResumeAds {
	NSLog(@"CAdsManager.showResumeAds");
	
#ifdef ADMOB_ENABLE
	// 재개 광고가 로드 되었을 경우
	if(self.isInit && self.isLoadResumeAds && self.resumeAds != nil) {
		UIViewController *pViewController = CiOSPlugin.sharedInst.rootViewController;
		[self.resumeAds presentFromRootViewController:pViewController];
	} else {
		[CDeviceMsgSender.sharedInst sendShowResumeAdsMsg:NO];
	}
#else
	[CDeviceMsgSender.sharedInst sendShowResumeAdsMsg:NO];
#endif			// #ifdef ADMOB_ENABLE
}

#ifdef ADMOB_ENABLE
//! 재개 광고를 출력했을 경우
- (void)adDidPresentFullScreenContent:(id<GADFullScreenPresentingAd>)a_pSender {
	NSLog(@"CAdsManager.adDidPresentFullScreenContent");
}

//! 재개 광고 출력에 실패했을 경우
- (void)ad:(id<GADFullScreenPresentingAd>)a_pSender didFailToPresentFullScreenContentWithError:(NSError *)a_pError {
	NSLog(@"CAdsManager.adDidFailToPresentFullScreenContentWithError: %@", a_pError);
	[CDeviceMsgSender.sharedInst sendShowResumeAdsMsg:NO];
}

//! 재개 광고가 닫혔을 경우
- (void)adDidDismissFullScreenContent:(id<GADFullScreenPresentingAd>)a_pSender {
	NSLog(@"CAdsManager.adDidDismissFullScreenContent");
	
	self.resumeAds = nil;
	self.isLoadResumeAds = NO;
	
	[CDeviceMsgSender.sharedInst sendShowResumeAdsMsg:YES];
}

//! 광고 요청자를 반환한다
- (GADRequest *)request {
	GADRequest *pRequest = [GADRequest request];
	pRequest.testDevices = self.deviceIDList;
	
	return pRequest;
}
#endif			// #ifdef ADMOB_ENABLE

#pragma mark - 클래스 메서드
//! 인스턴스를 반환한다
+ (instancetype)sharedInst {
	@synchronized(CAdsManager.class) {
		// 인스턴스가 없을 경우
		if(g_pInst == nil) {
			g_pInst = [[CAdsManager alloc] init];
		}
	}
	
	return g_pInst;
}
@end			// CAdsManager
