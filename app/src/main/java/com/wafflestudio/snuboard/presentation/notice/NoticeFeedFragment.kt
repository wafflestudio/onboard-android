package com.wafflestudio.snuboard.presentation.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wafflestudio.snuboard.R
import com.wafflestudio.snuboard.databinding.FragmentNoticeFeedBinding

class NoticeFeedFragment : Fragment() {

    lateinit var binding: FragmentNoticeFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_notice_feed,
                container,
                false
            )
        return binding.root
    }
}