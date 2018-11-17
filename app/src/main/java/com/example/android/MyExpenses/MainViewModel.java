package com.example.android.MyExpenses;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import android.util.Log;

import com.example.android.MyExpenses.database.AppDatabase;
import com.example.android.MyExpenses.database.ExpenseEntity;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<ExpenseEntity>> expenses;
    private LiveData<Double> totalExpenses;
    private final AppDatabase mDb;

    public MainViewModel(Application application) {
        super(application);
        mDb = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the expenses from the DataBase");
        expenses = mDb.expenseDao().loadAllExpenses();
        totalExpenses = mDb.expenseDao().totalExpenses();
    }

    public LiveData<List<ExpenseEntity>> getExpenses() {
        return expenses;
    }

    public LiveData<Double> getTotalExpenses() {
        return totalExpenses;
    }

    public void deleteExpense(ExpenseEntity expense) {
        mDb.expenseDao().deleteExpense(expense);
    }
}
