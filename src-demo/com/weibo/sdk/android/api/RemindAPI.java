package com.weibo.sdk.android.api;


import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.RequestListener;


public class RemindAPI extends WeiboAPI {
	public RemindAPI(Oauth2AccessToken accessToken) {
		super(accessToken);
	}
	
	
    private static final String SERVER_URL_PRIX = API_SERVER + "/remind/unread_count.json";


 /*
      				必选		类型及范围	说明
	source			false	string		采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。
	access_token	false	string		采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。
	uid				true	int64		需要获取消息未读数的用户UID，必须是当前登录用户。
	callback		false	string		JSONP回调函数，用于前端调用返回JS格式的信息。

*/
    
	public void remind(long uid, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		
		params.add("uid", uid);
		
		request( SERVER_URL_PRIX, params, HTTPMETHOD_GET, listener);
	}


}
