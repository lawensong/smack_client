package demo;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by Administrator on 2016/1/25.
 */
public class TConnectionListener implements ConnectionListener {
    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        System.out.println("auth!!!");
    }

    @Override
    public void connected(XMPPConnection connection) {
        System.out.println("connection!!!");
    }

    @Override
    public void connectionClosed() {
        System.out.println("close!!!");
    }

    @Override
    public void connectionClosedOnError(Exception e) {

    }

    @Override
    public void reconnectionSuccessful() {

    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    @Override
    public void reconnectionFailed(Exception e) {

    }
}
