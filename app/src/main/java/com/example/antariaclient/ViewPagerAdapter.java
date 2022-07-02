package com.example.antariaclient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.antariaclient.myfragments.EditContactsFragment;
import com.example.antariaclient.myfragments.EditPhotosFragment;
import com.example.antariaclient.myfragments.EditPublicationsFragment;
import com.example.antariaclient.myfragments.EditTablesFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private int size = 4;
    private Config config;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Config config) {
        super(fragmentActivity);
        this.config = config;
    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position < 4){
            switch (position){
                case 0:{
                    return EditPhotosFragment.newInstance();
                }
                case 1:{
                    return EditPublicationsFragment.newInstance();
                }
                case 2:{
                    return EditTablesFragment.newInstance("Df","sdf");
                }
                case 3:{
                    return EditContactsFragment.newInstance("df","dsg");
                }
            }
            size += 1;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return size;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        return (itemId <= size);
    }
}
