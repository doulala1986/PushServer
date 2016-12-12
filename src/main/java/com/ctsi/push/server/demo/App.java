package com.ctsi.push.server.demo;

import java.util.HashMap;

import com.ctsi.push.server.api.PushApi;
import com.ctsi.push.server.jpush.JPushApi;
import com.ctsi.push.message.CommandAction;
import com.ctsi.push.message.CommandMessage;
import com.ctsi.push.message.PushResponse;

/**
 * Hello world!
 *
 */
public class App {
	private static final String masterSecret = "daec95959b66542e0d0e8f21";

	private static final String appKey = "3ab4fa240f70a6e9f4f35177";

	static PushApi api = JPushApi.init(masterSecret, appKey);

	public static void main(String[] args) {
		System.out.println("Hello World!");
		CommandMessage message1 = CommandMessage.builder().toAlias("18911552163").noticeContent("测试", "123").build();
		
		HashMap<String,String> map=new HashMap<String,String>();
		map.put("user", "liyao");
		map.put("id", "123");
		
		CommandMessage message2= CommandMessage.builder().timeExpire(300).toAlias("13311097869").noticeContent("测试title", "内容").commandAction(CommandAction.COMMAND_TYPE_URL, "http://www.baidu.com", map).build();
				
		try {
			PushResponse response = api.push(message2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
