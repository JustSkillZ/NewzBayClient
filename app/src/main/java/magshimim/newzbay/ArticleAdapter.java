package magshimim.newzbay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

public class ArticleAdapter  extends ArrayAdapter<String>{

    //private Vector<Article> articles;

    public ArticleAdapter(Context context, String[] values) {
        super(context, R.layout.listview_articles, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.listview_articles, parent, false);
        String letter = getItem(position);
        TextView textView1 = (TextView) view.findViewById(R.id.textView1);
        textView1.setText(letter);
        ImageView imageView1 = (ImageView) view.findViewById(R.id.imageView1);
        imageView1.setImageResource(R.drawable.magshimim);
        return view;
    }
}