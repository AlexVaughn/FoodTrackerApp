package com.healthapp_av

/**
 *  Maintains data. Contains two primary components.
 *  1. FoodDatabase which is a SQLite database.
 *  2. WebData which can access a web api and retrieve new food data.
 */
class Model(private var controller: Controller) {

    private var foodDatabase = FoodDatabase(controller.mainActivity)
    private var webData = WebData()
    /**
     *  A list of foods for today that the user has logged. Presented in the overview.
     */
    var todaysFoods: ArrayList<Food> = ArrayList()
    /**
     *  A list of food objects that mirror what is in the databse.
     */
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

    /**
     *  Add a new food to the database and myFoods.
     */
    fun addFood(food: Food) {
        foodDatabase.add(food)
        myFoods.add(food)
    }

    /**
     *  Remove a single food from the database, myFoods, and todaysFoods.
     */
    fun removeMyFood(food: Food) {
        foodDatabase.remove(food.id)
        myFoods.remove(food)
        todaysFoods.remove(food)
    }

    /**
     *  Empty the database, myFoods, and todaysFoods.
     */
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
     *  Tells the database to fill itself with dummy data.
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