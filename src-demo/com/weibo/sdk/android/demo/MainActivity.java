package com.weibo.sdk.android.demo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.stringtree.json.JSONReader;
import org.stringtree.json.JSONValidatingReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.CommentsAPI;
import com.weibo.sdk.android.api.NotificationAPI;
import com.weibo.sdk.android.api.RemindAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.keep.AccessTokenKeeper;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;
/**
 * 
 * @author liyan (liyan9@staff.sina.com.cn)
 */
public class MainActivity extends Activity {

    private Weibo mWeibo;
    private static final String CONSUMER_KEY = "1572235510";// 替换为开发者的appkey，例如"1646212860 / 966056985";
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    private Button authBtn, apiBtn, ssoBtn, cancelBtn;
    private TextView mText;
    public static Oauth2AccessToken accessToken;
    public static final String TAG = "sinasdk";
    /**
     * SsoHandler 仅当sdk支持sso时有效，
     */
    SsoHandler mSsoHandler;

    
    /*
     * Stringtree JSON解析
     */
    
public void StringtreeReader(String response) {
	
//	JSONWriter writer = new JSONValidatingWriter();
	
	JSONReader reader = new JSONValidatingReader();
	
    //test data 
    
    List  result = null;
    HashMap map  = null;
    
    try
    {
         map = (HashMap) reader.read(response); 
         
    }catch (Exception e)
    {
        e.printStackTrace();
    }
    
    //loop the map 
    for(Iterator it = map.keySet().iterator(); it.hasNext(); )
    {
        System.out.println("JSONReader result is " + map.get(it.next()) );
    }
}


//    public void JsonReader(String response) throws JSONException{
//    	JSONObject jsonObject = new JSONObject(response);//weibolist为整个JSON字串
//
//    	JSONArray jsonArray = jsonObject.getJSONArray("statuses");
//
//    	//由于 jsonArray  为JSON数组，要用for循环依次获得 JSONObject
//
//    	for (int i = 0; i < jsonArray.length(); i++)
//    	    {
//    	        JSONObject d = jsonArray.getJSONObject(i); 
//    	        String text = d.getString("text");//这样就能解析出来了
//    	       //JSON解析
//    	        System.out.println(text);
//    	    }
//    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);

        authBtn = (Button) findViewById(R.id.auth);
        authBtn.setOnClickListener(new OnClickListener() {
        	
            @Override
            public void onClick(View v) {
                mWeibo.authorize(MainActivity.this, new AuthDialogListener());
            }
        });
        
        
        ssoBtn = (Button) findViewById(R.id.sso);// 触发sso的按钮
        try {
            Class sso = Class.forName("com.weibo.sdk.android.sso.SsoHandler");
            ssoBtn.setVisibility(View.VISIBLE);
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "com.weibo.sdk.android.sso.SsoHandler not found");

        }
        ssoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 下面两个注释掉的代码，仅当sdk支持sso时有效，
                 */

                mSsoHandler = new SsoHandler(MainActivity.this, mWeibo);
                mSsoHandler.authorize(new AuthDialogListener());
            }
        });
        
        cancelBtn = (Button) findViewById(R.id.apiCancel);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessTokenKeeper.clear(MainActivity.this);
                authBtn.setVisibility(View.VISIBLE);
                ssoBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
                mText.setText("");
            }
        });

//        apiBtn = (Button)findViewById(R.id.api);
        
        mText = (TextView) findViewById(R.id.show);
        MainActivity.accessToken = AccessTokenKeeper.readAccessToken(this);
        if (MainActivity.accessToken.isSessionValid()) {
            Weibo.isWifi = Utility.isWifi(this);
            try {
                Class sso = Class.forName("com.weibo.sdk.android.api.WeiboAPI");// 如果支持weiboapi的话，显示api功能演示入口按钮
//                apiBtn.setVisibility(View.VISIBLE);
            } catch (ClassNotFoundException e) {
                // e.printStackTrace();
                Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");

            }
            authBtn.setVisibility(View.INVISIBLE);
            ssoBtn.setVisibility(View.INVISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            String date = new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
                    .format(new java.util.Date(MainActivity.accessToken
                            .getExpiresTime()));
            mText.setText("access_token 仍在有效期内,无需再次登录: \naccess_token:"
                    + MainActivity.accessToken.getToken() + "\n有效期：" + date);
        } else {
            mText.setText("使用SSO登录前，请检查手机上是否已经安装新浪微博客户端，目前仅3.0.0及以上微博客户端版本支持SSO；如果未安装，将自动转为Oauth2.0进行认证");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);
            if (MainActivity.accessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new java.util.Date(MainActivity.accessToken
                                .getExpiresTime()));
                mText.setText("认证成功: \r\n access_token: " + token + "\r\n"
                        + "expires_in: " + expires_in + "\r\n有效期：" + date);
                try {
                    Class sso = Class
                            .forName("com.weibo.sdk.android.api.WeiboAPI");// 如果支持weiboapi的话，显示api功能演示入口按钮
//                    apiBtn.setVisibility(View.VISIBLE);
                    
//   API
                   
                    RemindAPI reminds = new RemindAPI(accessToken);
                    
                    reminds.remind(2152729781L, new RequestListener() {
						
						@Override
						public void onIOException(IOException e) {
							// TODO Auto-generated method stub
							System.out.println(e);
						}
						
						@Override
						public void onError(WeiboException e) {
							// TODO Auto-generated method stub
							System.out.println(e);
						}
						
						@Override
						public void onComplete(String response) {
							// TODO Auto-generated method stub
							Log.d("pengfei",response);
						}
					});
                    
                    CommentsAPI comments = new CommentsAPI(accessToken);
                    comments.timeline(0, 0, 10, 1, true, new RequestListener() {
						
						@Override
						public void onIOException(IOException e) {
							// TODO Auto-generated method stub
							System.out.println(e);
						}
						
						@Override
						public void onError(WeiboException e) {
							// TODO Auto-generated method stub
							System.out.println(e);
						}
						
						@Override
						public void onComplete(String response) {
							// TODO Auto-generated method stub
							Log.d("pengfei",response);
						}
					});
                    
                    

                } catch (ClassNotFoundException e) {
                    // e.printStackTrace();
                    Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");

                }
                
                cancelBtn.setVisibility(View.VISIBLE);
                AccessTokenKeeper.keepAccessToken(MainActivity.this,
                        accessToken);
                Toast.makeText(MainActivity.this, "认证成功", Toast.LENGTH_SHORT)
                        .show();
            }
            
        }

        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(getApplicationContext(),
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "Auth cancel",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(),
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * 下面两个注释掉的代码，仅当sdk支持sso时有效，
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

}
