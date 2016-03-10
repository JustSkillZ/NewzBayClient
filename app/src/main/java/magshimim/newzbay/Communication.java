package magshimim.newzbay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Communication implements Runnable {
    private ClientRead clientRead;
    public Communication() {
    }

    private Socket serverSocket;
    private String serverIP;
    private int dstport;
    private static boolean isConnect;

    @Override
    public void run() {
        serverIP = "109.65.174.197";
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
        if(isConnect)
        {
            clientRead = new ClientRead(serverSocket);
            new Thread(clientRead).start();
        }
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
        send("100#"); //START WITH THE CONVERSATION
        read();
    }
    public void read()
    {
        try {
            String line = in.readLine();
            while (line.compareTo("501#") != 0)
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
                if(line.equals("101#"))
                {
                    Log.d("Server", "101#");

                }
                else if(line.equals("103#") || line.equals("400#"))
                {
                    Log.d("Server", line);
                    send("106#");
                    send("104|1&1|1&2|4&1|4&2|8&1#");
                }
                else if(line.contains("107|")) {
                    Vector<String> id = new Vector<String>();
                    Vector<String> subject = new Vector<String>();
                    Vector<String> site = new Vector<String>();
                    line = line.substring(line.indexOf("|") + 1);
                    while (line.contains("|"))
                    {
                        String temp = line.substring(0, line.indexOf("|"));
                        id.add(temp.substring(0, temp.indexOf("&")));
                        temp = temp.substring((temp.indexOf("&") + 1));
                        subject.add(temp.substring(0, temp.indexOf("&")));
                        temp = temp.substring(temp.indexOf("&") + 1);
                        site.add(temp);
                        line = line.substring(line.indexOf("|") + 1);
                    }
                    Categories.setIdOfRSS(id);
                    Categories.setSubject(subject);
                    Categories.setSite(site);
                }
                else if(line.contains("115|")) {
                    line = line.substring(line.indexOf("|") + 1);
                    Vector<Article> subject = new Vector<Article>();
                    while (line.contains("|"))
                    {
                        String id, mainHeadLine, secondHeadLine, date, url, likes, imgURL;
                        String temp = line.substring(0, line.indexOf("|"));
                        id = temp.substring(0, temp.indexOf("&"));
                        temp = temp.substring((temp.indexOf("&") + 1));
                        mainHeadLine = temp.substring(0, temp.indexOf("&"));
                        temp = temp.substring((temp.indexOf("&") + 1));
                        secondHeadLine = temp.substring(0, temp.indexOf("&"));
                        temp = temp.substring(temp.indexOf("&") + 1);
                        date = temp.substring(0, temp.indexOf("&"));
                        temp = temp.substring(temp.indexOf("&") + 1);
                        url = temp.substring(0, temp.indexOf("&"));
                        temp = temp.substring(temp.indexOf("&") + 1);
                        imgURL = temp.substring(0, temp.indexOf("&"));
                        temp = temp.substring(temp.indexOf("&") + 1);
                        likes = temp;
                        line = line.substring(line.indexOf("|") + 1);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date dates = null;
                        try {

                            dates = formatter.parse(date);
                            System.out.println(dates);
                            System.out.println(formatter.format(dates));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Article article = new Article(Categories.getCurrentlyInUseCategory(), mainHeadLine, secondHeadLine, imgURL, dates, Categories.getSite().elementAt(Categories.getIdOfRSS().indexOf(id)), url, Integer.parseInt(likes), 0, false);
                        subject.add(article);
                    }
                    Categories.setCurrentlyInUse(subject);
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
        Log.d("send", str);
        out.println(str);
        out.flush();
    }
}