package com.supikashi.recharge.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.supikashi.recharge.data.PuzzleAssetCatalog
import com.supikashi.recharge.data.PuzzleAssetSet
import com.supikashi.recharge.data.PuzzleRules
import com.supikashi.recharge.database.Puzzle
import com.supikashi.recharge.utils.formatDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.stats_puzzle_all_completed
import recharge.composeapp.generated.resources.stats_puzzle_collection_button
import recharge.composeapp.generated.resources.stats_puzzle_completed_date
import recharge.composeapp.generated.resources.stats_puzzle_completed_title
import recharge.composeapp.generated.resources.stats_puzzle_current_title
import recharge.composeapp.generated.resources.stats_puzzle_no_completed
import recharge.composeapp.generated.resources.stats_puzzle_progress
import recharge.composeapp.generated.resources.stats_puzzles_title
import kotlin.time.Clock

@Composable
fun AchievementsSection(
    puzzles: List<Puzzle>,
    onOpenCollection: () -> Unit,
    isStatisticsScreen: Boolean,
    modifier: Modifier = Modifier
) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val currentPuzzle = currentPuzzleForUi(puzzles, isStatisticsScreen, today)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(Res.string.stats_puzzles_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        if (currentPuzzle != null) {
            CurrentPuzzleCard(
                puzzle = currentPuzzle,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = stringResource(Res.string.stats_puzzle_all_completed),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 35.dp)
            )
        }

        Button(
            onClick = onOpenCollection,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp)
        ) {
            Text(
                text = stringResource(Res.string.stats_puzzle_collection_button),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PuzzleCollectionContent(
    puzzles: List<Puzzle>,
    modifier: Modifier = Modifier
) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val currentPuzzle = currentPuzzleForUi(puzzles, false, today)
    val completedPuzzles = completedPuzzlesForUi(puzzles)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentPuzzle != null) {
            Text(
                text = stringResource(Res.string.stats_puzzle_current_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            CurrentPuzzleCard(
                puzzle = currentPuzzle,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = stringResource(Res.string.stats_puzzle_all_completed),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 35.dp)
            )
        }

        Text(
            text = stringResource(Res.string.stats_puzzle_completed_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        if (completedPuzzles.isEmpty()) {
            Text(
                text = stringResource(Res.string.stats_puzzle_no_completed),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 35.dp)
            )
        } else {
            completedPuzzles.forEach { puzzle ->
                CompletedPuzzleCard(
                    puzzle = puzzle,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CurrentPuzzleCard(
    puzzle: Puzzle,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            PuzzleGrid(
                puzzle = puzzle,
                collectedPieces = puzzle.collectedPieces,
                showLockedPieces = true
            )
            PuzzleGrid(
                puzzle = puzzle,
                collectedPieces = puzzle.collectedPieces,
                showLockedPieces = false
            )
        }

        Text(
            text = stringResource(
                Res.string.stats_puzzle_progress,
                puzzle.collectedPieces,
                PuzzleRules.PIECES_PER_PUZZLE
            ),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CompletedPuzzleCard(
    puzzle: Puzzle,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PuzzleGrid(
            puzzle = puzzle,
            collectedPieces = puzzle.collectedPieces,
            showLockedPieces = false
        )

        Text(
            text = stringResource(Res.string.stats_puzzle_completed_date, formatDate(puzzle.completedDate!!)),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PuzzleGrid(
    puzzle: Puzzle,
    collectedPieces: Int,
    showLockedPieces: Boolean,
    modifier: Modifier = Modifier
) {
    val assetSet = PuzzleAssetCatalog.assetSetFor(puzzle) ?: return
    val visiblePieces = collectedPieces.coerceIn(0, PuzzleRules.PIECES_PER_PUZZLE)

    val rows = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7)
    )

    val totalWidth = rows.firstOrNull()?.sumOf { id ->
        val piece = assetSet.pieces.firstOrNull { it.pieceNumber == id }
        piece?.let { painterResource(it.image).intrinsicSize.width.toDouble() } ?: 0.0
    }?.toFloat() ?: 1f

    val totalHeight = rows.sumOf { rowPieces ->
        val firstId = rowPieces.firstOrNull() ?: return@sumOf 0.0
        val piece = assetSet.pieces.firstOrNull { it.pieceNumber == firstId }
        piece?.let { painterResource(it.image).intrinsicSize.height.toDouble() } ?: 0.0
    }.toFloat().coerceAtLeast(1f)
    
    val dynamicAspectRatio = totalWidth / totalHeight

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val isHeightConstrained = maxHeight != androidx.compose.ui.unit.Dp.Infinity && maxHeight != androidx.compose.ui.unit.Dp.Unspecified && (maxWidth / maxHeight) > dynamicAspectRatio

        Box(
            modifier = Modifier
                .then(
                    if (isHeightConstrained) {
                        Modifier.fillMaxHeight().aspectRatio(dynamicAspectRatio, matchHeightConstraintsFirst = true)
                    } else {
                        Modifier.fillMaxWidth().aspectRatio(dynamicAspectRatio, matchHeightConstraintsFirst = false)
                    }
                )
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 4.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(4.dp)
        ) {
            Column(Modifier.fillMaxSize().blur(if (showLockedPieces) 50.dp else 0.dp)) {
                rows.forEach { rowPieces ->
                    val firstPieceId = rowPieces.firstOrNull() ?: return@forEach
                    val firstPiece = assetSet.pieces.firstOrNull { it.pieceNumber == firstPieceId }

                    val rowHeight = if (firstPiece != null) {
                        painterResource(firstPiece.image).intrinsicSize.height
                    } else 1f

                    PuzzleRow(
                        assetSet = assetSet,
                        pieceNumbers = rowPieces,
                        visiblePieces = visiblePieces,
                        showLockedPieces = showLockedPieces,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(rowHeight)
                    )
                }
            }
        }
    }
}

@Composable
private fun PuzzleRow(
    assetSet: PuzzleAssetSet,
    pieceNumbers: List<Int>,
    visiblePieces: Int,
    showLockedPieces: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        pieceNumbers.forEach { pieceNumber ->
            val piece = assetSet.pieces.firstOrNull { it.pieceNumber == pieceNumber }
            if (piece != null) {
                val painter = painterResource(piece.image)
                PuzzlePieceCell(
                    painter = painter,
                    isCollected = pieceNumber <= visiblePieces,
                    showLockedPieces = showLockedPieces,
                    modifier = Modifier
                        .weight(painter.intrinsicSize.width)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun PuzzlePieceCell(
    painter: androidx.compose.ui.graphics.painter.Painter,
    isCollected: Boolean,
    showLockedPieces: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (isCollected || showLockedPieces) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (showLockedPieces) {
                            Modifier.alpha(0.42f)
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

fun currentPuzzleForUi(puzzles: List<Puzzle>, isStatisticsScreen: Boolean, today: LocalDate): Puzzle? {
    val latestPuzzle = puzzles.maxByOrNull { it.id }
    return when {
        latestPuzzle == null -> Puzzle(id = 1)
        latestPuzzle.completedDate == null -> latestPuzzle
        latestPuzzle.completedDate == today && isStatisticsScreen -> latestPuzzle
        latestPuzzle.id < PuzzleAssetCatalog.availablePuzzleCount -> Puzzle(id = latestPuzzle.id + 1)
        else -> null
    }
}

fun completedPuzzlesForUi(puzzles: List<Puzzle>): List<Puzzle> {
    return puzzles
        .filter { it.completedDate != null }
        .sortedBy { it.id }
}
