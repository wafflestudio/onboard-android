package com.wafflestudio.snuboard.presentation.department

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.wafflestudio.snuboard.di.SharedPreferenceConst
import com.wafflestudio.snuboard.domain.model.*
import com.wafflestudio.snuboard.domain.usecase.DeleteFollowingTagUseCase
import com.wafflestudio.snuboard.domain.usecase.GetNoticesOfDepartmentUseCase
import com.wafflestudio.snuboard.domain.usecase.GetTagDepartmentInfoUseCase
import com.wafflestudio.snuboard.domain.usecase.PostFollowingTagUseCase
import com.wafflestudio.snuboard.utils.ErrorResponse
import com.wafflestudio.snuboard.utils.Event
import com.wafflestudio.snuboard.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DepartmentActivityViewModel
@Inject
constructor(
    private val getTagDepartmentInfoUseCase: GetTagDepartmentInfoUseCase,
    private val postFollowingTagUseCase: PostFollowingTagUseCase,
    private val deleteFollowingTagUseCase: DeleteFollowingTagUseCase,
    private val getNoticesOfDepartmentUseCase: GetNoticesOfDepartmentUseCase,
    @ApplicationContext appContext: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pref = PreferenceManager.getDefaultSharedPreferences(
        appContext
    )

    //    Tag Part
    private var homeTagString = listOf<String>()

    private val _tagDepartmentInfo = MutableLiveData<TagDepartmentFull>()
    val tagDepartmentInfo: LiveData<TagDepartmentFull>
        get() = _tagDepartmentInfo

    private val _isHomeTagsVisible = MutableLiveData(false)
    val isHomeTagsVisible: LiveData<Boolean>
        get() = _isHomeTagsVisible

    private val _isFilterOn = MutableLiveData(false)
    val isFilterOn: LiveData<Boolean>
        get() = _isFilterOn

    //    Notice Part
    private val _notices = MutableLiveData<List<Notice>>()
    val updateNotices = getNoticesOfDepartmentUseCase.updateNotice

    val notices: LiveData<List<Notice>>
        get() = _notices

    private val paginationLimit = 10
    private var paginationCursor: String? = null

    fun getNotices() {
        val tmpNoticeList = _notices.value?.toMutableList() ?: mutableListOf()
        if (paginationCursor == EOP)
            return
        getNoticesOfDepartmentUseCase
            .getNotices(
                tagDepartmentInfo.value!!.id,
                paginationLimit,
                paginationCursor,
                homeTagString
            )
            .subscribe({
                when (it) {
                    is NoticeList -> {
                        tmpNoticeList.addAll(it.notices)
                        _notices.value = tmpNoticeList
                        paginationCursor = if (it.nextCursor.isEmpty())
                            EOP
                        else
                            it.nextCursor
                    }
                    is ErrorResponse -> {
                        SingleEvent.triggerToast.value = Event(it.message)
                        Timber.e(it.message)
                    }
                }
            }, {
                Timber.e(it)
            })
    }

    private fun updateNotices() {
        paginationCursor = null
        getNoticesOfDepartmentUseCase
            .getNotices(
                tagDepartmentInfo.value!!.id,
                paginationLimit,
                paginationCursor,
                homeTagString
            )
            .subscribe({
                when (it) {
                    is NoticeList -> {
                        _notices.value = it.notices
                        paginationCursor = if (it.nextCursor.isEmpty())
                            EOP
                        else
                            it.nextCursor
                    }
                    is ErrorResponse -> {
                        SingleEvent.triggerToast.value = Event(it.message)
                        Timber.e(it.message)
                    }
                }
            }, {
                Timber.e(it)
            })
    }


    //    Tag Part
    private fun getHomeTagString(departmentId: Int) {
        val preferenceKey = SharedPreferenceConst.getDepartmentHomeKey(departmentId)
        val departmentHomeTagString = pref.getString(preferenceKey, "EMPTY")
        var departmentHomeTags = listOf<String>()
        if (departmentHomeTagString == "EMPTY" || departmentHomeTagString!!.isBlank()) {
            pref.edit {
                putString(preferenceKey, "")
            }
        } else {
            departmentHomeTags = departmentHomeTagString.split(",")
        }
        homeTagString = departmentHomeTags
        _isFilterOn.value = homeTagString.isNotEmpty()
    }

    fun toggleHomeTag(tagContent: String) {
        homeTagString = if (tagContent in homeTagString) {
            homeTagString.filter { it != tagContent }
        } else {
            val mutableHomeTagString = homeTagString.toMutableList()
            mutableHomeTagString.add(tagContent)
            mutableHomeTagString
        }

        val tmpTagDepartmentInfo = tagDepartmentInfo.value!!

        val homeTags = tmpTagDepartmentInfo.homeTags.map {
            if (it.content in homeTagString) {
                Tag(it.content, DepartmentColor.TAG_SELECTED_COLOR)
            } else {
                Tag(it.content, DepartmentColor.TAG_COLOR)
            }
        }

        val tagDepartmentFull = TagDepartmentFull(
            tmpTagDepartmentInfo.id,
            tmpTagDepartmentInfo.name,
            tmpTagDepartmentInfo.tags,
            homeTags,
            tmpTagDepartmentInfo.departmentColor
        )

        _tagDepartmentInfo.value = tagDepartmentFull
    }

    fun toggleHomeTagCard() {
        val tmpTagDepartmentInfo = tagDepartmentInfo.value!!

        if (isHomeTagsVisible.value!!) {
            getHomeTagString(tmpTagDepartmentInfo.id)
            val homeTags = tmpTagDepartmentInfo.homeTags.map {
                if (it.content in homeTagString) {
                    Tag(it.content, DepartmentColor.TAG_SELECTED_COLOR)
                } else {
                    Tag(it.content, DepartmentColor.TAG_COLOR)
                }
            }
            val tagDepartmentFull = TagDepartmentFull(
                tmpTagDepartmentInfo.id,
                tmpTagDepartmentInfo.name,
                tmpTagDepartmentInfo.tags,
                homeTags,
                tmpTagDepartmentInfo.departmentColor
            )
            _tagDepartmentInfo.value = tagDepartmentFull
        }
        _isHomeTagsVisible.value = !isHomeTagsVisible.value!!
    }

    fun applyHomeTags() {
        val tmpTagDepartmentInfo = tagDepartmentInfo.value!!
        val preferenceKey = SharedPreferenceConst.getDepartmentHomeKey(tmpTagDepartmentInfo.id)
        pref.edit {
            putString(preferenceKey, homeTagString.joinToString(separator = ","))
        }
        _isFilterOn.value = homeTagString.isNotEmpty()
        updateNotices()
        SingleEvent.triggerToast.value = Event("필터를 적용하였습니다.")
    }

    fun eraseHomeTags() {
        val tmpTagDepartmentInfo = tagDepartmentInfo.value!!
        val preferenceKey = SharedPreferenceConst.getDepartmentHomeKey(tmpTagDepartmentInfo.id)
        pref.edit {
            putString(preferenceKey, "")
        }
        homeTagString = listOf()
        val homeTags = tmpTagDepartmentInfo.homeTags.map {
            if (it.content in homeTagString) {
                Tag(it.content, DepartmentColor.TAG_SELECTED_COLOR)
            } else {
                Tag(it.content, DepartmentColor.TAG_COLOR)
            }
        }
        val tagDepartmentFull = TagDepartmentFull(
            tmpTagDepartmentInfo.id,
            tmpTagDepartmentInfo.name,
            tmpTagDepartmentInfo.tags,
            homeTags,
            tmpTagDepartmentInfo.departmentColor
        )
        _tagDepartmentInfo.value = tagDepartmentFull
        _isFilterOn.value = homeTagString.isNotEmpty()
        updateNotices()
        SingleEvent.triggerToast.value = Event("필터를 제거하였습니다.")
    }

    fun getTagDepartmentInfo(departmentId: Int) {
        getHomeTagString(departmentId)
        getTagDepartmentInfoUseCase
            .getTagDepartmentInfo(departmentId)
            .subscribe({
                when (it) {
                    is TagDepartment -> {
                        var homeTags = listOf<Tag>()
                        tagDepartmentInfo.value?.also { org ->
                            homeTags = org.homeTags
                        } ?: run {
                            homeTags = it.tags.map { tag ->
                                if (tag.content in homeTagString) {
                                    Tag(tag.content, DepartmentColor.TAG_SELECTED_COLOR)
                                } else {
                                    Tag(tag.content, DepartmentColor.TAG_COLOR)
                                }
                            }
                        }
                        val tagDepartmentFull = TagDepartmentFull(
                            it.id,
                            it.name,
                            it.tags,
                            homeTags,
                            it.departmentColor
                        )

                        _tagDepartmentInfo.value = tagDepartmentFull
                        getNotices()
                    }
                    is ErrorResponse -> {
                        SingleEvent.triggerToast.value = Event(it.message)
                        Timber.e(it.message)
                    }
                }
            }, {
                Timber.e(it)
            })
    }

    fun toggleFollowingTag(tagContent: String) {
        tagDepartmentInfo.value?.let {
            val designatedColor = it.tags.find { tag -> tag.content == tagContent }?.color
            when (designatedColor) {
                DepartmentColor.TAG_COLOR ->
                    postFollowingTagUseCase
                            .postFollowingTag(it.id, tagContent)
                DepartmentColor.TAG_SELECTED_COLOR ->
                    deleteFollowingTagUseCase
                            .deleteFollowingTag(it.id, tagContent)
                else ->
                    null
            }
                    ?.subscribe({ it1 ->
                        when (it1) {
                            is TagDepartment -> {
                                val tagDepartmentFull = TagDepartmentFull(
                                    it1.id,
                                    it1.name,
                                    it1.tags,
                                    it.homeTags,
                                    it1.departmentColor
                                )
                                _tagDepartmentInfo.value = tagDepartmentFull
                            }
                            is ErrorResponse -> {
                                SingleEvent.triggerToast.value = Event(it1.message)
                                Timber.e(it1.message)
                            }
                        }
                    }, { it1 ->
                        Timber.e(it1)
                    })
        }
    }

    fun changeDepartmentColor(departmentColor: DepartmentColor) {
        val tmpTagDepartment = tagDepartmentInfo.value!!
        val preferenceKey = SharedPreferenceConst.getDepartmentColorKey(tmpTagDepartment.id)
        pref.edit {
            putInt(preferenceKey, departmentColor.colorId)
        }
        _tagDepartmentInfo.value = TagDepartmentFull(
            tmpTagDepartment.id,
            tmpTagDepartment.name,
            tmpTagDepartment.tags,
            tmpTagDepartment.homeTags,
            departmentColor
        )
    }

    companion object {
        // Used to indicate cursor that it is End of Page
        private const val EOP = "EOP"

    }
}
