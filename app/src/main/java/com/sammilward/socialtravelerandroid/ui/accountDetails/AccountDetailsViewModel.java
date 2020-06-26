package com.sammilward.socialtravelerandroid.ui.accountDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountDetailsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AccountDetailsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the account details fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}