package com.lucdre.idleskills.cards.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.usecase.GetOwnedCardsUseCase
import com.lucdre.idleskills.cards.domain.usecase.UpgradeCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
 * @property getOwnedCardsUseCase For observing cards.
 * @property upgradeCardUseCase For upgrading cards.
 */
@HiltViewModel
class CardViewModel @Inject constructor(
    private val getOwnedCardsUseCase: GetOwnedCardsUseCase,
    private val upgradeCardUseCase: UpgradeCardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardUiState(isLoading = true))
    val uiState: StateFlow<CardUiState> = _uiState.asStateFlow()

    init {
        getOwnedCardsUseCase()
            .map { cards ->
                cards.groupBy { it.type.skill.displayName }
            }
            .flowOn(Dispatchers.Default) // Offload grouping to background thread
            .onEach { grouped ->
                _uiState.update { it.copy(
                    cardsBySkill = grouped,
                    isLoading = false
                ) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Attempts to upgrade a card.
     *
     * @param card The card to upgrade.
     */
    fun upgradeCard(card: Card) {
        viewModelScope.launch {
            upgradeCardUseCase(card).onFailure { error ->
                // TODO: Handle error in UI (e.g., show a Snackbar)
                android.util.Log.e("CardViewModel", "Failed to upgrade card: ${error.message}")
            }
        }
    }
}
