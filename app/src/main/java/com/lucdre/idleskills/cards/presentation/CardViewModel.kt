package com.lucdre.idleskills.cards.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardCalculator
import com.lucdre.idleskills.cards.domain.usecase.GetOwnedCardsUseCase
import com.lucdre.idleskills.cards.domain.usecase.UpgradeCardUseCase
import com.lucdre.idleskills.inventory.domain.ItemMetadata
import com.lucdre.idleskills.inventory.domain.ItemRegistry
import com.lucdre.idleskills.inventory.domain.usecase.GetInventoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * One-time UI effects for the card screen.
 */
sealed class CardUiEffect {
    data class ShowMessage(val message: String) : CardUiEffect()
    data object UpgradeSuccess : CardUiEffect()
}

/**
 * Representation of an upgrade requirement.
 *
 * @property requiredQuantity The amount needed for the upgrade.
 * @property ownedQuantity The amount currently in the player's inventory.
 * @property metadata Display information for the item.
 */
data class CardRequirementUiState(
    val requiredQuantity: Int,
    val ownedQuantity: Int,
    val metadata: ItemMetadata
)

/**
 * UI state for an individual card item.
 */
data class CardItemUiState(
    val card: Card,
    val canUpgrade: Boolean,
    val nextLevelBonus: Float,
    val requirements: List<CardRequirementUiState> = emptyList()
)

/**
 * UI state for the card screen.
 *
 * @property cardsByRarity A map of cards per rarity.
 * @property isLoading Whether the skill tree is being loaded.
 */
data class CardUiState(
    val cardsByRarity: Map<String, List<CardItemUiState>> = emptyMap(),
    val isLoading: Boolean = false
)

/**
 * ViewModel for handling skill tree interactions.
 *
 * @property getOwnedCardsUseCase For observing cards.
 * @property upgradeCardUseCase For upgrading cards.
 * @property cardCalculator For card-related calculations.
 * @property getInventoryUseCase For observing mapped inventory resources.
 * @property itemRegistry For looking up item metadata.
 */
@HiltViewModel
class CardViewModel @Inject constructor(
    private val getOwnedCardsUseCase: GetOwnedCardsUseCase,
    private val upgradeCardUseCase: UpgradeCardUseCase,
    private val cardCalculator: CardCalculator,
    private val getInventoryUseCase: GetInventoryUseCase,
    private val itemRegistry: ItemRegistry
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardUiState(isLoading = true))
    val uiState: StateFlow<CardUiState> = _uiState.asStateFlow()

    private val _uiEffects = MutableSharedFlow<CardUiEffect>()
    val uiEffects: SharedFlow<CardUiEffect> = _uiEffects.asSharedFlow()

    init {
        combine(
            getOwnedCardsUseCase(),
            getInventoryUseCase()
        ) { cards, inventory ->
            cards.map { card ->
                val requirements = cardCalculator.getUpgradeRequirements(card.type, card.level)
                CardItemUiState(
                    card = card,
                    canUpgrade = cardCalculator.canUpgrade(card, inventory),
                    nextLevelBonus = cardCalculator.getNextLevelBonus(card),
                    requirements = requirements.map { req ->
                        val owned = inventory.find { it.type == req.itemType }?.quantity ?: 0
                        CardRequirementUiState(
                            requiredQuantity = req.quantity,
                            ownedQuantity = owned,
                            metadata = itemRegistry.getMetadata(req.itemType)
                        )
                    }
                )
            }
            .groupBy { it.card.type.rarity }
        }
        .flowOn(Dispatchers.Default)
        .onEach { grouped ->
            _uiState.update { it.copy(
                cardsByRarity = grouped,
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
            upgradeCardUseCase(card)
                .onSuccess {
                    _uiEffects.emit(CardUiEffect.UpgradeSuccess)
                }
                .onFailure { error ->
                    _uiEffects.emit(CardUiEffect.ShowMessage(error.message ?: "Failed to upgrade card"))
                    android.util.Log.e("CardViewModel", "Failed to upgrade card: ${error.message}")
                }
        }
    }
}
