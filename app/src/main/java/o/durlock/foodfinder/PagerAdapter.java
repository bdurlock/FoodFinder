package o.durlock.foodfinder;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Brett on 3/27/2018.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int mTabCount;

    public PagerAdapter(FragmentManager fm, int TabCount){
        super(fm);
        this.mTabCount = TabCount;
    }

    @Override
    public Fragment getItem(int position){
        switch(position){
            case 0:
                ListFragment list = new ListFragment();
                return list;
            case 1:
                MapFragment map = new MapFragment();
                return map;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mTabCount;
    }
}
