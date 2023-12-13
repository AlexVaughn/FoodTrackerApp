package com.healthapp_av

import android.util.Log

class Controller(
    var mainActivity: MainActivity
) {
    private var model: Model
    private var view: MainView

    init {
        model = Model(this)
        model.myFoods = model.getAllFoods()
        model.todaysFoods = ArrayList(model.myFoods.shuffled().take(5))
        view = MainView(this)
    }

    fun searchWeb(query: String, doWithResult: (Food?) -> Unit) {
        model.searchWeb(query, doWithResult)
    }

    fun generateNewId(): Int {
        return model.generateNewId()
    }

    fun addFoodToModel(food: Food) {
        model.addFood(food)
    }

    fun getMyFoods(): ArrayList<Food> {
        return model.myFoods
    }

    fun getTodaysFoods(): ArrayList<Food> {
        return model.todaysFoods
    }

    fun addFoodToTodaysFood(food: Food) {
        model.todaysFoods.add(food)
    }

    fun removeAll() {
        model.removeAll()
    }

    fun removeMyFood(food: Food) {
        model.removeMyFood(food)
    }

    fun editFood(editedFood: Food) {
        model.editFood(editedFood)
    }

    fun populate() {
        model.populate()
    }

    fun queryNameOrTag(query: String): ArrayList<Food> {
        return model.queryNameOrTag(query)
    }

    fun removeTodaysFood(food: Food) {
        model.removeTodaysFood(food)
    }
}