package com.dnfapps.arrmatey.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

private val MIGRATION_1_2 = object: Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE instances ADD COLUMN headers TEXT NOT NULL DEFAULT '[]'"
        )
    }
}


val migrations = listOf(MIGRATION_1_2)