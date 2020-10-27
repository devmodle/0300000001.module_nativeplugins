package dante.distribution.android.Global.Function;

import dante.distribution.android.Global.Define.KGDefine;

//! 전역 함수
public abstract class GFunc {
	//! 문자열 유효 여부를 검사한다
	public static boolean isValid(String a_oString) {
		return a_oString != null && a_oString.length() >= 1;
	}
	
	//! 문자열 -> 논리로 변환한다
	public static boolean convertStringToBool(String a_oString) {
		return a_oString.equals(KGDefine.RESULT_TRUE);
	}
	
	//! 논리 -> 문자열로 변환한다
	public static String convertBoolToString(boolean a_bIsTrue) {
		return a_bIsTrue ? KGDefine.RESULT_TRUE : KGDefine.RESULT_FALSE;
	}
}
