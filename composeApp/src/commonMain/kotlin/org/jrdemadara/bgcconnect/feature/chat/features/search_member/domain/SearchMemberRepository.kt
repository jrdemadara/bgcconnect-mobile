package org.jrdemadara.bgcconnect.feature.chat.features.search_member.domain

import org.jrdemadara.bgcconnect.feature.chat.features.search_member.data.MemberDto

interface SearchMemberRepository {
    suspend fun searchMember(key: String, token: String): List<MemberDto>
}