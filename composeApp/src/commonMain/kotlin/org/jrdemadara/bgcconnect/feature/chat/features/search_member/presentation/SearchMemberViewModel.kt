package org.jrdemadara.bgcconnect.feature.chat.features.search_member.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.data.MemberDto
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.domain.SearchMemberUseCase

sealed class SearchMemberState {
    object Idle : SearchMemberState()
    object Loading : SearchMemberState()
    data class Success(val members: List<MemberDto>) : SearchMemberState() // Return the list of members
    data class Error(val error: String) : SearchMemberState()
}

@OptIn(FlowPreview::class)
class SearchMemberViewModel(
    private val searchMemberUseCase: SearchMemberUseCase,
) : ViewModel() {
    private val settings: Settings = Settings()
    val token = settings.getString("auth_token", defaultValue = "")
    private val _state = MutableStateFlow<SearchMemberState>(SearchMemberState.Idle)
    val state = _state.asStateFlow()

    private val _members = MutableStateFlow<List<MemberDto>>(emptyList())
    val members = _members.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(500) // ⏳ wait 500ms after last keystroke
                .filter { it.isNotBlank() }
                .collectLatest { key ->
                    performSearch(key)
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    private suspend fun performSearch(key: String) {
        _state.value = SearchMemberState.Loading
        try {
            val list = searchMemberUseCase(key, token)
            _members.value = list
            _state.value = SearchMemberState.Success(list)
        } catch (e: Exception) {
            _state.value = SearchMemberState.Error(e.message ?: "Unknown error")
        }
    }
}