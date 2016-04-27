package magshimim.newzbay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Vector;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    GlobalClass globalClass;
    CommentsHandler commentsHandler;
    Vector<Comment> comments;
    Context context;

    public CommentsAdapter(GlobalClass globalClass, Context context) {
        this.globalClass = globalClass;
        commentsHandler = globalClass.getCommentsHandler();
        comments = commentsHandler.getCommentsofCurrentArticle();
        this.context = context;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_comments, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int tempPosition = position;
        Picasso.with(globalClass.getCurrentActivity()).load(comments.get(position).getProfilePicURL()).into(holder.profilePic);
        holder.username.setText(comments.get(position).getUsername());
        holder.commentText.setText(comments.get(position).getCommentText());
        if(globalClass.getUser().getPicURL().equals(comments.get(position).getProfilePicURL()))
        {
            holder.username.setTextColor(globalClass.getResources().getColor(R.color.nb));
            holder.deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    globalClass.getCommunication().clientSend("124◘" + commentsHandler.getArticle().getUrl() + "○" + commentsHandler.getCommentsofCurrentArticle().get(tempPosition).getCommentText() + "#");
                                    commentsHandler.getCommentsofCurrentArticle().remove(tempPosition);
                                    commentsHandler.getArticle().decNumberOfComments();
                                    commentsHandler.getRecyclerAdapter().notifyDataSetChanged();
                                    ((TextView)((Activity) context).findViewById(R.id.tv_comments)).setText(commentsHandler.getArticle().getNumberOfComments() + "");
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("האם את/ה בטוח שברצונך למחוק את תגובתך?").setPositiveButton("מחק", dialogClickListener)
                            .setNegativeButton("ביטול", dialogClickListener).show();
                }
            });
        }
        else
        {
            holder.deleteComment.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentsHandler.getCommentsofCurrentArticle().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        de.hdodenhof.circleimageview.CircleImageView profilePic;
        TextView username;
        TextView commentText;
        ImageButton deleteComment;

        public ViewHolder(View itemView) {
            super(itemView);
            profilePic = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.civ_userPhoto);
            username = (TextView) itemView.findViewById(R.id.tv_username);
            commentText = (TextView) itemView.findViewById(R.id.tv_comment);
            deleteComment = (ImageButton) itemView.findViewById(R.id.ib_deleteComment);
        }
    }
}
