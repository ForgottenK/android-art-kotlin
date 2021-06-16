package com.example.myapplication.chapter_1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

/**
 *    @author wangruixiang
 *    @date 2021/6/14 11:52 PM
 */
class PostListFragment : Fragment(), IPostListView {
    private lateinit var postList: RecyclerView
    private lateinit var adapter: PostItemRecyclerAdapter
    var postClickListener: OnPostClickListener? = null

    private var presenter: IPostListPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contentView = inflater.inflate(R.layout.fragment_post_list, container, false)

        postList = contentView.findViewById(R.id.rv_fragment_post_list)
        postList.layoutManager = let<PostListFragment, RecyclerView.LayoutManager> {
            val columnCount = arguments?.getInt(KEY_COLUMN_COUNT)
            if (columnCount != null && columnCount > 1) {
                GridLayoutManager(context, columnCount)
            } else {
                LinearLayoutManager(context)
            }
        }
        adapter = PostItemRecyclerAdapter(onPostClickListener = postClickListener)
        postList.adapter = adapter

        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = PostListPresenter(this@PostListFragment).also {
            lifecycle.addObserver(it)
            it.requestPostListData()
        }
    }

    override fun onReceivePostListData(postList: List<Post>) {
        adapter.setData(postList)
    }

    override fun getDatabaseContext() = context

    class PostItemRecyclerAdapter(onPostClickListener: OnPostClickListener? = null) :
        RecyclerView.Adapter<PostItemRecyclerAdapter.ViewHolder>() {

        private val data = mutableListOf<Post>()
        var listener: OnPostClickListener? = onPostClickListener

        fun setData(posts: List<Post>) {
            data.clear()
            addData(posts)
        }

        fun addData(morePosts: List<Post>) {
            data.addAll(morePosts)
            notifyDataSetChanged()
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
    }

    interface OnPostClickListener {
        fun onPostClicked(post: Post)
    }

    companion object {
        const val KEY_COLUMN_COUNT = "key_column_count"

        fun newInstance(
            columnCount: Int = 1,
            onPostClickListener: OnPostClickListener? = null
        ): PostListFragment {
            val args = Bundle()
            args.putInt(KEY_COLUMN_COUNT, columnCount)
            val fragment = PostListFragment()
            fragment.arguments = args
            fragment.postClickListener = onPostClickListener
            return fragment
        }
    }
}

interface IPostListView {
    fun onReceivePostListData(postList: List<Post>)
    fun getDatabaseContext(): Context?
}