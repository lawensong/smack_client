package demo;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.java7.Java7SmackInitializer;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;


import java.io.IOException;
import java.util.Collection;

/**
 * Created by Administrator on 2016/1/20.
 */
public class Smack {
    private XMPPTCPConnection conn;

    public void conServer(){
        new Java7SmackInitializer().initialize();
        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder().setHost("192.168.16.175")
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setPort(5222).setServiceName("localhost").build();

        try {
            conn = new XMPPTCPConnection(conf);
            conn.connect();
            conn.login("shnanyang","123456");
        } catch (SmackException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (XMPPException e) {
//            e.printStackTrace();
        }
    }

    public boolean isConnection(){
        if(conn.isConnected()){
            return true;
        }else {
            return false;
        }
    }

    public String regist(String name, String password){
        if(conn == null){
            return "0";
        }
        return "1";
    }

    public boolean login(){
        try{
            conn.login("shnanyang", "123456");
            return true;
        }catch (Exception e){
            System.out.println("login get error");
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendMessage(String msg){
        try{
            Chat chat = ChatManager.getInstanceFor(conn).createChat("admin@localhost/test", new ChatMessageListener() {
                public void processMessage(Chat chat, Message message) {
                    // Print out any messages we get back to standard out.
                    System.out.println("Received message: " + message);
                }
            });
            chat.sendMessage(msg);
            chat.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void setPresence(int code){
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

    public void disconnect(){
        try {
            if (conn != null){
                conn.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
