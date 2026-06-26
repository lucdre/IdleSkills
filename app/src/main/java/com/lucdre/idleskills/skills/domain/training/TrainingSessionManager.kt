package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.core.domain.OfflineProgressResult
import com.lucdre.idleskills.core.domain.SessionRepositoryInterface
import com.lucdre.idleskills.core.domain.usecase.CalculateOfflineProgressUseCase
import com.lucdre.idleskills.region.domain.usecase.GetVisibleSkillsUseCase
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.training.usecase.GetAvailableTrainingMethodsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager responsible for training sessions.
 *
 * @property trainingService The service managing active training state.
 * @property calculateOfflineProgressUseCase Use case to calculate progress.
 * @property getVisibleSkillsUseCase Use case to determine which skills are currently available.
 * @property getAvailableTrainingMethodsUseCase Use case to fetch methods for a specific skill.
 * @property sessionRepository Repository for accessing session-level data.
 */
@Singleton
class TrainingSessionManager @Inject constructor(
    private val trainingService: TrainingService,
    private val calculateOfflineProgressUseCase: CalculateOfflineProgressUseCase,
    private val getVisibleSkillsUseCase: GetVisibleSkillsUseCase,
    private val getAvailableTrainingMethodsUseCase: GetAvailableTrainingMethodsUseCase,
    private val sessionRepository: SessionRepositoryInterface
) {
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _offlineProgress = MutableStateFlow<OfflineProgressResult?>(null)

    val offlineProgress: StateFlow<OfflineProgressResult?> = _offlineProgress.asStateFlow()

    /**
     * Initializes the training session.
     *
     * - Calculates and stores offline progress.
     * - Retrieves and resumes the last active training session if it exists.
     */
    fun initializeSession() {
        managerScope.launch {
            calculateOfflineProgressUseCase()?.let { result ->
                _offlineProgress.value = result
            }

            val activeTraining = sessionRepository.observeActiveTraining().first()
            if (activeTraining != null) {
                val skills = getVisibleSkillsUseCase()
                val skill = skills.find { it.name == activeTraining.skillName }
                if (skill != null) {
                    val methods = getAvailableTrainingMethodsUseCase(skill)
                    val method = methods.find { it.name == activeTraining.methodName }
                    if (method != null) {
                        trainingService.startTraining(skill, method)
                    }
                }
            }
        }
    }

    /**
     * Toggles training for a specific skill and method.
     *
     * @param skill The [Skill] to train.
     * @param method The [TrainingMethod] to use.
     */
    fun toggleTraining(skill: Skill, method: TrainingMethod) {
        val currentState = trainingService.trainingState.value
        if (currentState.activeSkillName == skill.name && currentState.activeMethod?.name == method.name) {
            trainingService.stopTraining()
        } else {
            trainingService.startTraining(skill, method)
        }
    }

    fun dismissOfflineProgress() {
        _offlineProgress.value = null
    }
}
