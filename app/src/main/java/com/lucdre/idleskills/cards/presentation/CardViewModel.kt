package com.lucdre.idleskills.cards.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the card screen.
 *
 * @property isLoading Whether the skill tree is being loaded.
 * @property cardsBySkill A map of cards per skill.
 */
data class CardUiState(
    val cardsBySkill: Map<String, List<Card>> = emptyMap(),
    val isLoading: Boolean = false
)

/**
 * ViewModel for handling skill tree interactions.
 *
 * @property cardRepository Repository for card data.
 */
@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardRepository: CardRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardUiState(isLoading = true))
    val uiState: StateFlow<CardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            cardRepository.getOwnedCards().collect { cards ->
                val grouped = cards.groupBy { it.type.skillName }
                _uiState.update { it.copy(
                    cardsBySkill = grouped,
                    isLoading = false
                ) }
            }
        }
    }
}
