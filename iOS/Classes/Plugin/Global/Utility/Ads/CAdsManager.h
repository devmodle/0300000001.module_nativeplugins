//
//  CAdsManager.h
//  Unity-iPhone
//
//  Created by 이상동 on 2020/10/30.
//

#import "../../Define/KGDefine.h"

NS_ASSUME_NONNULL_BEGIN

//! 광고 관리자
@interface CAdsManager : NSObject
#ifdef ADMOB_ENABLE
	<GADFullScreenContentDelegate>
#endif			// #ifdef ADMOB_ENABLE
{
	// Do Nothing
}

// 프로퍼티 {
@property (nonatomic, assign) BOOL isInit;

#ifdef ADMOB_ENABLE
@property (nonatomic, assign) BOOL isLoadResumeAds;

@property (nonatomic, copy) NSString *resumeAdsID;
@property (nonatomic, strong) NSArray *deviceIDList;

@property (nonatomic, readonly) GADRequest *request;
@property (nonatomic, strong) GADAppOpenAd *resumeAds;
#endif			// #ifdef ADMOB_ENABLE
// 프로퍼티 }

//! 초기화
- (void)init:(NSString *)a_pResumeAdsID withDeviceIDList:(NSArray *)a_pDeviceIDList;

//! 재개 광고를 로드한다
- (void)loadResumeAds;

//! 재개 광고를 출력한다
- (void)showResumeAds;

//! 인스턴스를 반환한다
+ (instancetype)sharedInst;
@end			// CAdsManager

NS_ASSUME_NONNULL_END
