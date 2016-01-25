package demo;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.java7.Java7SmackInitializer;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
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
        if(connection == null){
            openConnection();
        }
        return connection;
    }

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

    public void closeConnection(){
        if(connection!=null){
            if(connection.isConnected()){
                connection.disconnect();
            }

            connection = null;
        }
    }

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

    public boolean removeGroup(String groupName){
        try {
            Roster roster = Roster.getInstanceFor(getConnection());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

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

    public VCard getUserVcard(String name){
        VCard vCard = new VCard();
        try {
            vCard.load(getConnection(), name);
        }catch (Exception e){
            e.printStackTrace();
        }

        return vCard;
    }

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

    public List<HashMap<String, String>> searchUser(String name){
        HashMap<String, String> user = new HashMap<String, String>();
        List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
        try {
            ServiceDiscoveryManager.getInstanceFor(getConnection());
            UserSearchManager usm = new UserSearchManager(getConnection());

            Form searchForm = usm.getSearchForm(getConnection().getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("userAccount", true);
            answerForm.setAnswer("userPhote", name);
        }catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }

    public void changeStateMessage(String state){
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus(state);

        try {
            getConnection().sendStanza(presence);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean changeImage(File file){
        VCard vCard = new VCard();
        try {
            vCard.load(getConnection());

            byte[] bytes;
            bytes = getFileBytes(file);
            String encodedImage = StringUtils.encodeHex(bytes);
            vCard.setAvatar(bytes);
            vCard.setEncodedImage(encodedImage);
            vCard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"+encodedImage+"</BINVAL>", true);

            ByteArrayInputStream bais = new ByteArrayInputStream(vCard.getAvatar());
            //TODO
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

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
}
