package org.jrdemadara.bgcconnect.feature.chat.features.search_member.data

import kotlinx.serialization.Serializable


@Serializable
data class SearchMemberResponse(
    val data: List<MemberDto>
)

@Serializable
data class MemberDto(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val avatar: String? = null
)