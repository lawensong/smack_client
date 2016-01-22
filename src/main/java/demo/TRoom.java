package demo;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/1/22.
 */
public class TRoom {

    public void createRoom(XMPPTCPConnection connection, String roomName, String desc, String roomId){
        if(connection == null){
            return;
        }

        try {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = manager.getMultiUserChat(roomId);
            muc.create(roomName);

            Form form = muc.getConfigurationForm();
            Form answerForm = form.createAnswerForm();

            for(FormField field: form.getFields()){
                if(!FormField.Type.hidden.name().equals(field.getType()) && field.getValues()!=null){
                    answerForm.setDefaultAnswer(field.getVariable());
                }
            }

            answerForm.setAnswer(FormField.FORM_TYPE, "http://jabber.org/protocol/muc#roomconfig");
            answerForm.setAnswer("muc#roomconfig_roomname", roomName);
            answerForm.setAnswer("muc#roomconfig_roomdesc", desc);
            answerForm.setAnswer("muc#roomconfig_changesubject", true);

            List<String> maxUsers = new ArrayList<String>();
            maxUsers.add("50");
            answerForm.setAnswer("muc#roomconfig_maxusers", maxUsers);

            List<String> cast_values = new ArrayList<String>();
            cast_values.add("moderator");
            cast_values.add("participant");
            cast_values.add("visitor");
            answerForm.setAnswer("muc#roomconfig_presencebroadcast", cast_values);
            //设置为公共房间
            answerForm.setAnswer("muc#roomconfig_publicroom", true);
            //设置为永久房间
            answerForm.setAnswer("muc#roomconfig_persistentroom", true);
            //允许修改昵称
            answerForm.setAnswer("x-muc#roomconfig_canchangenick", true);
            //允许用户登录注册房间
            answerForm.setAnswer("x-muc#roomconfig_registration", true);

            muc.sendConfigurationForm(answerForm);
            muc.join(roomName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public MultiUserChat joinMultiUserChat(String user, String password, String roomId, XMPPTCPConnection connection){
        try {
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = manager.getMultiUserChat(roomId);

            muc.join(roomId, password);
            return muc;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<String> findMultiName(MultiUserChat muc){
        List<String> userList = new ArrayList<String>();
        Collection<String> occupants = muc.getOccupants();
        Iterator<String> it = occupants.iterator();
        while (it.hasNext()){
            userList.add(it.next());
        }
        return userList;
    }

}
