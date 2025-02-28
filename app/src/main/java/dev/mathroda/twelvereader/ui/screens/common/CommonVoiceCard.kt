package dev.mathroda.twelvereader.ui.screens.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mathroda.twelvereader.domain.Voice

@Composable
fun CommonVoiceCard(
    voice: Voice,
    colors: List<Color>,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    updateChosenVoice: (Voice) -> Unit
) {
    val name = remember(voice.name) {
        voice.name.split(" ")[0]
    }

    val age = remember(voice.age) {
        voice.age.replace("_", "-")
    }

    Card(
        modifier = modifier,
        onClick = { updateChosenVoice(voice) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 0.dp
        ),
        border = if (isSelected) BorderStroke(2.dp, Color.Black) else BorderStroke(1.dp, Color.LightGray),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AbstractCircleArt(
                colors = colors
            )

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "$age ${voice.accent} ${voice.gender}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun CommonVoiceRow(
    voice: Voice,
    colors: List<Color>,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    onPreview: (String) -> Unit,
    onFavoriteVoice: (Voice) -> Unit
) {
    val name = remember(voice.name) {
        voice.name.split(" ")[0]
    }

    val age = remember(voice.age) {
        voice.age.replace("_", "-")
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AbstractCircleArt(
            modifier = Modifier.size(54.dp),
            colors = colors
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$age ${voice.accent} ${voice.gender}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.typography.bodySmall.color.copy(0.6f)
                    ),
                )
            }

            Box(
                modifier = Modifier
                    .background(Color.LightGray.copy(0.4f), RoundedCornerShape(8.dp))
                    .bouncingClickable { onPreview(voice.previewUrl) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Headphones,
                        contentDescription = "Headset"
                    )

                    Text(
                        text = "Preview",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { onFavoriteVoice(voice) }
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun AbstractCircleArt(
    colors: List<Color>,
    modifier: Modifier = Modifier.size(48.dp)
) {

    val randomGradient = remember(colors) {
        Brush.sweepGradient(
            colors = colors.shuffled(),
            center = Offset(100f, 100f)
        )
    }

    Canvas(
        modifier = modifier.clip(CircleShape)
    ) {
        // Background smooth gradient
        drawCircle(
            brush = randomGradient,
            radius = size.minDimension / 2f,
            center = center
        )

        // Add distortions using path
        val path = Path().apply {
            moveTo(center.x, center.y)
            cubicTo(size.width * 0.3f, size.height * 0.1f, size.width * 0.7f, size.height * 0.9f, center.x, center.y)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.radialGradient(
                colors = colors,
                center = center,
                radius = size.minDimension * 0.5f
            ),
            style = Fill
        )
    }
}

@Composable
fun CommonVoiceRowShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShimmerCircle(modifier = Modifier.size(54.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column {
                ShimmerBox(modifier = Modifier.size(100.dp, 18.dp))
                ShimmerBox(modifier = Modifier.size(150.dp, 14.dp))
            }

            Box(
                modifier = Modifier
                    .background(Color.LightGray.copy(0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerBox(modifier = Modifier.size(60.dp, 16.dp))
                }
            }
        }
    }
}

@Composable
fun CommonVoiceCardShimmer(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 0.dp
        ),
        border = BorderStroke(1.dp, Color.LightGray),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShimmerAbstractCircleArt()

            ShimmerBox(modifier = Modifier.size(80.dp, 16.dp)) // Simulates name
            ShimmerBox(modifier = Modifier.size(120.dp, 14.dp)) // Simulates age, accent, gender
        }
    }
}

@Composable
fun ShimmerAbstractCircleArt(modifier: Modifier = Modifier.size(48.dp)) {
    val shimmerBrush = rememberShimmerBrush()

    Canvas(
        modifier = modifier
            .clip(CircleShape)
            .background(shimmerBrush)
    ) {}
}

@Composable
fun ShimmerBox(modifier: Modifier) {
    val shimmerBrush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(shimmerBrush)
    )
}

@Composable
fun ShimmerCircle(modifier: Modifier) {
    val shimmerBrush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(shimmerBrush)
    )
}

@Composable
fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by transition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )

    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0E0E0), // Light gray
            Color(0xFFF5F5F5), // Almost white
            Color(0xFFE0E0E0)  // Light gray again
        ),
        start = Offset(shimmerOffset, shimmerOffset),
        end = Offset(shimmerOffset + 400f, shimmerOffset + 400f)
    )
}