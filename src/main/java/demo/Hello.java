package demo;

/**
 * Created by Administrator on 2016/1/20.
 */
public class Hello {
    public static void main(String[] args){
        System.out.println("this is a test");
        Smack smack = new Smack();
        if(smack.conServer()){
            smack.sendMessage("hello, this is a test!");
            smack.setPresence(2);
            smack.disconnect();
        }
    }
}
