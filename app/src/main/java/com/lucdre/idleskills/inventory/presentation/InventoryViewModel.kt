package com.lucdre.idleskills.inventory.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.inventory.domain.Item
import com.lucdre.idleskills.inventory.domain.ItemRegistry
import com.lucdre.idleskills.inventory.domain.usecase.GetInventoryUseCase
import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.loot.domain.usecase.ObserveLootBoxCountUseCase
import com.lucdre.idleskills.loot.domain.usecase.OpenLootBoxUseCase
import com.lucdre.idleskills.skills.domain.skill.SkillType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the inventory and loot box management screen.
 *
 * @property inventoryItems The list of items currently in the player's inventory.
 * @property lootBoxes The list of loot boxes available for each skill.
 * @property lastRewards The rewards obtained from the most recently opened loot box.
 * @property isLoading Whether the inventory data is currently being loaded.
 */
@Immutable
data class InventoryUiState(
    val inventoryItems: List<Item> = emptyList(),
    val lootBoxes: List<LootBox> = emptyList(),
    val lastRewards: List<Item>? = null,
    val isLoading: Boolean = true
)

/**
 * ViewModel for managing the player's inventory items and loot box interaction.
 *
 * @property getInventoryUseCase Use case for observing inventory items.
 * @property observeLootBoxCountUseCase Use case for observing available loot boxes.
 * @property openLootBoxUseCase Use case for opening a loot box and receiving rewards.
 * @property itemRegistry Central registry for item metadata.
 */
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryUseCase: GetInventoryUseCase,
    private val observeLootBoxCountUseCase: ObserveLootBoxCountUseCase,
    private val openLootBoxUseCase: OpenLootBoxUseCase,
    private val itemRegistry: ItemRegistry
) : ViewModel() {

    private val _lastRewards = MutableStateFlow<List<Item>?>(null)

    val uiState: StateFlow<InventoryUiState> = combine(
        getInventoryUseCase(),
        observeLootBoxCountUseCase(),
        _lastRewards
    ) { inventory, loot, rewards ->
        InventoryUiState(
            inventoryItems = inventory,
            lootBoxes = loot,
            lastRewards = rewards,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryUiState()
    )

    fun onOpenBoxClick(skill: SkillType) {
        viewModelScope.launch {
            openLootBoxUseCase(skill).onSuccess { rewards ->
                _lastRewards.value = rewards.map { (type, quantity) ->
                    Item(
                        type = type,
                        quantity = quantity,
                        metadata = itemRegistry.getMetadata(type)
                    )
                }
            }
        }
    }

    fun clearRewards() {
        _lastRewards.value = null
    }
}
