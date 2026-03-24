package com.screenquest.domain.model

enum class PetMood {
    THRIVING,    // hp 80-100
    HAPPY,       // hp 60-79
    NEUTRAL,     // hp 40-59
    WORRIED,     // hp 20-39
    SUFFERING    // hp 0-19
}

data class PetState(
    val healthPoints: Int  // 0 to 100
) {
    val mood: PetMood get() = when {
        healthPoints >= 80 -> PetMood.THRIVING
        healthPoints >= 60 -> PetMood.HAPPY
        healthPoints >= 40 -> PetMood.NEUTRAL
        healthPoints >= 20 -> PetMood.WORRIED
        else               -> PetMood.SUFFERING
    }

    val moodLabel: String get() = when (mood) {
        PetMood.THRIVING  -> "Thriving"
        PetMood.HAPPY     -> "Happy"
        PetMood.NEUTRAL   -> "Okay"
        PetMood.WORRIED   -> "Worried"
        PetMood.SUFFERING -> "Suffering"
    }

    val healthFraction: Float get() = healthPoints / 100f
}