package magshimim.newzbay;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.PopupWindow;

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
        serverIP = "79.181.200.73";
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
                    if(guestLoginBtn != null)
                    {
                        guestLoginBtn.setBackground(globalClass.getCurrentActivity().getResources().getDrawable(R.drawable.button_rounded_corners));
                        guestLoginBtn.setTextColor(globalClass.getCurrentActivity().getResources().getColor(R.color.white));
                        guestLoginBtn.setAlpha((float) 1);
                        guestLoginBtn.setEnabled(true);
                    }

                    LoginButton facebookLoginBtn = (LoginButton) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.btn_Facebook);
                    if(facebookLoginBtn != null)
                    {
                        facebookLoginBtn.setBackground(globalClass.getCurrentActivity().getResources().getDrawable(R.drawable.button_rounded_corners_facebook));
                        facebookLoginBtn.setTextColor(globalClass.getCurrentActivity().getResources().getColor(R.color.white));
                        facebookLoginBtn.setAlpha((float) 1);
                        facebookLoginBtn.setEnabled(true);
                    }

                    SignInButton googleLoginBtn = (SignInButton) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.btn_Google);
                    if(googleLoginBtn != null)
                    {
                        googleLoginBtn.setEnabled(true);
                    }

                    if (globalClass.getUser() == null) {
                        ((entrance) globalClass.getCurrentActivity()).connectToSocialNets();
                    }
                    else
                    {
                        userConnected = true;
                    }
                }
            });
            clientRead = new ClientRead(serverSocket, globalClass, userConnected);
            Thread t = new Thread(clientRead);
            t.setName("Reading From The Server");
            t.start();
        }
        catch (IOException e) { //No connection to server
            isConnect = -1;
            ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final PopupWindow noConnection = new PopupWindow(
                            globalClass.getErrorHandler().getPopupWindowView_noConnectionWithServer(),
                            800,
                            800,
                            true);
                    noConnection.setAnimationStyle(R.style.AnimationFade);
                    try
                    {
                        noConnection.showAtLocation(((Activity) globalClass.getCurrentActivity()).findViewById(globalClass.getCurrentLayout()), Gravity.CENTER, 0, 0);
                    }
                    catch (IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                    globalClass.getErrorHandler().setNoConnectionWithServer(noConnection);
                }
            });
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
            String tempLine;
            do {
                tempLine = in.readLine();
                if(tempLine == null)
                {
                    break;
                }
                line = line + tempLine;
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
                        }
                        else
                        {
                            send("106#");
                        }
                        Log.d("Server", "101#");

                    } else if (line.equals("103#") || line.equals("400#")) {
                        Log.d("Server", line);
                    //    send("104|1&1|1&2|4&1|4&2|8&1#");
                    } else if (line.contains("107|")) {
                        Vector<String> id = new Vector<String>();
                        Vector<String> subject = new Vector<String>();
                        Vector<String> site = new Vector<String>();
                        line = line.substring(line.indexOf("|") + 1);
                        while (line.contains("|")) {
                            String temp = line.substring(0, line.indexOf("|"));
                            id.add(temp.substring(0, temp.indexOf("&")));
                            temp = temp.substring((temp.indexOf("&") + 1));
                            subject.add(temp.substring(0, temp.indexOf("&")));
                            temp = temp.substring(temp.indexOf("&") + 1);
                            site.add(temp);
                            line = line.substring(line.indexOf("|") + 1);
                        }
                        priorityHandler.setIdOfRSS(id);
                        priorityHandler.setSubject(subject);
                        priorityHandler.setSite(site);
                    } else if (line.contains("115|")) {
                        line = line.substring(line.indexOf("|") + 1);
                        for (int i = 0; i < categoriesHandler.getCurrentlyInUse().size(); i++) {
                            Article current = categoriesHandler.getCurrentlyInUse().get(i);
                            if (current.getPicture() != null && current.isPictureIsDawnloaded()) {
                                if(!current.getPicture().isRecycled())
                                {
                                    current.getPicture().recycle();
                                }
                            }
                        }
                        categoriesHandler.getCurrentlyInUse().clear();
                        while (line.contains("|")) {
                            String id, mainHeadLine, secondHeadLine, date, url, likes, imgURL;
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
                            Article article = new Article(categoriesHandler.getCurrentlyInUseCategory(globalClass.getUser()), mainHeadLine, secondHeadLine, imgURL, dates, priorityHandler.getSite().elementAt(priorityHandler.getIdOfRSS().indexOf(id)), url, Integer.parseInt(likes), 0, liked, globalClass);
                            categoriesHandler.getCurrentlyInUse().add(article);
                            categoriesHandler.getNewsfeed().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    categoriesHandler.getRecyclerAdapter().notifyDataSetChanged();
                                }
                            });
                        }
                    } else if (line.contains("119|")) {
                        line = line.substring(line.indexOf("|") + 1);
                        while (line.contains("|")) {
                            String id, mainHeadLine, secondHeadLine, date, url, likes, imgURL;
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
                            Article article = new Article(categoriesHandler.getCurrentlyInUseCategory(globalClass.getUser()), mainHeadLine, secondHeadLine, imgURL, dates, priorityHandler.getSite().elementAt(priorityHandler.getIdOfRSS().indexOf(id)), url, Integer.parseInt(likes), 0, liked, globalClass);
                            categoriesHandler.getCurrentlyInUse().addElement(article);
                            categoriesHandler.getNewsfeed().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    categoriesHandler.getRecyclerAdapter().notifyDataSetChanged();
                                }
                            });
                        }
                        categoriesHandler.setLoading(false);
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
           // cipherText = aesEncryption.encryptText(str, AESKey);
            String cipherText = aesEncryption.encrypt(str) + "##";
            Log.d("send", str + " | Encrypted: " + cipherText);
            out.println(cipherText);
            out.flush();
            if(out.checkError())
            {
                globalClass.getErrorHandler().setLastMsgToServer(str);
                ((Activity)globalClass.getCurrentActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final PopupWindow noConnection = new PopupWindow(
                                globalClass.getErrorHandler().getPopupWindowView_noConnectionWithServer(),
                                800,
                                800,
                                true);
                        noConnection.setAnimationStyle(R.style.AnimationFade);
                        noConnection.showAtLocation(((Activity) globalClass.getCurrentActivity()).findViewById(R.id.newsfeed_layout), Gravity.CENTER, 0, 0);
                        globalClass.getErrorHandler().setNoConnectionWithServer(noConnection);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}