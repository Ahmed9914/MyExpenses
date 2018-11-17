package com.example.android.MyExpenses.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface ExpenseDao {

    @Query("SELECT * FROM expense ORDER BY updated_at asc")
    LiveData<List<ExpenseEntity>> loadAllExpenses();

    @Insert
    void insertExpense(ExpenseEntity expenseEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateExpense(ExpenseEntity expenseEntity);

    @Delete
    void deleteExpense(ExpenseEntity expenseEntity);

    @Query("SELECT * FROM expense WHERE id = :id")
    LiveData<ExpenseEntity> loadExpenseById(int id);

    @Query("select sum(amount) from expense")
    LiveData<Double> totalExpenses();

//    @Query("select sum(amount) from expense where updated_at between :date1 and :date2")
//    LiveData<ExpenseEntity> totalExpenses(Date date1, Date date2);
}
