package magshimim.newzbay;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupWindow;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Communication implements Runnable {
    private ClientRead clientRead;
    private GlobalClass globalClass;
    private Context context;
    public Communication(GlobalClass globalClass, Context context) {
        this.globalClass = globalClass;
        this.context = context;
    }

    private Socket serverSocket;
    private String serverIP;
    private int dstport;
    private static int isConnect;

    @Override
    public void run() {
        serverIP = "79.176.24.82";
        isConnect = 0;
        dstport = 4444;
        try {
            serverSocket = new Socket();
            serverSocket.connect(new InetSocketAddress(serverIP, dstport), 5000);
            isConnect = 1;
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button guestLoginBtn = (Button) ((Activity)context).findViewById(R.id.btn_NB);
                    guestLoginBtn.setBackground(((Activity)context).getResources().getDrawable(R.drawable.button_rounded_corners));
                    guestLoginBtn.setTextColor(((Activity)context).getResources().getColor(R.color.white));
                    guestLoginBtn.setAlpha((float) 1);
                    guestLoginBtn.setEnabled(true);

                    LoginButton facebookLoginBtn = (LoginButton) ((Activity)context).findViewById(R.id.btn_Facebook);
                    facebookLoginBtn.setBackground(((Activity)context).getResources().getDrawable(R.drawable.button_rounded_corners_facebook));
                    facebookLoginBtn.setTextColor(((Activity)context).getResources().getColor(R.color.white));
                    facebookLoginBtn.setAlpha((float) 1);
                    facebookLoginBtn.setEnabled(true);

                    SignInButton googleLoginBtn = (SignInButton) ((Activity)context).findViewById(R.id.btn_Google);
                    googleLoginBtn.setEnabled(true);
                    if(globalClass.getUser() == null)
                    {
                        ((entrance)context).connectToSocialNets();
                    }
                    else
                    {
                        if(globalClass.getUser().getConnectedVia().equals("Google"))
                        {
                            clientSend("102&"+ Plus.AccountApi.getAccountName(((GoogleUser) globalClass.getUser()).getmGoogleApiClient())+"&"+((GoogleUser) globalClass.getUser()).getGoogleProfile().getName().getGivenName()+"#");
                        }
                        else if(globalClass.getUser().getConnectedVia().equals("Facebook"))
                        {
                            clientSend("102&" + ((FacebookUser) globalClass.getUser()).getFacebookUserEmail() + "&" + ((FacebookUser) globalClass.getUser()).getFacebookProfile().getFirstName() + "#");
                        }
                        else
                        {
                            clientSend("102&guest@guest.com&guest#");
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("check", "IO Exception");
            isConnect = -1;
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final PopupWindow noConnection = new PopupWindow(
                            globalClass.getErrorHandler().getPopupWindowView_noConnectionWithServer(),
                            800,
                            800,
                            true);
                    noConnection.setAnimationStyle(R.style.AnimationFade);
                    noConnection.showAtLocation(((Activity) context).findViewById(R.id.entrance_layout), Gravity.CENTER, 0, 0);
                    globalClass.getErrorHandler().setNoConnectionWithServer(noConnection);
                }
            });
        }
        if(isConnect == 1)
        {
            clientRead = new ClientRead(serverSocket, globalClass);
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

    public void clientSend(String str)
    {
        clientRead.send(str);
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
    private BufferedReader in;
    private GlobalClass globalClass;
    private CategoriesHandler categoriesHandler;
    private PublicKey publicKeyRSA;
    private SecretKey AESKey;
    private AESEncryption aesEncryption;

    public ClientRead(Socket serverSocket, GlobalClass globalClass) {
        this.serverSocket = serverSocket;
        this.globalClass = globalClass;
        categoriesHandler = globalClass.getCategoriesHandler();
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
        Log.d("send", "400#");
        out.println("400#");
        out.flush();
        try {
            String line = "";
            String buff = in.readLine();
            while(!buff.contains("#"))
            {
                line = line + buff + "\r\n" ;
                buff = in.readLine();
            }
            line = line + buff;
            line = line.substring(0, line.lastIndexOf("#") + 1);
            String line2 = "401|MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDH1AUEGsSgTgk95Qafv8zFF5g7BYx4hiuFnW/J\r\n" +
                    "eO1fuOOkulIoLX+aJ6fIIhODqVHLZEbZpHfdU+5KBtaivsq/3KSaoXGBcaG9Z4r6wf0lRIfnfVtB\r\n" +
                    "qG9KyTC9nVYLJ63he3CeyJXKp/R/vLZNbpmE5ZkdYRyc5+EY/w9VbuIH3QIDAQAB#";
            Log.d("check", "Response from server :  " + line);
            if(line.substring(0,3).equals("401"))
            {
                String key = line.substring(4, line.lastIndexOf("#"));
                byte[] array = key.getBytes();
                byte[] decode = Base64.decode(array, Base64.DEFAULT);
                X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(decode);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                publicKeyRSA = keyFactory.generatePublic(bobPubKeySpec);
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        aesEncryption = new AESEncryption();
        AESKey = null;
        do
        {
            AESKey = aesEncryption.getSecretEncryptionKey();
        }while(AESKey == null);

        String AESStrKey = Base64.encodeToString(AESKey.getEncoded(), Base64.DEFAULT);
        String privateAesSend = "402|";
        privateAesSend = privateAesSend + AESStrKey + "#";
//        privateAesSend = (encryptRsa(privateAesSend, publicKeyRSA)).toString();
        privateAesSend = (encryptRsa("Hello from the other side", publicKeyRSA)).toString();
        Log.d("send", privateAesSend);
        out.println(privateAesSend);
        out.flush();

        try {
            String line = in.readLine();
            line = aesEncryption.decryptText(line.getBytes(), AESKey);
            if(line.equals("103"))
            {
                send("100#"); //START WITH THE CONVERSATION
                read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] encryptRsa(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }
    public void read()
    {
        try {
            String line = "";
            do
            {
                line = in.readLine();
                line = aesEncryption.decryptText(line.getBytes(), AESKey);
                Log.d("check", "Response from server :  " + line);
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
                    categoriesHandler.setIdOfRSS(id);
                    categoriesHandler.setSubject(subject);
                    categoriesHandler.setSite(site);
                }
                else if(line.contains("115|")) {
                    line = line.substring(line.indexOf("|") + 1);
                    categoriesHandler.getCurrentlyInUse().clear();
                    while (line.contains("|"))
                    {
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
                        if((Integer.parseInt(temp)) == 1)
                        {
                            liked = true;
                        }
                        else
                        {
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
                        Article article = new Article(categoriesHandler.getCurrentlyInUseCategory(globalClass.getUser()), mainHeadLine, secondHeadLine, imgURL, dates, categoriesHandler.getSite().elementAt(categoriesHandler.getIdOfRSS().indexOf(id)), url, Integer.parseInt(likes), 0, liked, globalClass);
                        categoriesHandler.getCurrentlyInUse().add(article);
                    }
                    categoriesHandler.getNewsfeed().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((BaseAdapter) categoriesHandler.getListAdapter()).notifyDataSetChanged();
                        }
                    });
                } else if(line.contains("119|")) {
                    line = line.substring(line.indexOf("|") + 1);
                    while (line.contains("|"))
                    {
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
                        if((Integer.parseInt(temp)) == 1)
                        {
                            liked = true;
                        }
                        else
                        {
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
                        Article article = new Article(categoriesHandler.getCurrentlyInUseCategory(globalClass.getUser()), mainHeadLine, secondHeadLine, imgURL, dates, categoriesHandler.getSite().elementAt(categoriesHandler.getIdOfRSS().indexOf(id)), url, Integer.parseInt(likes), 0, liked, globalClass);
                        categoriesHandler.getCurrentlyInUse().addElement(article);
                    }
                    categoriesHandler.getNewsfeed().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((BaseAdapter) categoriesHandler.getListAdapter()).notifyDataSetChanged();
                        }
                    });
                    categoriesHandler.setLoading(false);
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
        byte[] cipherText = new byte[0];
        try {
            cipherText = aesEncryption.encryptText(str, AESKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("send", str);
        out.println(str);
        out.flush();
    }
}