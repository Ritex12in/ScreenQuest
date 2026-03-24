package com.screenquest.ui.stats

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.screenquest.R
import com.screenquest.domain.model.AppUsage
import com.screenquest.ui.theme.Amber
import com.screenquest.ui.theme.Green
import com.screenquest.ui.theme.GreenDim
import com.screenquest.ui.theme.Red
import com.screenquest.ui.theme.RedDim
import com.screenquest.ui.theme.SurfaceElevated
import com.screenquest.ui.theme.SurfaceVariant
import com.screenquest.ui.theme.TextSecondary
import com.screenquest.ui.theme.XPTrack

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
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
        StatsHeader()
        SuggestionCard(message = state.suggestionMessage)
        WeeklySummaryCard(
            averageMinutes = state.averageDailyMinutes,
            improvementPercent = state.improvementPercent
        )
        WeeklyBreakdownCard(apps = state.weeklyUsage)
        TopAppsThisWeekCard(apps = state.topApps)
    }
}

// --- Header ---

@Composable
private fun StatsHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.outline_bar_chart_24),
            contentDescription = null,
            tint = Amber,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = "Stats",
            style = MaterialTheme.typography.displayMedium
        )
    }
}

// --- Suggestion Card ---

@Composable
private fun SuggestionCard(message: String) {
    if (message.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.outline_stacked_line_chart_24),
            contentDescription = null,
            tint = Amber,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

// --- Weekly Summary Card ---

@Composable
private fun WeeklySummaryCard(
    averageMinutes: Int,
    improvementPercent: Int
) {
    val hours = averageMinutes / 60
    val minutes = averageMinutes % 60
    val isImproved = improvementPercent > 0
    val improvementColor = if (isImproved) Green else Red
    val improvementBg = if (isImproved) GreenDim else RedDim
    val sign = if (isImproved) "-" else "+"
    val absPercent = kotlin.math.abs(improvementPercent)

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
                text = "7-Day Average",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = "per day",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        if (absPercent > 0) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(improvementBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        if (isImproved) R.drawable.outline_north_east_24
                        else R.drawable.outline_south_west_24
                    ),
                    contentDescription = null,
                    tint = improvementColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$sign$absPercent%",
                    style = MaterialTheme.typography.titleMedium,
                    color = improvementColor
                )
                Text(
                    text = "vs last week",
                    style = MaterialTheme.typography.labelSmall,
                    color = improvementColor
                )
            }
        }
    }
}

// --- Weekly Breakdown Card ---

@Composable
private fun WeeklyBreakdownCard(apps: List<AppUsage>) {
    // Group by day and sum minutes per day
    val dailyTotals = apps
        .groupBy { it.dateEpochDay }
        .map { (epochDay, records) ->
            epochDay to records.sumOf { it.totalMinutesUsed }
        }
        .sortedBy { it.first }

    if (dailyTotals.isEmpty()) return

    val maxMinutes = dailyTotals.maxOf { it.second }.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(20.dp)
    ) {
        Text(
            text = "Daily Breakdown",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bar chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            dailyTotals.forEach { (epochDay, minutes) ->
                DayBar(
                    epochDay = epochDay,
                    minutes = minutes,
                    maxMinutes = maxMinutes,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DayBar(
    epochDay: Long,
    minutes: Int,
    maxMinutes: Int,
    modifier: Modifier = Modifier
) {
    val fraction = minutes.toFloat() / maxMinutes.toFloat()
    val animated by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(700),
        label = "bar_$epochDay"
    )
    val date = java.time.LocalDate.ofEpochDay(epochDay)
    val dayLabel = date.dayOfWeek.name.take(3)
        .lowercase()
        .replaceFirstChar { it.uppercase() }

    Column(
        modifier = modifier.padding(horizontal = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Height label
        if (minutes > 0) {
            val h = minutes / 60
            val m = minutes % 60
            Text(
                text = if (h > 0) "${h}h" else "${m}m",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((100 * animated).dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(if (fraction > 0.8f) Red else Amber)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = dayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

// --- Top Apps This Week ---

@Composable
private fun TopAppsThisWeekCard(apps: List<AppUsage>) {
    if (apps.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariant)
            .padding(20.dp)
    ) {
        Text(
            text = "Top Apps This Week",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        val maxMinutes = apps.maxOf { it.totalMinutesUsed }.coerceAtLeast(1)

        apps.forEachIndexed { index, app ->
            WeeklyAppRow(
                rank = index + 1,
                app = app,
                maxMinutes = maxMinutes
            )
            if (index < apps.lastIndex) {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun WeeklyAppRow(
    rank: Int,
    app: AppUsage,
    maxMinutes: Int
) {
    val fraction = app.totalMinutesUsed.toFloat() / maxMinutes.toFloat()
    val animated by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(600),
        label = "weekly_app_${app.packageName}"
    )
    val hours = app.totalMinutesUsed / 60
    val minutes = app.totalMinutesUsed % 60
    val timeLabel = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Rank number
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rank",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
        }

        Column(modifier = Modifier.weight(1f)) {
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
                color = when {
                    fraction > 0.8f -> Red
                    fraction > 0.5f -> Amber
                    else            -> Green
                },
                trackColor = XPTrack,
                strokeCap = StrokeCap.Round
            )
        }
    }
}