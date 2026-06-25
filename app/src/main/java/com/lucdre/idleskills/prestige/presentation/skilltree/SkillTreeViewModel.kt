package com.lucdre.idleskills.prestige.presentation.skilltree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.prestige.domain.usecase.GetAvailableSkillTreeNodesUseCase
import com.lucdre.idleskills.prestige.domain.usecase.PurchaseSkillTreeNodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling skill tree interactions.
 *
 * @property getAvailableSkillTreeNodesUseCase To get available nodes.
 * @property purchaseSkillTreeNodeUseCase To purchase nodes.
 * @property prestigeRepository Repository for prestige data.
 */
@HiltViewModel
class SkillTreeViewModel @Inject constructor(
    private val getAvailableSkillTreeNodesUseCase: GetAvailableSkillTreeNodesUseCase,
    private val purchaseSkillTreeNodeUseCase: PurchaseSkillTreeNodeUseCase,
    private val prestigeRepository: PrestigeRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillTreeUiState())
    val uiState: StateFlow<SkillTreeUiState> = _uiState.asStateFlow()

    init {
        // Start observing prestige changes immediately
        observePrestigeChanges()
    }

    /**
     * Observes prestige changes and updates the UI accordingly.
     */
    private fun observePrestigeChanges() {
        viewModelScope.launch {
            prestigeRepository.observePrestige().collect { prestige ->
                // Update points when prestige changes
                _uiState.value = _uiState.value.copy(
                    availablePoints = prestige.points.availablePrestigePoints
                )

                // Reload nodes if not currently loading
                if (!_uiState.value.isLoading) {
                    loadSkillTreeNodes()
                }
            }
        }
    }

    /**
     * Loads the skill tree nodes and current prestige points.
     */
    fun loadSkillTree() {
        loadSkillTreeNodes()
    }

    /**
     * Internal method to load skill tree nodes.
     */
    private fun loadSkillTreeNodes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val nodes = getAvailableSkillTreeNodesUseCase()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    nodes = nodes
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load skill tree: ${e.message}"
                )
            }
        }
    }

    /**
     * Attempts to purchase a skill tree node.
     *
     * @param nodeId The ID of the node to purchase.
     */
    fun purchaseNode(nodeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(errorMessage = null)

            try {
                val success = purchaseSkillTreeNodeUseCase(nodeId)

                if (success) {
                    // Reload skill tree to reflect changes
                    loadSkillTree()
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Cannot purchase this node. Check requirements and available points."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to purchase node: ${e.message}"
                )
            }
        }
    }
}

/**
 * UI state for the skill tree screen.
 *
 * @property isLoading Whether the skill tree is being loaded.
 * @property availablePoints Number of prestige points available to spend.
 * @property nodes Map of node availability information.
 * @property errorMessage Error message to display, if any.
 */
data class SkillTreeUiState(
    val isLoading: Boolean = false,
    val availablePoints: Int = 0,
    val nodes: Map<String, GetAvailableSkillTreeNodesUseCase.NodeAvailability> = emptyMap(),
    val errorMessage: String? = null,
)
