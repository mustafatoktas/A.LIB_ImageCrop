package com.toktasoft.imagecrop.sample.common.functions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import com.toktasoft.imagecrop.sample.common.enums.DampingRatioBouncy

private const val DEFAULT_DURATION_MILLIS = 700

// TODO 2 üstten gel 1 üste git

fun slideInFromTop(
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
): EnterTransition =
    slideInVertically(
        initialOffsetY = { -it },
        animationSpec = tween(durationMillis),
    ) + fadeIn()

fun slideInFromTop(
    bounce: Boolean,
    dampingRatio: DampingRatioBouncy = DampingRatioBouncy.MEDIUM,
): EnterTransition =
    slideInVertically(
        initialOffsetY = { -it },
        animationSpec = spring(
            dampingRatio = dampingRatio.float,
            stiffness = Spring.StiffnessVeryLow,
        ),
    ) + fadeIn()

fun slideOutToTop(
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
): ExitTransition =
    slideOutVertically(
        targetOffsetY = { -it },
        animationSpec = tween(durationMillis),
    ) + fadeOut()

// TODO 2 altan gel 1 alta git

fun slideInFromBottom(
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
): EnterTransition =
    slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(durationMillis),
    ) + fadeIn()

fun slideInFromBottom(
    bounce: Boolean,
    dampingRatio: DampingRatioBouncy = DampingRatioBouncy.MEDIUM,
): EnterTransition =
    slideInVertically(
        initialOffsetY = { it },
        animationSpec = spring(
            dampingRatio = dampingRatio.float,
            stiffness = Spring.StiffnessVeryLow,
        ),
    ) + fadeIn()

fun slideOutToBottom(
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
): ExitTransition =
    slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(durationMillis),
    ) + fadeOut()

// TODO 2 soldan gel 1 sola git

fun slideInFromLeft(
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(durationMillis),
    ) + fadeIn()

fun slideInFromLeft(
    bounce: Boolean,
    dampingRatio: DampingRatioBouncy = DampingRatioBouncy.MEDIUM,
): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = spring(
            dampingRatio = dampingRatio.float,
            stiffness = Spring.StiffnessVeryLow,
        ),
    ) + fadeIn()

fun slideOutToLeft(
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(durationMillis),
    ) + fadeOut()

// TODO 2 sağdan gel 1 sağa git

fun slideInFromRight(
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(durationMillis),
    ) + fadeIn()

fun slideInFromRight(
    bounce: Boolean,
    dampingRatio: DampingRatioBouncy = DampingRatioBouncy.MEDIUM,
): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = spring(
            dampingRatio = dampingRatio.float,
            stiffness = Spring.StiffnessVeryLow,
        ),
    ) + fadeIn()

fun slideOutToRight(
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(durationMillis),
    ) + fadeOut()

// TODO z ekseninde animasyonlar

fun myScaleIn(
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
): EnterTransition =
    scaleIn(
        initialScale = 0.0f,
        animationSpec = tween(durationMillis)
    ) + fadeIn()

fun myScaleIn(
    bounce: Boolean,
    dampingRatio: DampingRatioBouncy = DampingRatioBouncy.MEDIUM,
): EnterTransition =
    scaleIn(
        initialScale = 0.0f,
        animationSpec = spring(
            dampingRatio = dampingRatio.float,
            stiffness = Spring.StiffnessVeryLow,
        ),
    ) + fadeIn()

fun myScaleOut(
    durationMillis: Int = DEFAULT_DURATION_MILLIS,
): ExitTransition =
    scaleOut(
        targetScale = 0.0f,
        animationSpec = tween(durationMillis)
    ) + fadeOut()

// TODO hızlı geçişler

fun zoomInTransition(
    forwardTransitionSpeed: Int = 275
): EnterTransition =
    scaleIn(
        initialScale = 0.85f,
        animationSpec = tween(
            durationMillis = forwardTransitionSpeed,
            easing = FastOutSlowInEasing
        ),
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = forwardTransitionSpeed,
            easing = FastOutSlowInEasing,
        ),
    )

fun zoomOutTransition(
    forwardTransitionSpeed: Int = 275
): ExitTransition =
    scaleOut(
        targetScale = 1.15f,
        animationSpec = tween(
            durationMillis = forwardTransitionSpeed,
            easing = FastOutSlowInEasing
        ),
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = forwardTransitionSpeed,
            easing = FastOutSlowInEasing,
        ),
    )

fun popZoomInTransition(
    backwardTransitionSpeed: Int = 250
): EnterTransition =
    scaleIn(
        initialScale = 1.15f,
        animationSpec = tween(
            durationMillis = backwardTransitionSpeed,
            easing  = LinearOutSlowInEasing,
        ),
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = backwardTransitionSpeed,
            easing = LinearOutSlowInEasing,
        ),
    )

fun popZoomOutTransition(
    backwardTransitionSpeed: Int = 250
): ExitTransition =
    scaleOut(
        targetScale = 0.85f,
        animationSpec = tween(
            durationMillis = backwardTransitionSpeed,
            easing = LinearOutSlowInEasing,
        ),
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = backwardTransitionSpeed,
            easing = LinearOutSlowInEasing,
        ),
    )
