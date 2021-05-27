package com.wafflestudio.snuboard.presentation.department

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.wafflestudio.snuboard.R
import com.wafflestudio.snuboard.databinding.FragmentDepartmentHomeBinding
import com.wafflestudio.snuboard.presentation.TagClickListener
import com.wafflestudio.snuboard.presentation.TagListAdapter
import com.wafflestudio.snuboard.presentation.notice.HeartClickListener
import com.wafflestudio.snuboard.presentation.notice.NoticeInfiniteScrollListener
import com.wafflestudio.snuboard.presentation.notice.NoticeListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DepartmentHomeFragment : Fragment() {

    lateinit var binding: FragmentDepartmentHomeBinding
    private val departmentActivityViewModel: DepartmentActivityViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_department_home,
                container,
                false
            )
        binding.run {
            lifecycleOwner = this@DepartmentHomeFragment
            activityViewModel = departmentActivityViewModel
            tagRecyclerView.run {
                adapter = TagListAdapter(
                    TagClickListener {
                        departmentActivityViewModel.toggleHomeTag(it)
                    }
                )
                layoutManager = FlexboxLayoutManager(binding.root.context).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.FLEX_START
                }
            }
            applyButton.setOnClickListener {
                departmentActivityViewModel.applyHomeTags()
            }
            eraseButton.setOnClickListener {
                departmentActivityViewModel.eraseHomeTags()
            }
            noticeRecyclerView.run {
                val myLayoutManager = LinearLayoutManager(requireContext())
                layoutManager = myLayoutManager
                adapter = NoticeListAdapter(
                    HeartClickListener {
//                        departmentActivityViewModel.toggleSavedNotice(it)
                    }
                )
                (adapter as NoticeListAdapter).registerAdapterDataObserver(
                    object : RecyclerView.AdapterDataObserver() {
                        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                            if (positionStart == 0)
                                smoothScrollToPosition(0)
                        }
                    }
                )
                clearOnScrollListeners()
                addOnScrollListener(NoticeInfiniteScrollListener(myLayoutManager) {
                    departmentActivityViewModel.getNotices()
                })
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.app_bar_fragment_department_home, menu)
    }
}
