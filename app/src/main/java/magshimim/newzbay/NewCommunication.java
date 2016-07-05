package magshimim.newzbay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

public class NewCommunication
{
    private final String BASE_URL = "http://newzbay.ddns.net";
    private String PORT = "4646";
    private String IP;
    private Retrofit retrofit;
    private NewzBayAPI newzBayAPI;
    private String token;
    private boolean firstRegistration;

    public NewCommunication()
    {
    }

    public void getIPFromBaseURL(final GlobalClass globalClass)
    {
        token = "";
        IP = "http://";
        firstRegistration = true;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        newzBayAPI = retrofit.create(NewzBayAPI.class);

        Call<InfoFromServer> call = newzBayAPI.getIP();
        call.enqueue(new Callback<InfoFromServer>()
        {
            @Override
            public void onResponse(Call<InfoFromServer> call, Response<InfoFromServer> response)
            {
                IP = IP + response.body().getMessage() + ":" + PORT;
                retrofit = new Retrofit.Builder()
                        .baseUrl(IP)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                newzBayAPI = retrofit.create(NewzBayAPI.class);
                if(globalClass.getCurrentActivity() instanceof ActivityEntrance)
                {
                    ((ActivityEntrance) globalClass.getCurrentActivity()).connectToSocialNets();
                }
            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setMessage("ההתחברות לשרת נכשלה")
                        .setPositiveButton("נסה שנית", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getIPFromBaseURL(globalClass);
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

    public void authenticate(String email, String picURL, String name, final Context context, final GlobalClass globalClass)
    {
        Call<AuthenticationRecieve> call = newzBayAPI.authenticateUser(new AuthenticationSend(email, picURL, name));
        call.enqueue(new Callback<AuthenticationRecieve>() {
            @Override
            public void onResponse(Call<AuthenticationRecieve> call, Response<AuthenticationRecieve> response)
            {
                if(response.body().getStatus() == 200)
                {
                    token = response.body().getToken();
                    getAllRSS(globalClass.getPriorityHandler());
                    if(response.body().getMessage().equals("Welcome back"))
                    {
                        firstRegistration = false;
                    }
                    if(context instanceof ActivityEntrance)
                    {
                        ((ActivityEntrance) context).moveToNewsFeed();
                        if(firstRegistration)
                        {
                            Intent priority = new Intent(context, ActivityPriority.class);
                            context.startActivity(priority);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthenticationRecieve> call, Throwable t)
            {

            }
        });
    }

    public void setPriority(ArrayList<Priority> priorityList, final Context context)
    {
        Call<InfoFromServer> call = newzBayAPI.setPriority(token, priorityList);
        call.enqueue(new Callback<InfoFromServer>() {
            @Override
            public void onResponse(Call<InfoFromServer> call, Response<InfoFromServer> response)
            {
//                if(response.body().getStatus() == 200)
//                {
//                    Toast.makeText(context, "העדפות נוספו בהצלחה!", Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {

            }
        });
    }

    public void getAllRSS(final PriorityHandler priorityHandler)
    {
        Call<SubWeb> call = newzBayAPI.getAllRSS(token);
        call.enqueue(new Callback<SubWeb>() {
            @Override
            public void onResponse(Call<SubWeb> call, Response<SubWeb> response)
            {
                for(int i = 0; i < response.body().getSubWeb().size(); i++)
                {
                    priorityHandler.getRssSites().add(new RSS(
                            response.body().getSubWeb().get(i).getID(),
                            response.body().getSubWeb().get(i).getSubject(),
                            response.body().getSubWeb().get(i).getWebsite()));
                }
            }

            @Override
            public void onFailure(Call<SubWeb> call, Throwable t)
            {

            }
        });
    }

    public void deletePrioritySubject(String subject)
    {
        Call<InfoFromServer> call = newzBayAPI.deletePrioritySubject(token, subject);
        call.enqueue(new Callback<InfoFromServer>() {
            @Override
            public void onResponse(Call<InfoFromServer> call, Response<InfoFromServer> response)
            {

            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {

            }
        });
    }

    public void getUserPriority(String subject, final GlobalClass globalClass)
    {
        Call<UserPriority> call = newzBayAPI.getUserPriority(token, subject);
        call.enqueue(new Callback<UserPriority>() {
            @Override
            public void onResponse(Call<UserPriority> call, Response<UserPriority> response)
            {
                for(int i = 0; i < response.body().getPriority().size(); i++)
                {
                    globalClass.getPriorityHandler().getClientPriority().add(response.body().getPriority().get(i).getWebsite());
                    globalClass.getPriorityHandler().getRecyclerAdapter().notifyDataSetChanged();
                }
                globalClass.getPriorityHandler().createRemovedSitesList();
            }

            @Override
            public void onFailure(Call<UserPriority> call, Throwable t)
            {

            }
        });
    }

    public void getArticles(String subject, final GlobalClass globalClass)
    {
        Call<JsonRecievedArticles> call = newzBayAPI.getArticles(token, subject);
        call.enqueue(new Callback<JsonRecievedArticles>() {
            @Override
            public void onResponse(Call<JsonRecievedArticles> call, Response<JsonRecievedArticles> response)
            {
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
                    if(response.body().getArticles().size() == 0)
                    {
                        Toast.makeText(globalClass.getCurrentActivity(), "לא ביצעת העדפה בנושא זה", Toast.LENGTH_LONG).show();
                    }
                    ProgressBar pb = (ProgressBar) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.pb_loadingArticles);
                    if (pb != null)
                    {
                        pb.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonRecievedArticles> call, Throwable t)
            {
                Log.d("Failure", "Get Articles");
            }
        });
    }

    public void like(String articleURL)
    {
        Call<InfoFromServer> call = newzBayAPI.like(token, articleURL);
        call.enqueue(new Callback<InfoFromServer>()
        {
            @Override
            public void onResponse(Call<InfoFromServer> call, Response<InfoFromServer> response)
            {

            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {

            }
        });
    }

    public void addComment(String articleURL, String comment, final GlobalClass globalClass, final CommentsHandler commentsHandler, final User user, final String commentText)
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
            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setTitle("הפעולה נכשלה")
                        .setMessage("לא היה ניתן להוסיף את התגובה, אנא נסה/י שנית מאוחר יותר")
                        .setNeutralButton("אוקיי", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        })
                        .show();
            }
        });
    }

    public void deleteComment(String articleURL, String id, final GlobalClass globalClass, final int tempPosition)
    {
        Call<InfoFromServer> call = newzBayAPI.deleteComment(token, articleURL, id);
        call.enqueue(new Callback<InfoFromServer>()
        {
            @Override
            public void onResponse(Call<InfoFromServer> call, Response<InfoFromServer> response)
            {
                if(response.body().getStatus() == 200)
                {
                    globalClass.getCommentsHandler().getCommentsOfCurrentArticle().remove(tempPosition);
                    globalClass.getCommentsHandler().getArticle().decNumberOfComments();
                    globalClass.getCommentsHandler().getCommentsRecyclerAdapter().notifyDataSetChanged();
                    ((TextView) ((Activity) globalClass.getCurrentActivity()).findViewById(R.id.tv_comments)).setText(globalClass.getCommentsHandler().getArticle().getNumberOfComments() + "");
                }
            }

            @Override
            public void onFailure(Call<InfoFromServer> call, Throwable t)
            {
                new AlertDialog.Builder(globalClass.getCurrentActivity(), R.style.NBAlertDialog)
                        .setTitle("הפעולה נכשלה")
                        .setMessage("לא היה ניתן למחוק את התגובה, אנא נסה/י שנית מאוחר יותר")
                        .setNeutralButton("אוקיי", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        })
                        .show();
            }
        });
    }

    public void getComments(String articleURL, final GlobalClass globalClass)
    {
        Call<JsonRecievedComments> call = newzBayAPI.getComments(token, articleURL);
        call.enqueue(new Callback<JsonRecievedComments>()
        {
            @Override
            public void onResponse(Call<JsonRecievedComments> call, Response<JsonRecievedComments> response)
            {
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
            }

            @Override
            public void onFailure(Call<JsonRecievedComments> call, Throwable t)
            {

            }
        });
    }
}
