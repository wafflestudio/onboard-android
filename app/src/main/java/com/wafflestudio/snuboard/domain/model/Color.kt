package com.wafflestudio.snuboard.domain.model

import com.wafflestudio.snuboard.R
import java.io.Serializable

enum class DepartmentColor(val colorName: String, val colorId: Int) {
    BLACK("검정", R.color.black),
    SKY("하늘", R.color.sky),
    POMEGRANATE("석류", R.color.pomegranate),
    CITRUS("감귤", R.color.citrus),
    JADE("비취", R.color.jade),
    KOREAN_DAISY("들국", R.color.korean_daisy),
    PEA("완두", R.color.pea),
    MEDITERRANEAN("지중해", R.color.mediterranean),
    AMETHYST("자수정", R.color.amethyst),
    LAVENDER("라벤더", R.color.lavender),
    TAG_COLOR("태그", R.color.tag_default),
    TAG_SELECTED_COLOR("태그선택", R.color.tag_selected);

    companion object {
        fun fromColorId(colorId: Int): DepartmentColor? =
                values().find { it.colorId == colorId }
    }
}

enum class BasicColor(val colorId: Int) {
    FILTER_COLOR(R.color.filter_default),
    FILTER_ON_COLOR(R.color.filter_on);
}

data class Tag(val content: String, val color: DepartmentColor) : Serializable
