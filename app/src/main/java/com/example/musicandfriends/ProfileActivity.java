package com.example.musicandfriends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    UserService service = MainActivity.service;
    ProgressDialog pd;
    View view;
    Button profileBack;
    Button addToFriends;
    ImageView profileAvatar;
    String args = "";
    TextView profileSubscr;
    RecyclerView friends;
    static User curUser;
    SearchAdapterItem user;
    int ID = -1;
    int myID = -1;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().inflate(R.layout.activity_profile, null);
        setContentView(view);
        profileBack = findViewById(R.id.profileBack);
        profileAvatar = findViewById(R.id.profileAvatar);
        addToFriends = findViewById(R.id.addToFriends);
        profileSubscr = findViewById(R.id.profileSubscr);
        profileAvatar.setClipToOutline(true);
        profileBack.setOnClickListener(this);
        addToFriends.setOnClickListener(this);
        friends = findViewById(R.id.profileRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        friends.setLayoutManager(manager);

        Bundle arguments = getIntent().getExtras();
        ID = arguments.getInt("ID");
        myID = arguments.getInt("myID");

        pd = new ProgressDialog(this);
        pd.setMessage("Загрузка...");
        pd.show();
        new MyAsyncTask(this, null).execute("load_page", String.valueOf(ID));

        args = "get_status";
        pd.show();
        new MyAsyncTask(this, null).execute(String.valueOf(ID), String.valueOf(myID));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profileBack){
            ProfileActivity.this.finish();
        }

        if (v.getId() == R.id.addToFriends){
            if (status == 1 || status == 0) {
                args = "friends_req_add";
                pd.show();
                new MyAsyncTask(this, null).execute(String.valueOf(ID), String.valueOf(myID));
            }
            if (status == 3){
                args = "friends_del";
                pd.show();
                new MyAsyncTask(this, null).execute(String.valueOf(ID), String.valueOf(myID));
            }
        }
    }

    public void setButtons(int status){
        this.status = status;
        switch (status){
            case 0:
                break;
            case 1:
                profileSubscr.setText("Подписан на вас");
                addToFriends.setText("Добавить в друзья");
                break;
            case 2:
                profileSubscr.setText("Заявка отправлена");
                addToFriends.setText("Добавить в друзья");
                break;
            case 3:
                profileSubscr.setText("У вас в друзьях");
                addToFriends.setText("Удалить из друзей");
                break;
        }
    }
}