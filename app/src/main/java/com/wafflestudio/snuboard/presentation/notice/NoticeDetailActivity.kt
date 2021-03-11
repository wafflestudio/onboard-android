package com.wafflestudio.snuboard.presentation.notice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.wafflestudio.snuboard.R
import com.wafflestudio.snuboard.databinding.ActivityNoticeDetailBinding
import com.wafflestudio.snuboard.presentation.TagListAdapter
import com.wafflestudio.snuboard.utils.SingleEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeDetailActivity : AppCompatActivity() {

    private val binding: ActivityNoticeDetailBinding by lazy {
        DataBindingUtil.setContentView(
                this,
                R.layout.activity_notice_detail
        ) as ActivityNoticeDetailBinding
    }

    private val noticeDetailActivityViewModel: NoticeDetailActivityViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SingleEvent.triggerToast.observe(this) {
            it.getContentIfNotHandled()?.let { it1 ->
                Toast.makeText(this, it1, Toast.LENGTH_SHORT).show()
            }
        }
        noticeDetailActivityViewModel.notice.observe(this) {
            invalidateOptionsMenu()
        }
        binding.run {
            lifecycleOwner = this@NoticeDetailActivity
            viewModel = noticeDetailActivityViewModel

            setSupportActionBar(toolBar)
            supportActionBar!!.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_navigate_before)
            }

            val tagRecyclerView = binding.root.findViewById<RecyclerView>(R.id.tag_recycler_view)
            tagRecyclerView.run {
                adapter = TagListAdapter(null)
                layoutManager = FlexboxLayoutManager(binding.root.context).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.FLEX_START
                }
            }

            contentWebView.apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        mainLayout.requestLayout()
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url ?: return false
                        startActivity(Intent(Intent.ACTION_VIEW, url))
                        return true
                    }
                }
                settings.javaScriptEnabled = true
                isEnabled = false
            }
        }

        val noticeId = intent.getIntExtra(EXTRA_NOTICE_ID, -1)
        if (noticeId == -1) finish()
        noticeDetailActivityViewModel.getNotice(noticeId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_activity_notice_detail, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        if (noticeDetailActivityViewModel.notice.value?.isScrapped == true) {
            val icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_favorite, null)
            icon?.setTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(this, R.color.purple1)))
            menu?.findItem(R.id.scrap_button)?.icon = icon
        } else {
            val icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_favorite_border, null)
            icon?.setTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(this, R.color.gray3)))
            menu?.findItem(R.id.scrap_button)?.icon = icon
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.scrap_button ->
                noticeDetailActivityViewModel.toggleSavedNotice()
            R.id.link_button -> {
                val url = noticeDetailActivityViewModel.notice.value?.link
                url?.let {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
                }
                return true
            }
            else ->
                return false
        }
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.slide_to_right)
    }

    companion object {
        fun intent(context: Context, noticeId: Int) =
                Intent(context, NoticeDetailActivity::class.java)
                        .putExtra(EXTRA_NOTICE_ID, noticeId)

        private const val EXTRA_NOTICE_ID = "extra_notice_id"
    }
}