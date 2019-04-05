/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.MyExpenses;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.android.MyExpenses.database.AppDatabase;
import com.example.android.MyExpenses.database.ExpenseEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddExpenseActivity extends AppCompatActivity {

    // Extra for the expense ID to be received in the intent
    public static final String EXTRA_EXPENSE_ID = "extraExpenseId";
    // Extra for the expense ID to be received after rotation
    public static final String INSTANCE_EXPENSE_ID = "instanceExpenseId";
    // Constant for default expense id to be used when not in update mode
    public static final int DEFAULT_EXPENSE_ID = -1;
    // Constant for logging
    private static final String TAG = AddExpenseActivity.class.getSimpleName();
    // Fields for views
    EditText descriptionText;
    static EditText dateText;
    EditText mAmountText;
    Button mButton;
    static Date chosenDate;

    AddExpenseViewModel viewModel;

    private int mExpenseId = DEFAULT_EXPENSE_ID;

    // Member variable for the Database
    //private AppDatabase mDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_EXPENSE_ID)) {
            mExpenseId = savedInstanceState.getInt(INSTANCE_EXPENSE_ID, DEFAULT_EXPENSE_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_EXPENSE_ID)) {
            mExpenseId = intent.getIntExtra(EXTRA_EXPENSE_ID, DEFAULT_EXPENSE_ID);
            if (mExpenseId != DEFAULT_EXPENSE_ID) {
                mButton.setText(R.string.update_button);
            }

            // populate the UI
            // COMPLETED (9) Remove the logging and the call to loadExpenseById, this is done in the ViewModel now
            // COMPLETED (10) Declare a AddExpenseViewModelFactory using mDb and mExpenseId
            AddExpenseViewModelFactory factory = new AddExpenseViewModelFactory(this.getApplication(), mExpenseId);
            // COMPLETED (11) Declare a AddExpenseViewModel variable and initialize it by calling ViewModelProviders.of
            // for that use the factory created above AddExpenseViewModel
            viewModel = ViewModelProviders.of(this, factory).get(AddExpenseViewModel.class);

            // COMPLETED (12) Observe the LiveData object in the ViewModel. Use it also when removing the observer
            viewModel.getExpense().observe(this, new Observer<ExpenseEntity>() {
                @Override
                public void onChanged(@Nullable ExpenseEntity expenseEntity) {
                    viewModel.getExpense().removeObserver(this);
                    populateUI(expenseEntity);
                }
            });
            }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_EXPENSE_ID, mExpenseId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        descriptionText = findViewById(R.id.editTextExpenseDescription);
        mAmountText = findViewById(R.id.amountTextExpenseDescription);
        mButton = findViewById(R.id.saveButton);
        dateText = findViewById(R.id.dateText);
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param expense the taskEntry to populate the UI
     */
    private void populateUI(ExpenseEntity expense) {
        if (expense == null) {
            return;
        }

        descriptionText.setText(expense.getDescription());
        mAmountText.setText(((Double) expense.getAmount()).toString());
        if (expense.getUpdatedAt() != null) {
            dateText.setText(DateFormat.getDateInstance().format(expense.getUpdatedAt()));
        } else{
            dateText.setText(DateFormat.getDateInstance().format(new Date()));
        }
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked(View v) {
        String description = "";
        Double amount = 0.0;
        Date date;
        if (!descriptionText.getText().toString().isEmpty()) {
            description = descriptionText.getText().toString();
        }
        if (!mAmountText.getText().toString().isEmpty() &&
                Double.parseDouble(mAmountText.getText().toString()) > 0) {
            amount = Double.parseDouble(mAmountText.getText().toString());
        }
        if (!dateText.getText().toString().isEmpty()){
            try {
                date = DateFormat.getDateInstance().parse(dateText.getText().toString());
            } catch (ParseException e){
                date = new Date();
            }
        } else{
            date = new Date();
        }

        if (!description.equals("") && amount > 0 && date != null) {
            final ExpenseEntity expense = new ExpenseEntity(description, amount, date);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mExpenseId == DEFAULT_EXPENSE_ID) {
                            // insert new expense
                            viewModel.insertExpense(expense);
                        } else {
                            //update expense
                            expense.setId(mExpenseId);
                            viewModel.updateExpense(expense);
                        }
                        finish();
                    }
                });
        }
    }

    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            if (getActivity() != null) {
                return new DatePickerDialog(getActivity(), this, year, month, day);
            }
            return null;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            chosenDate = calendar.getTime();
            dateText.setText(SimpleDateFormat.getDateInstance().format(chosenDate));
        }
    }

}
