package com.wafflestudio.snuboard.domain.translater

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.wafflestudio.snuboard.data.retrofit.dto.NoticeDto
import com.wafflestudio.snuboard.data.retrofit.dto.NoticeFileDto
import com.wafflestudio.snuboard.data.retrofit.dto.NoticeListDto
import com.wafflestudio.snuboard.di.SharedPreferenceConst
import com.wafflestudio.snuboard.domain.model.DepartmentColor
import com.wafflestudio.snuboard.domain.model.Notice
import com.wafflestudio.snuboard.domain.model.NoticeFile
import com.wafflestudio.snuboard.domain.model.NoticeList
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NoticeMapper
@Inject
constructor(@ApplicationContext appContext: Context) {

    private val pref = PreferenceManager.getDefaultSharedPreferences(
            appContext
    )

    fun mapFromListNoticeDto(dto: NoticeListDto): NoticeList {
        val notices = dto.notices.map {
            mapFromNoticeDto(it)
        }

        return NoticeList(
                notices,
                dto.nextCursor
        )
    }

    fun mapFromNoticeDto(dto: NoticeDto): Notice {

        val files = dto.files.map { mapToFileFromFileDto(it) }

        val preferenceKey = SharedPreferenceConst.getDepartmentKey(dto.departmentId)
        var departmentColorId = pref.getInt(preferenceKey, -1)
        if (departmentColorId == -1) {
            pref.edit {
                putInt(preferenceKey, DepartmentColor.POMEGRANATE.colorId)
            }
            departmentColorId = DepartmentColor.POMEGRANATE.colorId
        }
        val departmentColor = DepartmentColor.fromColorId(departmentColorId)

        val dateFormatUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS.SSS'Z'", Locale.KOREA)
        dateFormatUTC.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormatUTC.parse(dto.createdAt)
        val shortDateFormatKST = SimpleDateFormat("yy/MM/dd", Locale.KOREA)
        val longDateFormatKST = SimpleDateFormat("yy/MM/dd HH:mm", Locale.KOREA)
        shortDateFormatKST.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        longDateFormatKST.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val createdAtYYMMDD = shortDateFormatKST.format(date!!)
        val createdAtYYMMDDhhmm = longDateFormatKST.format(date)

        return Notice(
                dto.id,
                dto.departmentName,
                dto.departmentId,
                departmentColor!!,
                dto.title,
                dto.preview,
                dto.content,
                createdAtYYMMDD,
                createdAtYYMMDDhhmm,
                dto.tags,
                dto.isPinned,
                dto.link,
                files,
                dto.isScrapped

        )
    }

    private fun mapToFileFromFileDto(dto: NoticeFileDto): NoticeFile {
        return NoticeFile(dto.id, dto.name, dto.link)
    }
}