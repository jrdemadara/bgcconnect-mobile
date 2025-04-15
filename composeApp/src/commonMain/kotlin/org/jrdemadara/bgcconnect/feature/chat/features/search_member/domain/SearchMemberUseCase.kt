package org.jrdemadara.bgcconnect.feature.chat.features.search_member.domain

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.data.MemberDto
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.domain.SearchMemberRepository

class SearchMemberUseCase(
    private val repository: SearchMemberRepository
) {
    suspend operator fun invoke(key: String, token: String): List<MemberDto> {
        try {
            // Call the repository to fetch the members based on the key
            val members = repository.searchMember(key, token)

            // Check if the list is empty (if needed)
            if (members.isEmpty()) {
                throw Exception("No members found for the provided keyword.")
            }

            // Return the fetched members
            return members
        } catch (e: Exception) {
            // Catch any errors and throw a more meaningful exception
            println("Errrrooooor $e")
            throw Exception("Error fetching member data: ${e.message}")
        }
    }
}