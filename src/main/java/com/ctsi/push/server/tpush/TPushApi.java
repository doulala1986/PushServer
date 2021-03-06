package com.ctsi.push.server.tpush;

import cn.jpush.api.push.PushResult;
import com.ctsi.push.message.CommandAction;
import com.ctsi.push.message.CommandMessage;
import com.ctsi.push.message.NoticeContent;
import com.ctsi.push.message.PushResponse;
import com.ctsi.push.server.api.PushApi;
import com.google.gson.Gson;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.TimeInterval;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by doulala on 2016/12/19.
 */
public class TPushApi implements PushApi {

    private XingeApp xinge_android, xinge_iOS;
    private Gson g = new Gson();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TPushApi(TPushKeyStore androidKeyStore, TPushKeyStore iOSKeyStore) {
        xinge_android = new XingeApp(androidKeyStore.getAccessId(), androidKeyStore.getSecretKey());
        xinge_iOS = new XingeApp(iOSKeyStore.getAccessId(), iOSKeyStore.getSecretKey());
    }

    private static TPushApi api = null;

    public static TPushApi init(TPushKeyStore androidKeyStore, TPushKeyStore iOSKeyStore) {
        if (api == null) {
            synchronized (TPushApi.class) {
                if (api == null) {
                    api = new TPushApi(androidKeyStore, iOSKeyStore);
                }
            }
        }
        return api;
    }

    private boolean debug = false;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public static TPushApi get() {
        if (api == null) {
            throw new RuntimeException("请先调用init方法进行初始化");
        }
        return api;
    }


    @Override
    public PushResponse push(CommandMessage message) throws Exception {
        ArrayList<JSONObject> result = null;
        if (message == null)
            return new PushResponse("", -1, "CommamdMessage结构异常：" + message.toString());
        if (message.isHaveNoTags()) {
            result = pushToAlias(message);
        } else if (message.isHaveNoAlias()) {
            result = pushToTags(message);
        }
        return responseConverter(result);
    }

    public static PushResponse responseConverter(ArrayList<JSONObject> results) {
        StringBuilder sb = new StringBuilder();
        int resultcode = 0;
        for (JSONObject result : results) {//{:-1,'err_msg':'message type error!\
            if (result.getInt("ret_code") != 0) {
                resultcode = -1;
                sb.append("Error Message:");
                sb.append(result.toString());
                sb.append("\n");
            } else {
                sb.append("Success Message:");
                sb.append(result.toString());
                sb.append("\n");
            }
        }

        int response_code = resultcode;
        String message = sb.toString();
        return new PushResponse("", response_code, message);

    }


    private Message androidMessage(CommandMessage command) {
        if (command == null)
            return null;
        Message message = new Message();
        message.setExpireTime((int) command.getExpire());
        NoticeContent content = command.getNoticeContent();
        if (content != null) {
            message.setTitle(content.getTitle());
            message.setContent(content.getSubTitle());
        }
        message.setType(com.tencent.xinge.Message.TYPE_MESSAGE);
        HashMap<String, Object> custom = new HashMap<>();
        CommandAction action = command.getCommandAction();
        if (action != null) {
            Map<String, String> extras = action.toExtra();
            custom.putAll(extras);
            message.setCustom(custom);
        }
        return message;
    }

    private MessageIOS iOSMessage(CommandMessage command) {
        if (command == null)
            return null;
        NoticeContent content = command.getNoticeContent();
        if (content == null)
            return null;
        MessageIOS message = new MessageIOS();
        message.setExpireTime((int) command.getExpire());

        JSONObject object = new JSONObject();
        object.put("title", content.getTitle());
        object.put("body", content.getSubTitle());
        message.setAlert(object);
        message.setBadge(1);
        message.setSound("beep.wav");
        Map<String, Object> custom = new HashMap<String, Object>();
        CommandAction action = command.getCommandAction();
        if (action != null) {
            Map<String, String> extras = action.toExtra();
            custom.putAll(extras);
            message.setCustom(custom);
        }
        return message;
    }


    public ArrayList<JSONObject> pushToTags(CommandMessage command) {
        List<String> tagList = new ArrayList<String>();
        tagList.addAll(command.getTags());
        Message androidMessage = androidMessage(command);
        MessageIOS iOSMessage = iOSMessage(command);
        ArrayList<JSONObject> result = new ArrayList<>();


        if (androidMessage != null) {
            JSONObject ret = xinge_android.pushTags(0, tagList, "OR", androidMessage);
            result.add(ret);
        }
        if (iOSMessage != null) {
            JSONObject ret = xinge_iOS.pushTags(0, tagList, "OR", iOSMessage, debug ? XingeApp.IOSENV_DEV : XingeApp.IOSENV_PROD);
            result.add(ret);
        }
        return result;
    }

    public ArrayList<JSONObject> pushToAlias(CommandMessage command) {

        List<String> accountList = new ArrayList<String>();
        accountList.addAll(command.getAlias());
        Message androidMessage = androidMessage(command);
        MessageIOS iOSMessage = iOSMessage(command);
        ArrayList<JSONObject> result = new ArrayList<>();
        if (androidMessage != null) {
            JSONObject ret = xinge_android.pushAccountList(0, accountList, androidMessage);
            result.add(ret);
        }
        if (iOSMessage != null) {
            JSONObject ret = xinge_iOS.pushAccountList(0, accountList, iOSMessage, debug ? XingeApp.IOSENV_DEV : XingeApp.IOSENV_PROD);
            result.add(ret);
        }
        return result;
    }


}
