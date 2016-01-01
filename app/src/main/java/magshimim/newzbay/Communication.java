package magshimim.newzbay;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Communication {
    private String serverIP;
    private int dstport;
    private Socket clientSocket = null;
    private Communication()
    {
        new Thread(new ClientThread()).start();
    }
    public boolean checkComment(String comment)
    {
        boolean check = true;
        return check;
    }
    public void encrypting(String msg)
    {

    }
    public void decrypting(String msg)
    {

    }
    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(serverIP);

                clientSocket = new Socket(serverAddr, dstport);

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }
}