package com.screenquest.ui.quests

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.screenquest.R
import com.screenquest.domain.model.Quest
import com.screenquest.domain.model.QuestDifficulty
import com.screenquest.domain.model.QuestType
import com.screenquest.ui.theme.Amber
import com.screenquest.ui.theme.AmberDim
import com.screenquest.ui.theme.Blue
import com.screenquest.ui.theme.BlueDim
import com.screenquest.ui.theme.Green
import com.screenquest.ui.theme.GreenDim
import com.screenquest.ui.theme.Red
import com.screenquest.ui.theme.RedDim
import com.screenquest.ui.theme.SurfaceElevated
import com.screenquest.ui.theme.SurfaceVariant
import com.screenquest.ui.theme.TextDisabled
import com.screenquest.ui.theme.TextSecondary

@Composable
fun QuestsScreen(
    viewModel: QuestsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        QuestsHeader()
        ActiveQuestsSection(
            quests = state.activeQuests,
            onComplete = viewModel::onQuestCompleted
        )
        if (state.completedQuests.isNotEmpty()) {
            CompletedQuestsSection(quests = state.completedQuests)
        }
    }
}

// --- Header ---

@Composable
private fun QuestsHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.outline_videogame_asset_24),
            contentDescription = null,
            tint = Amber,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = "Quests",
            style = MaterialTheme.typography.displayMedium
        )
    }
}

// --- Active Quests ---

@Composable
private fun ActiveQuestsSection(
    quests: List<Quest>,
    onComplete: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SectionLabel(text = "Active")

        Spacer(modifier = Modifier.height(8.dp))

        if (quests.isEmpty()) {
            EmptyState(message = "No active quests. Open the app tomorrow for new ones.")
        } else {
            quests.forEach { quest ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(),
                ) {
                    QuestCard(
                        quest = quest,
                        onComplete = { onComplete(quest.id) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// --- Completed Quests ---

@Composable
private fun CompletedQuestsSection(quests: List<Quest>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SectionLabel(text = "Completed")

        Spacer(modifier = Modifier.height(8.dp))

        quests.forEach { quest ->
            CompletedQuestCard(quest = quest)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// --- Quest Card ---

@Composable
private fun QuestCard(
    quest: Quest,
    onComplete: () -> Unit
) {
    val typeColor = questTypeColor(quest.type)
    val typeDimColor = questTypeDimColor(quest.type)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .border(
                width = 1.dp,
                color = typeDimColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuestTypeBadge(type = quest.type)
                DifficultyBadge(difficulty = quest.difficulty)
            }
            XPRewardLabel(xp = quest.xpReward)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = quest.title,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = quest.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        if (quest.targetMinutes > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Target: under ${quest.targetMinutes} min",
                style = MaterialTheme.typography.bodySmall,
                color = typeColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = typeDimColor,
                contentColor = typeColor
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_check_24),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mark Complete",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// --- Completed Quest Card ---

@Composable
private fun CompletedQuestCard(quest: Quest) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceElevated)
            .alpha(0.6f)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quest.title,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.LineThrough,
                color = TextDisabled
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = quest.completedDate?.toString() ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled
            )
        }
        XPRewardLabel(xp = quest.xpReward, dimmed = true)
    }
}

// --- Reusable Components ---

@Composable
private fun QuestTypeBadge(type: QuestType) {
    val color = questTypeColor(type)
    val dimColor = questTypeDimColor(type)
    val label = when (type) {
        QuestType.DAILY  -> "Daily"
        QuestType.WEEKLY -> "Weekly"
        QuestType.BOSS   -> "Boss"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(dimColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun DifficultyBadge(difficulty: QuestDifficulty) {
    val label = when (difficulty) {
        QuestDifficulty.EASY   -> "Easy"
        QuestDifficulty.MEDIUM -> "Medium"
        QuestDifficulty.HARD   -> "Hard"
    }
    val color = when (difficulty) {
        QuestDifficulty.EASY   -> Green
        QuestDifficulty.MEDIUM -> Amber
        QuestDifficulty.HARD   -> Red
    }
    val dimColor = when (difficulty) {
        QuestDifficulty.EASY   -> GreenDim
        QuestDifficulty.MEDIUM -> AmberDim
        QuestDifficulty.HARD   -> RedDim
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(dimColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun XPRewardLabel(xp: Int, dimmed: Boolean = false) {
    Text(
        text = "+$xp XP",
        style = MaterialTheme.typography.labelLarge,
        color = if (dimmed) TextDisabled else Amber
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary
    )
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

// --- Helpers ---

private fun questTypeColor(type: QuestType): Color = when (type) {
    QuestType.DAILY  -> Amber
    QuestType.WEEKLY -> Blue
    QuestType.BOSS   -> Red
}

private fun questTypeDimColor(type: QuestType): Color = when (type) {
    QuestType.DAILY  -> AmberDim
    QuestType.WEEKLY -> BlueDim
    QuestType.BOSS   -> RedDim
}