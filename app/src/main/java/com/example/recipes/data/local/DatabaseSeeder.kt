package com.example.recipes.data.local

import com.example.recipes.data.local.dao.RecipeDao
import com.example.recipes.data.local.entities.Ingredient
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeStep
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.relations.RecipeFullDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seed(recipeDao: RecipeDao) = withContext(Dispatchers.IO) {
        // Empty initial state: do not seed mock recipes, and never delete user recipes
    }

    fun getSeedRecipes(): List<RecipeFullDetails> {
        val now = System.currentTimeMillis()

        return listOf(
            // 1. Сливочная паста от бабушки
            RecipeFullDetails(
                recipe = Recipe(
                    title = "Сливочная паста от бабушки",
                    description = "Нежная сливочная паста с чесноком, густыми сливками, пармезаном и свежей зеленью.",
                    imgPath = null,
                    cookingTime = 25,
                    servings = 4,
                    difficulty = "Легко",
                    category = "Паста",
                    source = "Семейный рецепт",
                    notes = "Оставьте немного воды от варки пасты, чтобы отрегулировать густоту соуса.",
                    isDraft = false,
                    isFavorite = true,
                    createdAt = now - 600_000L,
                    lastViewedAt = now - 60_000L
                ),
                ingredients = listOf(
                    Ingredient(text = "350 г пасты феттуччине", orderIndex = 1),
                    Ingredient(text = "2 стакана жирных сливок", orderIndex = 2),
                    Ingredient(text = "3 зубчика чеснока, измельчить", orderIndex = 3),
                    Ingredient(text = "1 стакан тертого сыра пармезан", orderIndex = 4),
                    Ingredient(text = "2 ст. л. сливочного масла", orderIndex = 5),
                    Ingredient(text = "Свежая петрушка, мелко нарезанная", orderIndex = 6),
                    Ingredient(text = "Соль и свежемолотый черный перец", orderIndex = 7)
                ),
                steps = listOf(
                    RecipeStep(stepNumber = 1, instruction = "Отварите феттуччине в большой кастрюле с подсоленной кипящей водой до состояния аль денте."),
                    RecipeStep(stepNumber = 2, instruction = "В сковороде на среднем огне растопите сливочное масло и обжарьте измельченный чеснок до появления аромата."),
                    RecipeStep(stepNumber = 3, instruction = "Влейте жирные сливки и доведите до легкого кипения в течение 3-4 минут."),
                    RecipeStep(stepNumber = 4, instruction = "Всыпьте тертый пармезан и перемешивайте, пока он полностью не расплавится."),
                    RecipeStep(stepNumber = 5, instruction = "Добавьте пасту в сливочный соус и перемешайте, при необходимости добавив немного воды от пасты."),
                    RecipeStep(stepNumber = 6, instruction = "Украсьте свежей петрушкой и молотым черным перцем перед подачей.")
                ),
                tags = listOf(
                    Tag(name = "Итальянская"),
                    Tag(name = "Паста")
                )
            ),

            // 2. Лимонный тарт
            RecipeFullDetails(
                recipe = Recipe(
                    title = "Лимонный тарт",
                    description = "Классический французский лимонный тарт с хрустящей песочной основой и освежающим лимонным курдом.",
                    imgPath = null,
                    cookingTime = 50,
                    servings = 8,
                    difficulty = "Средне",
                    category = "Десерты",
                    source = "Французская пекарня",
                    notes = "Дайте полностью остыть перед нарезкой, чтобы получились аккуратные ровные кусочки.",
                    isDraft = false,
                    isFavorite = true,
                    createdAt = now - 500_000L,
                    lastViewedAt = null
                ),
                ingredients = listOf(
                    Ingredient(text = "1 основа из песочного теста", orderIndex = 1),
                    Ingredient(text = "4 крупных яйца", orderIndex = 2),
                    Ingredient(text = "150 г сахара", orderIndex = 3),
                    Ingredient(text = "120 мл свежевыжатого лимонного сока", orderIndex = 4),
                    Ingredient(text = "Цедра 2 лимонов", orderIndex = 5),
                    Ingredient(text = "100 г сливочного масла, нарезанного кубиками", orderIndex = 6)
                ),
                steps = listOf(
                    RecipeStep(stepNumber = 1, instruction = "Выпекайте песочную основу при температуре 180°C в течение 15-20 минут до золотистого цвета."),
                    RecipeStep(stepNumber = 2, instruction = "Взбейте яйца, сахар, лимонный сок и цедру в термостойкой миске."),
                    RecipeStep(stepNumber = 3, instruction = "Готовьте на водяной бане при постоянном помешивании до загустения."),
                    RecipeStep(stepNumber = 4, instruction = "Снимите с огня, дайте немного остыть и венчиком вмешайте холодное сливочное масло кубиками до глянцевой текстуры."),
                    RecipeStep(stepNumber = 5, instruction = "Перелейте начинку в готовую основу и уберите в холодильник минимум на 3 часа.")
                ),
                tags = listOf(
                    Tag(name = "Десерты"),
                    Tag(name = "Выпечка")
                )
            ),

            // 3. Тартин на закваске
            RecipeFullDetails(
                recipe = Recipe(
                    title = "Тартин на закваске",
                    description = "Ремесленный хрустящий хлеб на закваске с воздушным мякишем и приятной легкой кислинкой.",
                    imgPath = null,
                    cookingTime = 240,
                    servings = 10,
                    difficulty = "Сложно",
                    category = "Выпечка",
                    source = "Ремесленный пекарь",
                    notes = "Время ферментации зависит от температуры в помещении.",
                    isDraft = false,
                    isFavorite = false,
                    createdAt = now - 400_000L,
                    lastViewedAt = null
                ),
                ingredients = listOf(
                    Ingredient(text = "500 г пшеничной хлебопекарной муки", orderIndex = 1),
                    Ingredient(text = "350 г теплой воды", orderIndex = 2),
                    Ingredient(text = "100 г активной пшеничной закваски", orderIndex = 3),
                    Ingredient(text = "10 г мелкой морской соли", orderIndex = 4)
                ),
                steps = listOf(
                    RecipeStep(stepNumber = 1, instruction = "Смешайте муку, воду и закваску; оставьте на 30 минут для автолиза."),
                    RecipeStep(stepNumber = 2, instruction = "Добавьте соль и выполняйте складывания теста каждые 30 минут в течение 2 часов."),
                    RecipeStep(stepNumber = 3, instruction = "Оставьте на базовое брожение, пока тесто не увеличится в объеме примерно на 50%."),
                    RecipeStep(stepNumber = 4, instruction = "Сформируйте круглую буханку и переложите в корзину для расстойки."),
                    RecipeStep(stepNumber = 5, instruction = "Уберите на холодную ферментацию на ночь в холодильник."),
                    RecipeStep(stepNumber = 6, instruction = "Выпекайте в разогретом чугунке при 230°C: 20 минут под крышкой, затем 20 минут без крышки.")
                ),
                tags = listOf(
                    Tag(name = "Выпечка"),
                    Tag(name = "Хлеб")
                )
            ),

            // 4. Бабушкин яблочный пирог
            RecipeFullDetails(
                recipe = Recipe(
                    title = "Бабушкин яблочный пирог",
                    description = "Нежные яблоки с корицей в хрустящем золотистом слоеном тесте.",
                    imgPath = null,
                    cookingTime = 75,
                    servings = 8,
                    difficulty = "Средне",
                    category = "Десерты",
                    source = "Бабушкина кулинарная книга",
                    notes = "Используйте кисло-сладкие яблоки (например, Гренни Смит) для лучшего вкуса.",
                    isDraft = false,
                    isFavorite = true,
                    createdAt = now - 300_000L,
                    lastViewedAt = null
                ),
                ingredients = listOf(
                    Ingredient(text = "2 пласта теста для пирога", orderIndex = 1),
                    Ingredient(text = "6 стаканов нарезанных яблок (очищенных)", orderIndex = 2),
                    Ingredient(text = "3/4 стакана сахара", orderIndex = 3),
                    Ingredient(text = "2 ст. л. пшеничной муки", orderIndex = 4),
                    Ingredient(text = "1 ч. л. молотой корицы", orderIndex = 5),
                    Ingredient(text = "1/4 ч. л. мускатного ореха", orderIndex = 6),
                    Ingredient(text = "2 ст. л. сливочного масла", orderIndex = 7),
                    Ingredient(text = "1 яйцо, взбитое с 1 ст. л. воды (для смазывания)", orderIndex = 8)
                ),
                steps = listOf(
                    RecipeStep(stepNumber = 1, instruction = "Разогрейте духовку до 200°C. Выложите нижний слой теста в форму для пирога."),
                    RecipeStep(stepNumber = 2, instruction = "В миске перемешайте нарезанные яблоки с сахаром, мукой, корицей и мускатным орехом."),
                    RecipeStep(stepNumber = 3, instruction = "Выложите начинку в форму и распределите сверху кусочки сливочного масла."),
                    RecipeStep(stepNumber = 4, instruction = "Накройте верхним пластом теста, защипните края, сделайте надрезы для пара и смажьте яйцом."),
                    RecipeStep(stepNumber = 5, instruction = "Выпекайте 45-50 минут до золотистой корочки и появления пузырьков начинки.")
                ),
                tags = listOf(
                    Tag(name = "Десерты"),
                    Tag(name = "Выпечка")
                )
            ),

            // 5. Запеченная курица с травами
            RecipeFullDetails(
                recipe = Recipe(
                    title = "Запеченная курица с травами",
                    description = "Сочная запеченная курица с розмарином, тимьяном, чесноком и лимонным маслом.",
                    imgPath = null,
                    cookingTime = 80,
                    servings = 4,
                    difficulty = "Средне",
                    category = "Ужин",
                    source = "Воскресные обеды",
                    notes = "Дайте постоять 15 минут перед нарезкой, чтобы мясо осталось сочным.",
                    isDraft = false,
                    isFavorite = false,
                    createdAt = now - 200_000L,
                    lastViewedAt = null
                ),
                ingredients = listOf(
                    Ingredient(text = "1 целая курица (около 1,8 кг)", orderIndex = 1),
                    Ingredient(text = "3 ст. л. оливкового масла", orderIndex = 2),
                    Ingredient(text = "4 ст. л. сливочного масла, размягченного", orderIndex = 3),
                    Ingredient(text = "3 веточки свежего розмарина, измельчить", orderIndex = 4),
                    Ingredient(text = "4 веточки свежего тимьяна", orderIndex = 5),
                    Ingredient(text = "1 головка чеснока, разрезанная поперек пополам", orderIndex = 6),
                    Ingredient(text = "1 лимон, разрезанный пополам", orderIndex = 7),
                    Ingredient(text = "Крупная соль и свежемолотый черный перец", orderIndex = 8)
                ),
                steps = listOf(
                    RecipeStep(stepNumber = 1, instruction = "Разогрейте духовку до 220°C. Обсушите курицу бумажными полотенцами."),
                    RecipeStep(stepNumber = 2, instruction = "Смешайте размягченное сливочное масло с розмарином, тимьяном, солью и перцем."),
                    RecipeStep(stepNumber = 3, instruction = "Аккуратно распределите пряное масло под кожей курицы и внутри полости."),
                    RecipeStep(stepNumber = 4, instruction = "Внутрь курицы поместите половинки лимона и головку чеснока."),
                    RecipeStep(stepNumber = 5, instruction = "Свяжите ножки кулинарной нитью и полейте кожу оливковым маслом."),
                    RecipeStep(stepNumber = 6, instruction = "Запекайте 1 час 15 минут до достижения внутренней температуры 74°C.")
                ),
                tags = listOf(
                    Tag(name = "Ужин"),
                    Tag(name = "Птица")
                )
            ),

            // 6. Летний гаспачо
            RecipeFullDetails(
                recipe = Recipe(
                    title = "Летний гаспачо",
                    description = "Освежающий испанский холодный томатный суп со спелыми летними овощами.",
                    imgPath = null,
                    cookingTime = 15,
                    servings = 4,
                    difficulty = "Легко",
                    category = "Супы",
                    source = "Андалусская кухня",
                    notes = "Подавайте хорошо охлажденным в жаркие летние дни.",
                    isDraft = false,
                    isFavorite = false,
                    createdAt = now - 100_000L,
                    lastViewedAt = null
                ),
                ingredients = listOf(
                    Ingredient(text = "1 кг спелых помидоров, крупно нарезанных", orderIndex = 1),
                    Ingredient(text = "1 огурец, очищенный и нарезанный кубиками", orderIndex = 2),
                    Ingredient(text = "1 красный сладкий перец, очищенный от семян", orderIndex = 3),
                    Ingredient(text = "1 зубчик чеснока", orderIndex = 4),
                    Ingredient(text = "2 ст. л. винного уксуса", orderIndex = 5),
                    Ingredient(text = "1/3 стакана оливкового масла extra virgin", orderIndex = 6),
                    Ingredient(text = "1 ч. л. соли", orderIndex = 7)
                ),
                steps = listOf(
                    RecipeStep(stepNumber = 1, instruction = "Соедините помидоры, огурец, сладкий перец и чеснок в блендере."),
                    RecipeStep(stepNumber = 2, instruction = "Измельчите до полностью однородной текстуры."),
                    RecipeStep(stepNumber = 3, instruction = "На низкой скорости блендера медленно влейте уксус и оливковое масло для эмульгации."),
                    RecipeStep(stepNumber = 4, instruction = "Добавьте соль по вкусу."),
                    RecipeStep(stepNumber = 5, instruction = "Охлаждайте в холодильнике не менее 2 часов перед подачей."),
                    RecipeStep(stepNumber = 6, instruction = "Подавайте охлажденным, сбрызнув оливковым маслом и украсив кубиками огурца.")
                ),
                tags = listOf(
                    Tag(name = "Супы"),
                    Tag(name = "Испанская"),
                    Tag(name = "Вегетарианское")
                )
            ),

            // 7. Овсяная каша с кардамоном
            RecipeFullDetails(
                recipe = Recipe(
                    title = "Овсяная каша с кардамоном",
                    description = "Согревающая овсяная каша со свежемолотым кардамоном, ягодами и кленовым сиропом.",
                    imgPath = null,
                    cookingTime = 10,
                    servings = 2,
                    difficulty = "Легко",
                    category = "Завтрак",
                    source = "Скандинавское утро",
                    notes = "Слегка прогрейте семена кардамона на сухой сковороде перед измельчением для яркого аромата.",
                    isDraft = false,
                    isFavorite = false,
                    createdAt = now,
                    lastViewedAt = null
                ),
                ingredients = listOf(
                    Ingredient(text = "1 стакан овсяных хлопьев", orderIndex = 1),
                    Ingredient(text = "2 стакана молока", orderIndex = 2),
                    Ingredient(text = "1/2 ч. л. свежемолотого зеленого кардамона", orderIndex = 3),
                    Ingredient(text = "1 ст. л. кленового сиропа или меда", orderIndex = 4),
                    Ingredient(text = "Щепотка морской соли", orderIndex = 5),
                    Ingredient(text = "Свежие ягоды и лепестки миндаля для подачи", orderIndex = 6)
                ),
                steps = listOf(
                    RecipeStep(stepNumber = 1, instruction = "В небольшом сотейнике соедините овсяные хлопья, молоко, молотый кардамон и соль."),
                    RecipeStep(stepNumber = 2, instruction = "Доведите до легкого кипения на среднем огне, периодически помешивая."),
                    RecipeStep(stepNumber = 3, instruction = "Варите 5-7 минут до кремовой и густой консистенции."),
                    RecipeStep(stepNumber = 4, instruction = "Вмешайте кленовый сироп."),
                    RecipeStep(stepNumber = 5, instruction = "Разложите по тарелкам, украсьте свежими ягодами и миндальными лепестками.")
                ),
                tags = listOf(
                    Tag(name = "Завтрак"),
                    Tag(name = "Полезное")
                )
            )
        )
    }
}
