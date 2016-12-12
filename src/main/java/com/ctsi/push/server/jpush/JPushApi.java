package com.ctsi.push.server.jpush;

import com.ctsi.push.server.api.PushApi;
import com.ctsi.push.message.CommandMessage;
import com.ctsi.push.message.PushResponse;
import cn.jpush.api.push.PushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;

public class JPushApi implements PushApi {

	// private String masterSecret="b857c8c5d033ff534828b795";
	
	// private String appKey="1008fb255a7f1b8bd339a745";

	private String masterSecret;
	private String appKey;

	private static JPushApi api = null;

	private JPushApi(String masterSecret, String appKey) {
		refreshKeys(masterSecret, appKey);
	}

	private void refreshKeys(String masterSecret, String appKey) {
		this.masterSecret = masterSecret;
		this.appKey = appKey;
	}

	public static JPushApi init(String masterSecret, String appKey) {

		if (api == null) {
			synchronized (JPushApi.class) {
				if (api == null) {
					api = new JPushApi(masterSecret, appKey);
				} else {
					api.refreshKeys(masterSecret, appKey);
				}
			}
		} else {
			api.refreshKeys(masterSecret, appKey);
		}
		return api;
	}

	public static JPushApi get() {
		if (api == null) {
			throw new RuntimeException("请先调用init方法进行初始化");
		}
		return api;

	}

	public PushResponse push(CommandMessage message) throws Exception {
		// TODO Auto-generated method stub
		PushClient pushClient = new PushClient(masterSecret, appKey);
		PushPayload pushPayload = JPushConverter.messageConverter(message);
		PushResult result = pushClient.sendPush(pushPayload);//throws APIConnectionException, APIRequestException 
		PushResponse response=JPushConverter.responseConverter(result);
		return response;
	}

}
