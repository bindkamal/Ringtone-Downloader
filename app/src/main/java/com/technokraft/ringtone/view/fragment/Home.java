package com.technokraft.ringtone.view.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.technokraft.ringtone.adapter.RingToneAdapter;
import com.technokraft.ringtone.interfaces.SongEventListener;
import com.technokraft.ringtone.model.Song;
import com.technokraft.ringtone.viewmodel.RingToneViewModel;
import com.technokraft.ringzone.R;
import com.technokraft.ringzone.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.Navigation;

public class Home extends Fragment implements SongEventListener {

    RingToneViewModel ringToneViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final FragmentHomeBinding fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        ringToneViewModel = ViewModelProviders.of(requireActivity()).get(RingToneViewModel.class);
        fragmentHomeBinding.setRingToneViewModel(ringToneViewModel);
        fragmentHomeBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final RingToneAdapter ringToneAdapter = new RingToneAdapter(this);
        ringToneViewModel.mSongList.observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable List<Song> songs) {
                if (songs.size() > 0) {
                    ringToneAdapter.setSongList(songs);
                } else {
                    ringToneAdapter.setSongList(new ArrayList<Song>());
                    Toast.makeText(requireActivity(), "No Data Found!", Toast.LENGTH_SHORT).show();
                }
                fragmentHomeBinding.recyclerView.setAdapter(ringToneAdapter);
            }
        });

        // below code helps to make the keyboard search button utilised to search the mSong
        final InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(requireActivity().INPUT_METHOD_SERVICE);
        fragmentHomeBinding.searchKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ringToneViewModel.searchSong();
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        ringToneViewModel.mSpinner.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean spinnerVisible) {
                if (spinnerVisible) {
                    fragmentHomeBinding.progressSpinner.setVisibility(View.VISIBLE);
                } else {
                    fragmentHomeBinding.progressSpinner.setVisibility(View.GONE);
                }
            }
        });

        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void previewSongClick(Song previewSong) {
        ringToneViewModel.mSong.setValue(previewSong);
        ringToneViewModel.loadSound();
        Navigation.findNavController(getActivity(), R.id.recycler_view).navigate(R.id.action_home_to_songPreview);
    }

    @Override
    public void onResume() {
        super.onResume();
        ringToneViewModel.mMediaPlayer.reset();
    }
}
