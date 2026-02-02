package com.supikashi.recharge.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.models.RestActivity
import com.supikashi.recharge.models.RestType
import com.supikashi.recharge.theme.mascotPrimary
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.home
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RestActivitiesScreen(
    type: RestType,
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val localDensity = LocalDensity.current
    var sourceHeight by remember { mutableStateOf(0.dp) }
    val activities = getActivitiesForType(type)
    
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.mascotPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
                verticalArrangement = Arrangement.Center
            ) {
                 Spacer(Modifier.height(sourceHeight))
                 
                 Box(
                     modifier = Modifier.weight(1f).fillMaxHeight(),
                     contentAlignment = Alignment.Center
                 ) {
                     val pagerState = rememberPagerState(pageCount = { activities.size })
                     
                     HorizontalPager(
                         state = pagerState,
                         contentPadding = PaddingValues(horizontal = 40.dp),
                         pageSpacing = 10.dp,
                         modifier = Modifier.fillMaxSize()
                     ) { page ->
                         val pageOffset = (
                             (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                         ).absoluteValue
                         
                         val scale = 1f - (0.1f * pageOffset.coerceIn(0f, 1f))
                         val alpha = 1f - (0.5f * pageOffset.coerceIn(0f, 1f))

                         ActivityCard(
                             activity = activities[page],
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .height(500.dp)
                                 .graphicsLayer {
                                     scaleX = scale
                                     scaleY = scale
                                     this.alpha = alpha
                                 }
                         )
                     }
                 }
                 
                 Spacer(Modifier.height(20.dp))
            }

            TopBar(
                leftAction = onNavigateBack,
                rightAction = onNavigateHome,
                leftIcon = Res.drawable.arrow_back,
                rightIcon = Res.drawable.home,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = paddingValues.calculateTopPadding())
                    .align(Alignment.TopCenter)
                    .onGloballyPositioned { coordinates ->
                        sourceHeight = with(localDensity) { coordinates.size.height.toDp() }
                    }
            )
        }
    }
}

@Composable
private fun CardContent(
    headerText: String,
    title: String,
    text: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = headerText,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .align(Alignment.Start)
                .heightIn(min = 24.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        
        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .height(35.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ActivityCard(
    activity: RestActivity,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(500)
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 10f * density
            },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        )
    ) {
        if (rotation <= 90f) {
            CardContent(
                headerText = "ДЛИТЕЛЬНОСТЬ: ${activity.durationMin}-${activity.durationMax} минут",
                title = activity.name,
                text = activity.description,
                buttonText = "Открыть инструкцию",
                onButtonClick = { isFlipped = true }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f
                    }
            ) {
                CardContent(
                    headerText = "ИНСТРУКЦИЯ",
                    title = activity.name,
                    text = activity.instruction,
                    buttonText = "Назад",
                    onButtonClick = { isFlipped = false }
                )
            }
        }
    }
}

private fun getActivitiesForType(type: RestType?): List<RestActivity> {
    return when (type) {
        RestType.ACTIVE -> listOf(
            RestActivity(
                name = "Растяжка",
                description = "Комплекс упражнений для расслабления мышц и улучшения кровообращения. Подходит для снятия напряжения после долгой работы за компьютером.",
                durationMin = 5,
                durationMax = 10,
                instruction = "1. Встаньте прямо, ноги на ширине плеч.\n2. Медленно наклоните голову влево, затем вправо.\n3. Сделайте круговые движения плечами.\n4. Потянитесь руками вверх, вставая на носочки."
            ),
            RestActivity(
                name = "Прогулка",
                description = "Короткая прогулка на свежем воздухе поможет размяться, проветрить голову и набраться энергии для продолжения работы.",
                durationMin = 15,
                durationMax = 20,
                instruction = "1. Оденьтесь по погоде.\n2. Выйдите на улицу.\n3. Идите в спокойном темпе, глубоко дышите свежим воздухом."
            ),
            RestActivity(
                name = "Легкая зарядка",
                description = "Простые физические упражнения для активации организма. Повышает концентрацию и продуктивность.",
                durationMin = 5,
                durationMax = 10,
                instruction = "1. Приседания: 10-15 раз.\n2. Отжимания от стены или пола: 10 раз.\n3. Бег на месте: 1 минута."
            )
        )
        RestType.CALM -> listOf(
            RestActivity(
                name = "Медитация",
                description = "Практика осознанности и дыхательные упражнения для успокоения ума и снятия стресса. Помогает восстановить ментальное равновесие.",
                durationMin = 10,
                durationMax = 15,
                instruction = "1. Сядьте в удобную позу с прямой спиной.\n2. Закройте глаза и сосредоточьтесь на дыхании.\n3. Отпускайте мысли, возвращая внимание к вдохам и выдохам."
            ),
            RestActivity(
                name = "Дыхательные упражнения",
                description = "Техники глубокого дыхания для расслабления нервной системы и снижения уровня стресса.",
                durationMin = 5,
                durationMax = 10,
                instruction = "1. Вдох носом на 4 счета.\n2. Задержка дыхания на 4 счета.\n3. Выдох ртом на 4 счета.\n4. Задержка на 4 счета. Повторяйте."
            ),
            RestActivity(
                name = "Прослушивание музыки",
                description = "Спокойная музыка или звуки природы помогут расслабиться и перезагрузить мозг.",
                durationMin = 10,
                durationMax = 20,
                instruction = "1. Наденьте наушники или включите колонки.\n2. Выберите плейлист со спокойной музыкой или звуками природы.\n3. Закройте глаза и просто слушайте."
            )
        )
        RestType.CREATIVE -> listOf(
            RestActivity(
                name = "Рисование",
                description = "Свободное творчество помогает отвлечься от рабочих задач и активировать правое полушарие мозга.",
                durationMin = 15,
                durationMax = 30,
                instruction = "1. Возьмите лист бумаги и карандаш/ручку.\n2. Рисуйте что угодно, не задумываясь о результате.\n3. Сосредоточьтесь на процессе создания линий и форм."
            ),
            RestActivity(
                name = "Чтение",
                description = "Короткий отрывок из любимой книги или статьи на интересную тему для переключения внимания.",
                durationMin = 15,
                durationMax = 30,
                instruction = "1. Возьмите книгу, которую давно хотели почитать.\n2. Прочитайте несколько страниц или одну главу."
            ),
            RestActivity(
                name = "Письмо",
                description = "Свободное письмо, ведение дневника или записывание мыслей помогает структурировать идеи.",
                durationMin = 10,
                durationMax = 15,
                instruction = "1. Откройте заметки или возьмите блокнот.\n2. Пишите все, что приходит в голову (фрирайтинг) в течение 5 минут."
            )
        )
        null -> emptyList()
    }
}
