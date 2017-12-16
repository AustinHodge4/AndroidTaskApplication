package com.example.austin.myapplication;
import android.arch.lifecycle.ViewModel;
/**
 * Created by austin on 12/15/17.
 */

class MainActivityViewModel extends ViewModel {
    private boolean mIsSigningIn;

    MainActivityViewModel(){
        mIsSigningIn = false;
    }
    boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

}
