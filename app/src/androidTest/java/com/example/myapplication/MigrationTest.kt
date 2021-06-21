package com.example.myapplication

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.chapter_1.model.db.AppDatabase
import com.example.myapplication.chapter_1.model.db.Converters
import com.example.myapplication.chapter_1.model.entity.Post
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

/**
 *    @author wangruixiang
 *    @date 2021/6/21 10:33 PM
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    companion object {
        private const val TEST_DB_NAME = "migration-test"

        private val POST = Post(1, "master", "post #1", Date(), true)

        private val ALL_MIGRATIONS = arrayOf(AppDatabase.MIGRATION_1_2)
    }

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrationFrom1to2_containsCorrectData() {
        // Create the database in version 1
        helper.createDatabase(TEST_DB_NAME, 1).apply {
            // Insert some data
            insertPost(POST.id, POST.user, POST.content, POST.date, this)
            //Prepare for the next version
            close()
        }

        // Re-open the database with version 2 and provide MIGRATION_1_2 as the migration process.
        helper.runMigrationsAndValidate(TEST_DB_NAME, 2, true, AppDatabase.MIGRATION_1_2)

        // MigrationTestHelper automatically verifies the schema changes, but not the data validity
        // Validate that the data was migrated properly.
        val dbPost = getMigratedRoomDatabase().postDao().getAllPosts()[0]
        assertEquals(dbPost.id, POST.id)
        assertEquals(dbPost.user, POST.user)
        assertEquals(dbPost.content, POST.content)
        assertEquals(dbPost.date, POST.date)
        assertEquals(dbPost.like, false)
    }

    @Test
    @Throws(IOException::class)
    fun startInVersion2_containsCorrectData() {
        // Create the database with version 2
        helper.createDatabase(TEST_DB_NAME, 2).apply {
            // insert some data
            insertPost(POST, this)
            close()
        }

        // open the db with Room.
        val appDatabase = getMigratedRoomDatabase()

        // verify that the data is correct
        val dbPost = appDatabase.postDao().getAllPosts()[0]
        assertEquals(dbPost.id, POST.id)
        assertEquals(dbPost.user, POST.user)
        assertEquals(dbPost.content, POST.content)
        assertEquals(dbPost.date, POST.date)
        assertEquals(dbPost.like, POST.like)
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB_NAME, 1).apply {
            close()
        }

        // Open latest version of the database. Room will validate the schema
        // once all migrations execute.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            TEST_DB_NAME
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase
            close()
        }
    }

    private fun getMigratedRoomDatabase(): AppDatabase {
        val appDatabase = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            TEST_DB_NAME
        ).addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
        // close the database and release any stream resources when the test finishes
        helper.closeWhenFinished(appDatabase)
        return appDatabase
    }

    private fun insertPost(
        id: Int, user: String, content: String, date: Date, db: SupportSQLiteDatabase
    ) {
        val values = ContentValues()
        values.put("id", id)
        values.put("user", user)
        values.put("content", content)
        values.put("date", Converters().dateToTimestamp(date))
        db.insert("post_table", SQLiteDatabase.CONFLICT_REPLACE, values)
    }

    private fun insertPost(post: Post, db: SupportSQLiteDatabase) {
        val values = ContentValues()
        values.put("id", post.id)
        values.put("user", post.user)
        values.put("content", post.content)
        values.put("date", Converters().dateToTimestamp(post.date))
        values.put("like_post", if (post.like) 1 else 0)
        db.insert("post_table", SQLiteDatabase.CONFLICT_REPLACE, values)
    }
}