package com.e.basicmovieinforus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> movies;

    public MovieAdapter(ArrayList<Movie> list){
        movies = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivPoster;
        TextView tvTitle;
        TextView tvDate;
        TextView tvOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvOverview = itemView.findViewById(R.id.tvOverview);
        }
    }

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(movies.get(i));

        viewHolder.tvTitle.setText(movies.get(i).getTitle());
        viewHolder.tvDate.setText(movies.get(i).getDate());
        viewHolder.tvOverview.setText(movies.get(i).getOverview());

        String poster = movies.get(i).getPoster();
        Context context = viewHolder.ivPoster.getContext();

        if (!poster.equals("")){
            Picasso.with(context).load(movies.get(i).getPoster()).placeholder(R.drawable.ic_launcher_foreground).into(viewHolder.ivPoster);
        } else {
            Picasso.with(context).load(R.drawable.ic_launcher_foreground).placeholder(R.drawable.ic_launcher_foreground).into(viewHolder.ivPoster);
        }

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


}
