package com.supikashi.recharge.data

import com.supikashi.recharge.database.Puzzle
import org.jetbrains.compose.resources.DrawableResource
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.puzzle_1_1
import recharge.composeapp.generated.resources.puzzle_1_2
import recharge.composeapp.generated.resources.puzzle_1_3
import recharge.composeapp.generated.resources.puzzle_1_4
import recharge.composeapp.generated.resources.puzzle_1_5
import recharge.composeapp.generated.resources.puzzle_1_6
import recharge.composeapp.generated.resources.puzzle_1_7
import recharge.composeapp.generated.resources.puzzle_2_1
import recharge.composeapp.generated.resources.puzzle_2_2
import recharge.composeapp.generated.resources.puzzle_2_3
import recharge.composeapp.generated.resources.puzzle_2_4
import recharge.composeapp.generated.resources.puzzle_2_5
import recharge.composeapp.generated.resources.puzzle_2_6
import recharge.composeapp.generated.resources.puzzle_2_7
import recharge.composeapp.generated.resources.puzzle_3_1
import recharge.composeapp.generated.resources.puzzle_3_2
import recharge.composeapp.generated.resources.puzzle_3_3
import recharge.composeapp.generated.resources.puzzle_3_4
import recharge.composeapp.generated.resources.puzzle_3_5
import recharge.composeapp.generated.resources.puzzle_3_6
import recharge.composeapp.generated.resources.puzzle_3_7

data class PuzzlePieceAsset(
    val pieceNumber: Int,
    val image: DrawableResource
)

data class PuzzleAssetSet(
    val id: Int,
    val pieces: List<PuzzlePieceAsset>
)

object PuzzleAssetCatalog {
    val sets = listOf(
        PuzzleAssetSet(
            id = 1,
            pieces = listOf(
                PuzzlePieceAsset(1, Res.drawable.puzzle_1_1),
                PuzzlePieceAsset(2, Res.drawable.puzzle_1_2),
                PuzzlePieceAsset(3, Res.drawable.puzzle_1_3),
                PuzzlePieceAsset(4, Res.drawable.puzzle_1_4),
                PuzzlePieceAsset(5, Res.drawable.puzzle_1_5),
                PuzzlePieceAsset(6, Res.drawable.puzzle_1_6),
                PuzzlePieceAsset(7, Res.drawable.puzzle_1_7)
            )
        ),
        PuzzleAssetSet(
            id = 2,
            pieces = listOf(
                PuzzlePieceAsset(1, Res.drawable.puzzle_2_1),
                PuzzlePieceAsset(2, Res.drawable.puzzle_2_2),
                PuzzlePieceAsset(3, Res.drawable.puzzle_2_3),
                PuzzlePieceAsset(4, Res.drawable.puzzle_2_4),
                PuzzlePieceAsset(5, Res.drawable.puzzle_2_5),
                PuzzlePieceAsset(6, Res.drawable.puzzle_2_6),
                PuzzlePieceAsset(7, Res.drawable.puzzle_2_7)
            )
        ),
        PuzzleAssetSet(
            id = 3,
            pieces = listOf(
                PuzzlePieceAsset(1, Res.drawable.puzzle_3_1),
                PuzzlePieceAsset(2, Res.drawable.puzzle_3_2),
                PuzzlePieceAsset(3, Res.drawable.puzzle_3_3),
                PuzzlePieceAsset(4, Res.drawable.puzzle_3_4),
                PuzzlePieceAsset(5, Res.drawable.puzzle_3_5),
                PuzzlePieceAsset(6, Res.drawable.puzzle_3_6),
                PuzzlePieceAsset(7, Res.drawable.puzzle_3_7)
            )
        )
    )

    val availablePuzzleCount: Int
        get() = sets.size

    fun assetSetFor(puzzle: Puzzle): PuzzleAssetSet? = sets.getOrNull(puzzle.id - 1)
}
