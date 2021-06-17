package com.example.myapplication.chapter_1.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.chapter_1.model.entity.Post

/**
 *    @author wangruixiang
 *    @date 2021/6/14 11:10 PM
 */
class PostDetailFragment : Fragment() {
    private lateinit var detailText: TextView
    private lateinit var btnLikePost: Button
    private lateinit var btnGotoDetail: Button

    lateinit var post: Post

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailText = view.findViewById(R.id.tv_fragment_detail_content)
        btnLikePost = view.findViewById(R.id.btn_post_liked)
        btnGotoDetail = view.findViewById(R.id.btn_goto_post_detail)

        btnLikePost.setOnClickListener {
            // TODO: 2021/6/17 change like status
        }
        btnLikePost.visibility = View.GONE

        btnGotoDetail.setOnClickListener {
            val intent = Intent(activity, PostDetailActivity::class.java)
            intent.putExtra(PostDetailActivity.KEY_POST, post)
            startActivity(intent)
        }
        btnGotoDetail.visibility = View.GONE

        arguments?.getParcelable<Post>(KEY_POST)?.let {
            post = it
            detailText.text = post.toString()
            btnLikePost.visibility = View.VISIBLE
            btnGotoDetail.visibility = View.VISIBLE
        }
    }

    companion object {
        const val KEY_POST = "key_post"

        fun newInstance(post: Post?): PostDetailFragment {
            val args = Bundle()
            args.putParcelable(KEY_POST, post)
            val fragment = PostDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }
}