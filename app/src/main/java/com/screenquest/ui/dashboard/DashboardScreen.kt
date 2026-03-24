package com.screenquest.ui.dashboard

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.screenquest.R
import com.screenquest.domain.model.AppUsage
import com.screenquest.domain.model.PetMood
import com.screenquest.domain.model.PetState
import com.screenquest.domain.model.Player
import com.screenquest.domain.model.XPTable
import com.screenquest.ui.theme.Amber
import com.screenquest.ui.theme.AmberDim
import com.screenquest.ui.theme.MoodHappy
import com.screenquest.ui.theme.MoodNeutral
import com.screenquest.ui.theme.MoodSuffering
import com.screenquest.ui.theme.MoodThriving
import com.screenquest.ui.theme.MoodWorried
import com.screenquest.ui.theme.SurfaceElevated
import com.screenquest.ui.theme.SurfaceVariant
import com.screenquest.ui.theme.TextSecondary
import com.screenquest.ui.theme.XPFill
import com.screenquest.ui.theme.XPTrack

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
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
        DashboardHeader(player = state.player)
        XPProgressCard(player = state.player)
        PetCard(petState = state.petState)
        TodaySummaryCard(totalMinutes = state.totalMinutesToday)
        TopAppsCard(apps = state.topApps)
    }
}

// --- Header ---

@Composable
private fun DashboardHeader(player: Player?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = player?.nickname ?: "Player",
                style = MaterialTheme.typography.displayMedium
            )
        }
        StreakBadge(streak = player?.currentStreak ?: 0)
    }
}

@Composable
private fun StreakBadge(streak: Int) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AmberDim)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.outline_calendar_check_24),
            contentDescription = null,
            tint = Amber,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$streak",
            style = MaterialTheme.typography.titleLarge,
            color = Amber
        )
        Text(
            text = "streak",
            style = MaterialTheme.typography.labelSmall,
            color = Amber
        )
    }
}

// --- XP Progress Card ---

@Composable
private fun XPProgressCard(player: Player?) {
    val progress by animateFloatAsState(
        targetValue = player?.levelProgressFraction ?: 0f,
        animationSpec = tween(durationMillis = 800),
        label = "xp_progress"
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
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "Level ${player?.currentLevel ?: 1}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = XPTable.levelTitle(player?.currentLevel ?: 1),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Text(
                text = "${player?.totalXP ?: 0} XP",
                style = MaterialTheme.typography.titleMedium,
                color = Amber
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = XPFill,
            trackColor = XPTrack,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(8.dp))

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

// --- Pet Card ---

@Composable
private fun PetCard(petState: PetState?) {
    val hp = petState?.healthPoints ?: 100
    val mood = petState?.mood ?: PetMood.THRIVING
    val moodColor = moodColor(mood)

    val animatedHP by animateFloatAsState(
        targetValue = petState?.healthFraction ?: 1f,
        animationSpec = tween(durationMillis = 800),
        label = "pet_hp"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_pets_24),
                contentDescription = null,
                tint = moodColor,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Companion",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = petState?.moodLabel ?: "Thriving",
                    style = MaterialTheme.typography.bodySmall,
                    color = moodColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { animatedHP },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = moodColor,
                trackColor = XPTrack,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$hp / 100 HP",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

private fun moodColor(mood: PetMood): Color = when (mood) {
    PetMood.THRIVING  -> MoodThriving
    PetMood.HAPPY     -> MoodHappy
    PetMood.NEUTRAL   -> MoodNeutral
    PetMood.WORRIED   -> MoodWorried
    PetMood.SUFFERING -> MoodSuffering
}

// --- Today Summary Card ---

@Composable
private fun TodaySummaryCard(totalMinutes: Int) {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Today's Screen Time",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m",
                style = MaterialTheme.typography.displayMedium
            )
        }

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.outline_timer_24),
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(32.dp)
        )
    }
}

// --- Top Apps Card ---

@Composable
private fun TopAppsCard(apps: List<AppUsage>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(20.dp)
    ) {
        Text(
            text = "Most Used Today",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (apps.isEmpty()) {
            Text(
                text = "No usage data yet",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        } else {
            val maxMinutes = apps.maxOf { it.totalMinutesUsed }.coerceAtLeast(1)
            apps.forEach { app ->
                AppUsageRow(app = app, maxMinutes = maxMinutes)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AppUsageRow(app: AppUsage, maxMinutes: Int) {
    val fraction = app.totalMinutesUsed.toFloat() / maxMinutes.toFloat()
    val animated by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(600),
        label = "app_bar_${app.packageName}"
    )
    val hours = app.totalMinutesUsed / 60
    val minutes = app.totalMinutesUsed % 60
    val timeLabel = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = app.appName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = timeLabel,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { animated },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Amber,
            trackColor = XPTrack,
            strokeCap = StrokeCap.Round
        )
    }
}