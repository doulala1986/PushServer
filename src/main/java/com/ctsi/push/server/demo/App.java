package com.ctsi.push.server.demo;

import java.util.HashMap;

import com.ctsi.push.server.api.PushApi;
import com.ctsi.push.server.jpush.JPushApi;
import com.ctsi.push.message.CommandAction;
import com.ctsi.push.message.CommandMessage;
import com.ctsi.push.message.PushResponse;
import com.ctsi.push.server.tpush.TPushApi;
import com.ctsi.push.server.tpush.TPushKeyStore;

/**
 * Hello world!
 */
public class App {
    //for JPush
    	private static final String masterSecret = "693693b39093bd7bce4d8be2";
    	private static final String appKey = "f954367d81978289b4427797";
    	static PushApi api = JPushApi.init(masterSecret, appKey);


    //for TPush
    private static final long accessId_iOS =2200246531l;
    private static final String secretKey_iOS  = "ba17d9c24cf17e40412241a7664be136";
    private static final TPushKeyStore keystore_iOS=new TPushKeyStore(accessId_iOS,secretKey_iOS);

    private static final long accessId_android =2100246530l;
    private static final String secretKey_android  = "1c18da34353ee1a22c8d24a6ea9e0b64";
    private static final TPushKeyStore keystore_android=new TPushKeyStore(accessId_android,secretKey_android);


//    static PushApi api = TPushApi.init(keystore_android, keystore_iOS);

    public static void main(String[] args) {
        System.out.println("Hello World!");
        CommandMessage message1 = CommandMessage.builder().toAlias("18911552161").noticeContent("测试", "123").build();
        CommandMessage message3 = CommandMessage.builder().toAlias("18911552161").noticeContent("测试2", "1232").build();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user", "liyao");
        map.put("id", "123");

        CommandMessage message2 = CommandMessage.builder().timeExpire(0).toAlias("18911552163").commandAction(CommandAction.COMMAND_TYPE_URL, "http://www.baidu.com", map).build();

        try {
            PushResponse response = api.push(message2);
            System.out.println(response.getMessage());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
