package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.skills.domain.skill.SkillType

/**
 * Unique identifiers for all training methods in the game.
 * Each entry has a stable ID for persistence.
 *
 * @property id Stable string ID for database persistence.
 * @property skill The skill this method belongs to.
 * @property displayName Human-readable name shown in the UI.
 */
enum class TrainingMethodType(
    val id: String,
    val skill: SkillType,
    val displayName: String
) {
    // Woodcutting
    WC_TREE("wc_tree", SkillType.WOODCUTTING, "Tree"),
    WC_OAK("wc_oak", SkillType.WOODCUTTING, "Oak Tree"),
    WC_WILLOW("wc_willow", SkillType.WOODCUTTING, "Willow Tree"),
    WC_MAPLE("wc_maple", SkillType.WOODCUTTING, "Maple Tree"),
    WC_YEW("wc_yew", SkillType.WOODCUTTING, "Yew Tree"),
    WC_MAGIC("wc_magic", SkillType.WOODCUTTING, "Magic Tree"),
    WC_CHEAT("wc_cheat", SkillType.WOODCUTTING, "Cheat Tree"),

    // Mining
    MN_COPPER("mn_copper", SkillType.MINING, "Copper Rock"),
    MN_TIN("mn_tin", SkillType.MINING, "Tin Rock"),
    MN_IRON("mn_iron", SkillType.MINING, "Iron Rock"),
    MN_COAL("mn_coal", SkillType.MINING, "Coal Rock"),
    MN_MITHRIL("mn_mithril", SkillType.MINING, "Mithril Rock"),
    MN_ADAMANT("mn_adamant", SkillType.MINING, "Adamant Rock"),
    MN_RUNE("mn_rune", SkillType.MINING, "Rune Rock"),
    MN_DRAGON("mn_dragon", SkillType.MINING, "Dragon Rock"),
    MN_CHEAT("mn_cheat", SkillType.MINING, "Cheat Rock"),

    // Fishing
    FS_SHRIMP("fs_shrimp", SkillType.FISHING, "Shrimp"),
    FS_SARDINE("fs_sardine", SkillType.FISHING, "Sardine"),
    FS_ANCHOVY("fs_ancho", SkillType.FISHING, "Anchovy"),
    FS_TROUT("fs_trout", SkillType.FISHING, "Trout"),
    FS_SALMON("fs_salmon", SkillType.FISHING, "Salmon"),
    FS_TUNA("fs_tuna", SkillType.FISHING, "Tuna"),
    FS_LOBSTER("fs_lobster", SkillType.FISHING, "Lobster"),
    FS_SWORDFISH("fs_swordfish", SkillType.FISHING, "Swordfish"),
    FS_SHARK("fs_shark", SkillType.FISHING, "Shark"),
    FS_CHEAT("fs_cheat", SkillType.FISHING, "Cheat Fish");

    companion object {
        fun fromId(id: String): TrainingMethodType? {
            return entries.find { it.id == id }
        }
    }
}
