package com.user.ratingsdk

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

@Composable
fun RatingView(
    modifier: Modifier = Modifier,
    rating: Float,
    onRatingChanged: (newRating: Float) -> Unit = {},
    onRatingCompleted: () -> Unit = {},
    maxRating: Int = 5,
    enableDragging: Boolean = true,
    enableTapping: Boolean = true,
    unratedContent: @Composable (maxRating: Int) -> Unit = { max -> UnratedStarContent(maxRating = max) },
    ratedContent: @Composable (maxRating: Int) -> Unit = { max -> RatedStarContent(maxRating = max) }
) {
    Box(
        modifier = modifier
            .then(
                if (enableDragging) {
                    Modifier
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    onRatingCompleted()
                                }
                            ) { change, _ ->
                                val width = size.width.toFloat()
                                val newRating = (change.position.x / width) * maxRating
                                onRatingChanged(newRating.coerceIn(0f, maxRating.toFloat()))
                            }
                        }
                } else {
                    Modifier
                }
            )
            .then(
                if (enableTapping) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val width = size.width.toFloat()
                            val rawRating = (offset.x / width) * maxRating
                            val newRating = ceil(rawRating)
                            onRatingChanged(newRating.coerceIn(0f, maxRating.toFloat()))
                            onRatingCompleted()
                        }
                    }
                } else {
                    Modifier
                }
            ),

        ) {
        unratedContent(maxRating)
        Box(
            modifier = Modifier
                .drawWithCache {
                    onDrawWithContent {
                        if (rating > 0) {
                            clipRect(
                                left = 0f,
                                top = 0f,
                                right = size.width * rating / maxRating,
                                bottom = size.height
                            ) {
                                this@onDrawWithContent.drawContent()
                            }
                        }
                    }
                }
        ) {
            ratedContent(maxRating)
        }
    }
}

@Composable
private fun RatedStarContent(modifier: Modifier = Modifier, maxRating: Int) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        repeat(maxRating) { i ->
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_rating_star_filled),
                contentDescription = "Rating star ${i + 1}",
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun UnratedStarContent(modifier: Modifier = Modifier, maxRating: Int) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        repeat(maxRating) { i ->
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_rating_star_non_filled),
                contentDescription = "Rating star ${i + 1}",
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )
        }
    }
}