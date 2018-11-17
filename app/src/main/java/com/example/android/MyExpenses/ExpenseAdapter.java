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

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.MyExpenses.database.ExpenseEntity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * This ExpenseAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<ExpenseEntity> mExpenseEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the ExpenseAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public ExpenseAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new ExpenseViewHolder that holds the view for each task
     */
    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.expense_layout, parent, false);

        return new ExpenseViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        // Determine the values of the wanted data
        ExpenseEntity expenseEntity = mExpenseEntries.get(position);
        String description = expenseEntity.getDescription();
        Double amount = expenseEntity.getAmount();
        String updatedAt = dateFormat.format(expenseEntity.getUpdatedAt());

        //Set values
        holder.expenseDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);
        holder.amountView.setText(amount.toString());
    }


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mExpenseEntries == null) {
            return 0;
        }
        return mExpenseEntries.size();
    }

    public List<ExpenseEntity> getExpenses() {
        return mExpenseEntries;
    }

    /**
     * When data changes, this method updates the list of expenseEntities
     * and notifies the adapter to use the new values on it
     */
    public void setExpenses(List<ExpenseEntity> expenseEntities) {
        mExpenseEntries = expenseEntities;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView expenseDescriptionView;
        TextView updatedAtView;
        TextView amountView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public ExpenseViewHolder(View itemView) {
            super(itemView);

            expenseDescriptionView = itemView.findViewById(R.id.expenseDescription);
            updatedAtView = itemView.findViewById(R.id.expenseAddedAt);
            amountView = itemView.findViewById(R.id.amountTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mExpenseEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}