package o.durlock.foodfinder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * This is how the fragment view works on the main page
 * Created by Brett on 3/27/2018.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private int mTabCount;

    public PagerAdapter(FragmentManager fm, int TabCount){
        super(fm);
        this.mTabCount = TabCount;
    }

    @Override
    public Fragment getItem(int position){
        switch(position){
            case 0:
                return new ListFragment();
            case 1:
                return new MapFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mTabCount;
    }
}
