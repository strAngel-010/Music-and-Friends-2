package com.example.musicandfriends;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SearchAdapter extends ArrayAdapter<SearchAdapterItem> {
    public SearchAdapter(@NonNull Context context, int resource, @NonNull List<SearchAdapterItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final SearchAdapterItem user = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_adapter_item, null);
        }

        ((TextView) convertView.findViewById(R.id.adapterProfileName)).setText(user.profileName);
        ((TextView) convertView.findViewById(R.id.adapterMusicPreferences)).setText(user.musicPreferences);
        ImageView avatar = convertView.findViewById(R.id.adapterAvatar);
        avatar.setClipToOutline(true);
        if (user.avatar != null){
            avatar.setImageURI(Uri.fromFile(user.avatar));
        }
        return convertView;
    }
}
