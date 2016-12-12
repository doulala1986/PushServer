package com.ctsi.push.server.api;

import com.ctsi.push.message.CommandMessage;
import com.ctsi.push.message.PushResponse;

public interface PushApi {

	PushResponse push(CommandMessage message) throws Exception;//sync
	 
}
