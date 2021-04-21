package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    companion object {
        const val TAG = "wangruixiang"
    }

    private var subscription: Subscription? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Flowable.create(FlowableOnSubscribe<Int> {
            for (i in 0 until 150) {
                Log.d(TAG, "发送了事件$i")
                it.onNext(i)
            }
            it.onComplete()
        }, BackpressureStrategy.LATEST)
            .subscribe(object : Subscriber<Int> {
                override fun onComplete() {
                    Log.d(TAG, "onComplete()")
                }

                override fun onSubscribe(s: Subscription?) {
                    Log.d(TAG, "onSubscribe()")
                    subscription = s
                }

                override fun onNext(t: Int?) {
                    t?.let { Log.d(TAG, "接收到事件$it") }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                }
            })

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            subscription?.request(128)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }
}