package com.wafflestudio.snuboard.presentation.notice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.wafflestudio.snuboard.R
import com.wafflestudio.snuboard.databinding.ActivityNoticeSearchBinding

class NoticeSearchActivity : AppCompatActivity() {

    private val binding: ActivityNoticeSearchBinding by lazy {
        DataBindingUtil.setContentView(
                this,
                R.layout.activity_notice_search
        ) as ActivityNoticeSearchBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {

        }
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, NoticeSearchActivity::class.java)
    }
}