package com.neerajsingh.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.neerajsingh.popularmovies2.Data.Movie;
import com.neerajsingh.popularmovies2.Network.PopularMoviesApplication;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neeraj.singh on 28/04/16.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder>{
    private final NavigationInterface callback;
    private ArrayList<Movie> list;
    Context context;
    public MoviesAdapter(List<Movie> list,NavigationInterface callback){
        this.list = (ArrayList)list;
        this.callback = callback;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//        Glide.with(context).load(PopularMoviesApplication.BASE_IMAGE_URL+PopularMoviesApplication.POSTER_SIZE+list.get(position).getPosterPath()).into(holder.img);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    callback.openPage(list.get(position));
                }
            }
        });
        Picasso.with(context).load(PopularMoviesApplication.BASE_IMAGE_URL+PopularMoviesApplication.POSTER_SIZE+"/"+list.get(position).getPosterPath()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.poster_image);
        }
    }
}
