package com.example.myapplication.chapter_1.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.chapter_1.model.entity.Post
import com.example.myapplication.chapter_1.viewmodel.SharedPostViewModel

class PostItemRecyclerAdapter(private val sharedPostViewModel: SharedPostViewModel) :
    ListAdapter<Post, PostItemRecyclerAdapter.ViewHolder>(PostsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)
        holder.post = post
        holder.id.text = post.id.toString()
        holder.content.text = post.content
        holder.view.setOnClickListener {
            sharedPostViewModel.setSelectedPost(post)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.item_post_id)
        val content: TextView = view.findViewById(R.id.item_post_content)
        var post: Post? = null

        override fun toString(): String {
            return super.toString() + " '" + content.text + "'"
        }
    }

    class PostsComparator : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }

    companion object {
        const val TAG = "wangruixiang"
    }
}