package com.example.TmdbApp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;


public class Player extends Fragment  {
    YouTubePlayerView youTubePlayerView;

    public Player() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_player,container,false);

        String playerUrl = "";
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            playerUrl = bundle.getString("url", "https://www.youtube.com/watch?v=QefliFe16nc");
        }
        youTubePlayerView = viewGroup.findViewById(R.id.youtube_player_view);

        final String finalPlayerUrl = playerUrl;
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                String videoId = playerUrl;
                Log.e("playerurl",finalPlayerUrl);
                youTubePlayer.loadVideo("https://www.youtube.com/watch?v=deKH0pQ7-rg", 0);
            }
        });
        return viewGroup;
    }


    public void stopPlayer()
    {
        youTubePlayerView.release();
    }



}