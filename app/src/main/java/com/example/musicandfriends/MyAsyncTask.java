  package com.example.musicandfriends;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liulishuo.filedownloader.services.FileDownloadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.musicandfriends.MainActivity.TAG;

class MyAsyncTask extends AsyncTask<String, String, String> {
    private View view;
    private UserService service = MainActivity.service;
    private PageFragment pageFragment;
    private User curUser;
    private User[] userList;
    SearchAdapterItem user;
    private File avatarFile;
    private ImageView avatar;
    private ProfileActivity profileActivity;
    private String[] args;
    public MyAsyncTask(PageFragment pageFragment, ImageView avatar){
        this.view = pageFragment.view;
        this.pageFragment = pageFragment;
        this.avatar = avatar;
    }
    public MyAsyncTask(ProfileActivity profileActivity, ImageView avatar){
        this.view = profileActivity.view;
        this.profileActivity = profileActivity;
        this.avatar = avatar;
    }
    @Override
    protected String doInBackground(String... params) {
        args = params;
        try {
            if (params[0].equals("load_page")){
                Log.d("async_task", "load_page");
                Call<User> call = service.getProfile(Integer.parseInt(params[1]));
                Response<User> response = call.execute();
                curUser = response.body();
                Log.d("async_task", "load_page_2");

                TextView profileName = null;
                TextView musicPreferences = null;
                TextView notifications = null;
                TextView city = null;
                if (pageFragment != null) {
                    profileName = view.findViewById(R.id.mainPageProfileName);
                    musicPreferences = view.findViewById(R.id.mainPageMusicPreferences);
                    city = view.findViewById(R.id.mainPageCity);
                    notifications = view.findViewById(R.id.mainPageNotifications);
                    notifications.setText(String.valueOf(curUser.getNotificationCount()));
                } else if (profileActivity != null){
                    profileName = view.findViewById(R.id.profileName);
                    musicPreferences = view.findViewById(R.id.profileMusicPreferences);
                    city = view.findViewById(R.id.profileCity);
                }

                profileName.setText(curUser.getName());
                musicPreferences.setText(curUser.getTextMusicPreferences());
                city.setText(curUser.getCity());

                getAvatar(Integer.parseInt(params[1]));
            }

            if (params[0].equals("friends_req_add")){
                if (Integer.parseInt(params[2]) != Integer.parseInt(params[1])){
                    Call<Boolean> call = service.friendsReqAdd(Integer.parseInt(params[2]), Integer.parseInt(params[1]));
                    call.execute();
                }
            }

            if (params[0].equals("friends_del")){
                Call<Boolean> call = service.friendsDel(Integer.parseInt(params[2]), Integer.parseInt(params[1]));
                call.execute();
            }

            if (params[0].equals("get_status")){
                Call<Integer> call = service.getStatus(Integer.parseInt(params[2]), Integer.parseInt(params[1]));
                Response<Integer> response = call.execute();
                int status = response.body();
                profileActivity.setButtons(status);
            }

            if (params[0].equals("get_users")){
                if (params[1].equals("search_page")){
                    Call<User[]> call = service.getSearchPageContent(Integer.parseInt(params[3]), 5, params[2]);
                    Response<User[]> response = call.execute();
                    userList = response.body();
                }
            }

            if (params[0].equals("set_music_prefs")){
                service.setMusicPrefs(pageFragment.curUser.getID(), pageFragment.curUser.getMusicPreferences()).execute();
                TextView textView = view.findViewById(R.id.mainPageMusicPreferences);
                textView.setText(pageFragment.curUser.getTextMusicPreferences());
            }

            if (params[0].equals("set_avatar")){
                service.setAvatar(pageFragment.mbp, Integer.parseInt(params[1])).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (args[0].equals("friends_del") || args[0].equals("friends_req_add")){
            try{
                profileActivity.pd.show();
                new MyAsyncTask(profileActivity, null).execute("get_status", args[2], args[1]);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if (args[0].equals("load_page")){
            if (pageFragment != null){
                pageFragment.friends.setAdapter(new RecyclerAdapter(makeFriendsList(curUser)));
                pageFragment.curUser = curUser;
            }
            if (profileActivity != null) {
                profileActivity.friends.setAdapter(new RecyclerAdapter(makeFriendsList(curUser)));
            }
        }
        if (args[0].equals("get_users")){
            pageFragment.listView.setAdapter(new SearchAdapter(view.getContext(), 0, makeUser(userList)));
            pageFragment.userList = userList;
        }
        if (pageFragment != null){
            pageFragment.pd.cancel();
        }
        if (profileActivity != null){
            profileActivity.pd.cancel();
        }
    }

    public void getAvatar(int ID){
        Call<ResponseBody> call = service.downloadAvatar(ID);
        call.enqueue(new Callback<ResponseBody>() {
            File f = null;
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        if (pageFragment != null) {
                            f = new File(pageFragment.getContext().getCacheDir(), ID+"avatar.png");
                        } else {
                            f = new File(profileActivity.getCacheDir(), ID+"avatar.png");
                        }

                        InputStream inputStream = null;
                        OutputStream outputStream = null;

                        byte[] fileReader = new byte[4096];
                        inputStream = response.body().byteStream();
                        outputStream = new FileOutputStream(f);
                        while (true) {
                            int read = inputStream.read(fileReader);
                            if (read == -1) {
                                break;
                            }
                            outputStream.write(fileReader, 0, read);
                        }
                        outputStream.flush();
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e){
                        Log.d("error", "writeResponse");
                        e.printStackTrace();
                    }
                    try {
                        f.createNewFile();
                        if (avatar != null){
                            avatar.setImageURI(Uri.fromFile(f));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("error", "avatar_at");
            }
        });
    }

    public ArrayList<SearchAdapterItem> makeFriendsList(User curUser){
        ArrayList<SearchAdapterItem> arr = new ArrayList<>();
        for (int i = 0; i < curUser.friendsIDs.length; ++i){
            user = new SearchAdapterItem();
            user.ID = curUser.getFriendsIDs()[i];
            user.profileName = curUser.getFriendsNames()[i];
            arr.add(user);
        }
        return arr;
    }

    public ArrayList<SearchAdapterItem> makeUser(User[] userList){
        ArrayList<SearchAdapterItem> arr = new ArrayList<>();
        for (int i = 0; i < userList.length; ++i){
            user = new SearchAdapterItem();
            user.ID = userList[i].getID();
            user.profileName = userList[i].getName();
            user.musicPreferences = userList[i].getTextMusicPreferences();
            Log.d("user", user.ID+" "+user.profileName);
            arr.add(user);
        }
        return arr;
    }
}
