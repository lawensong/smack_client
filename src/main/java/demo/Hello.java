package demo;

/**
 * Created by Administrator on 2016/1/20.
 */
public class Hello {
    public static void main(String[] args){
//        System.out.println("this is a test");
//        Smack smack = new Smack();
//        smack.conServer();
//        if(smack.isConnection()){
//            smack.sendMessage("hello, this is a test!");
//            smack.setPresence(2);
//            smack.disconnect();
//        }

        SmackClient smackClient = new SmackClient();
        smackClient.getConnection();
        smackClient.login("shnanyang", "123456");
        smackClient.setPresence(1);
//        smackClient.regist("tom", "123456");
//        smackClient.setPresence(3);
//        smackClient.addGroup("friends");
//        smackClient.addUser("admin", "admin", "friends");
        try {
            Thread.sleep(5000);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("***********************************************************");
//        smackClient.getEntriesByGroup("friends");
//        smackClient.getAllEntries();
//        smackClient.getGroups();
//        smackClient.getHisMessage();
//        smackClient.getGroups();
//        smackClient.getUserVcard();
//        smackClient.createRoom("shnanyang", "boy7", "");
//        smackClient.joinMultiUserChat("shnanyang", "boy7", "123456");
        smackClient.sendMessage("hello world!");
        smackClient.addPacketListener();
        try {
            Thread.sleep(40000);
        }catch (Exception e){
            e.printStackTrace();
        }

        smackClient.closeConnection();
    }
}
