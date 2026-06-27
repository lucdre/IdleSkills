package com.lucdre.idleskills.cards.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardCalculator
import com.lucdre.idleskills.cards.domain.usecase.GetOwnedCardsUseCase
import com.lucdre.idleskills.cards.domain.usecase.UpgradeCardUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for an individual card item.
 */
data class CardItemUiState(
    val card: Card,
    val upgradeRequirement: Int,
    val canUpgrade: Boolean,
    val nextLevelBonus: Float
)

/**
 * UI state for the card screen.
 *
 * @property cardsBySkill A map of cards per skill.
 * @property isLoading Whether the skill tree is being loaded.
 */
data class CardUiState(
    val cardsBySkill: Map<SkillType, List<CardItemUiState>> = emptyMap(),
    val isLoading: Boolean = false
)

/**
 * ViewModel for handling skill tree interactions.
 *
 * @property getOwnedCardsUseCase For observing cards.
 * @property upgradeCardUseCase For upgrading cards.
 * @property cardCalculator For card-related calculations.
 */
@HiltViewModel
class CardViewModel @Inject constructor(
    private val getOwnedCardsUseCase: GetOwnedCardsUseCase,
    private val upgradeCardUseCase: UpgradeCardUseCase,
    private val cardCalculator: CardCalculator
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardUiState(isLoading = true))
    val uiState: StateFlow<CardUiState> = _uiState.asStateFlow()

    init {
        getOwnedCardsUseCase()
            .map { cards ->
                cards.map { card ->
                    CardItemUiState(
                        card = card,
                        upgradeRequirement = cardCalculator.getUpgradeRequirement(card.level),
                        canUpgrade = cardCalculator.canUpgrade(card),
                        nextLevelBonus = cardCalculator.getNextLevelBonus(card)
                    )
                }
                .groupBy { it.card.type.skill }
            }
            .flowOn(Dispatchers.Default)
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
