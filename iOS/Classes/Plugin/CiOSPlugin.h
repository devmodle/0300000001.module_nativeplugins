//
//  CiOSPlugin.h
//  Unity-iPhone
//
//  Created by 이상동 on 2020/02/26.
//
#pragma once

#import "Global/Define/KGDefine.h"
#import "Global/Utility/External/Keychain/KeychainItemWrapper.h"
#import "UnityAppController.h"

NS_ASSUME_NONNULL_BEGIN

//! iOS 플러그인
@interface CiOSPlugin : NSObject <MFMailComposeViewControllerDelegate> {
	NSString *m_pDeviceID;
	
	KeychainItemWrapper *m_pKeychainItemWrapper;
	UIActivityIndicatorView *m_pActivityIndicatorView;
	
	NSArray *m_pImpactGeneratorList;
	NSDictionary *m_pUnityMsgHandlerList;
	
	UISelectionFeedbackGenerator *m_pSelectionGenerator;
	UINotificationFeedbackGenerator *m_pNotificationGenerator;
	
#if defined FIREBASE_MODULE_ENABLE
	NSMutableDictionary *m_pTrackingList;
#endif			// #if defined FIREBASE_MODULE_ENABLE
}

// 프로퍼티 {
@property (nonatomic, copy) NSString *deviceID;
@property (nonatomic, assign) UIInterfaceOrientation orientation;

@property (nonatomic, strong, readonly) KeychainItemWrapper *keychainItemWrapper;
@property (nonatomic, strong, readonly) UIActivityIndicatorView *activityIndicatorView;

@property (nonatomic, strong, readonly) NSArray *impactGeneratorList;
@property (nonatomic, strong, readonly) NSDictionary *unityMsgHandlerList;

@property (nonatomic, strong, readonly) UISelectionFeedbackGenerator *selectionGenerator;
@property (nonatomic, strong, readonly) UINotificationFeedbackGenerator *notificationGenerator;

@property (nonatomic, strong, readonly) UnityAppController *unityAppController;
@property (nonatomic, strong, readonly) UIViewController *rootViewController;

#if defined FIREBASE_MODULE_ENABLE
@property (nonatomic, strong) NSMutableDictionary *trackingList;
#endif			// #if defined FIREBASE_MODULE_ENABLE
// 프로퍼티 }

//! 인스턴스를 반환한다
+ (instancetype)sharedInst;
@end			// CiOSPlugin

NS_ASSUME_NONNULL_END
