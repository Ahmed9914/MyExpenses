package com.example.android.MyExpenses;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.MyExpenses.database.AppDatabase;
import com.example.android.MyExpenses.database.ExpenseEntity;

// COMPLETED (5) Make this class extend ViewModel
public class AddExpenseViewModel extends ViewModel {

    // COMPLETED (6) Add a expense member variable for the ExpenseEntity object wrapped in a LiveData
    private LiveData<ExpenseEntity> expense;
    private final AppDatabase mDb;

    // COMPLETED (8) Create a constructor where you call loadExpenseById of the expenseDao to initialize the tasks variable
    // Note: The constructor should receive the database and the expenseId
    public AddExpenseViewModel(Application application, int expenseId) {
        mDb = AppDatabase.getInstance(application);
        expense = mDb.expenseDao().loadExpenseById(expenseId);
    }

    // COMPLETED (7) Create a getter for the expense variable
    public LiveData<ExpenseEntity> getExpense() {
        return expense;
    }

    public void updateExpense(ExpenseEntity expense){
        mDb.expenseDao().updateExpense(expense);
    }

    public void insertExpense(ExpenseEntity expense){
        mDb.expenseDao().insertExpense(expense);
    }

}
