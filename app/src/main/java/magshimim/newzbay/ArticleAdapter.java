package magshimim.newzbay;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class ArticleAdapter  extends ArrayAdapter<Article>{

    private ListView listView;
    private Vector<Article> articles;
    private Context context;
    private Button like;
    private TextView countLikes;
    private Button comment;
    private TextView countComments;
    private ImageButton picture;

    public ArticleAdapter(Context context, Vector<Article> values) {
        super(context, R.layout.listview_articles, values);
        articles = values;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.listview_articles, parent, false);
        TextView mainHeadline = (TextView) view.findViewById(R.id.tv_mainHeadline);
        mainHeadline.setText(articles.elementAt(position).getMainHeadline());
        countLikes = (TextView) view.findViewById(R.id.tv_likes);
        countLikes.setText(articles.get(position).getNumberOfLikes() + " Likes");
        countComments = (TextView) view.findViewById(R.id.tv_comments);
        countComments.setText("    " + articles.get(position).getNumberOfComments() + " Comments");
        like = (Button) view.findViewById(R.id.btn_like);
        if(articles.get(position).getLiked())
        {
            like.setText("Unlike");
        }
        else
        {
            like.setText("Like");
        }
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                like = (Button) v.findViewById(R.id.btn_like);
                countLikes = (TextView) listView.getChildAt(position).findViewById(R.id.tv_likes);
                if (articles.get(position).getLiked()) {
                    like.setText("Like");
                    articles.get(position).setLiked(false);
                    articles.get(position).decNumberOfLikes();
                    countLikes.setText(articles.get(position).getNumberOfLikes() + " Likes");

                }
                else {
                    like.setText("Unlike");
                    articles.get(position).setLiked(true);
                    articles.get(position).incNumberOfLikes();
                    countLikes.setText(articles.get(position).getNumberOfLikes() + " Likes");
                }
            }
        });
        picture = (ImageButton) view.findViewById(R.id.ib_picture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                listView = (ListView) parentRow.getParent();
                int position = listView.getPositionForView(parentRow);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(articles.elementAt(position).getUrl()));
                context.startActivity(intent);
            }
        });
        return view;
    }
}