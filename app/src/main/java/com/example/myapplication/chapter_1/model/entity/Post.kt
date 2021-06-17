package com.example.myapplication.chapter_1.model.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

/**
 *    @author wangruixiang
 *    @date 2021/6/14 11:00 PM
 */
@Entity(tableName = "post_table")
data class Post(
    @PrimaryKey var id: Int,
    var user: String,
    var content: String,
    var date: Date = Date(),
    @ColumnInfo(name = "like_post", defaultValue = "0") var like: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Date(parcel.readLong()),
        parcel.readString().toBoolean()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(user)
        parcel.writeString(content)
        parcel.writeLong(date.time)
        parcel.writeString(like.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }

    fun toDisplayString(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return "user: $user\ncontent: $content\ndate: ${simpleDateFormat.format(date)}\nlike: $like"
    }
}