package com.example.austin.myapplication;
import android.arch.lifecycle.ViewModel;
/**
 * Created by austin on 12/15/17.
 */

class MainActivityViewModel extends ViewModel {
    private boolean mIsSigningIn;

    private Filters mFilters;

    MainActivityViewModel(){
        mIsSigningIn = false;
        mFilters = Filters.getDefault();
    }

    boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }
    Filters getFilters() {
        return mFilters;
    }

    void setFilters(Filters mFilters) {
        this.mFilters = mFilters;
    }

}
