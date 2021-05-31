package com.example.musicandfriends;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<SearchAdapterItem> friends;

    public RecyclerAdapter (ArrayList<SearchAdapterItem> friends){
        this.friends = friends;
    }
    public View itemView;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_page_friend, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(friends.get(position).profileName);
        holder.avatar.setClipToOutline(true);
        if (friends.get(position).avatar != null){
            holder.avatar.setImageURI(Uri.fromFile(friends.get(position).avatar));
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                intent.putExtra("myID", PageFragment.curUser.getID());
                intent.putExtra("ID", friends.get(position).ID);
                (v.getContext()).startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView avatar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.managerName);
            avatar = itemView.findViewById(R.id.managerAvatar);
        }
    }
}
