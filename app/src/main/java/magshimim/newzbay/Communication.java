package magshimim.newzbay;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Communication implements Runnable {
    private ClientRead clientRead;
    public Communication() {
    }

    private Socket serverSocket;
    private String serverIP;
    private int dstport;
    private boolean isConnect;

    @Override
    public void run() {
        serverIP = "79.182.153.253";
        isConnect = false;
        dstport = 4444;
        try {
            serverSocket = new Socket(serverIP, dstport);
            isConnect = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("check", "IO Exception");
            isConnect = false;
        }
        clientRead = new ClientRead(serverSocket);
        new Thread(clientRead).start();
    }
    /*finally
    {
        try
        {
            System.out.println("Connection Closing..");
            if (is != null)
            {
                is.close();
                System.out.println(" Socket Input Stream Closed");
            }

            if (os != null)
            {
                os.close();
                System.out.println("Socket Out Closed");
            }
            if (serverSocket != null)
            {
                serverSocket.close();
                System.out.println("Socket Closed");
            }

        }
        catch (IOException ie)
        {
            System.out.println("Socket Close Error");
        }
    }//end finally*/
    public boolean getIsConnect()
    {
        return isConnect;
    }
    public void clientSend(String str)
    {
        clientRead.send(str);
    }
}
class ClientRead extends Thread {
    private Socket serverSocket;
    public PrintWriter out;
    public BufferedReader in;
    public ClientRead(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run()
    {
        try
        {
            out = new PrintWriter(serverSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        }
        catch (IOException e)
        {
            Log.d("check", "IO Error/ Client terminated abruptly");
        }
        catch (NullPointerException e)
        {
            Log.d("check", "Client Closed");
        }
        read();
    }
    public void read()
    {
        try {
            String line = in.readLine();
            while (line.compareTo("QUIT") != 0)
            {
                Log.d("check", "Response from server :  " + line);
                line = in.readLine();
                try {
                    sleep(200);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {

            Log.d("check", "IO Error/ Client terminated abruptly");
        } catch (NullPointerException e) {
            Log.d("check", "Client Closed");
        }
    }
    public void send(String str)
    {
        Log.d("send", "send");
        out.println(str);
        out.flush();
    }
}