package demo;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.java7.Java7SmackInitializer;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.*;
import java.util.*;

/**
 * Created by Administrator on 2016/1/25.
 */
public class SmackClient {
    private String SERVER_HOST = "192.168.16.175";
    private int PORT = 5222;
    private String SERVER_NAME = "localhost";
    private static XMPPTCPConnection connection = null;
    private TConnectionListener tConnectionListener;

    synchronized public static XMPPTCPConnection getInstace(){
        return connection;
    }

    public XMPPTCPConnection getConnection(){
        SmackConfiguration.DEBUG = true;
        if(connection == null){
            openConnection();
        }
        return connection;
    }
    /**
     * 初始化连接
     * @return
     */
    public void openConnection(){
        try {
            if(null == connection || !connection.isAuthenticated()){
                new Java7SmackInitializer().initialize();
                XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setHost(SERVER_HOST).setPort(PORT).setServiceName(SERVER_NAME).build();
                connection = new XMPPTCPConnection(conf);
                connection.connect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 关闭连接
     * @return
     */
    public void closeConnection(){
        if(connection!=null){
            if(connection.isConnected()){
                connection.disconnect();
            }

            connection = null;
        }
    }
    /**
     * 登陆
     * @return
     */
    public boolean login(String account, String password){
        try {
            if(connection==null){
                return false;
            }
            getConnection().login(account, password);

            Presence presence = new Presence(Presence.Type.available);
            getConnection().sendStanza(presence);

            tConnectionListener = new TConnectionListener();
            getConnection().addConnectionListener(tConnectionListener);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 注册用户
     * @return
     */
    public boolean regist(String account, String password){
        if(getConnection()==null){
            return false;
        }

//        Registration reg = new Registration();
//        reg.setType(IQ.Type.set);
//        reg.setTo(getConnection().getServiceName());
//        Form form = new Form(DataForm.Type.form);
//        FormField field = new FormField("username");
//        field.addValue(account);
//        form.addField(field);
//        FormField field1 = new FormField("password");
//        field1.addValue(password);
//        form.addField(field1);
//
//        reg.addExtension(form.getDataFormToSend());
        AccountManager accountManager = AccountManager.getInstance(getConnection());
        AccountManager.sensitiveOperationOverInsecureConnectionDefault(true);
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", account);
        map.put("password", password);
        try {
            accountManager.createAccount(account, password, map);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
    /**
     * 更改用户状态
     * @return
     */
    public void setPresence(int code){
        XMPPTCPConnection conn = getConnection();
        if(conn == null){
            return;
        }

        Presence presence;
        try{
            switch (code){
                case 0:
                    presence = new Presence(Presence.Type.available);
                    conn.sendStanza(presence);
                    System.out.println("online");
                    System.out.println(presence.toXML());
                    break;
                case 1:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.chat);
                    conn.sendStanza(presence);
                    System.out.println("chat");
                    System.out.println(presence.toXML());
                    break;
                case 2:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.away);
                    conn.sendStanza(presence);
                    System.out.println("away");
                    System.out.println(presence.toXML());
                    break;
                case 3:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.dnd);
                    conn.sendStanza(presence);
                    System.out.println("busy");
                    System.out.println(presence.toXML());
                    break;
                case 4:
                    Roster roster = Roster.getInstanceFor(conn);
                    Collection<RosterEntry> entries = roster.getEntries();
                    for(RosterEntry entry: entries){
                        presence = new Presence(Presence.Type.available);
                        presence.setFrom(conn.getUser());
                        presence.setTo(entry.getUser());
                        conn.sendStanza(presence);
                        System.out.println(presence.toXML());
                    }
                    break;
                case 5:
                    presence = new Presence(Presence.Type.unavailable);
                    conn.sendStanza(presence);
                    System.out.println("unavailable");
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 获取所有组
     * @return
     */
    public List<RosterGroup> getGroups(){
        if(getConnection() == null){
            return null;
        }

        List<RosterGroup> groupList = new ArrayList<RosterGroup>();
        Roster roster = Roster.getInstanceFor(getConnection());
        Collection<RosterGroup> rosterGroups = roster.getGroups();
        Iterator<RosterGroup> i = rosterGroups.iterator();
        while (i.hasNext()){
            groupList.add(i.next());
        }
        System.out.println("get roster "+groupList);
        return groupList;
    }
    /**
     * 添加一个分组
     * @return
     */
    public boolean addGroup(String groupName){
        if(getConnection() == null){
            return false;
        }

        try {
            Roster roster = Roster.getInstanceFor(getConnection());
            roster.createGroup(groupName);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
    /**
     * 删除分组
     * @return
     */
    public boolean removeGroup(String groupName){
        try {
            Roster roster = Roster.getInstanceFor(getConnection());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 获取某个分组里面的所有好友
     * @return
     */
    public List<RosterEntry> getEntriesByGroup(String groupName){
        List<RosterEntry> entryList = new ArrayList<RosterEntry>();

        Roster roster = Roster.getInstanceFor(getConnection());
        RosterGroup rosterGroup = roster.getGroup(groupName);
        if(rosterGroup != null){
            Collection<RosterEntry> rosterEntries = rosterGroup.getEntries();
            Iterator<RosterEntry> i = rosterEntries.iterator();
            while (i.hasNext()){
                entryList.add(i.next());
            }
        }
        System.out.println("group: "+groupName+" users: "+entryList);
        return entryList;
    }
    /**
     * 获取所有好友信息
     * @return
     */
    public List<RosterEntry> getAllEntries(){
        List<RosterEntry> rosterEntries = new ArrayList<RosterEntry>();

        Roster roster = Roster.getInstanceFor(getConnection());
        Collection<RosterEntry> entries = roster.getEntries();
        Iterator<RosterEntry> i = entries.iterator();
        while (i.hasNext()){
            rosterEntries.add(i.next());
        }

        return rosterEntries;
    }
    /**
     * 添加好友，无分组
     * @return
     */
    public boolean addUser(String userName, String name){
        try {
            Roster roster = Roster.getInstanceFor(getConnection());
            roster.createEntry(userName, name, null);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
    /**
     * 添加好友，有分组
     * @return
     */
    public boolean addUser(String userName, String name, String groupName){
        try {
            Presence presence = new Presence(Presence.Type.subscribe);
            presence.setTo(userName);
            userName += "@"+getConnection().getServiceName();
            getConnection().sendStanza(presence);

            Roster roster = Roster.getInstanceFor(getConnection());
            roster.createEntry(userName, name, new String[]{groupName});
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
    /**
     * 获取用户VCard信息
     * @return
     */
    public VCard getUserVcard(String name){
        VCard vCard = new VCard();
        try {
            vCard.load(getConnection(), name);
        }catch (Exception e){
            e.printStackTrace();
        }

        return vCard;
    }
    /**
     * 删除好友
     * @return
     */
    public boolean removeUser(String name){
        Roster roster = Roster.getInstanceFor(getConnection());

        try {
            RosterEntry entry;
            if(name.contains("@")){
                entry = roster.getEntry(name);
            }else{
                entry = roster.getEntry(name+"@"+getConnection().getServiceName());
            }

            roster.removeEntry(entry);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
    /**
     * 查询用户
     * @return
     */
    public List<HashMap<String, String>> searchUser(String name){
        HashMap<String, String> user = null;
        List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
        try {
            ServiceDiscoveryManager.getInstanceFor(getConnection());
            UserSearchManager usm = new UserSearchManager(getConnection());

            Form searchForm = usm.getSearchForm(getConnection().getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("userAccount", true);
            answerForm.setAnswer("userPhote", name);
            ReportedData data = usm.getSearchResults(answerForm, "search"+getConnection().getServiceName());

            List<ReportedData.Row> rowList = data.getRows();
            Iterator<ReportedData.Row> i = rowList.iterator();
            ReportedData.Row row = null;
            while (i.hasNext()){
                user = new HashMap<String, String>();
                row = i.next();
                user.put("userAccount", row.getValues("userAccount").toString());
                user.put("userPhote", row.getValues("userPhote").toString());
                results.add(user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }
    /**
     * 修改心情
     * @return
     */
    public void changeStateMessage(String state){
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus(state);

        try {
            getConnection().sendStanza(presence);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 修改用户头像
     * @return
     */
    public boolean changeImage(File file){
        VCard vCard = new VCard();
        try {
            vCard.load(getConnection());

            byte[] bytes;
            bytes = getFileBytes(file);
            String encodedImage = StringUtils.encodeHex(bytes);
            vCard.setAvatar(bytes);
            vCard.setEncodedImage(encodedImage);
            vCard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage + "</BINVAL>", true);

            ByteArrayInputStream bais = new ByteArrayInputStream(vCard.getAvatar());
            //TODO
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
    /**
     * 文件转字节
     * @return
     */
    public byte[] getFileBytes(File file) throws IOException{
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            int bytes = (int)file.length();
            byte[] buffer = new byte[bytes];
            int readBytes = bis.read(buffer);
            if(readBytes != buffer.length){
                throw new IOException("entry file not read");
            }
            return buffer;
        }finally {
            if(bis!=null){
                bis.close();
            }
        }
    }
    /**
     * 删除当前用户
     * @return
     */
    public boolean deleteAccount(){
        try {
            AccountManager accountManager = AccountManager.getInstance(getConnection());
            accountManager.deleteAccount();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 修改密码
     * @return
     */
    public boolean changePassword(String password){
        try {
            AccountManager accountManager = AccountManager.getInstance(getConnection());
            accountManager.changePassword(password);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 初始化会议室列表
     * @return
     */
    public List<HostedRoom> getHostRooms(){
        List<HostedRoom> roominfos = new ArrayList<HostedRoom>();
        Collection<HostedRoom> hostedRooms = null;
        try {
            ServiceDiscoveryManager.getInstanceFor(getConnection());
            hostedRooms = MultiUserChatManager.getInstanceFor(getConnection()).getHostedRooms(getConnection().getServiceName());
            for(HostedRoom entry: hostedRooms){
                roominfos.add(entry);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return roominfos;
    }

    /**
     * 创建房间
     * @param user
     * @param roomName
     * @param password
     * @return
     */
    public MultiUserChat createRoom(String user, String roomName, String password){
        MultiUserChat muc = null;
        try {
            muc = MultiUserChatManager.getInstanceFor(getConnection())
                    .getMultiUserChat(roomName + "@conference" + getConnection().getServiceName());
            muc.create(roomName);

            Form form = muc.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            List<FormField> formFields = form.getFields();
            Iterator<FormField> fields = formFields.iterator();
            while (fields.hasNext()){
                FormField field = (FormField)fields.next();
                if(!FormField.FORM_TYPE.equals(field.getType()) && field.getVariable()!=null){
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }

            List<String> owners = new ArrayList<String>();
            owners.add(getConnection().getUser());
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            // 设置聊天室是持久聊天室，即将要被保存下来
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            // 房间仅对成员开放
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            // 允许占有者邀请其他人
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            if (!password.equals("")) {
                // 进入是否需要密码
                submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
                        true);
                // 设置进入密码
                submitForm.setAnswer("muc#roomconfig_roomsecret", password);
            }
            // 能够发现占有者真实 JID 的角色
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
            // 登录房间对话
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
            // 仅允许注册的昵称登录
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
            // 允许使用者修改昵称
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
            // 允许用户注册房间
            submitForm.setAnswer("x-muc#roomconfig_registration", false);
            muc.sendConfigurationForm(submitForm);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return muc;
    }

    /**
     * 加入会议室
     * @param user
     * @param roomsName
     * @param password
     * @return
     */
    public MultiUserChat joinMultiUserChat(String user, String roomsName, String password){
        try {
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(getConnection())
                    .getMultiUserChat(roomsName + "@conference" + getConnection().getServiceName());
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxChars(0);
            muc.join(user, password, history, SmackConfiguration.getDefaultPacketReplyTimeout());
            return muc;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询会议室成员名字
     * @param muc
     * @return
     */
    public List<String> findMultiUser(MultiUserChat muc){
        List<String> listUser = muc.getOccupants();
        return listUser;
    }

    /**
     * 传送文件
     * @param user
     * @param filePath
     */
    public void sendFile(String user, String filePath){
        FileTransferManager manager = FileTransferManager.getInstanceFor(getConnection());
        OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(user);

        try {
            transfer.sendFile(new File(filePath), "You won't believe this");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取离线信息
     * @return
     */
    public Map<String, List<HashMap<String, String>>> getHisMessage(){
        Map<String, List<HashMap<String, String>>> offlineMsgs = null;
        try {
            OfflineMessageManager offlineMessageManager = new OfflineMessageManager(getConnection());
            List<Message> messageList = offlineMessageManager.getMessages();
            Iterator<Message> it = messageList.iterator();

            int count = messageList.size();
            if(count < 1){
                return null;
            }

            offlineMsgs = new HashMap<String, List<HashMap<String, String>>>();
            while (it.hasNext()){
                Message message = it.next();
                String fromUser = message.getFrom();
                HashMap<String, String> history = new HashMap<String, String>();
                history.put("useraccount", getConnection().getUser());
                history.put("friendaccount", fromUser);
                history.put("info", message.getBody());
                history.put("type", "left");
                if(offlineMsgs.containsKey(fromUser)){
                    offlineMsgs.get(fromUser).add(history);
                }else {
                    List<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
                    temp.add(history);
                    offlineMsgs.put(fromUser, temp);
                }
            }

            offlineMessageManager.deleteMessages();
        }catch (Exception e){
            e.printStackTrace();
        }
        return offlineMsgs;
    }
}