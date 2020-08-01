package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    private int no_of_tabs;
    public PageAdapter(@NonNull FragmentManager fm,int no_of_tabs) {
        super(fm);
        this.no_of_tabs = no_of_tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if(position==0){
            return new WorldFragment();
        }
        else if(position==1){
            return new BusinessFragment();
        }
        else if(position==2){
            return new PoliticsFragment();
        }
        else if(position==3){
            return new SportsFragment();
        }
        else if(position==4){
            return new TechnologyFragment();
        }
        else if(position==5){
            return new ScienceFragment();
        }
        else{
            return null;
        }

    }

    @Override
    public int getCount() {
        return no_of_tabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
