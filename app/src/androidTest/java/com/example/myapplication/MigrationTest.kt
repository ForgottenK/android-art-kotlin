package com.example.myapplication

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
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

        private val POST = Post(1, "master", "post #1", Date(), true, 1)
    }

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrationFrom1to3_containsCorrectData() {
        // Create the database in version 1
        helper.createDatabase(TEST_DB_NAME, 1).apply {
            // Insert some data
            insertPost(POST.id, POST.user, POST.content, POST.date, this)
            //Prepare for the next version
            close()
        }

        // Re-open the database with version 3 and provide MIGRATION_1_2 & MIGRATION_2_3 as the migration process.
        helper.runMigrationsAndValidate(
            TEST_DB_NAME,
            3,
            true,
            AppDatabase.MIGRATION_1_2,
            AppDatabase.MIGRATION_2_3
        )

        // MigrationTestHelper automatically verifies the schema changes, but not the data validity
        // Validate that the data was migrated properly.
        val dbPost = getMigratedRoomDatabase().postDao().getAllPosts()[0]
        assertEquals(POST.id, dbPost.id)
        assertEquals(POST.user, dbPost.user)
        assertEquals(POST.content, dbPost.content)
        assertEquals(POST.date, dbPost.date)
        assertEquals(false, dbPost.like)
        assertEquals(null, dbPost.likeCount)
    }

    @Test
    @Throws(IOException::class)
    fun migrationFrom2to3_containsCorrectData() {
        helper.createDatabase(TEST_DB_NAME, 2).apply {
            insertPost(POST.id, POST.user, POST.content, POST.date, POST.like, this)
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB_NAME, 3, true, AppDatabase.MIGRATION_2_3)

        val dbPost = getMigratedRoomDatabase().postDao().getAllPosts()[0]
        assertEquals(POST.id, dbPost.id)
        assertEquals(POST.user, dbPost.user)
        assertEquals(POST.content, dbPost.content)
        assertEquals(POST.date, dbPost.date)
        assertEquals(POST.like, dbPost.like)
        assertEquals(null, dbPost.likeCount)
    }

    @Test
    @Throws(IOException::class)
    fun startInVersion3_containsCorrectData() {
        // Create the database with version 2
        helper.createDatabase(TEST_DB_NAME, 3).apply {
            // insert some data
            insertPost(POST, this)
            close()
        }

        // open the db with Room.
        val appDatabase = getMigratedRoomDatabase()

        // verify that the data is correct
        val dbPost = appDatabase.postDao().getAllPosts()[0]
        assertEquals(POST.id, dbPost.id)
        assertEquals(POST.user, dbPost.user)
        assertEquals(POST.content, dbPost.content)
        assertEquals(POST.date, dbPost.date)
        assertEquals(POST.like, dbPost.like)
        assertEquals(POST.likeCount, dbPost.likeCount)
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
        ).addMigrations(*AppDatabase.ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase
            close()
        }
    }

    private fun getMigratedRoomDatabase(): AppDatabase {
        val appDatabase = Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            TEST_DB_NAME
        ).addMigrations(*AppDatabase.ALL_MIGRATIONS)
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

    private fun insertPost(
        id: Int, user: String, content: String, date: Date, like: Boolean, db: SupportSQLiteDatabase
    ) {
        val values = ContentValues()
        values.put("id", id)
        values.put("user", user)
        values.put("content", content)
        values.put("date", Converters().dateToTimestamp(date))
        values.put("like_post", if (like) 1 else 0)
        db.insert("post_table", SQLiteDatabase.CONFLICT_REPLACE, values)
    }

    private fun insertPost(post: Post, db: SupportSQLiteDatabase) {
        val values = ContentValues()
        values.put("id", post.id)
        values.put("user", post.user)
        values.put("content", post.content)
        values.put("date", Converters().dateToTimestamp(post.date))
        values.put("like_post", if (post.like) 1 else 0)
        values.put("like_count", post.likeCount)
        db.insert("post_table", SQLiteDatabase.CONFLICT_REPLACE, values)
    }
}