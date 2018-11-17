package com.example.android.MyExpenses.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

import java.util.Date

@Entity(tableName = "expense")
class ExpenseEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var description: String? = null
    var amount: Double = 0.0
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date? = null

    @Ignore
    constructor(description: String, amount: Double, updatedAt: Date) {
        this.description = description
        this.amount = amount
        this.updatedAt = updatedAt
    }

    constructor(id: Int, description: String, amount: Double, updatedAt: Date) {
        this.id = id
        this.description = description
        this.amount = amount
        this.updatedAt = updatedAt
    }
}
