package com.richarddewan.workmanagerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.richarddewan.workmanagerapp.data.local.entity.UserEntity
import io.reactivex.Single


/*
created by Richard Dewan 18/07/2021
*/

@Dao
interface UserDao {

    @Insert
    fun insert(userEntity: UserEntity) : Single<Long>

    @Update
    fun update(userEntity: UserEntity) : Single<Int>


}