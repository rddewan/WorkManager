package com.richarddewan.workmanagerapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/*
created by Richard Dewan 18/07/2021
*/

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "password")
    val password: String
)