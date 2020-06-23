//
//  GlobalFunction.m
//  Unity-iPhone
//
//  Created by 이상동 on 2020/01/11.
//

#import "Func+Global.h"

namespace Func {
	//! 문자열 유효 여부를 검사한다
	BOOL IsValidString(NSString *a_pString) {
		return a_pString != nil && a_pString.length >= 1;
	}
	
	//! 진동 타입 유효 여부를 검사한다
	BOOL IsValidVibrateType(EVibrateType a_eType) {
		return a_eType > EVibrateType::NONE && a_eType < EVibrateType::MAX_VALUE;
	}

	//! 문자열 -> 논리로 변화한다
	BOOL ConvertStringToBool(NSString *a_pString) {
		return [a_pString isEqualToString:@(RESULT_TRUE)];
	}

	//! 논리 -> 문자열로 변환한다
	NSString * ConvertBoolToString(BOOL a_bIsTrue) {
		return a_bIsTrue ? @(RESULT_TRUE) : @(RESULT_FALSE);
	}

	//! 객체 -> JSON 문자열로 변환한다
	NSString * ConvertObjectToJSONString(NSObject *a_pObject, NSError **a_pError) {
		auto pData = [NSJSONSerialization dataWithJSONObject:a_pObject
													 options:NSJSONWritingPrettyPrinted
													   error:a_pError];
		
		return [[NSString alloc] initWithData:pData encoding:NSUTF8StringEncoding];
	}

	//! JSON 문자열 -> 객체로 변환한다
	NSObject * ConvertJSONStringToObject(NSString *a_pJSONString, NSError **a_pError) {
		auto pData = [a_pJSONString dataUsingEncoding:NSUTF8StringEncoding];
		
		return [NSJSONSerialization JSONObjectWithData:pData
											   options:NSJSONReadingMutableContainers
												 error:a_pError];
	}

	//! URL 요청을 전송한다
	void SendURLRequest(NSURLRequest *a_pRequest, void (^a_pfnCallback)(NSData *, NSURLResponse *, NSError *)) {
		auto pDataTask = [NSURLSession.sharedSession dataTaskWithRequest:a_pRequest
													   completionHandler:a_pfnCallback];
		
		[pDataTask resume];
	}

	//! URL 요청을 생성한다
	NSMutableURLRequest * CreateURLRequest(NSString *a_pURL, NSString *a_pMethod, double a_dblTimeout) {
		auto pRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:a_pURL]
												cachePolicy:NSURLRequestUseProtocolCachePolicy
											timeoutInterval:a_dblTimeout];
		
		pRequest.HTTPMethod = a_pMethod;
		return pRequest;
	}
}
