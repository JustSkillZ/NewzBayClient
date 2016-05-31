package magshimim.newzbay;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Communication implements Runnable
{
    private GlobalClass globalClass;
    private Socket socket;
    private String serverIP;
    private int dstport;
    private boolean userConnected;
    private PrintWriter out;
    private BufferedReader in;
    private CategoriesHandler categoriesHandler;
    private CommentsHandler commentsHandler;
    private PriorityHandler priorityHandler;
    private AESEncryption aesEncryption;

    public Communication(GlobalClass globalClass)
    {
        this.globalClass = globalClass;
        categoriesHandler = globalClass.getCategoriesHandler();
        priorityHandler = globalClass.getPriorityHandler();
        commentsHandler = globalClass.getCommentsHandler();
        aesEncryption = new AESEncryption();
    }

    @Override
    public void run()
    {
        serverIP = globalClass.getErrorHandler().getServerIP(); //Nir PC's IP
        dstport = 4444; //Communication port
        userConnected = false;
        socket = new Socket();
        try
        {
            socket.connect(new InetSocketAddress(serverIP, dstport), 5000);
            ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable() //Connected to NB server
            {
                @Override
                public void run() //Set GUI buttons clickable in order to choose social net or guest
                {
                    Button connectToServer = (Button) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.btn_connectToServer);
                    if (connectToServer != null)
                    {
                        connectToServer.setVisibility(View.GONE);
                    }

                    EditText serverIPText = (EditText) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.editText_serverIP);
                    if (serverIPText != null)
                    {
                        serverIPText.setVisibility(View.GONE);
                    }


                    View editTextShadow = (View) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.view_shadow);
                    if (editTextShadow != null)
                    {
                        editTextShadow.setVisibility(View.GONE);
                    }

                    Button guestLoginBtn = (Button) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.btn_NB);
                    if (guestLoginBtn != null)
                    {
                        guestLoginBtn.setBackground(globalClass.getCurrentActivity().getResources().getDrawable(R.drawable.button_rounded_corners));
                        guestLoginBtn.setTextColor(globalClass.getCurrentActivity().getResources().getColor(R.color.white));
                        guestLoginBtn.setAlpha((float) 1);
                        guestLoginBtn.setEnabled(true);
                    }

                    LoginButton facebookLoginBtn = (LoginButton) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.btn_Facebook);
                    if (facebookLoginBtn != null)
                    {
                        facebookLoginBtn.setBackground(globalClass.getCurrentActivity().getResources().getDrawable(R.drawable.button_rounded_corners_facebook));
                        facebookLoginBtn.setTextColor(globalClass.getCurrentActivity().getResources().getColor(R.color.white));
                        facebookLoginBtn.setAlpha((float) 1);
                        facebookLoginBtn.setEnabled(true);
                    }

                    SignInButton googleLoginBtn = (SignInButton) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.btn_Google);
                    if (googleLoginBtn != null)
                    {
                        googleLoginBtn.setEnabled(true);
                    }

                    if (globalClass.getUser() != null) //If user reconnected to the server
                    {
                        userConnected = true;
                    }
                    else //If first entrance, log in into social nets or guest
                    {
                        ((ActivityEntrance) globalClass.getCurrentActivity()).connectToSocialNets();
                    }
                }
            });
            try
            {
                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }
            catch (IOException e)
            {
                Log.d("check", "IO Error/ Client terminated abruptly");
            }
            catch (NullPointerException e)
            {
                Log.d("check", "Client Closed");
            }
            out.println("404◘" + aesEncryption.getAesKey() + "##"); //Get AES Encryption key, and send it to NB server
            out.flush();
            try
            {
                if (in.readLine().equals("405#")) //When server returns 405, connect officially to the server
                {
                    send("100#"); //First message with encryption. Its like an ID that the user uses NB legal application
                    read(); //Loop of reading messages
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (IOException e) //No connection to server
        {
            globalClass.getErrorHandler().reConnect(); //Reconnect
            ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable() //Connected to NB server
            {
                @Override
                public void run() //Set GUI buttons clickable in order to choose social net or guest
                {
                    Button connectToServer = (Button) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.btn_connectToServer);
                    if (connectToServer != null)
                    {
                        connectToServer.setVisibility(View.VISIBLE);
                    }

                    EditText serverIPText = (EditText) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.editText_serverIP);
                    if (serverIPText != null)
                    {
                        serverIPText.setVisibility(View.VISIBLE);
                        serverIPText.setText("");
                    }

                    View editTextShadow = (View) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.view_shadow);
                    if (editTextShadow != null)
                    {
                        editTextShadow.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public void read()
    {
        try
        {
            String line = "";
            do
            {
                line = line + in.readLine();
                if (line.contains("##")) //Read until get ##
                {
                    line = line.substring(0, line.length() - 2);
                    line = aesEncryption.decrypt(line);
                }
                if (line.contains("#"))
                {
                    Log.d("check", "Response from server :  " + line);
                    if (line.equals("101#")) //Client Accepted by the server
                    {
                        if (userConnected)
                        {
                            send(globalClass.getErrorHandler().getConnectingClientMsg());
                            send(globalClass.getErrorHandler().getLastMsgToServer());
                            globalClass.getErrorHandler().setLastMsgToServer("");
                        }
                        else
                        {
                            send("106#"); //Client asks for sites of each subject

                            if (!globalClass.getErrorHandler().getConnectingClientMsg().equals("")) //If was problem with sending 102 to server (connect a user to social net or guest)
                            {
                                send(globalClass.getErrorHandler().getConnectingClientMsg());
                            }
                        }
                    }
                    else if (line.equals("103#")) //User registered successfully
                    {
                        ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Intent priority = new Intent(globalClass.getCurrentActivity(), ActivityPriority.class);
                                globalClass.getCurrentActivity().startActivity(priority);
                            }
                        });
                    }
                    else if (line.contains("107◘")) //Client got sites of each subject, and id of each site
                    {
                        String id, subject, site;
                        line = line.substring(line.indexOf("◘") + 1);
                        while (line.contains("◘"))
                        {
                            String temp = line.substring(0, line.indexOf("◘"));
                            id = temp.substring(0, temp.indexOf("○"));
                            temp = temp.substring((temp.indexOf("○") + 1));
                            subject = temp.substring(0, temp.indexOf("○"));
                            temp = temp.substring(temp.indexOf("○") + 1);
                            site = temp;
                            line = line.substring(line.indexOf("◘") + 1);
                            priorityHandler.getRssSites().add(new RSS(id, subject, site));
                        }
                    }
                    else if (line.contains("115◘") || line.contains("127◘")) //Client got articles from a category. 127 is hot news.
                    {
                        boolean hotNews = line.contains("127◘");
                        categoriesHandler.getCurrentlyInUse().clear();
                        categoriesHandler.getHotNewsArticles().clear();
                        if (!line.equals("115◘#") && !line.equals("127◘#")) //If empty don't parse line
                        {
                            line = line.substring(line.indexOf("◘") + 1);
                            categoriesHandler.getCurrentlyInUse().clear();
                            while (line.contains("◘"))
                            {
                                String id, mainHeadLine, secondHeadLine, date, siteName = "", url, likes, comments, imgURL;
                                Boolean liked;
                                String temp = line.substring(0, line.indexOf("◘"));
                                id = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring((temp.indexOf("○") + 1));
                                mainHeadLine = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring((temp.indexOf("○") + 1));
                                secondHeadLine = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                date = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                url = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                imgURL = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                likes = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                comments = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                if ((Integer.parseInt(temp)) == 1)
                                {
                                    liked = true;
                                }
                                else
                                {
                                    liked = false;
                                }
                                line = line.substring(line.indexOf("◘") + 1);
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date dates = null;
                                try
                                {
                                    dates = formatter.parse(date);

                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                                for (int i = 0; i < priorityHandler.getRssSites().size(); i++)
                                {
                                    if (priorityHandler.getRssSites().get(i).getId().equals(id))
                                    {
                                        siteName = priorityHandler.getRssSites().get(i).getSite();
                                    }
                                }
                                Article article = new Article(categoriesHandler.getCurrentlyInUseCategory(globalClass.getUser()), mainHeadLine, secondHeadLine, imgURL, dates, siteName, url, Integer.parseInt(likes), Integer.parseInt(comments), liked, globalClass);
                                if(hotNews)
                                {
                                    categoriesHandler.getHotNewsArticles().add(article);
                                }
                                else
                                {
                                    categoriesHandler.getCurrentlyInUse().add(article);
                                }
                                if(hotNews && categoriesHandler.getHotNewsPageAdapter() != null)
                                {
                                    ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            categoriesHandler.getHotNewsPageAdapter().notifyDataSetChanged();
                                        }
                                    });
                                }
                                else
                                {
                                    ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            categoriesHandler.getArticlesRecyclerAdapter().notifyDataSetChanged();
                                            ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loadingArticles);
                                            if(pb != null)
                                            {
                                                pb.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        else
                        {
                            ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    categoriesHandler.getCurrentlyInUse().clear();
                                    categoriesHandler.getArticlesRecyclerAdapter().notifyDataSetChanged();
                                    Toast.makeText(globalClass.getCurrentActivity(), "לא ביצעת העדפה בנושא זה", Toast.LENGTH_LONG).show();
                                    ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loadingArticles);
                                    if(pb != null)
                                    {
                                        pb.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                        }
                    }
                    else if (line.contains("119◘")) //Client got more articles from current subject
                    {
                        if (!line.equals("119◘#")) //If empty don't parse line
                        {
                            line = line.substring(line.indexOf("◘") + 1);
                            while (line.contains("◘"))
                            {
                                String id, mainHeadLine, secondHeadLine, date, siteName = "", url, likes, comments, imgURL;
                                Boolean liked;
                                String temp = line.substring(0, line.indexOf("◘"));
                                id = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring((temp.indexOf("○") + 1));
                                mainHeadLine = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring((temp.indexOf("○") + 1));
                                secondHeadLine = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                date = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                url = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                imgURL = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                likes = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                comments = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                liked = (Integer.parseInt(temp)) == 1;
                                line = line.substring(line.indexOf("◘") + 1);
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date dates = null;
                                try
                                {

                                    dates = formatter.parse(date);
                                    System.out.println(dates);
                                    System.out.println(formatter.format(dates));

                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                                for (int i = 0; i < priorityHandler.getRssSites().size(); i++)
                                {
                                    if (priorityHandler.getRssSites().get(i).getId().equals(id))
                                    {
                                        siteName = priorityHandler.getRssSites().get(i).getSite();
                                    }
                                }
                                Article article = new Article(categoriesHandler.getCurrentlyInUseCategory(globalClass.getUser()), mainHeadLine, secondHeadLine, imgURL, dates, siteName, url, Integer.parseInt(likes), Integer.parseInt(comments), liked, globalClass);
                                categoriesHandler.getCurrentlyInUse().addElement(article);
                                ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        categoriesHandler.getArticlesRecyclerAdapter().notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                        categoriesHandler.setLoading(false);
                        ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loadingArticles);
                                if(pb != null)
                                {
                                    pb.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                    else if (line.contains("121◘")) //Client got comments of current article
                    {
                        if (!line.equals("121◘◘#")) //If empty don't parse line
                        {
                            commentsHandler.getCommentsOfCurrentArticle().clear();
                            line = line.substring(line.indexOf("◘") + 1);
                            while (line.contains("◘"))
                            {
                                String username, picURL, commentText, id = "-1";
                                Boolean clientComment = false;
                                String temp = line.substring(0, line.indexOf("◘"));
                                username = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring((temp.indexOf("○") + 1));
                                picURL = temp.substring(0, temp.indexOf("○"));
                                temp = temp.substring(temp.indexOf("○") + 1);
                                if(temp.contains("►"))
                                {
                                    id = temp.substring(temp.indexOf("►") + 1);
                                    temp = temp.substring(0, temp.indexOf("►"));
                                    clientComment = true;
                                }
                                commentText = temp;
                                line = line.substring(line.indexOf("◘") + 1);
                                Comment comment = new Comment(username, picURL, commentText, clientComment, id);
                                commentsHandler.getCommentsOfCurrentArticle().addElement(comment);
                                ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        commentsHandler.getCommentsRecyclerAdapter().notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                    else if (line.contains("129◘")) //Client gets his priority of current subject
                    {
                        String temp;
                        line = line.substring(line.indexOf("◘") + 1);
                        while (line.contains("◘"))
                        {
                            temp = line.substring(0, line.indexOf("◘"));
                            line = line.substring(line.indexOf("◘") + 1);
                            priorityHandler.getClientPriority().add(temp);
                            ((Activity) globalClass.getCurrentActivity()).runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    priorityHandler.getRecyclerAdapter().notifyDataSetChanged();
                                }
                            });
                        }
                        priorityHandler.createRemovedSitesList();
                    }
                    else if(line.contains("123◘")) //Client gets the id of comment he wrote
                    {
                        line = line.substring(0,line.length() - 1);
                        boolean done = false;
                        for(int i = 0; i < commentsHandler.getCommentsOfCurrentArticle().size(); i++)
                        {
                            if(!done)
                            {
                                if(commentsHandler.getCommentsOfCurrentArticle().get(i).getClientComment() && commentsHandler.getCommentsOfCurrentArticle().get(i).getId().equals("-1"))
                                {
                                    commentsHandler.getCommentsOfCurrentArticle().get(i).setId(line.substring(line.indexOf("◘") + 1));
                                    if(commentsHandler.getCommentActivity() != null)
                                    {
                                        ((Activity) commentsHandler.getCommentActivity()).runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                commentsHandler.getCommentsRecyclerAdapter().notifyDataSetChanged();
                                            }
                                        });
                                    }
                                    done = true;
                                }
                            }
                        }
                    }
                    line = "";
                }
            }
            while (line.compareTo("501#") != 0); //501 is disconnected from the server
        }
        catch (IOException e)
        {
            Log.d("check", "IO Error/ Client terminated abruptly");
        }
        catch (NullPointerException e)
        {
            Log.d("check", "Client Closed");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void send(String str)
    {
        try
        {
            String cipherText = aesEncryption.encrypt(str) + "##";
            Log.d("send", str);
            out.println(cipherText);
            out.flush();
            if (out.checkError())
            {
                globalClass.getErrorHandler().setLastMsgToServer(str);
                globalClass.getErrorHandler().reConnect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}