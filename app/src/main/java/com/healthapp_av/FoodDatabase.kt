package com.healthapp_av

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

private const val CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS "foods" (
        "id"	        INTEGER NOT NULL,
        "name"	        TEXT,
        "calories"	    INTEGER,
        "servingSize"   REAL,
        "totalFat"      REAL,
        "saturatedFat"  REAL,
        "protein"       REAL,
        "sodium"        REAL,
        "potassium"     REAL,
        "cholesterol"   REAL,
        "carbs"         REAL,
        "fiber"         REAL,
        "sugar"         REAL,
        "tags"	        TEXT,
        "imageId"       INTEGER,
        PRIMARY KEY("id")
        );
    """

private const val DUMMY_DATA = """
        INSERT INTO "foods" VALUES (1,'Apple',53,100.0,0.2,0.0,0.3,1.0,11.0,0.0,14.1,2.4,10.3,'fruit,healthy,snack',1);
        INSERT INTO "foods" VALUES (2,'Pizza',262,100.0,9.8,4.5,11.4,587.0,217.0,16.0,32.9,2.3,3.6,'fast food',-1);
        INSERT INTO "foods" VALUES (3,'Vanilla Yogurt',86,100.0,1.2,0.8,4.9,66.0,134.0,5.0,13.7,0.0,13.7,'healthy,dairy',5);
        INSERT INTO "foods" VALUES (4,'Banana',89,100.0,0.3,0.1,1.1,1.0,22.0,0.0,23.2,2.6,12.3,'fruit,healthy,snack',1);
        INSERT INTO "foods" VALUES (5,'Hamburger',242,100.0,11.8,4.7,15.2,349.0,138.0,54.0,17.9,0.0,0.0,'fast food',4);
        INSERT INTO "foods" VALUES (6,'Orange',50,100.0,0.1,0.0,0.9,1.0,23.0,0.0,12.4,2.2,8.4,'fruit,healthy,snack',1);
        INSERT INTO "foods" VALUES (7,'Pasta',156,100.0,0.9,0.2,5.7,1.0,58.0,0.0,31.3,1.8,0.6,'italian',3);
        INSERT INTO "foods" VALUES (8,'Broccoli',35,100.0,0.4,0.1,2.4,41.0,65.0,0.0,7.3,3.3,1.4,'vegetable,healthy,side',2);
        INSERT INTO "foods" VALUES (9,'Chocolate Ice Cream',218,100.0,10.8,6.8,3.8,75.0,107.0,33.0,28.6,1.2,25.6,'dessert,snack',-1);
        INSERT INTO "foods" VALUES (10,'Steak',273,100.0,18.8,7.3,26.0,52.0,194.0,95.0,0.0,0.0,0.0,'meat,protein',4);
        INSERT INTO "foods" VALUES (11,'Salmon',208,100.0,12.1,2.4,22.0,61.0,253.0,63.0,0.0,0.0,0.0,'seafood,healthy,protein',4);
        INSERT INTO "foods" VALUES (12,'Milk',51,100.0,1.9,1.2,3.5,52.0,100.0,8.0,4.9,0.0,0.0,'beverage,dairy',5);
        INSERT INTO "foods" VALUES (13,'Rice',127,100.0,0.3,0.1,2.7,1.0,42.0,0.0,28.4,0.4,0.1,'grain,carb',3);
        INSERT INTO "foods" VALUES (14,'Egg',147,100.0,9.7,3.1,12.5,139.0,199.0,371.0,0.7,0.0,0.4,'protein',4);
        INSERT INTO "foods" VALUES (15,'Carrot',34,100.0,0.2,0.0,0.8,57.0,30.0,0.0,8.3,3.0,3.4,'vegetable,healthy',2);
    """

/**
 *  A sqLite database. Stores food information.
 */
class FoodDatabase(context: Context): SQLiteOpenHelper(context, "FoodDatabase", null, 1) {

    private val tableName = "foods"

    init {
        reset(writableDatabase)
        insertDummyData()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        reset(db)
    }

    private fun reset(db: SQLiteDatabase?) {
        db?.execSQL("DROP TABLE IF EXISTS foods")
        onCreate(db)
    }

    fun insertDummyData() {
        DUMMY_DATA.trimIndent().split("\n")
            .forEach { writableDatabase.execSQL(it) }
    }

    private fun cursorToFoodList(cursor: Cursor?): ArrayList<Food> {
        var foods: ArrayList<Food> = ArrayList()
        if (cursor == null) { return foods }
        with(cursor) {
            while (moveToNext()) {
                foods.add(Food(
                    getInt(nullableIndex(getColumnIndex("id"))!!),
                    getString(nullableIndex(getColumnIndex("name"))!!),
                    getInt(nullableIndex(getColumnIndex("calories"))!!),
                    getDouble(nullableIndex(getColumnIndex("servingSize"))!!),
                    getDouble(nullableIndex(getColumnIndex("totalFat"))!!),
                    getDouble(nullableIndex(getColumnIndex("saturatedFat"))!!),
                    getDouble(nullableIndex(getColumnIndex("protein"))!!),
                    getDouble(nullableIndex(getColumnIndex("sodium"))!!),
                    getDouble(nullableIndex(getColumnIndex("potassium"))!!),
                    getDouble(nullableIndex(getColumnIndex("cholesterol"))!!),
                    getDouble(nullableIndex(getColumnIndex("carbs"))!!),
                    getDouble(nullableIndex(getColumnIndex("fiber"))!!),
                    getDouble(nullableIndex(getColumnIndex("sugar"))!!),
                    tagsStringToTagList(getString(nullableIndex(getColumnIndex("tags"))!!)),
                    getInt(nullableIndex(getColumnIndex("imageId"))!!),
                ))
            }
        }
        return foods
    }

    private fun nullableIndex(index: Int): Int? {
        if (index == -1) { return null }
        return index
    }

    fun generateNewId(): Int {
        return all().maxBy { it.id }.id + 1
    }

    fun all(): ArrayList<Food> {
        return cursorToFoodList(readableDatabase.rawQuery("SELECT * FROM $tableName", null))
    }

    fun add(food: Food) {
        writableDatabase.execSQL("INSERT INTO \"$tableName\" VALUES (" +
                "${food.id}," +
                "'${food.name}'," +
                "${food.calories}," +
                "${food.servingSize}," +
                "${food.totalFat}," +
                "${food.saturatedFat}," +
                "${food.protein}," +
                "${food.sodium}," +
                "${food.potassium}," +
                "${food.cholesterol}," +
                "${food.carbs}," +
                "${food.fiber}," +
                "${food.sugar}," +
                "'${food.tagsToString()}'," +
                "${food.imageId}" +
                ");");
    }

    fun edit(editedFood: Food) {
        val contentValues = ContentValues()
        contentValues.put("name", editedFood.name)
        contentValues.put("calories", editedFood.calories)
        contentValues.put("servingSize", editedFood.servingSize)
        contentValues.put("totalFat", editedFood.totalFat)
        contentValues.put("saturatedFat", editedFood.saturatedFat)
        contentValues.put("protein", editedFood.protein)
        contentValues.put("sodium", editedFood.sodium)
        contentValues.put("potassium", editedFood.potassium)
        contentValues.put("cholesterol", editedFood.cholesterol)
        contentValues.put("carbs", editedFood.carbs)
        contentValues.put("fiber", editedFood.fiber)
        contentValues.put("sugar", editedFood.sugar)
        contentValues.put("tags", editedFood.tagsToString())
        contentValues.put("imageId", editedFood.imageId)
        val whereClause = "id=?"
        val whereArgs = arrayOf(editedFood.id.toString())
        writableDatabase.update(tableName, contentValues, whereClause, whereArgs)
    }

    fun remove(id: Int) {
        val whereClause = "id=?"
        val whereArgs = arrayOf(id.toString())
        writableDatabase.delete(tableName, whereClause, whereArgs)
    }

    fun removeAll() {
        writableDatabase.delete(tableName, null, null)
        reset(writableDatabase)
    }

    fun queryNameOrTag(query: String): ArrayList<Food> {
        return cursorToFoodList(readableDatabase.rawQuery(
            "SELECT * FROM $tableName WHERE name LIKE '%$query%' OR tags LIKE '%$query%'", null))
    }
}