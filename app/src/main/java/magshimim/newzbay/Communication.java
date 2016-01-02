package magshimim.newzbay;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Communication {
    private String serverIP;
    private int dstport;
    public Communication()
    {
        serverIP = "109.64.59.139";
        dstport = 4444;
        Log.d("check", "Communication");
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
        private Socket clientSocket;
        @Override
        public void run() {

            try {
                clientSocket = new Socket(serverIP, dstport);
                Log.d("check", "Connected to server");
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try
            {
                /*DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                dos.writeUTF("check");
                dos.flush();
                dos.close();*/
                clientSocket.close();
            }
            catch (UnknownHostException e1)
            {
                e1.printStackTrace();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }

    }
}