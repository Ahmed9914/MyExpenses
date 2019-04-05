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
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.MyExpenses.database.AppDatabase;
import com.example.android.MyExpenses.database.ExpenseEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;


public class MainActivity extends AppCompatActivity implements ExpenseAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private ExpenseAdapter mAdapter;
    private TextView mTotalExpenses;

    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewExpenses);

        mTotalExpenses = findViewById(R.id.total_amount_value_textView);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new ExpenseAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage(R.string.delete_item_confirmation)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        int position = viewHolder.getAdapterPosition();
                                        List<ExpenseEntity> expenses = mAdapter.getExpenses();
                                        viewModel.deleteExpense(expenses.get(position));
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        })
                        .create()
                        .show();
                    }
                }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddExpenseActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddExpenseActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddExpenseActivity.class);
                addTaskIntent.putExtra(AddExpenseActivity.EXTRA_EXPENSE_ID, AddExpenseActivity.DEFAULT_EXPENSE_ID);
                startActivity(addTaskIntent);
            }
        });

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getExpenses().observe(this, new Observer<List<ExpenseEntity>>() {
            @Override
            public void onChanged(@Nullable List<ExpenseEntity> expenseEntities) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setExpenses(expenseEntities);
            }
        });
        viewModel.getTotalExpenses().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double totalExpenses) {
                if (totalExpenses != null)
                    mTotalExpenses.setText(totalExpenses.toString());
                else mTotalExpenses.setText("0");
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddExpenseActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
        intent.putExtra(AddExpenseActivity.EXTRA_EXPENSE_ID, itemId);
        startActivity(intent);
    }
}
