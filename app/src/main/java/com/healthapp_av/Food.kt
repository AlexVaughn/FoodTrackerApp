package com.healthapp_av

import kotlinx.serialization.Serializable
import java.math.RoundingMode

/**
 *  Creates a new Food object from data pulled from the web.
 */
fun foodFromData(id: Int, data: FoodData, tags: ArrayList<String>, imageId: Int): Food {
    return Food(
        id,
        data.name,
        data.calories.toInt(),
        data.serving_size_g,
        data.fat_total_g,
        data.fat_saturated_g,
        data.protein_g,
        data.sodium_mg,
        data.potassium_mg,
        data.cholesterol_mg,
        data.carbohydrates_total_g,
        data.fiber_g,
        data.sugar_g,
        tags,
        imageId,
    )
}

/**
 *  Takes a string of tags formatted like:
 *
 *  'tag1,tag2,tag3'
 *
 *  and turns it into a list.
 */
fun tagsStringToTagList(tags: String): ArrayList<String> {
    return tags.split(",")
        .map { it -> it.trim() } as ArrayList<String>
}

/**
 *  Receives a list of foods, creates a new food object with the sum total of all the fields.
 */
fun totalFoods(foods: ArrayList<Food>): Food {
    var totalFood = Food(-1, "Total", 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, arrayListOf("Total"), -1)
    for (food in foods) {
        totalFood.calories += food.calories
        totalFood.servingSize += food.servingSize
        totalFood.totalFat += food.totalFat
        totalFood.saturatedFat += food.saturatedFat
        totalFood.protein += food.protein
        totalFood.sodium += food.sodium
        totalFood.potassium += food.potassium
        totalFood.cholesterol += food.cholesterol
        totalFood.carbs += food.carbs
        totalFood.fiber += food.fiber
        totalFood.sugar += food.sugar
    }
    totalFood.servingSize = totalFood.servingSize.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    totalFood.totalFat = totalFood.totalFat.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    totalFood.saturatedFat = totalFood.saturatedFat.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    totalFood.protein = totalFood.protein.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    totalFood.sodium = totalFood.sodium.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    totalFood.potassium = totalFood.potassium.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    totalFood.cholesterol = totalFood.cholesterol.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    totalFood.carbs = totalFood.carbs.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    totalFood.fiber = totalFood.fiber.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    totalFood.sugar = totalFood.sugar.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    return totalFood
}

/**
 *  Used by Model to store information about a food. This is used to create items
 *  in the Recycler view.
 */
class Food (
    var id:             Int,
    var name:           String,
    var calories:       Int,
    var servingSize:    Double,
    var totalFat:       Double,
    var saturatedFat:   Double,
    var protein:        Double,
    var sodium:         Double,
    var potassium:      Double,
    var cholesterol:    Double,
    var carbs:          Double,
    var fiber:          Double,
    var sugar:          Double,
    var tags:           ArrayList<String>,
    var imageId:        Int,
) {

    /**
     *  Creates a string formatted like:
     *
     *  'tag1,tag2,tag3'
     *
     *  from the list of tags.
     */
    fun tagsToString(): String {
        if (tags.size == 0) { return "" }
        return tags.reduce { acc, next -> "$acc, $next" }
    }
}

enum class ImageIds(val value: Int) {
    None(-1),
    Fruit(1),
    Vegetable(2),
    Grain(3),
    Protein(4),
    Dairy(5),
}

fun imageNameToId(name: String): Int {
    return when (name) {
        "Fruit" -> ImageIds.Fruit.value
        "Vegetable" -> ImageIds.Vegetable.value
        "Grain" -> ImageIds.Grain.value
        "Protein" -> ImageIds.Protein.value
        "Dairy" -> ImageIds.Dairy.value
        else -> ImageIds.None.value
    }
}

fun imageIdToName(id: Int): String {
    return when (id) {
        ImageIds.Fruit.value -> "Fruit"
        ImageIds.Vegetable.value -> "Vegetable"
        ImageIds.Grain.value -> "Grain"
        ImageIds.Protein.value -> "Protein"
        ImageIds.Dairy.value -> "Dairy"
        else -> "None"
    }
}

/**
 *  Holds data for a food. Can be constructed from a json file. Used by WebData to retrieve data
 *  from the web. Since this is serializable, DO NOT change the field names.
 */
@Serializable
data class FoodData (
    var name:                   String,
    val calories:               Double,
    val serving_size_g:         Double,
    val fat_total_g:            Double,
    val fat_saturated_g:        Double,
    val protein_g:              Double,
    val sodium_mg:              Double,
    val potassium_mg:           Double,
    val cholesterol_mg:         Double,
    val carbohydrates_total_g:  Double,
    val fiber_g:                Double,
    val sugar_g:                Double,
)

/**
 *  Holds a list of FoodData. Can be constructed from a json file. Used by WebData to retrieve data
 *  from the web. Since this is serializable, DO NOT change the field names.
 */
@Serializable
data class FoodDataList(val items: List<FoodData>)