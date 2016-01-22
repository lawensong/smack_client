package demo;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/22.
 */
public class TChat {
    private Map<String, Chat> chatManage = new HashMap<String, Chat>();

    public Chat getFriendChat(XMPPTCPConnection connection, String friend, ChatMessageListener listener){
        if(connection == null){
            return null;
        }

        for(String first: chatManage.keySet()){
            if(first.equals(friend)){
                return chatManage.get(first);
            }
        }

        Chat chat = ChatManager.getInstanceFor(connection).createChat(friend, listener);
        chatManage.put(friend, chat);
        return chat;
    }
}
