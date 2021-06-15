package com.example.myapplication.chapter_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R

/**
 *    @author wangruixiang
 *    @date 2021/6/14 11:10 PM
 */
class PostDetailFragment : Fragment() {
    lateinit var detailText: TextView

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

        val post: Post? = arguments?.getParcelable(KEY_POST)
        post?.let {
            detailText.text = post.toString()
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