package demo;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Created by Administrator on 2016/1/20.
 */
public class Smack {
    private static Logger logger = Logger.getLogger(Hello.class);
    public XMPPTCPConnection connection = null;

    public boolean conServer(){
        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder().setHost("localhost")
                .setPort(5222).setServiceName("localhost").build();
        try{
            connection = new XMPPTCPConnection(conf);
            connection.connect();
            return true;
        }catch (Exception e){
            System.out.println("connection get error");
            e.printStackTrace();
        }
        return false;
    }

    public String regist(String name, String password){
        if(connection == null){
            return "0";
        }
        return "1";
    }

    public boolean login(){
        try{
            connection.login("shnanyang", "123456");
            return true;
        }catch (Exception e){
            System.out.println("login get error");
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendMessage(String msg){
        try{
            Chat chat = ChatManager.getInstanceFor(connection).createChat("admin@localhost/test", new ChatMessageListener() {
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
        if(connection == null){
            return;
        }

        Presence presence;
        try{
            switch (code){
                case 0:
                    presence = new Presence(Presence.Type.available);
                    connection.sendStanza(presence);
                    System.out.println("online");
                    System.out.println(presence.toXML());
                case 1:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.chat);
                    connection.sendStanza(presence);
                    System.out.println("chat");
                    System.out.println(presence.toXML());
                case 2:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.away);
                    connection.sendStanza(presence);
                    System.out.println("away");
                    System.out.println(presence.toXML());
                case 3:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.dnd);
                    connection.sendStanza(presence);
                    System.out.println("busy");
                    System.out.println(presence.toXML());
                case 4:
                    Roster roster = Roster.getInstanceFor(connection);
                    Collection<RosterEntry> entries = roster.getEntries();
                    for(RosterEntry entry: entries){
                        presence = new Presence(Presence.Type.available);
                        presence.setFrom(connection.getUser());
                        presence.setTo(entry.getUser());
                        connection.sendStanza(presence);
                        System.out.println(presence.toXML());
                    }
                case 5:
                    presence = new Presence(Presence.Type.unavailable);
                    connection.sendStanza(presence);
                    System.out.println("unavailable");
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disconnect(){
        try {
            if (connection != null){
                connection.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void main(String[] args){
        System.out.println("this is a test");
        if(conServer()){
            login();
            sendMessage("hello, this is a test!");
            setPresence(2);
            disconnect();
        }
    }
}
