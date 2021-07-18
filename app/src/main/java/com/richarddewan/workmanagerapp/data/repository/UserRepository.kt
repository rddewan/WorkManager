package com.richarddewan.workmanagerapp.data.repository

import com.richarddewan.workmanagerapp.data.local.db.AppDatabase
import com.richarddewan.workmanagerapp.data.local.entity.UserEntity
import io.reactivex.Single


/*
created by Richard Dewan 18/07/2021
*/

class UserRepository(private val database: AppDatabase) {

    fun insert(userEntity: UserEntity): Single<Long> = database.userDao().insert(userEntity)

    fun update(userEntity: UserEntity): Single<Int> = database.userDao().update(userEntity)

}