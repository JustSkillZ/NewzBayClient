package magshimim.newzbay;

import android.app.Activity;
import android.util.Log;
import android.widget.Button;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Communication implements Runnable {
    private ClientRead clientRead;
    private GlobalClass globalClass;
    private Socket serverSocket;
    private String serverIP;
    private int dstport;
    private static int isConnect;
    private boolean userConnected;

    public Communication(GlobalClass globalClass) {
        this.globalClass = globalClass;
    }

    @Override
    public void run() {
        serverIP = "109.67.60.124";
        isConnect = 0;
        dstport = 4444;
        userConnected = false;
        serverSocket = new Socket();
        try {
            serverSocket.connect(new InetSocketAddress(serverIP, dstport), 5000);
            isConnect = 1;
            ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button guestLoginBtn = (Button) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.btn_NB);
                    if (guestLoginBtn != null) {
                        guestLoginBtn.setBackground(globalClass.getCurrentActivity().getResources().getDrawable(R.drawable.button_rounded_corners));
                        guestLoginBtn.setTextColor(globalClass.getCurrentActivity().getResources().getColor(R.color.white));
                        guestLoginBtn.setAlpha((float) 1);
                        guestLoginBtn.setEnabled(true);
                    }

                    LoginButton facebookLoginBtn = (LoginButton) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.btn_Facebook);
                    if (facebookLoginBtn != null) {
                        facebookLoginBtn.setBackground(globalClass.getCurrentActivity().getResources().getDrawable(R.drawable.button_rounded_corners_facebook));
                        facebookLoginBtn.setTextColor(globalClass.getCurrentActivity().getResources().getColor(R.color.white));
                        facebookLoginBtn.setAlpha((float) 1);
                        facebookLoginBtn.setEnabled(true);
                    }

                    SignInButton googleLoginBtn = (SignInButton) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.btn_Google);
                    if (googleLoginBtn != null) {
                        googleLoginBtn.setEnabled(true);
                    }

                    if (globalClass.getUser() == null) {
                        ((entrance) globalClass.getCurrentActivity()).connectToSocialNets();
                    } else {
                        userConnected = true;
                    }

                    clientRead = new ClientRead(serverSocket, globalClass, userConnected);
                    Thread t = new Thread(clientRead);
                    t.start();
                }
            });
        }
        catch (IOException e) { //No connection to server
            isConnect = -1;
            globalClass.getErrorHandler().reConnect();
        }
    }

    public void clientSend(String str)
    {
        if(clientRead != null)
        {
            clientRead.send(str);
        }
    }

    public static int isConnect() {
        return isConnect;
    }

    public static void setIsConnect(int isConnect) {
        Communication.isConnect = isConnect;
    }
}
class ClientRead extends Thread {
    private Socket serverSocket;
    private PrintWriter out;
    private BufferedWriter bufferedWriter;
    private BufferedReader in;
    private GlobalClass globalClass;
    private CategoriesHandler categoriesHandler;
    private PriorityHandler priorityHandler;
    private AESEncryption aesEncryption;
    private boolean userConnected;

    public ClientRead(Socket serverSocket, GlobalClass globalClass, boolean userConnected) {
        this.serverSocket = serverSocket;
        this.globalClass = globalClass;
        categoriesHandler = globalClass.getCategoriesHandler();
        priorityHandler = globalClass.getPriorityHandler();
        aesEncryption = new AESEncryption();
        this.userConnected = userConnected;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(serverSocket.getOutputStream());
            bufferedWriter = new BufferedWriter(out);
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (IOException e) {
            Log.d("check", "IO Error/ Client terminated abruptly");
        } catch (NullPointerException e) {
            Log.d("check", "Client Closed");
        }
        out.println("404|" + aesEncryption.getAesKey() + "##");
        out.flush();
        try {
            if(in.readLine().equals("405#"))
            {
                send("100#");
                read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read()
    {
        try {

            String line = "";
            do {
                line = line + in.readLine();
                if (line.contains("##")) {
                    line = line.substring(0,line.length() - 2);
                    line = aesEncryption.decrypt(line);
                }
                if(line.contains("#"))
                {
                    //line = aesEncryption.decryptText(line.getBytes(), AESKey);
                    Log.d("check", "Response from server :  " + line);
                    if (line.equals("101#")) {
                        if(userConnected)
                        {
                            send(globalClass.getErrorHandler().getConnectingClientMsg());
                            send(globalClass.getErrorHandler().getLastMsgToServer());
                            globalClass.getErrorHandler().setLastMsgToServer("");
                        }
                        else
                        {
                            send("106#");
                        }

                    }
                    else if (line.equals("103#") || line.equals("400#"))
                    {
                        send("104|1&1|1&2|4&1|4&2|8&1#");
                    }
                    else if (line.contains("107|")) {
                        String id, subject, site;
                        line = line.substring(line.indexOf("|") + 1);
                        while (line.contains("|")) {
                            String temp = line.substring(0, line.indexOf("|"));
                            id = temp.substring(0, temp.indexOf("&"));
                            temp = temp.substring((temp.indexOf("&") + 1));
                            subject = temp.substring(0, temp.indexOf("&"));
                            temp = temp.substring(temp.indexOf("&") + 1);
                            site = temp;
                            line = line.substring(line.indexOf("|") + 1);
                            priorityHandler.getCategorySites().add(new CategorySite(id, subject, site));
                        }
                    }
                    else if (line.contains("115|") || line.contains("127|"))
                    {
                        if(!line.equals("115|#") && !line.equals("127|#"))
                        {
                            line = line.substring(line.indexOf("|") + 1);
                            categoriesHandler.getCurrentlyInUse().clear();
                            while (line.contains("|")) {
                                String id, mainHeadLine, secondHeadLine, date, siteName = "", url, likes, imgURL;
                                Boolean liked;
                                String temp = line.substring(0, line.indexOf("|"));
                                id = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring((temp.indexOf("☺") + 1));
                                mainHeadLine = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring((temp.indexOf("☺") + 1));
                                secondHeadLine = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring(temp.indexOf("☺") + 1);
                                date = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring(temp.indexOf("☺") + 1);
                                url = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring(temp.indexOf("☺") + 1);
                                imgURL = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring(temp.indexOf("☺") + 1);
                                likes = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring(temp.indexOf("☺") + 1);
                                if ((Integer.parseInt(temp)) == 1) {
                                    liked = true;
                                } else {
                                    liked = false;
                                }
                                line = line.substring(line.indexOf("|") + 1);
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date dates = null;
                                try {
                                    dates = formatter.parse(date);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                for(int i = 0; i < priorityHandler.getCategorySites().size(); i++)
                                {
                                    if(priorityHandler.getCategorySites().get(i).getId().equals(id))
                                    {
                                        siteName = priorityHandler.getCategorySites().get(i).getSite();
                                    }
                                }
                                Article article = new Article(categoriesHandler.getCurrentlyInUseCategory(globalClass.getUser()), mainHeadLine, secondHeadLine, imgURL, dates, siteName, url, Integer.parseInt(likes), 0, liked, globalClass);
                                categoriesHandler.getCurrentlyInUse().add(article);
                                ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(categoriesHandler.getHotNewsPageAdapter() != null) {
                                            categoriesHandler.getHotNewsPageAdapter().notifyDataSetChanged();
                                        }
                                        categoriesHandler.getRecyclerAdapter().notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                        else
                        {
                            globalClass.getErrorHandler().reConnect();
                        }
                    }
                    else if (line.contains("119|"))
                    {
                        if(!line.equals("119|#"))
                        {
                            line = line.substring(line.indexOf("|") + 1);
                            while (line.contains("|")) {
                                String id, mainHeadLine, secondHeadLine, date, siteName = "", url, likes, imgURL;
                                Boolean liked;
                                String temp = line.substring(0, line.indexOf("|"));
                                id = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring((temp.indexOf("☺") + 1));
                                mainHeadLine = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring((temp.indexOf("☺") + 1));
                                secondHeadLine = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring(temp.indexOf("☺") + 1);
                                date = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring(temp.indexOf("☺") + 1);
                                url = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring(temp.indexOf("☺") + 1);
                                imgURL = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring(temp.indexOf("☺") + 1);
                                likes = temp.substring(0, temp.indexOf("☺"));
                                temp = temp.substring(temp.indexOf("☺") + 1);
                                if ((Integer.parseInt(temp)) == 1) {
                                    liked = true;
                                } else {
                                    liked = false;
                                }
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
                                for(int i = 0; i < priorityHandler.getCategorySites().size(); i++)
                                {
                                    if(priorityHandler.getCategorySites().get(i).getId().equals(id))
                                    {
                                        siteName = priorityHandler.getCategorySites().get(i).getSite();
                                    }
                                }
                                Article article = new Article(categoriesHandler.getCurrentlyInUseCategory(globalClass.getUser()), mainHeadLine, secondHeadLine, imgURL, dates, siteName, url, Integer.parseInt(likes), 0, liked, globalClass);
                                categoriesHandler.getCurrentlyInUse().addElement(article);
                                ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        categoriesHandler.getRecyclerAdapter().notifyDataSetChanged();
                                    }
                                });
                            }
                            categoriesHandler.setLoading(false);
                        }
                    }
                    line = "";
                }
            }while (line.compareTo("501#") != 0);
        } catch (IOException e) {

            Log.d("check", "IO Error/ Client terminated abruptly");

        } catch (NullPointerException e) {
            Log.d("check", "Client Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String str)
    {
        try {
            //cipherText = aesEncryption.encryptText(str, AESKey);
            String cipherText = aesEncryption.encrypt(str) + "##";
            Log.d("send", str + " | Encrypted: " + cipherText);
            out.println(cipherText);
            out.flush();
            if(out.checkError())
            {
                globalClass.getErrorHandler().setLastMsgToServer(str);
                globalClass.getErrorHandler().reConnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}