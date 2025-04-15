package org.jrdemadara.bgcconnect.feature.chat.features.search_member.data

import org.jrdemadara.bgcconnect.feature.chat.features.search_member.domain.SearchMemberRepository

class SearchMemberRepositoryImpl(private val api: SearchMemberApi) : SearchMemberRepository {
    override suspend fun searchMember(key: String, token: String): List<MemberDto> {
        return api.searchMember(key, token)
    }
}