package com.toktasoft.imagecrop.sample.presentation.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.toktasoft.imagecrop.sample.domain.model.CroppedImage
import com.toktasoft.imagecrop.sample.domain.model.sharedKey
import java.io.File
import kotlin.math.ceil

@Composable
fun SharedTransitionScope.ImageGalleryCard(
    images: List<CroppedImage>,
    reviewedImageId: Int?,
    onImageClick: (CroppedImage) -> Unit,
    onLongImageClick: (CroppedImage) -> Unit,
) {
    if (images.isEmpty()) return

    val columns = 3
    val spacing = 6.dp
    val innerPadding = 8.dp

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // kartın kullanılabilir toplam genişliği (dp)
            val totalWidth = maxWidth
            // iç padding ve sütun aralıklarını düş
            val availableWidth = totalWidth - innerPadding * 2 - spacing * (columns - 1)
            // her hücrenin kare kenar uzunluğu
            val cell = availableWidth / columns

            val rows = remember(images.size) {
                ceil(images.size / columns.toFloat()).toInt()
            }

            // toplam grid yüksekliği: üst/alt padding + satırlar + satır aralıkları
            val gridHeight = innerPadding * 2 + cell * rows + spacing * (rows - 1)

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier
                    .height(gridHeight)
                    .padding(all = innerPadding)
                    .fillMaxWidth(),
            ) {
                items(
                    items = images,
                    key = { it.id },
                ) { croppedImage ->

                    GalleryThumbnail(
                        image = croppedImage,
                        size = cell,
                        isReviewed = croppedImage.id == reviewedImageId,
                        onClick = { onImageClick(croppedImage) },
                        onLongClick = { onLongImageClick(croppedImage) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SharedTransitionScope.GalleryThumbnail(
    image: CroppedImage,
    size: Dp,
    isReviewed: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val context = LocalContext.current

    // The cell keeps its size while the thumbnail is open in the review.
    Box(
        modifier = Modifier.size(size),
    ) {
        AnimatedVisibility(
            visible = !isReviewed,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            AsyncImage(
                contentDescription = null,
                contentScale = ContentScale.Crop,
                model = ImageRequest.Builder(context)
                    .data(File(image.uri))
                    .memoryCacheKey(image.uri)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(image.sharedKey),
                        animatedVisibilityScope = this@AnimatedVisibility,
                    )
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                    ).size(size)
                    .clip(RoundedCornerShape(8.dp)),
            )
        }
    }
}
