package com.screenquest.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.screenquest.R
import com.screenquest.domain.model.Player
import com.screenquest.domain.model.XPTable
import com.screenquest.ui.theme.Amber
import com.screenquest.ui.theme.AmberDim
import com.screenquest.ui.theme.Green
import com.screenquest.ui.theme.GreenDim
import com.screenquest.ui.theme.Blue
import com.screenquest.ui.theme.BlueDim
import com.screenquest.ui.theme.SurfaceElevated
import com.screenquest.ui.theme.SurfaceVariant
import com.screenquest.ui.theme.TextSecondary
import com.screenquest.ui.theme.XPFill
import com.screenquest.ui.theme.XPTrack

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ProfileHeader(
            player = state.player,
            levelTitle = state.levelTitle
        )
        LevelProgressCard(
            player = state.player,
            progressFraction = state.progressFraction,
            nextLevelXP = state.nextLevelXP
        )
        StatsGridCard(player = state.player)
        MilestoneCard(player = state.player)
    }
}

// --- Header ---

@Composable
private fun ProfileHeader(
    player: Player?,
    levelTitle: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar placeholder — replace with your vector or image
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_person_24),
                contentDescription = null,
                tint = Amber,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = player?.nickname ?: "Player",
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = levelTitle.ifEmpty { "Phone Slave" },
            style = MaterialTheme.typography.bodyLarge,
            color = Amber,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Level ${player?.currentLevel ?: 1}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// --- Level Progress Card ---

@Composable
private fun LevelProgressCard(
    player: Player?,
    progressFraction: Float,
    nextLevelXP: Int
) {
    val animated by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(900),
        label = "level_progress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progress to Level ${(player?.currentLevel ?: 1) + 1}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${player?.totalXP ?: 0} XP",
                style = MaterialTheme.typography.titleMedium,
                color = Amber
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { animated },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = XPFill,
            trackColor = XPTrack,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${player?.xpProgress ?: 0} / ${player?.xpNeededToLevelUp ?: 100} XP",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Text(
                text = "Next: ${XPTable.levelTitle((player?.currentLevel ?: 1) + 1)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

// --- Stats Grid ---

@Composable
private fun StatsGridCard(player: Player?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(20.dp)
    ) {
        Text(
            text = "Lifetime Stats",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCell(
                iconRes = R.drawable.outline_calendar_check_24,
                label = "Best Streak",
                value = "${player?.longestStreak ?: 0} days",
                tint = Amber,
                modifier = Modifier.weight(1f)
            )
            StatCell(
                iconRes = R.drawable.outline_calendar_check_24,
                label = "Current Streak",
                value = "${player?.currentStreak ?: 0} days",
                tint = Green,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val focusHours = (player?.totalFocusMinutes ?: 0) / 60
            val focusMinutes = (player?.totalFocusMinutes ?: 0) % 60
            StatCell(
                iconRes = R.drawable.outline_timer_24,
                label = "Focus Time",
                value = if (focusHours > 0) "${focusHours}h ${focusMinutes}m"
                else "${focusMinutes}m",
                tint = Blue,
                modifier = Modifier.weight(1f)
            )
            StatCell(
                iconRes = R.drawable.outline_kid_star_24,
                label = "Total XP",
                value = "${player?.totalXP ?: 0}",
                tint = Amber,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCell(
    iconRes: Int,
    label: String,
    value: String,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val dimColor = when (tint) {
        Amber -> AmberDim
        Green -> GreenDim
        Blue  -> BlueDim
        else  -> SurfaceElevated
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(dimColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = tint
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

// --- Milestone Card ---

@Composable
private fun MilestoneCard(player: Player?) {
    val milestones = buildMilestones(player)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(20.dp)
    ) {
        Text(
            text = "Milestones",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        milestones.forEachIndexed { index, milestone ->
            MilestoneRow(milestone = milestone)
            if (index < milestones.lastIndex) {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun MilestoneRow(milestone: Milestone) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (milestone.achieved) AmberDim else SurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_rewarded_ads_24),
                contentDescription = null,
                tint = if (milestone.achieved) Amber else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = milestone.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (milestone.achieved) MaterialTheme.colorScheme.onSurface
                else TextSecondary
            )
            Text(
                text = milestone.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        if (milestone.achieved) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_check_24),
                contentDescription = "Achieved",
                tint = Green,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// --- Milestones Data ---

data class Milestone(
    val title: String,
    val description: String,
    val achieved: Boolean
)

private fun buildMilestones(player: Player?): List<Milestone> {
    val streak = player?.currentStreak ?: 0
    val longestStreak = player?.longestStreak ?: 0
    val totalXP = player?.totalXP ?: 0
    val level = player?.currentLevel ?: 1

    return listOf(
        Milestone(
            title = "First Step",
            description = "Open the app for the first time",
            achieved = true
        ),
        Milestone(
            title = "3-Day Streak",
            description = "Maintain your goal for 3 days straight",
            achieved = longestStreak >= 3
        ),
        Milestone(
            title = "Week Warrior",
            description = "Maintain your goal for 7 days straight",
            achieved = longestStreak >= 7
        ),
        Milestone(
            title = "Level 5",
            description = "Reach level 5",
            achieved = level >= 5
        ),
        Milestone(
            title = "XP Hunter",
            description = "Earn 1000 total XP",
            achieved = totalXP >= 1000
        ),
        Milestone(
            title = "Month Master",
            description = "Maintain your goal for 30 days straight",
            achieved = longestStreak >= 30
        ),
        Milestone(
            title = "Digital Monk",
            description = "Reach the highest level",
            achieved = level >= 10
        )
    )
}