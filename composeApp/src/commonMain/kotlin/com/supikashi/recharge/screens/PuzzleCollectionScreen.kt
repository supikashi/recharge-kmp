package com.supikashi.recharge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.components.PuzzleCollectionContent
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.theme.mascotPrimary
import com.supikashi.recharge.viewmodels.StatisticsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.home
import recharge.composeapp.generated.resources.stats_puzzle_collection_title

@Composable
fun PuzzleCollectionScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: StatisticsViewModel = koinViewModel()
) {
    val puzzles by viewModel.puzzles.collectAsStateWithLifecycle()
    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.mascotPrimary)
                .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
                .fillMaxSize()
        ) {
            TopBar(
                leftAction = onNavigateBack,
                leftIcon = Res.drawable.arrow_back,
                rightAction = onNavigateHome,
                rightIcon = Res.drawable.home,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Text(
                text = stringResource(Res.string.stats_puzzle_collection_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 30.dp,
                            topEnd = 30.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PuzzleCollectionContent(puzzles = puzzles)
            }
        }
    }
}
