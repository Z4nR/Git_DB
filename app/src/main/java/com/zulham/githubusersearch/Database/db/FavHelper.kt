package com.zulham.githubusersearch.Database.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.zulham.githubusersearch.Database.db.DatabaseContract.FavColumns.Companion.IS_FAV
import com.zulham.githubusersearch.Database.db.DatabaseContract.FavColumns.Companion.TABLE_NAME
import com.zulham.githubusersearch.Database.db.DatabaseContract.FavColumns.Companion.USER_NAME
import com.zulham.githubusersearch.Database.db.DatabaseContract.FavColumns.Companion._ID
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

class FavHelper(context: Context) {

    companion object{
        private const val DB_TABLE = TABLE_NAME
        private lateinit var databaseHelper: DatabaseHelper
        private var INSTANCE: FavHelper? = null
        @InternalCoroutinesApi
        fun getInstance(context: Context): FavHelper =
                INSTANCE ?: synchronized(this){
                    INSTANCE ?: FavHelper(context)
                }

        private lateinit var database: SQLiteDatabase
    }

    init {
        databaseHelper = DatabaseHelper(context)
    }

    @Throws(SQLException::class)
    fun open() {
        database = databaseHelper.writableDatabase
    }

    fun close() {
        databaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    fun queryAll(): Cursor{
        return database.query(
                DB_TABLE,
                null,
                null,
                null,
                null,
                null,
                null,
                "$_ID ASC"
        )
    }

    fun queryById(id: String): Cursor {
        return database.query(
                DB_TABLE,
                null,
                "$IS_FAV = ?",
                arrayOf(id),
                null,
                null,
                null,
                null)
    }

    fun insert(values: ContentValues?): Long {
        if (!database.isOpen) {
            open()
        }
        return database.insert(DB_TABLE, null, values)
    }

    fun update(id: String, values: ContentValues?): Int {
        if (!database.isOpen) {
            open()
        }
        return database.update(DB_TABLE, values, "$USER_NAME = ?", arrayOf(id))
    }

}