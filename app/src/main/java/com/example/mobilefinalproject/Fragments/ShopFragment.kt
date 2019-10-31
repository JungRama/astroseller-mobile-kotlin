package com.example.mobilefinalproject.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mobilefinalproject.R

class ShopFragment : Fragment() {
    companion object {
        fun newInstance(): ShopFragment {
            return ShopFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_shop, container, false)
    }
}