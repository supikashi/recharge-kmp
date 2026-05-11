package com.supikashi.recharge.data
/*
import com.supikashi.recharge.models.CardContent
import com.supikashi.recharge.models.RestActivity
import com.supikashi.recharge.models.RestType
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.active_1_1
import recharge.composeapp.generated.resources.active_1_2
import recharge.composeapp.generated.resources.active_1_3
import recharge.composeapp.generated.resources.active_1_4
import recharge.composeapp.generated.resources.active_2_1
import recharge.composeapp.generated.resources.active_2_2
import recharge.composeapp.generated.resources.active_3_1
import recharge.composeapp.generated.resources.active_3_2
import recharge.composeapp.generated.resources.active_3_3
import recharge.composeapp.generated.resources.active_4_1
import recharge.composeapp.generated.resources.active_4_2
import recharge.composeapp.generated.resources.active_4_3
import recharge.composeapp.generated.resources.active_5_1
import recharge.composeapp.generated.resources.active_5_2
import recharge.composeapp.generated.resources.active_5_3

fun getActivitiesForType(type: RestType?): List<RestActivity> {
    return when (type) {
        RestType.ACTIVE -> listOf(
            RestActivity(
                name = "Латеральные наклоны",
                durationMin = 5,
                durationMax = 10,
                steps = listOf(
                    CardContent(
                        title = "Латеральные наклоны",
                        description = "Для тех, кто чувствует, что засиделся и понимает, что пора помочь своей спине.\n\n" +
                                "Помогает увеличить количество кислорода, поступающего в тело, и снять напряжение с шеи: в дыхание активнее включаются другие части тела."
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Сидя в кресле или на стуле, поставь стопы вместе на пол.",
                        image = Res.drawable.active_1_1
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "2. На вдохе наклонись вправо, вытягивая левый бок и наполняя ребра слева воздухом.",
                        image = Res.drawable.active_1_2
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "3. В максимальной точке амплитуды начни выдох от низа живота и плавно вернись в исходное положение.",
                        image = Res.drawable.active_1_3
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "4. Повтори упражнение с наклоном в другую сторону.",
                        image = Res.drawable.active_1_4
                    ),
                )
            ),
            RestActivity(
                name = "Сгибания и разгибания грудного отдела",
                durationMin = 5,
                durationMax = 10,
                steps = listOf(
                    CardContent(
                        title = "Сгибания и разгибания грудного отдела",
                        description = "Это упражнение растягивает и тонизирует мышцы груди и область между лопаток. При сидячей работе первые часто становятся зажаты и укорочены, а вторые — натягиваются и напрягаются. Разминка помогает сбалансировать работу этих мышц и поставить плечи ровнее."
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Поставь ноги на ширине плеч, сцепи руки в замок за спиной.\n" +
                                "На вдохе потянись руками вверх, не опуская вперед грудную клетку.",
                        image = Res.drawable.active_2_1
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "2. На выдохе сцепи ладони перед грудью и потянись ими вперед, а областью между лопаток — назад.",
                        image = Res.drawable.active_2_2
                    ),
                )
            ),
            RestActivity(
                name = "Расслабление шеи",
                durationMin = 5,
                durationMax = 10,
                steps = listOf(
                    CardContent(
                        title = "Расслабление шеи",
                        description = "В сидячем положении часто зажимается трапециевидная мышца спины и мышцы, поднимающие лопатки.\n\n" +
                                "Упражнение помогает их расслабить и вернуть плечи в естественное положение."
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Положи правую ладонь на левое плечо, при этом левое ухо поверни вверх.",
                        image = Res.drawable.active_3_1
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "2. На выдохе потяни голову, напрягая боковую и заднюю поверхности шеи.",
                        image = Res.drawable.active_3_2
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "3. На вдохе вернись в исходное положение и расслабь мышцы. После нескольких повторов поменяй стороны.",
                        image = Res.drawable.active_3_3
                    ),
                ),
            ),
            RestActivity(
                name = "Вытяжение ребер",
                durationMin = 5,
                durationMax = 10,
                steps = listOf(
                    CardContent(
                        title = "Вытяжение ребер",
                        description = "В сидячем положении грудная клетка часто зажимается, а дыхание становится поверхностным, из-за чего организм получает мало кислорода и быстрее устает, снижается концентрация.\n\n" +
                                "Это упражнение расслабляет межреберные мышцы и диафрагму, увеличивает глубину вдоха."
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Поставь ноги на ширине плеч, сцепи руки в локтевой замок перед грудью.",
                        image = Res.drawable.active_4_1
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "2. На вдохе вытяни вверх ребра с левой стороны, не теряя натяжения слева.",
                        image = Res.drawable.active_4_2
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "3. Подтяни вверх ребра с правой стороны. На выдохе, сохраняя бока в напряжении, опусти плечи и потянись вперед ключицами.",
                        image = Res.drawable.active_4_3
                    ),
                ),
            ),
            RestActivity(
                name = "Наклоны таза",
                durationMin = 5,
                durationMax = 10,
                steps = listOf(
                    CardContent(
                        title = "Наклоны таза",
                        description = "В положении сидя поясница не двигается, поэтому за долгие часы в кресле спина сильно напрягается и устает.\n\n" +
                                "Движения тазом позволяют мышцам вытянуться и почувствовать тонус. При этом с поясницы снимается нагрузка, а пресс начинает служить поддержкой для спины — так ее проще держать прямой."
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Отодвинься от спинки стула, чтобы между ней и поясницей появилось немного пространства. Поставь стопы на пол.",
                        image = Res.drawable.active_5_1
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "2. На вдохе потянись головой вверх и немного прогни поясницу вперед.",
                        image = Res.drawable.active_5_2
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "3. На выдохе потянись спиной в сторону стула, чтобы она скруглилась. Голову при этом можно опустить вперед.",
                        image = Res.drawable.active_5_3
                    ),
                ),
            )
        )
        RestType.CALM -> listOf(
            RestActivity(
                name = "Якорение в настоящем",
                durationMin = 10,
                durationMax = 15,
                steps = listOf(
                    CardContent(
                        title = "Якорение в настоящем",
                        description = "Если чувствуешь, что теряешь связь с реальностью и пришло время вернуться на Землю"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "Найди:\n\n" +
                                "5 вещей, которые видишь.\n\n" +
                                "4 вещи, которые можешь потрогать.\n\n" +
                                "3 звука, которые ты слышишь.\n\n" +
                                "2 запаха, которые можешь уловить.\n\n" +
                                "1 вкус, который ощущаешь или вспоминаешь."
                    ),
                )
            ),
            RestActivity(
                name = "Фокус на ощущениях",
                durationMin = 5,
                durationMax = 10,
                steps = listOf(
                    CardContent(
                        title = "Фокус на ощущениях",
                        description = "Если знаешь, что сейчас твое тело нуждается в особом внимании"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Почувствуй, как твои ноги стоят на полу, как руки касаются поверхности.\n\n" +
                                "2. Отметь, где в теле есть напряжение, и постарайся расслабить эти зоны.\n\n" +
                                "3. Сделай 2-3 мягких круговых движения плечами и шеей."
                    ),
                )
            ),
            RestActivity(
                name = "Наблюдение за пространством",
                durationMin = 5,
                durationMax = 10,
                steps = listOf(
                    CardContent(
                        title = "Наблюдение за пространством",
                        description = "Для тех, кто ищет связь с собой через место, где он находится"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Медленно осмотри комнату вокруг, замечая мелкие детали: цвет стен, текстуру предметов, свет и тень.\n\n" +
                                "2. Попробуй отметить 5 вещей, которые раньше не замечал."
                    ),
                )
            ),
            RestActivity(
                name = "Осознанное дыхание",
                durationMin = 5,
                durationMax = 10,
                steps = listOf(
                    CardContent(
                        title = "Осознанное дыхание",
                        description = "Для тех, кто понимает, что даже просто дышать – нужно уметь!"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Сядь удобно, закройте глаза.\n\n" +
                                "2. Сделай глубокий вдох через нос, ощущая, как воздух наполняет легкие.\n\n" +
                                "3. На 2-3 секунды задержи дыхание, затем медленно выдохните через рот.\n\n" +
                                "4. Повтори 5-7 раз, обращая внимание только на процесс дыхания."
                    ),
                )
            ),
            RestActivity(
                name = "Список благодарности",
                durationMin = 10,
                durationMax = 15,
                steps = listOf(
                    CardContent(
                        title = "Список благодарности",
                        description = "Если понимаешь, что слишком долго ругал себя, то пришло время это исправить"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Подумай о том, за что ты благодарен себе прямо сейчас.\n\n" +
                                "2. Выпиши 3-5 вещей от руки на бумагу, это очень важно.\n\n" +
                                "3. Попробуй прочувствовать, как это наполняет тебя теплом.",
                        additional = "*Для большего положительного эффекта рекомендуется делать это регулярно."
                    ),
                )
            ),
            RestActivity(
                name = "Медленное письмо от руки",
                durationMin = 10,
                durationMax = 15,
                steps = listOf(
                    CardContent(
                        title = "Медленное письмо от руки",
                        description = "Иногда нет ничего сложнее, чем описать свои чувства – как раз пришло время это исправить!"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Возьми бумагу и ручку, напиши пару строк о том, что ты чувствуешь прямо сейчас.\n\n" +
                                "2. Можно просто описать, что ты видишь перед собой.\n\n" +
                                "3. Пиши неторопливо, наслаждаясь каждым движением руки."
                    ),
                )
            )
        )
        RestType.CREATIVE -> listOf(
            RestActivity(
                name = "Быстрые скетчи",
                durationMin = 5,
                durationMax = 10,
                steps = listOf(
                    CardContent(
                        title = "Быстрые скетчи",
                        description = "Для тех, у кого есть свободная минутка и карандаш под рукой"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Возьми лист бумаги и ручку (или карандаш)\n\n" +
                                "2. Нарисуй что-то простое: смайлик, цветок, геометрический узор или забавного персонажа.\n\n" +
                                "3. Можно сделать мини-зарисовку предмета, который ты видишь перед собой."
                    ),
                )
            ),
            RestActivity(
                name = "Создание узоров",
                durationMin = 5,
                durationMax = 10,
                steps = listOf(
                    CardContent(
                        title = "Создание узоров",
                        description = "Если вдруг хочется познать искусство рисования зентангл"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Начни рисовать любые повторяющиеся линии, завитки, геометрические фигуры.\n\n" +
                                "2. Позволь руке двигаться свободно, без конкретного плана.",
                        additional = "*Такой процесс помогает расслабиться и немного «переключиться»."
                    ),
                )
            ),
            RestActivity(
                name = "Короткие стишки",
                durationMin = 10,
                durationMax = 15,
                steps = listOf(
                    CardContent(
                        title = "Короткие стишки",
                        description = "Для начинающих поэтов"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Придумай рифму к случайному слову (например, «вдохновение» — «творение»)\n\n" +
                                "2. Придумай еще несколько таких рифм и попробуй создать из них короткое стихотворение или хайку.",
                        additional = "*Можно делать это в шуточной или философской форме."
                    ),
                )
            ),
            RestActivity(
                name = "Создание персонажа",
                durationMin = 10,
                durationMax = 15,
                steps = listOf(
                    CardContent(
                        title = "Создание персонажа",
                        description = "Для тех, кто мечтал попробовать себя в придумывании героев собственных историй"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Запиши 3 случайных качества (например, «ленивый, мечтательный, боится воды»).\n\n" +
                                "2. Придумай, кто это может быть: человек, животное, сказочное существо?\n\n" +
                                "3. Добавь детали: имя, где живет, что любит делать.",
                    ),
                )
            ),
            RestActivity(
                name = "Оригами и поделки",
                durationMin = 15,
                durationMax = 20,
                steps = listOf(
                    CardContent(
                        title = "Оригами и поделки",
                        description = "Для тех, кто много лет не создавал ничего своими руками и наконец почувствовал вдохновение"
                    ),
                    CardContent(
                        title = "Что делать:",
                        description = "1. Сделай простую фигуру из бумаги, например, журавлика или кораблик.\n\n" +
                                "2. Или попробуй создать небольшую скульптуру из подручных материалов (скрепки, бумага, ластик).\n\n" +
                                "3. Можно выбрать какую-то тему и взять её как вдохновение для этой композиции.",
                    ),
                )
            )
        )
        null -> emptyList()
    }
}
*/
