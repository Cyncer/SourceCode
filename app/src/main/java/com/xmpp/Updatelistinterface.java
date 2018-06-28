package com.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

public interface Updatelistinterface {
	void onNewMessage();

	void refrehsList(Chat chat, Message message, String profile);
}
