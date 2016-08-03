package magshimim.newzbay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Communication
{
    private final String BASE_URL = "http://ec2-52-28-112-77.eu-central-1.compute.amazonaws.com:4646";
    private Retrofit retrofit;
    private NewzBayAPI newzBayAPI;
    private String token;
    private boolean firstRegistration;
    private Runnable methodCall;

    public Communication(GlobalClass globalClass)
    {
        firstRegistration = true;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        newzBayAPI = retrofit.create(NewzBayAPI.class);

        methodCall = null;
    }

    public void authenticate(final String email, final String picURL, final String name, final GlobalClass globalClass) //0
    {
        ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
        if (pb != null)
        {
            pb.setVisibility(View.VISIBLE);
        }
        if(globalClass.getCurrentActivity() instanceof ActivityEntrance)
        {
            ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.tv_connectingToServer).setVisibility(View.VISIBLE);
            ((ActivityEntrance) globalClass.getCurrentActivity()).disableButtons();
        }
        Call<AuthenticationRecieve> call = newzBayAPI.authenticateUser(new AuthenticationSend(email, picURL, name));
        call.enqueue(new Callback<AuthenticationRecieve>() {
            @Override
            public void onResponse(Call<AuthenticationRecieve> call, Response<AuthenticationRecieve> response)
            {
                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                if (pb != null)
                {
                    pb.setVisibility(View.INVISIBLE);
                }
                if (globalClass.getCurrentActivity() instanceof ActivityEntrance)
                {
                    ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.tv_connectingToServer).setVisibility(View.INVISIBLE);
                }
                if(response.body().getStatus() == 200)
                {
                    token = response.body().getToken();
                    getAllRSS(globalClass);
                    if(response.body().getMessage().equals("Welcome back"))
                    {
                        firstRegistration = false;
                    }
                    if(globalClass.getCurrentActivity() instanceof ActivityEntrance)
                    {
                        ((ActivityEntrance) globalClass.getCurrentActivity()).moveToNewsFeed();
                        if(firstRegistration)
                        {
                            Intent priority = new Intent(globalClass.getCurrentActivity(), ActivityPriority.class);
                            globalClass.getCurrentActivity().startActivity(priority);
                        }
                    }
                    if(methodCall != null)
                    {
                        methodCall.run();
                    }
                    methodCall = null;
                }
            }

            @Override
            public void onFailure(Call<AuthenticationRecieve> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setMessage("אירעה שגיאה")
                        .setCancelable(false)
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                authenticate(email, picURL, name, globalClass);
                            }
                        })
                        .show();
            }
        });
    }

    public void setPriority(final ArrayList<Priority> priorityList, final GlobalClass globalClass) //1
    {
        Call<InfoFromServer> call = newzBayAPI.setPriority(token, priorityList);
        call.enqueue(new Callback<InfoFromServer>() {
            @Override
            public void onResponse(Call<InfoFromServer> call, Response<InfoFromServer> response)
            {
                if(response.body().getStatus() == 200)
                {
                    Toast.makeText(globalClass.getCurrentActivity(), "העדפות נוספו בהצלחה!", Toast.LENGTH_LONG).show();
                }
                else if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                setPriority(priorityList, globalClass);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }
            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setMessage("אירעה שגיאה")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                setPriority(priorityList, globalClass);
                            }
                        })
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    public void getAllRSS(final GlobalClass globalClass) //2
    {
        Call<SubWeb> call = newzBayAPI.getAllRSS(token);
        call.enqueue(new Callback<SubWeb>()
        {
            @Override
            public void onResponse(Call<SubWeb> call, Response<SubWeb> response)
            {
                if(response.body().getStatus() == 200)
                {
                    for(int i = 0; i < response.body().getSubWeb().size(); i++)
                    {
                        globalClass.getPriorityHandler().getRssSites().add(new RSS(
                                response.body().getSubWeb().get(i).getID(),
                                response.body().getSubWeb().get(i).getSubject(),
                                response.body().getSubWeb().get(i).getWebsite()));
                    }
                }
                else if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                getAllRSS(globalClass);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }
            }

            @Override
            public void onFailure(Call<SubWeb> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setMessage("אירעה שגיאה")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getAllRSS(globalClass);
                            }
                        })
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    public void deletePrioritySubject(final String subject, final GlobalClass globalClass) //3
    {
        Call<InfoFromServer> call = newzBayAPI.deletePrioritySubject(token, subject);
        call.enqueue(new Callback<InfoFromServer>() {
            @Override
            public void onResponse(Call<InfoFromServer> call, Response<InfoFromServer> response)
            {
                if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                deletePrioritySubject(subject, globalClass);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }
            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setMessage("אירעה שגיאה")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deletePrioritySubject(subject, globalClass);
                            }
                        })
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    public void getUserPriority(final String subject, final GlobalClass globalClass) //4
    {
        ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
        if (pb != null)
        {
            pb.setVisibility(View.VISIBLE);
        }
        Call<UserPriority> call = newzBayAPI.getUserPriority(token, subject);
        call.enqueue(new Callback<UserPriority>() {
            @Override
            public void onResponse(Call<UserPriority> call, Response<UserPriority> response)
            {
                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                if (pb != null)
                {
                    pb.setVisibility(View.INVISIBLE);
                }
                if(response.body().getStatus() == 200)
                {
                    for(int i = 0; i < response.body().getPriority().size(); i++)
                    {
                        globalClass.getPriorityHandler().getClientPriority().add(response.body().getPriority().get(i).getWebsite());
                        globalClass.getPriorityHandler().getRecyclerAdapter().notifyDataSetChanged();
                    }
                    globalClass.getPriorityHandler().createRemovedSitesList();
                }
                else if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                getUserPriority(subject, globalClass);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }

            }

            @Override
            public void onFailure(Call<UserPriority> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setMessage("אירעה שגיאה")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getUserPriority(subject, globalClass);
                            }
                        })
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                                if (pb != null)
                                {
                                    pb.setVisibility(View.INVISIBLE);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    public void getArticles(final String subject, final GlobalClass globalClass) //5
    {
        ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
        if (pb != null)
        {
            pb.setVisibility(View.VISIBLE);
        }
        Call<JsonRecievedArticles> call = newzBayAPI.getArticles(token, subject);
        call.enqueue(new Callback<JsonRecievedArticles>() {
            @Override
            public void onResponse(Call<JsonRecievedArticles> call, Response<JsonRecievedArticles> response)
            {
                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                if (pb != null)
                {
                    pb.setVisibility(View.INVISIBLE);
                }
                globalClass.getCategoriesHandler().getCurrentlyInUse().clear();
                if(response.body().getStatus() == 200)
                {
                    globalClass.getCategoriesHandler().getCurrentlyInUse().clear();
                    for(int i = 0; i < response.body().getArticles().size(); i++)
                    {
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
                        Date date = null;
                        try
                        {
                            date = formatter.parse(response.body().getArticles().get(i).getDate());

                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                        if((globalClass.getCategoriesHandler().getCurrentlyInUse().size()) % 8 == 0 && globalClass.getCategoriesHandler().getCurrentlyInUse().size() != 0)
                        {
                            globalClass.getCategoriesHandler().getCurrentlyInUse().add( new Article());
                        }
                        globalClass.getCategoriesHandler().getCurrentlyInUse().add( new Article(
                                globalClass.getPriorityHandler().getSubjectByID(response.body().getArticles().get(i).getID()),
                                globalClass.getPriorityHandler().getWebsiteByID(response.body().getArticles().get(i).getID()),
                                response.body().getArticles().get(i).getMainHeadline(),
                                response.body().getArticles().get(i).getSecondHeadline(),
                                response.body().getArticles().get(i).getPicURL(),
                                date,
                                response.body().getArticles().get(i).getUrl(),
                                response.body().getArticles().get(i).getNumberOfLikes(),
                                response.body().getArticles().get(i).getNumberOfComments(),
                                response.body().getArticles().get(i).isLiked(),
                                globalClass
                        ));
                    }
                    globalClass.getCategoriesHandler().getArticlesRecyclerAdapter().notifyDataSetChanged();
                    globalClass.getCategoriesHandler().setLoading(false);
                    if(response.body().getArticles().size() == 0)
                    {
                        Toast.makeText(globalClass.getCurrentActivity(), "לא ביצעת העדפה בנושא זה", Toast.LENGTH_LONG).show();
                    }
                }
                else if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                getArticles(subject, globalClass);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonRecievedArticles> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setMessage("אירעה שגיאה")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getArticles(subject, globalClass);
                            }
                        })
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                globalClass.getCategoriesHandler().setLoading(false);
                                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                                if (pb != null)
                                {
                                    pb.setVisibility(View.INVISIBLE);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    public void like(final String articleURL, final GlobalClass globalClass) //6
    {
        Call<InfoFromServer> call = newzBayAPI.like(token, articleURL);
        call.enqueue(new Callback<InfoFromServer>()
        {
            @Override
            public void onResponse(Call<InfoFromServer> call, Response<InfoFromServer> response)
            {
                if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                like(articleURL, globalClass);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }
            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {

            }
        });
    }

    public void addComment(final String articleURL, final String comment, final GlobalClass globalClass, final CommentsHandler commentsHandler, final User user, final String commentText) //7
    {
        Call<InfoFromServer> call = newzBayAPI.addComment(token, articleURL, comment);
        call.enqueue(new Callback<InfoFromServer>()
        {
            @Override
            public void onResponse(Call<InfoFromServer> call, Response<InfoFromServer> response)
            {
                if(response.body().getStatus() == 200)
                {
                    commentsHandler.getCommentsOfCurrentArticle().addElement(new Comment(user.getFullName(), user.getPicURL(), commentText, true, response.body().getMessage()));
                    commentsHandler.getCommentsRecyclerAdapter().notifyDataSetChanged();
                    commentsHandler.getArticle().incNumberOfComments();
                }
                else if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                addComment(articleURL, comment, globalClass, commentsHandler, user, commentText);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }
            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setTitle("הפעולה נכשלה")
                        .setMessage("לא היה ניתן להוסיף את התגובה, אנא נסה/י שנית מאוחר יותר")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                addComment(articleURL, comment, globalClass, commentsHandler, user, commentText);
                            }
                        })
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    public void deleteComment(final String articleURL, final String id, final GlobalClass globalClass, final Comment comment) //8
    {
        Call<InfoFromServer> call = newzBayAPI.deleteComment(token, articleURL, id);
        call.enqueue(new Callback<InfoFromServer>()
        {
            @Override
            public void onResponse(Call<InfoFromServer> call, Response<InfoFromServer> response)
            {
                if(response.body().getStatus() == 200)
                {
                    globalClass.getCommentsHandler().getCommentsOfCurrentArticle().remove(comment);
                    globalClass.getCommentsHandler().getArticle().decNumberOfComments();
                    globalClass.getCommentsHandler().getCommentsRecyclerAdapter().notifyDataSetChanged();
                    ((TextView) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.tv_comments)).setText(globalClass.getCommentsHandler().getArticle().getNumberOfComments() + "");
                }
                else if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                deleteComment(articleURL, id, globalClass, comment);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }
            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setTitle("הפעולה נכשלה")
                        .setMessage("לא היה ניתן למחוק את התגובה, אנא נסה/י שנית מאוחר יותר")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteComment(articleURL, id, globalClass, comment);
                            }
                        })
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    public void getComments(final String articleURL, final GlobalClass globalClass) //9
    {
        ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
        if (pb != null)
        {
            pb.setVisibility(View.VISIBLE);
        }
        Call<JsonRecievedComments> call = newzBayAPI.getComments(token, articleURL);
        call.enqueue(new Callback<JsonRecievedComments>()
        {
            @Override
            public void onResponse(Call<JsonRecievedComments> call, Response<JsonRecievedComments> response)
            {
                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                if (pb != null)
                {
                    pb.setVisibility(View.INVISIBLE);
                }
                if(response.body().getStatus() == 200)
                {
                    globalClass.getCommentsHandler().getCommentsOfCurrentArticle().clear();
                    for(int i = 0; i < response.body().getComments().size(); i++)
                    {
                        globalClass.getCommentsHandler().getCommentsOfCurrentArticle().addElement(new Comment(
                                response.body().getComments().get(i).getName()
                                ,
                                response.body().getComments().get(i).getPicURL(),
                                response.body().getComments().get(i).getComment(),
                                !response.body().getComments().get(i).getId().equals(""), //Check if the author is the user
                                response.body().getComments().get(i).getId())); //if ID == "" the author is not the user
                    }
                    globalClass.getCommentsHandler().getCommentsRecyclerAdapter().notifyDataSetChanged();
                }
                else if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                getComments(articleURL, globalClass);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonRecievedComments> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setMessage("אירעה שגיאה")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getComments(articleURL, globalClass);
                            }
                        })
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                                if (pb != null)
                                {
                                    pb.setVisibility(View.INVISIBLE);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    public void getHotNews(final GlobalClass globalClass) //10
    {
        ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
        if (pb != null)
        {
            pb.setVisibility(View.VISIBLE);
        }
        Call<JsonRecievedArticles> call = newzBayAPI.getHotNews(token);
        call.enqueue(new Callback<JsonRecievedArticles>() {
            @Override
            public void onResponse(Call<JsonRecievedArticles> call, Response<JsonRecievedArticles> response)
            {
                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                if (pb != null)
                {
                    pb.setVisibility(View.INVISIBLE);
                }
                if(response.body().getStatus() == 200 && globalClass.getCategoriesHandler().getHotNewsArticles().size() == 0)
                {
                        for(int i = 0; i < response.body().getArticles().size(); i++)
                        {
                            if(response.body().getArticles().get(i).getDate() != null)
                            {
                                SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
                                Date date = null;
                                try
                                {
                                    date = formatter.parse(response.body().getArticles().get(i).getDate());

                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                                globalClass.getCategoriesHandler().getHotNewsArticles().add( new Article(
                                        globalClass.getPriorityHandler().getSubjectByID(response.body().getArticles().get(i).getID()),
                                        globalClass.getPriorityHandler().getWebsiteByID(response.body().getArticles().get(i).getID()),
                                        response.body().getArticles().get(i).getMainHeadline(),
                                        response.body().getArticles().get(i).getSecondHeadline(),
                                        response.body().getArticles().get(i).getPicURL(),
                                        date,
                                        response.body().getArticles().get(i).getUrl(),
                                        response.body().getArticles().get(i).getNumberOfLikes(),
                                        response.body().getArticles().get(i).getNumberOfComments(),
                                        response.body().getArticles().get(i).isLiked(),
                                        globalClass
                                ));
                            }
                        }
                    globalClass.getCategoriesHandler().getHotNewsPageAdapter().notifyDataSetChanged();
                }
                else if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                getHotNews(globalClass);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonRecievedArticles> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setMessage("אירעה שגיאה")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getHotNews(globalClass);
                            }
                        })
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                                if (pb != null)
                                {
                                    pb.setVisibility(View.INVISIBLE);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    public void getMoreArticles(final String subject, final String lastArticleURL, final GlobalClass globalClass) //11
    {
        ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
        if (pb != null)
        {
            pb.setVisibility(View.VISIBLE);
        }
        Call<JsonRecievedArticles> call = newzBayAPI.getMoreArticles(token, subject, lastArticleURL);
        call.enqueue(new Callback<JsonRecievedArticles>() {
            @Override
            public void onResponse(Call<JsonRecievedArticles> call, Response<JsonRecievedArticles> response)
            {
                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                if (pb != null)
                {
                    pb.setVisibility(View.INVISIBLE);
                }
                if(response.body().getStatus() == 200)
                {
                    for(int i = 0; i < response.body().getArticles().size(); i++)
                    {
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
                        Date date = null;
                        try
                        {
                            date = formatter.parse(response.body().getArticles().get(i).getDate());

                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                        if((globalClass.getCategoriesHandler().getCurrentlyInUse().size()) % 8 == 0 && globalClass.getCategoriesHandler().getCurrentlyInUse().size() != 0)
                        {
                            globalClass.getCategoriesHandler().getCurrentlyInUse().add( new Article());
                        }
                        globalClass.getCategoriesHandler().getCurrentlyInUse().add( new Article(
                                globalClass.getPriorityHandler().getSubjectByID(response.body().getArticles().get(i).getID()),
                                globalClass.getPriorityHandler().getWebsiteByID(response.body().getArticles().get(i).getID()),
                                response.body().getArticles().get(i).getMainHeadline(),
                                response.body().getArticles().get(i).getSecondHeadline(),
                                response.body().getArticles().get(i).getPicURL(),
                                date,
                                response.body().getArticles().get(i).getUrl(),
                                response.body().getArticles().get(i).getNumberOfLikes(),
                                response.body().getArticles().get(i).getNumberOfComments(),
                                response.body().getArticles().get(i).isLiked(),
                                globalClass
                        ));
                    }
                    globalClass.getCategoriesHandler().getArticlesRecyclerAdapter().notifyDataSetChanged();
                    globalClass.getCategoriesHandler().setLoading(false);
                    pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                    if (pb != null)
                    {
                        pb.setVisibility(View.INVISIBLE);
                    }
                }
                else if(response.body().getStatus() == 400)
                {
                    if(response.body().getMessage().equals("Authentication failed. token not found."))
                    {
                        methodCall = new Runnable() {
                            public void run() {
                                getMoreArticles(subject, lastArticleURL, globalClass);
                            }
                        };
                        User user = globalClass.getUser();
                        authenticate(user.getEmail(), user.getPicURL(), user.getFullName(), globalClass);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonRecievedArticles> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setMessage("אירעה שגיאה")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getMoreArticles(subject, lastArticleURL, globalClass);
                            }
                        })
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                globalClass.getCategoriesHandler().setLoading(false);
                                ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loading);
                                if (pb != null)
                                {
                                    pb.setVisibility(View.INVISIBLE);
                                }
                            }
                        })
                        .show();
            }
        });
    }
}
