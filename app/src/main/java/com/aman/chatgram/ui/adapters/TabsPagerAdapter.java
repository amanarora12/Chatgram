package com.aman.chatgram.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.aman.chatgram.ui.fragments.ChatListFragment;
import com.aman.chatgram.ui.fragments.ContactListFragment;

/**
 * Created by Aman on 12-06-2016.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new ChatListFragment();
            case 1:
                return new ContactListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
