package com.weibo.sdk.android.api;


import android.R.integer;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.RequestListener;


public class NotificationAPI extends WeiboAPI {
	public NotificationAPI(Oauth2AccessToken accessToken) {
		super(accessToken);
	}
	
	
    private static final String SERVER_URL_PRIX = API_SERVER + "/notification/send.json";


    /*
     * 
 					必选	类型及范围	说明
	//	source			false		string	采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。
	//	access_token	false		string	采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。
		uids			true		string	接收通知的用户UID，支持1-100个用户，用逗号分隔。
		tpl_id			true		int		发送通知所对应的模版ID，必须是通过审核，且启用中的模版。
		objects1		false		string	通知的自定义变量1，可以是人也可以是系统实物，支持@用户昵称，最多不超过32个字节。
		objects1_count	false		int		通知的自定义变量1的数量，正整数，最多不超过2个字节，支持0-99。
		objects2		false		string	通知的自定义变量2，可以是人也可以是系统实物，支持@用户昵称，最多不超过32个字节。
		objects2_count	false		int		通知的自定义变量2的数量，正整数，最多不超过2个字节，支持0-99。
		objects3		false		string	通知的自定义变量3，可以是人也可以是系统实物，支持@用户昵称，最多不超过32个字节。
		objects3_count	false		int		通知的自定义变量3的数量，正整数，最多不超过2个字节，支持0-99。
		action_url		false		string	通知的处理链接，支持短链接，最多不超过20个字节。
	
	*/
    
	public void notify( String uids, int tpl_id, String objects1, int objects1_count, String action_url, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		
		params.add("uids", uids);
		params.add("tpl_id", tpl_id);
		params.add("objects1", objects1);
		params.add("object1_count", objects1_count);
		params.add("action_url", action_url);
		
		request( SERVER_URL_PRIX + "/notification/send.json", params, HTTPMETHOD_GET, listener);
	}

//	/**
//	 * 通过个性化域名获取用户资料以及用户最新的一条微博
//	 * @param domain 需要查询的个性化域名。
//	 * @param listener
//	 */
//	public void domainShow( String domain, RequestListener listener) {
//		WeiboParameters params = new WeiboParameters();
//		params.add("domain", domain);
//		request( SERVER_URL_PRIX + "/domain_show.json", params, HTTPMETHOD_GET, listener);
//	}
}
