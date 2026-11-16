package com.toktasoft.imagecrop.sample.presentation.main.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp

@Composable
fun MakeBackgroundBlur(
    conditions: Boolean,
    content: @Composable () -> Unit,
) {
    val blurRadius by animateDpAsState(
        targetValue = if (conditions) 18.dp else 0.dp,
        label = "backgroundBlur",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (blurRadius > 0.dp)
                    Modifier.blur(blurRadius)
                else Modifier
            ),
    ) {
        content()
    }
}
