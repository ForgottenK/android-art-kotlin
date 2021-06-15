package com.example.myapplication.chapter_1

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
import kotlinx.coroutines.*
import java.util.*

/**
 *    @author wangruixiang
 *    @date 2021/6/14 11:52 PM
 */
class PostListFragment : Fragment() {
    lateinit var postList: RecyclerView
    lateinit var adapter: PostItemRecyclerAdapter

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
        adapter = PostItemRecyclerAdapter()
        postList.adapter = adapter

        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setPostClickListener(object : OnPostClickListener {
            override fun onPostClicked(post: Post) {
                // TODO: 2021/6/15 选中对应帖子
            }
        })
        GlobalScope.launch {
            val postList = getPostList()
            withContext(Dispatchers.Main) {
                adapter.setData(postList)
            }
        }
    }

    suspend fun getPostList(): List<Post> {
        delay(500)
        val users = listOf("frank", "bob", "mary", "eric", "kevin", "tom", "justin")
        val dates = generateDates()

        val postList = mutableListOf<Post>()
        for (i in 1..50) {
            postList.add(Post(i.toString(), users.random(), "Post #$i", dates.random()))
        }
        return postList
    }

    private fun generateDates(): List<Date> {
        val dates = mutableListOf<Date>()

        val calendar = GregorianCalendar()
        dates.add(calendar.time)
        calendar.add(Calendar.YEAR, -1)
        dates.add(calendar.time)
        calendar.add(Calendar.MONTH, 6)
        dates.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 12)
        dates.add(calendar.time)
        calendar.add(Calendar.MONTH, 3)
        dates.add(calendar.time)

        return dates
    }

    class PostItemRecyclerAdapter() :
        RecyclerView.Adapter<PostItemRecyclerAdapter.ViewHolder>() {

        private val data = mutableListOf<Post>()
        private var listener: OnPostClickListener? = null

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
            holder.id.text = post.id
            holder.content.text = post.content
            holder.content.setOnClickListener {
                listener?.onPostClicked(post)
            }
        }

        fun setPostClickListener(listener: OnPostClickListener) {
            this.listener = listener
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

        fun newInstance(): PostListFragment {
            return newInstance(1)
        }

        fun newInstance(columnCount: Int): PostListFragment {
            val args = Bundle()
            args.putInt(KEY_COLUMN_COUNT, columnCount)
            val fragment = PostListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}