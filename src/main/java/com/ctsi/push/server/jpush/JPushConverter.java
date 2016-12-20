package com.ctsi.push.server.jpush;

import java.util.Map;

import com.ctsi.push.message.CommandAction;
import com.ctsi.push.message.CommandMessage;
import com.ctsi.push.message.NoticeContent;
import com.ctsi.push.message.PushResponse;

import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.IosAlert;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/**
 * Created by doulala on 2016/10/9.
 */

public class JPushConverter {

    //

    /**
     * 把PushMessage转化成 极光(第三方)协议 输出可直接发送的推送内容
     *
     * @param message
     * @return
     */
    public static PushPayload messageConverter(CommandMessage message) {

        PushPayload.Builder builder = PushPayload.newBuilder();
        builder.setPlatform(Platform.all());// 设置平台

        /** 设置推送目标 **/
        Audience.Builder audienceBuilder = Audience.newBuilder();
        if (!message.isHaveNoAlias()) {

            audienceBuilder.addAudienceTarget(AudienceTarget.alias(message.getAlias()));
        }
        if (!message.isHaveNoTags()) {
            audienceBuilder.addAudienceTarget(AudienceTarget.tag(message.getTags()));
        }
        builder.setAudience(audienceBuilder.build());


        /** 设置推送消息 **/
        Message.Builder messageBuilder = Message.newBuilder();
        IosNotification.Builder iOSNoticeBuilder = IosNotification.newBuilder();
//        .addPlatformNotification(IosNotification.newBuilder()
//                .setAlert(ALERT)
//                .setBadge(5)
//                .setSound("happy.caf")
//                .addExtra("from", "JPush")
//                .build()
        // 设置消息内容

        NoticeContent noticeContent = message.getNoticeContent();
        boolean needIOSNotification = true;
        if (noticeContent != null) {
            messageBuilder.setTitle(noticeContent.getTitle());
            messageBuilder.setMsgContent(noticeContent.getSubTitle());
            iOSNoticeBuilder.setAlert(
                    IosAlert.newBuilder().setTitleAndBody(noticeContent.getTitle(), noticeContent.getSubTitle()).build());
        } else {
            //没有noticeContent，无需iOS推送
            needIOSNotification = false;
            messageBuilder.setMsgContent("");
        }
        // 设置推送命令
        CommandAction action = message.getCommandAction();
        if (action != null) {
            Map<String, String> extras = action.toExtra();
            messageBuilder.addExtras(extras);
            iOSNoticeBuilder.addExtras(extras);
        }
        builder.setMessage(messageBuilder.build());
        if (needIOSNotification)
            builder.setNotification(Notification.newBuilder().addPlatformNotification(iOSNoticeBuilder.build()).build());

        /**
         * 其他设置
         *
         * sendno int 可选 推送序号 纯粹用来作为 API 调用标识，API 返回时被原样返回，以方便 API 调用方匹配请求与返回。
         *
         * time_to_live int 可选 离线消息保留时长 推送当前用户不在线时，为该用户保留多长时间的离线消息，以便其上线时再次推送。默认
         * 86400 （1 天），最长 10 天。设置为 0 表示不保留离线消息，只有推送当前在线的用户可以收到。
         *
         * override_msg_id long 可选 要覆盖的消息ID 如果当前的推送要覆盖之前的一条推送，这里填写前一条推送的 msg_id
         * 就会产生覆盖效果，即：1）该 msg_id 离线收到的消息是覆盖后的内容；2）即使该 msg_id Android
         * 端用户已经收到，如果通知栏还未清除，则新的消息内容会覆盖之前这条通知；覆盖功能起作用的时限是：1 天。 如果在覆盖指定时限内该
         * msg_id 不存在，则返回 1003 错误，提示不是一次有效的消息覆盖操作，当前的消息不会被推送。
         *
         * apns_production boolean 可选 APNs是否生产环境 True 表示推送生产环境，False 表示要推送开发环境；
         * 如果不指定则为推送生产环境。
         *
         * (消息) JPush 官方 API LIbrary (SDK) 默认设置为推送 “开发环境”。
         *
         * big_push_duration int 可选 定速推送时长（分钟） 又名缓慢推送，把原本尽可能快的推送速度，降低下来，在给定的 n
         * 分钟内，均匀地向这次推送的目标用户推送。最大值为 1440。未设置则不是定速推送。
         *
         */
        Options.Builder optionBuilder = Options.newBuilder();
        optionBuilder.setTimeToLive(message.getExpire());
//		optionBuilder.setApnsProduction(false);
        builder.setOptions(optionBuilder.build());
        return builder.build();
    }


    public static PushResponse responseConverter(PushResult result) {
        int response_code = result.getResponseCode() == PushResponse.ERROR_CODE_OK ? PushResponse.ERROR_CODE_OK : result.getResponseCode();
        String message = "";
        String msg_id = String.valueOf(result.msg_id);
        return new PushResponse(msg_id, response_code, message);
    }

}
