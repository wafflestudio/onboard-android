package com.wafflestudio.snuboard.utils

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.Gravity
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snuboard.R
import com.wafflestudio.snuboard.domain.model.*
import com.wafflestudio.snuboard.presentation.TagListAdapter
import com.wafflestudio.snuboard.presentation.department.CollegeDepartmentListAdapter
import com.wafflestudio.snuboard.presentation.department.FollowingDepartmentListAdapter
import com.wafflestudio.snuboard.presentation.notice.NoticeListAdapter

@SuppressLint("RtlHardcoded")
@BindingAdapter("drawer_open")
fun bindDrawerOpen(view: DrawerLayout, bool: Boolean) {
    if (bool)
        view.openDrawer(Gravity.LEFT)
    else
        view.close()
}

@BindingAdapter("heart_filled")
fun bindHeartFilled(view: ImageView, bool: Boolean) {
    if (bool) {
        view.setImageResource(R.drawable.ic_favorite)
        view.imageTintList = ColorStateList
                .valueOf(ContextCompat.getColor(view.context, R.color.purple1))
    } else {
        view.setImageResource(R.drawable.ic_favorite_border)
        view.imageTintList = ColorStateList
                .valueOf(ContextCompat.getColor(view.context, R.color.gray3))
    }
}

@BindingAdapter("notice_items")
fun bindNoticeItems(view: RecyclerView, items: List<Notice>?) {
    val adapt = view.adapter as NoticeListAdapter
    items?.also {
        adapt.submitList(it)
    }
}

@BindingAdapter("notice_tag_items")
fun bindNoticeTagItems(view: RecyclerView, item: Notice?) {
    val adapt = view.adapter as TagListAdapter
    item?.also {
        val items = mutableListOf<Tag>(
                Tag(it.departmentName, it.departmentColor)
        )
        items.addAll(
                it.tags.map { it1 ->
                    Tag(it1, DepartmentColor.TAG_COLOR)
                }
        )
        adapt.submitList(items)
    }
}

@BindingAdapter("tag_items")
fun bindTagItems(view: RecyclerView, contents: List<String>?) {
    val adapt = view.adapter as TagListAdapter
    contents?.also {
        adapt.submitList(
                it.map { it1 ->
                    Tag(it1, DepartmentColor.TAG_COLOR)
                }
        )
    }
}

@BindingAdapter("college_department_items")
fun bindCollegeDepartmentItems(view: RecyclerView, items: List<CollegeDepartment>?) {
    val adapt = view.adapter as CollegeDepartmentListAdapter
    items?.also {
        adapt.submitList(it)
    }
}

@BindingAdapter("following_department_items")
fun bindFollowingDepartmentItems(view: RecyclerView, items: List<FollowingDepartment>?) {
    val adapt = view.adapter as FollowingDepartmentListAdapter
    items?.also {
        adapt.submitList(it)
    }
}
