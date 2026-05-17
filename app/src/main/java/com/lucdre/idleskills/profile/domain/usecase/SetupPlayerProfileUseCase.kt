package com.lucdre.idleskills.profile.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import javax.inject.Inject

/**
 * Use case for setting up the player profile at the start of the game.
 *
 * @property prestigeRepository The repository for prestige data (which stores profile for now).
 */
class SetupPlayerProfileUseCase @Inject constructor(
    private val prestigeRepository: PrestigeRepositoryInterface,
) {
    /**
     * Sets up the player profile with a username and favorite skill.
     *
     * @param username The player's chosen username.
     * @param favoriteSkill The player's favorite skill.
     * @return True if setup was successful.
     */
    suspend operator fun invoke(username: String, favoriteSkill: String): Boolean {
        if (username.isBlank() || favoriteSkill.isBlank()) return false

        val currentPrestige = prestigeRepository.getPrestige()
        
        val updatedProfile = currentPrestige.playerProfile.copy(
            username = username,
            favoriteSkill = favoriteSkill
        )

        val updatedPrestige = currentPrestige.copy(
            playerProfile = updatedProfile
        )

        prestigeRepository.updatePrestige(updatedPrestige)
        return true
    }
}
