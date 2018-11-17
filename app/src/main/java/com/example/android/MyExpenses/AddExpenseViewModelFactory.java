package com.example.android.MyExpenses;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.MyExpenses.database.AppDatabase;

// COMPLETED (1) Make this class extend ViewModel ViewModelProvider.NewInstanceFactory
public class AddExpenseViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    // COMPLETED (2) Add two member variables. One for the database and one for the taskId
    private final AppDatabase mDb;
    private final int mExpenseId;
    private final Application mApp;

    public AddExpenseViewModelFactory(Application application, int taskId) {
        mApp = application;
        mDb = AppDatabase.getInstance(mApp);
        mExpenseId = taskId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddExpenseViewModel(mApp, mExpenseId);
    }
}
