package magshimim.newzbay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SitesAdapter extends RecyclerView.Adapter<SitesAdapter.ViewHolder> {

    private PriorityHandler priorityHandler;
    private CategoriesHandler categoriesHandler;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView siteLogo;

        public ViewHolder(View itemView)
        {
            super(itemView);
            this.siteLogo = (ImageView) itemView.findViewById(R.id.ib_site_logo);
        }
    }

    public SitesAdapter(GlobalClass globalClass)
    {
        priorityHandler = globalClass.getPriorityHandler();
        categoriesHandler = globalClass.getCategoriesHandler();
    }

    @Override
    public SitesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_priority_sites, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.siteLogo.setImageBitmap(categoriesHandler.getSiteLogo().get(priorityHandler.getSitesOfCurrentSubject().get(position)));

    }

    @Override
    public int getItemCount() {
        return priorityHandler.getSitesOfCurrentSubject().size();
    }
}