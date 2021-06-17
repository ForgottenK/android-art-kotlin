package com.example.myapplication.chapter_1.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.chapter_1.model.entity.Post
import com.example.myapplication.chapter_1.view.PostListFragment

class PostItemRecyclerAdapter(onPostClickListener: PostListFragment.OnPostClickListener? = null) :
    RecyclerView.Adapter<PostItemRecyclerAdapter.ViewHolder>() {

    private val data = mutableListOf<Post>()
    var listener: PostListFragment.OnPostClickListener? = onPostClickListener

    fun setData(posts: List<Post>) {
        data.clear()
        addData(posts)
    }

    fun addData(morePosts: List<Post>) {
        data.addAll(morePosts)
        notifyDataSetChanged()
    }

    fun insertData(post: Post, index: Int) {
        if (index < data.size) {
            data.add(index, post)
            Log.d(
                TAG,
                "PostItemRecyclerAdapter.insertData, post = $post, data.size = ${data.size}"
            )
            notifyItemInserted(index)
        } else {
            throw IndexOutOfBoundsException("PostItemRecyclerAdapter.insertData() out of bound, index = $index but data.size = ${data.size}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = data[position]
        holder.post = post
        holder.id.text = post.id.toString()
        holder.content.text = post.content
        holder.view.setOnClickListener {
            listener?.onPostClicked(post)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.item_post_id)
        val content: TextView = view.findViewById(R.id.item_post_content)
        var post: Post? = null

        override fun toString(): String {
            return super.toString() + " '" + content.text + "'"
        }

    }

    companion object {
        const val TAG = "wangruixiang"
    }
}