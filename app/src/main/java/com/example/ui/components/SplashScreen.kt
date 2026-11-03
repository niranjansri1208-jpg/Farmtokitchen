package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import kotlinx.coroutines.delay

@Composable
fun CinematicSplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationPhase by remember { mutableIntStateOf(1) } // 1: Seed, 2: Sprout, 3: Text, 4: Complete
    var typewriterCharCount by remember { mutableIntStateOf(0) }
    val fullTitle = "FarmTrace"

    // Infinite transitions for ambient floating items & sweep line
    val infiniteTransition = rememberInfiniteTransition(label = "SplashAmbient")

    val beamPosition by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LightBeam"
    )

    val vegFloatOffset by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "VegFloat"
    )

    val logoPulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoPulse"
    )

    // Sequence timer: Phase 1 (0-800ms), Phase 2 (800-1800ms), Phase 3 (1800-3000ms), Done (3200ms)
    LaunchedEffect(Unit) {
        delay(600)
        animationPhase = 2 // Grow sprout & logo
        delay(900)
        animationPhase = 3 // Typewriter title & tagline
        for (i in 1..fullTitle.length) {
            typewriterCharCount = i
            delay(70)
        }
        delay(1200)
        animationPhase = 4
        delay(300)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DarkBackground, ForestGreen, Color(0xFF0F261B))
                )
            )
            .testTag("cinematic_splash_screen")
    ) {
        // 1. Ambient Background Canvas (Light beam, faint hills, particle seeds)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Soft sweep light beam
            val beamX = w * beamPosition
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, LimeGreen.copy(alpha = 0.12f), Color.Transparent),
                    start = Offset(beamX - 80f, 0f),
                    end = Offset(beamX + 80f, h)
                ),
                start = Offset(beamX, 0f),
                end = Offset(beamX + 60f, h),
                strokeWidth = 140f
            )

            // Distant agricultural landscape silhouettes
            val hillPath = Path().apply {
                moveTo(0f, h * 0.78f)
                cubicTo(w * 0.25f, h * 0.74f, w * 0.45f, h * 0.82f, w * 0.7f, h * 0.76f)
                cubicTo(w * 0.85f, h * 0.72f, w * 0.95f, h * 0.80f, w, h * 0.78f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(hillPath, color = Color(0xFF0B1E14).copy(alpha = 0.55f))

            // Floating glowing particles
            val particlePositions = listOf(
                Offset(w * 0.15f, h * 0.25f),
                Offset(w * 0.82f, h * 0.30f),
                Offset(w * 0.28f, h * 0.65f),
                Offset(w * 0.75f, h * 0.70f),
                Offset(w * 0.48f, h * 0.15f)
            )
            particlePositions.forEach { pt ->
                drawCircle(LimeGreen.copy(alpha = 0.25f), radius = 3.5f, center = pt)
                drawCircle(Color.White.copy(alpha = 0.6f), radius = 1.5f, center = pt)
            }
        }

        // 2. Floating Background Vegetables
        FloatingVegIcon("🍅", modifier = Modifier.offset(x = 36.dp, y = (120 + vegFloatOffset).dp))
        FloatingVegIcon("🥕", modifier = Modifier.align(Alignment.TopEnd).offset(x = (-40).dp, y = (160 - vegFloatOffset).dp))
        FloatingVegIcon("🍆", modifier = Modifier.align(Alignment.BottomStart).offset(x = 42.dp, y = (-160 + vegFloatOffset).dp))
        FloatingVegIcon("🌶️", modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-36).dp, y = (-180 - vegFloatOffset).dp))

        // 3. Skip Button (Top Right)
        Button(
            onClick = onSplashFinished,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .testTag("skip_intro_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Skip", color = CreamWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Skip Intro",
                    tint = LimeGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // 4. Center Logo & Brand Sequence
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Seed to Sprout Emblem
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(if (animationPhase >= 2) logoPulse else 0.85f),
                contentAlignment = Alignment.Center
            ) {
                // Outer glowing circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, LimeGreen.copy(alpha = if (animationPhase >= 2) 0.6f else 0.2f), CircleShape)
                        .background(ForestGreen.copy(alpha = 0.65f))
                )

                if (animationPhase == 1) {
                    // Phase 1: Glowing Golden-Green Seed
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(LimeGreen)
                            .border(3.dp, Color.White, CircleShape)
                    )
                } else {
                    // Phase 2 & 3: Growing Leaf Sprout + Scanning Arc
                    Canvas(modifier = Modifier.size(90.dp)) {
                        val w = size.width
                        val h = size.height

                        // Sprout Stem
                        val stemPath = Path().apply {
                            moveTo(w * 0.5f, h * 0.85f)
                            cubicTo(w * 0.5f, h * 0.6f, w * 0.42f, h * 0.45f, w * 0.5f, h * 0.25f)
                        }
                        drawPath(stemPath, color = FreshGreen, style = Stroke(width = 6f, cap = StrokeCap.Round))

                        // Left Leaf
                        val leftLeaf = Path().apply {
                            moveTo(w * 0.48f, h * 0.52f)
                            cubicTo(w * 0.2f, h * 0.5f, w * 0.22f, h * 0.32f, w * 0.45f, h * 0.38f)
                            close()
                        }
                        drawPath(leftLeaf, color = FreshGreen)

                        // Right Primary Leaf
                        val rightLeaf = Path().apply {
                            moveTo(w * 0.50f, h * 0.45f)
                            cubicTo(w * 0.78f, h * 0.40f, w * 0.76f, h * 0.20f, w * 0.52f, h * 0.28f)
                            close()
                        }
                        drawPath(rightLeaf, color = LimeGreen)

                        // Central Trace Node
                        drawCircle(Color.White, radius = 6f, center = Offset(w * 0.5f, h * 0.25f))
                        drawCircle(LimeGreen, radius = 3.5f, center = Offset(w * 0.5f, h * 0.25f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated App Name (Typewriter)
            val displayedTitle = if (animationPhase >= 3) {
                fullTitle.take(typewriterCharCount)
            } else ""

            Text(
                text = displayedTitle,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline Reveal
            AnimatedVisibility(
                visible = animationPhase >= 3,
                enter = fadeIn(tween(800))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "EVERY VEGETABLE HAS A STORY.",
                        color = LimeGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "From Farm to Your Table",
                        color = CreamWhite.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Bottom progress indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (p in 1..3) {
                Box(
                    modifier = Modifier
                        .size(if (animationPhase == p) 20.dp to 5.dp else 6.dp to 5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (animationPhase >= p) LimeGreen else Color.White.copy(alpha = 0.2f))
                )
            }
        }
    }
}

@Composable
private fun FloatingVegIcon(
    emoji: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(46.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 22.sp)
        }
    }
}

private infix fun androidx.compose.ui.unit.Dp.to(height: androidx.compose.ui.unit.Dp): androidx.compose.ui.unit.DpSize {
    return androidx.compose.ui.unit.DpSize(this, height)
}
