package com.example.musicandfriends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static final String TAG = "myLogs";
    static final int PAGE_COUNT = 2;
    int ID = -1;
    private static final String serverUrl = "http:/192.168.149.19:8080";
    public static final String APP_PREFERENCES = "usersettings";
    public static final String EMAIL = "email";
    public static final String PASS = "pass";
    static UserService service;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(UserService.class);
        SharedPreferences sp = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        authorise(sp.getString(EMAIL, "0"), sp.getString(PASS, "0"), 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.authUser){
            EditText email = findViewById(R.id.authEmailEditText);
            EditText pass = findViewById(R.id.authPassEditText);
            if (email.getText() != null &&  pass.getText() != null){
                SharedPreferences sp = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(EMAIL, String.valueOf(email.getText()));
                editor.putString(PASS, String.valueOf(pass.getText()));
                editor.commit();
                authorise(String.valueOf(email.getText()), String.valueOf(pass.getText()), 1);
            }
        }
        if (v.getId() == R.id.userReg){
            userAuth(1,0);
        }
        if (v.getId() == R.id.regBack){
            userAuth(0, 0);
        }
        if (v.getId() == R.id.regUser){
            String name = String.valueOf(((EditText)findViewById(R.id.regNameEditText)).getText());
            String surname = String.valueOf(((EditText)findViewById(R.id.regSurnameEditText)).getText());
            String email = String.valueOf(((EditText)findViewById(R.id.regEmailEditText)).getText());
            String pass = String.valueOf(((EditText)findViewById(R.id.regPassEditText)).getText());
            String city = String.valueOf(((EditText)findViewById(R.id.regCityEditText)).getText());
            if (name != null && surname != null && email != null && pass != null && city != null){
                Call<Integer> call = service.userReg(name+" "+surname, email, pass, city);
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.isSuccessful()){
                            if (response.body() == -1){
                                userAuth(1, 0);
                            } else {
                                SharedPreferences sp = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString(EMAIL, email);
                                editor.putString(PASS, pass);
                                editor.commit();
                                setContentView(R.layout.activity_main);
                                ID = response.body();
                                pager = findViewById(R.id.pager);
                                pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), response.body());
                                pagerAdapter.notifyDataSetChanged();
                                pager.setAdapter(pagerAdapter);

                                pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageSelected(int position) {

                                    }
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset,
                                                               int positionOffsetPixels) {
                                    }
                                    @Override
                                    public void onPageScrollStateChanged(int state) {
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.d("error", "reg failed");
                    }
                });
            }
        }

    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        int ID;

        public MyFragmentPagerAdapter(FragmentManager supportFragmentManager, int ID) {
            super(supportFragmentManager);
            this.ID = ID;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return "Мой профиль";
                case 1: return "Поиск";
                //case 2: return "Карта";
            }
            return "";
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position, service, ID);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

    public void userAuth(int page, int vis){
        switch (page){
            case 0:
            {
                setContentView(R.layout.user_auth);
                TextView err = findViewById(R.id.authError);
                switch (vis){
                    case 0: err.setVisibility(View.INVISIBLE);break;
                    case 1: err.setVisibility(View.VISIBLE);break;
                }
                Button auth = findViewById(R.id.authUser);
                Button reg = findViewById(R.id.userReg);
                auth.setOnClickListener(this);
                reg.setOnClickListener(this);
                break;
            }
            case 1:
            {
                setContentView(R.layout.user_reg);
                Button regBack = findViewById(R.id.regBack);
                Button regUser = findViewById(R.id.regUser);
                regUser.setOnClickListener(this);
                regBack.setOnClickListener(this);
                break;
            }
        }
    }

    public void exit(){
        userAuth(0, 0);
    }

    public void authorise(String email, String pass, int option){
        Call<Integer> call = service.userAuth(email, pass);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()){
                    if (response.body() == -1){
                        switch (option){
                            case 0: userAuth(0, 0);break;
                            case 1: userAuth(0, 1);break;
                        }

                    } else {
                        setContentView(R.layout.activity_main);
                        ID = response.body();
                        pager = findViewById(R.id.pager);
                        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), response.body());
                        pagerAdapter.notifyDataSetChanged();
                        pager.setAdapter(pagerAdapter);

                        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageSelected(int position) {

                            }
                            @Override
                            public void onPageScrolled(int position, float positionOffset,
                                                       int positionOffsetPixels) {
                            }
                            @Override
                            public void onPageScrollStateChanged(int state) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("error", "auth failed");
            }
        });
    }
}