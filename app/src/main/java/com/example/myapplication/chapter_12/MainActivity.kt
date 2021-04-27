package com.example.myapplication.chapter_12

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import com.example.myapplication.R
import com.example.myapplication.utils.MyUtils

/**
 *    @author wangruixiang
 *    @date 2021/4/27 2:53 AM
 */
class MainActivity : Activity(), AbsListView.OnScrollListener {
    companion object {
        private const val TAG = "wangruixiang"
    }

    private val urlList = arrayListOf<String>()
    var imageLoader: ImageLoader? = null
    private var imageGridView: GridView? = null
    private var imageAdapter: BaseAdapter? = null
    private var isGridViewIdle = true
    private var imageWidth = 0
    private var isWifi = false
    private var canGetBitmapFromNetWork = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imageloader)
        initData()
        initView()
        imageLoader = ImageLoader.build(this@MainActivity)
    }

    private fun initData() {
        val imageUrls = arrayOf(
            "https://youimg1.c-ctrip.com/target/1007050000000s4w4051C.jpg",
            "https://youimg1.c-ctrip.com/target/100u050000000s4w6B0F7.jpg",
            "https://youimg1.c-ctrip.com/target/100o0y000000m2teu73A9.jpg",
            "https://youimg1.c-ctrip.com/target/100v050000000s4w1E38E.jpg",
            "https://youimg1.c-ctrip.com/target/100w0y000000ma0dcB7DD.jpg",
            "https://youimg1.c-ctrip.com/target/100l0h0000008zwqi8727.jpg",
            "https://youimg1.c-ctrip.com/target/100j0h0000008zzt7D777.jpg",
            "https://youimg1.c-ctrip.com/target/100o0h0000008z8ue834B.jpg",
            "https://youimg1.c-ctrip.com/target/1001050000000s4w4A8D2.jpg",
            "https://youimg1.c-ctrip.com/target/10010y000000mgwf2E5E2.jpg",
            "https://youimg1.c-ctrip.com/target/100p0y000000m33vuEE40.jpg",
            "https://youimg1.c-ctrip.com/target/10030y000000m5pd30DDA.jpg",
            "https://youimg1.c-ctrip.com/target/100r0y000000m2eb99EF8.jpg",
            "https://youimg1.c-ctrip.com/target/100s0y000000mauqeA312.jpg",
            "https://youimg1.c-ctrip.com/target/10040y000000m2yi97685.jpg",
            "https://youimg1.c-ctrip.com/target/fd/tg/g5/M0B/07/DE/CggYr1dBXb-AED5SAAFagds343A454.jpg",
            "https://youimg1.c-ctrip.com/target/fd/tg/g3/M0B/CD/6F/CggYGVZz8tqANpd5AAQM630ohSQ196.jpg",
            "https://youimg1.c-ctrip.com/target/100l0h0000008zwqj7746.jpg",
            "https://youimg1.c-ctrip.com/target/100m0h0000008z83n6D75.jpg",
            "https://youimg1.c-ctrip.com/target/100n0h0000008z7lyEE79.jpg",
            "https://youimg1.c-ctrip.com/target/100m070000002r39jF67B.jpg",
            "https://youimg1.c-ctrip.com/target/100r070000002r393BFC2.jpg",
            "https://youimg1.c-ctrip.com/target/100s070000002r397E187.jpg",
            "https://youimg1.c-ctrip.com/target/100t070000002r394B4D2.jpg",
            "https://youimg1.c-ctrip.com/target/100u070000002r39hDF09.jpg",
            "https://youimg1.c-ctrip.com/target/fd/tg/g6/M09/16/E1/CggYslc_0ACAI461AAMnm8AZVk8879.jpg",
            "https://youimg1.c-ctrip.com/target/fd/tg/g5/M02/06/22/CggYsVc_0ASAJQnSAATiF4kvoKw452.jpg",
            "https://youimg1.c-ctrip.com/target/fd/tg/g5/M09/02/0E/CggYr1c_0AqAZqlPAAOU4EMswjQ452.jpg",
            "https://youimg1.c-ctrip.com/target/fd/tg/g5/M04/06/22/CggYsVc_0AyAISbAAAPVRkGh9HU709.jpg",
            "https://youimg1.c-ctrip.com/target/fd/tg/g6/M05/57/63/CggYs1c4gwyAYpoAAAIbM18mhjo968.jpg",
            "https://youimg1.c-ctrip.com/target/fd/tg/g6/M03/57/62/CggYs1c4gwmAfWHuAAIHzdZYnUk515.jpg",
            "https://youimg1.c-ctrip.com/target/fd/tg/g6/M05/57/62/CggYs1c4gwqAGBkfAAHK6fYUOl0407.jpg",
            "https://youimg1.c-ctrip.com/target/fd/tg/g6/M05/44/34/CggYtFc4gwiAFX1TAAA9Zb8r0gI422.jpg",
            "https://youimg1.c-ctrip.com/target/10050i0000009hjo00B90.jpg",
            "https://youimg1.c-ctrip.com/target/100p0i0000009gz4lD3DA.jpg",
            "https://youimg1.c-ctrip.com/target/100i0i0000009gvg493CD.jpg",
            "https://youimg1.c-ctrip.com/target/10030i0000009gvu6B8F2.jpg",
            "https://youimg1.c-ctrip.com/target/100d0i0000009guitA78A.jpg",
            "https://youimg1.c-ctrip.com/target/100q0i0000009h7yd13BC.jpg"
        )
        for (url in imageUrls) {
            urlList.add(url)
        }
        val screenWidth: Int = MyUtils.getScreenMetrics(this).widthPixels
        val space = MyUtils.dp2px(this, 20f).toInt()
        imageWidth = (screenWidth - space) / 3
        isWifi = MyUtils.isWifi(this)
        if (isWifi) {
            canGetBitmapFromNetWork = true
        }
    }

    private fun initView() {
        imageGridView = findViewById(R.id.gridView1)
        imageAdapter = ImageAdapter(this)
        imageGridView?.adapter = imageAdapter
        imageGridView?.setOnScrollListener(this)
        if (!isWifi) {
            AlertDialog.Builder(this)
                .setMessage("初次使用会从网络下载大概5MB的图片，确认要下载吗？")
                .setTitle("注意")
                .setPositiveButton("是") { _, _ ->
                    canGetBitmapFromNetWork = true
                    imageAdapter?.notifyDataSetChanged()
                }
                .setNegativeButton("否", null)
                .show()
        }
    }

    private inner class ImageAdapter(context: Context) : BaseAdapter() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private val defaultDrawable: Drawable =
            context.resources.getDrawable(R.drawable.image_default)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val itemView = convertView ?: inflater.inflate(R.layout.item_image_list, parent, false)
            val holder = itemView.tag as ViewHolder? ?: ViewHolder().apply {
                imageView = itemView.findViewById(R.id.item_image_list_image)
                itemView.tag = this
            }

            holder.imageView?.let {
                val tag = it.tag as String?
                val uri = getItem(position)
                if (uri != tag) {
                    it.setImageDrawable(defaultDrawable)
                }
                if (isGridViewIdle && canGetBitmapFromNetWork) {
                    it.tag = uri
                    imageLoader?.bindBitmap(uri, it, imageWidth, imageWidth)
                }
            }

            return itemView
        }

        override fun getItem(position: Int): String = urlList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = urlList.size
    }

    private class ViewHolder {
        var imageView: ImageView? = null
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        isGridViewIdle = scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
        if (isGridViewIdle) {
            imageAdapter?.notifyDataSetChanged()
        }
    }

    override fun onScroll(
        view: AbsListView, firstVisibleItem: Int,
        visibleItemCount: Int, totalItemCount: Int
    ) {
        // ignored
    }
}