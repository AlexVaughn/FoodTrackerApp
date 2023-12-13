package com.healthapp_av

class Model(private var controller: Controller) {

    private var foodDatabase = FoodDatabase(controller.mainActivity)
    private var webData = WebData()
    var todaysFoods: ArrayList<Food> = ArrayList()
    var myFoods: ArrayList<Food> = ArrayList()

    fun searchWeb(query: String, doWithResult: (Food?) -> Unit) {
        webData.search(query, doWithResult)
    }

    fun getAllFoods(): ArrayList<Food> {
        return foodDatabase.all()
    }

    fun generateNewId(): Int {
        return foodDatabase.generateNewId()
    }

    fun addFood(food: Food) {
        foodDatabase.add(food)
        myFoods.add(food)
    }

    fun removeMyFood(food: Food) {
        foodDatabase.remove(food.id)
        myFoods.remove(food)
        todaysFoods.remove(food)
    }

    fun removeAll() {
        foodDatabase.removeAll()
        myFoods.clear()
        todaysFoods.clear()
    }

    fun editFood(editedFood: Food) {
        foodDatabase.edit(editedFood)
        editMyFood(editedFood)
        editTodaysFood(editedFood)
    }

    fun editMyFood(editedFood: Food) {
        var index = myFoods.indexOfFirst { it.id == editedFood.id }
        if (index != -1) { myFoods[index] = editedFood }
    }

    fun editTodaysFood(editedFood: Food) {
        for (i in 0 until todaysFoods.size) {
            if (todaysFoods[i].id == editedFood.id) {
                todaysFoods[i] = editedFood
            }
        }
    }

    /**
     *  The database must be wiped before filling it with dummy data.
     */
    fun populate() {
        if (myFoods.size == 0) {
            foodDatabase.insertDummyData()
            myFoods = foodDatabase.all()
        }
    }

    fun queryNameOrTag(query: String): ArrayList<Food> {
        return foodDatabase.queryNameOrTag(query)
    }

    fun removeTodaysFood(food: Food) {
        todaysFoods.retainAll { it.id != food.id }
    }
}