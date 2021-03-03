package com.example.scutapp.user.talkto.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.scutapp.user.talkto.fragments.ChatsFragment;
import com.example.scutapp.user.talkto.fragments.DoctorsFragment;

public class MessageViewPagerAdapter extends FragmentPagerAdapter {
    private String title[] = {"Chats", "Doctors"};

    public MessageViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1)
            return DoctorsFragment.getInstance(position);
        else
            return ChatsFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
