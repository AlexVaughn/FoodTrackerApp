package com.healthapp_av

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.media.Image
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OverView(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {
    private lateinit var mainView: MainView
    private var dayReport: DayReport
    var singleFood: SingleFood
    private var addFoodButton: CardView
    var foodRecycler: FoodRecycler

    init {
        LayoutInflater.from(context).inflate(R.layout.overview, this, true)
        dayReport = findViewById(R.id.dayReport)
        singleFood = findViewById(R.id.singleFoodOverview)
        addFoodButton = findViewById(R.id.addTodayFood)
        foodRecycler = findViewById(R.id.foodRecyclerOverview)
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.date).text = sdf.format(Date())
    }

    fun onBind(mainView: MainView) {
        this.mainView = mainView
        updateTotal()
        foodRecycler.setAdapter(mainView, FoodListType.TodaysFoods)
        foodRecycler.foodListAdapter.notifyDataSetChanged()
        addFoodButton.setOnClickListener { onAddFoodClick() }
    }

    fun updateTotal() {
        singleFood.fill(totalFoods(mainView.getTodaysFoods()))
    }

    private fun onAddFoodClick() {
        mainView.selectingMyFood = true
        mainView.viewPager.currentItem = Pages.MyFoods.ordinal
        mainView.viewPager.post {
            mainView.viewPager.isUserInputEnabled = false
            mainView.navigation.setSelectionState( {onCancelClick()}, {onConfirmClick()})
        }
    }

    private fun onCancelClick() {
        mainView.getCurrentPageView().findViewById<FoodRecycler>(R.id.myFoodsRecycler).collapseIfSelected()
        mainView.navigation.endSelectionState()
        mainView.viewPager.isUserInputEnabled = true
        mainView.viewPager.currentItem = Pages.Overview.ordinal
        mainView.selectingMyFood = false
    }

    private fun onConfirmClick() {
        var myFoodsRecycler: FoodRecycler = mainView.getCurrentPageView().findViewById(R.id.myFoodsRecycler)
        if (myFoodsRecycler!!.selectedItem != null) {
            mainView.addFoodToTodaysFood(myFoodsRecycler!!.selectedItem!!.food)
            foodRecycler.foodListAdapter.notifyItemInserted(mainView.getTodaysFoods().size - 1)
            singleFood.fill(totalFoods(mainView.getTodaysFoods()))
            myFoodsRecycler.collapseIfSelected()
            mainView.navigation.endSelectionState()
            mainView.viewPager.isUserInputEnabled = true
            mainView.viewPager.currentItem = Pages.Overview.ordinal
            mainView.selectingMyFood = false
        }
    }
}


class DayReport(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {
    private var date: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.day_report, this, true)
        date = findViewById(R.id.date)
    }
}


class SingleFood(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {
    private var name: TextView
    private var calories: TextView
    private var servingSize: TextView
    private var totalFat: TextView
    private var saturatedFat: TextView
    private var protein: TextView
    private var sodium: TextView
    private var potassium: TextView
    private var cholesterol: TextView
    private var carbs: TextView
    private var fiber: TextView
    private var sugar: TextView
    private var tags: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.single_food, this, true)
        name = findViewById(R.id.name)
        calories = findViewById(R.id.calories)
        servingSize = findViewById(R.id.servingSize)
        totalFat = findViewById(R.id.totalFat)
        saturatedFat = findViewById(R.id.saturatedFat)
        protein = findViewById(R.id.protein)
        sodium = findViewById(R.id.sodium)
        potassium = findViewById(R.id.potassium)
        cholesterol = findViewById(R.id.cholesterol)
        carbs = findViewById(R.id.carbs)
        fiber = findViewById(R.id.fiber)
        sugar = findViewById(R.id.sugar)
        tags = findViewById(R.id.tags)
    }

    fun fill(food: Food) {
        name.text = "Name: ${food.name}"
        calories.text = "Calories: ${food.calories}"
        servingSize.text = "Serving Size: ${food.servingSize}"
        totalFat.text = "Total Fat: ${food.totalFat}"
        saturatedFat.text = "Saturated Fat: ${food.saturatedFat}"
        protein.text = "Protein: ${food.protein}"
        sodium.text = "Sodium: ${food.sodium}"
        potassium.text = "Potassium: ${food.potassium}"
        cholesterol.text = "Cholesterol: ${food.cholesterol}"
        carbs.text = "Carbs: ${food.carbs}"
        fiber.text = "Fiber: ${food.fiber}"
        sugar.text = "Sugar: ${food.sugar}"
        tags.text = "Tags: ${food.tagsToString()}"
    }
}


class CustomImage(context: Context, attrs: AttributeSet? = null): View(context, attrs) {
    var bitmap: Bitmap? = null
    private var rect: RectF = RectF()

    init {
        pivotY = measuredHeight.toFloat()
        rect.left = 0F
        rect.top = 0F
        rect.right = 1000F
        rect.bottom = 1000F
    }

    override fun onDraw(canvas: Canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap!!, null, rect, null)
        }
    }

    fun setImage(food: Food, mainView: MainView) {
        val oldBitmap = bitmap
        bitmap = when (food.imageId) {
            ImageIds.None.value -> null
            ImageIds.Fruit.value -> mainView.images[R.drawable.fruit]
            ImageIds.Vegetable.value -> mainView.images[R.drawable.vegetables]
            ImageIds.Grain.value -> mainView.images[R.drawable.grain]
            ImageIds.Protein.value -> mainView.images[R.drawable.protein]
            ImageIds.Dairy.value -> mainView.images[R.drawable.dairy]
            else -> null
        }
        if (bitmap != oldBitmap) {
            this.invalidate()
        }
    }
}


class FoodRecycler(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {
    var foods: RecyclerView
    lateinit var foodListAdapter: FoodListAdapter
    var selectedItem: FoodItem? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.food_recycler, this, true)
        foods = findViewById(R.id.foodRecycler)
        foods.layoutManager = LinearLayoutManager(context)
    }

    fun setAdapter(mainView: MainView, foodListType: FoodListType) {
        foodListAdapter = FoodListAdapter(context, mainView, foodListType, this)
        foods.adapter = foodListAdapter
    }

    fun updateItemDetailIfBound(food: Food) {
        for (child in foods.children) {
            var foodItem = foods.getChildViewHolder(child) as FoodItem
            if (foodItem.food.id == food.id) {
                foodItem.updateFood(food)
            }
        }
    }

    fun collapseIfSelected() {
        if (selectedItem != null) {
            selectedItem!!.collapse()
        }
    }
}


class Navigation(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {
    private lateinit var viewPager: ViewPager2
    private var overviewNav: TextView
    private var myFoodsNav: TextView
    private var createNewNav: TextView
    private var cancelButton: TextView
    private var confirmButton: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.navigation, this, true)
        overviewNav = findViewById(R.id.overviewNav)
        myFoodsNav = findViewById(R.id.myFoodsNav)
        createNewNav = findViewById(R.id.createNewNav)
        cancelButton = findViewById(R.id.cancelEditButton)
        confirmButton = findViewById(R.id.confirmEditButton)
    }

    fun setBindings(viewPager: ViewPager2) {
        this.viewPager = viewPager
        overviewNav.setOnClickListener { onClickOverview() }
        myFoodsNav.setOnClickListener { onClickMyFoods() }
        createNewNav.setOnClickListener { onClickCreateNew() }
    }

    fun setSelectionState(onClickCancel: () -> Unit, onClickConfirm: () -> Unit) {
        overviewNav.visibility = GONE
        myFoodsNav.visibility = GONE
        createNewNav.visibility = GONE
        cancelButton.visibility = VISIBLE
        confirmButton.visibility = VISIBLE
        cancelButton.setOnClickListener { onClickCancel() }
        confirmButton.setOnClickListener { onClickConfirm() }
    }

    fun endSelectionState() {
        overviewNav.visibility = VISIBLE
        myFoodsNav.visibility = VISIBLE
        createNewNav.visibility = VISIBLE
        cancelButton.visibility = GONE
        confirmButton.visibility = GONE
        cancelButton.setOnClickListener(null)
        confirmButton.setOnClickListener(null)
    }

    private fun onClickOverview() {
        if (viewPager.currentItem != Pages.Overview.ordinal) { viewPager.currentItem = Pages.Overview.ordinal }
    }

    private fun onClickMyFoods() {
        if (viewPager.currentItem != Pages.MyFoods.ordinal) { viewPager.currentItem = Pages.MyFoods.ordinal }
    }

    private fun onClickCreateNew() {
        if (viewPager.currentItem != Pages.CreateNew.ordinal) { viewPager.currentItem = Pages.CreateNew.ordinal }
    }
}


class MyFoods(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {
    private lateinit var mainView: MainView
    var foodRecycler: FoodRecycler
    private var searchBar: SearchView
    private var clearButton: TextView
    private var populateButton: TextView
    private var searchHolder: FrameLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.my_foods, this, true)
        foodRecycler = findViewById(R.id.myFoodsRecycler)
        searchBar = findViewById(R.id.searchMyFoods)
        clearButton = findViewById(R.id.clearButton)
        populateButton = findViewById(R.id.populateButton)
        searchHolder = findViewById(R.id.searchHolder)
    }

    fun onBind(mainView: MainView) {
        this.mainView = mainView
        foodRecycler.setAdapter(mainView, FoodListType.MyFoods)
        foodRecycler.foodListAdapter.notifyDataSetChanged()
        clearButton.setOnClickListener { onClickClearButton() }
        populateButton.setOnClickListener { onClickPopulateButton() }
        searchHolder.setOnClickListener { onClickSearchHolder() }
        setSearchBinding()
    }

    fun onPageExit() {
        clearSearchBar()
        foodRecycler.collapseIfSelected()
    }

    private fun onClickSearchHolder() {
        searchBar.isIconified = false
    }

    private fun setSearchBinding() {
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                foodRecycler.collapseIfSelected()
                // If the search bar is closed, this will be empty
                if (newText.isEmpty()) {
                    mainView.endProxyMyFoodsSearch(foodRecycler)
                }
                else {
                    mainView.queryNameOrTag(newText, foodRecycler)
                }
                return true
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        searchBar.setOnQueryTextFocusChangeListener { _, hasFocus ->
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            val currentFocusedView = mainView.activity.currentFocus
            if (currentFocusedView != null) {
                if (hasFocus) {
                    inputMethodManager.hideSoftInputFromWindow(
                        currentFocusedView.windowToken,
                        InputMethodManager.RESULT_SHOWN)
                }
                else {
                    inputMethodManager.hideSoftInputFromWindow(
                        currentFocusedView.windowToken,
                        InputMethodManager.HIDE_IMPLICIT_ONLY)
                }
            }
            true
        }
    }

    private fun clearSearchBar() {
        searchBar.setQuery("", false)
        searchBar.clearFocus()
    }

    private fun onClickClearButton() {
        clearSearchBar()
        mainView.removeAll()
        foodRecycler.foodListAdapter.notifyDataSetChanged()
    }

    private fun onClickPopulateButton() {
        mainView.populate(foodRecycler)
        foodRecycler.foodListAdapter.notifyDataSetChanged()
    }
}


class Selector(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {
    var description: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.selector, this, true)
        description = findViewById(R.id.description)
    }
}


class CreateNew(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {
    private lateinit var viewPager: ViewPager2
    private lateinit var pageAdapter: PageAdapter
    private var selector1: Selector
    private var selector2: Selector

    init {
        LayoutInflater.from(context).inflate(R.layout.create_new, this, true)
        selector1 = findViewById(R.id.selector1)
        selector2 = findViewById(R.id.selector2)
        selector1.description.text = "Create a food by\nsearching the web"
        selector2.description.text = "Create a new\ncustom food"
    }

    fun setViewPagerBindings(viewPager: ViewPager2, pageAdapter: PageAdapter) {
        this.viewPager = viewPager
        this.pageAdapter = pageAdapter
        selector1.setOnClickListener { onClickSelector1() }
        selector2.setOnClickListener { onClickSelector2() }
    }

    private fun onClickSelector1() {
        pageAdapter.enableEditor()
        viewPager.currentItem = Pages.Editor.ordinal
        viewPager.post {
            pageAdapter.pages[Pages.Editor.ordinal]!!.itemView
                .findViewById<Editor>(R.id.editor).configure(pageAdapter, true)
        }
    }

    private fun onClickSelector2() {
        pageAdapter.enableEditor()
        viewPager.currentItem = Pages.Editor.ordinal
        viewPager.post {
            pageAdapter.pages[Pages.Editor.ordinal]!!.itemView
                .findViewById<Editor>(R.id.editor).configure(pageAdapter, false)
        }
    }
}


class Editor(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {
    private lateinit var mainView: MainView
    private lateinit var pageAdapter: PageAdapter
    var mode: EditorMode = EditorMode.Create
    var workingFood: Food? = null
    private var searchBar: SearchView
    private var nameField: TextInputEditText
    private var caloriesField: TextInputEditText
    private var servingSizeField: TextInputEditText
    private var totalFatField: TextInputEditText
    private var saturatedFatField: TextInputEditText
    private var proteinField: TextInputEditText
    private var sodiumField: TextInputEditText
    private var potassiumField: TextInputEditText
    private var cholesterolField: TextInputEditText
    private var carbsField: TextInputEditText
    private var fiberField: TextInputEditText
    private var sugarField: TextInputEditText
    private var tagsField: TextInputEditText
    private var imageSpinner: Spinner
    private lateinit var spinnerItems: List<String>

    init {
        LayoutInflater.from(context).inflate(R.layout.editor, this, true)
        searchBar = findViewById(R.id.searchBar)
        nameField = findViewById(R.id.nameField)
        caloriesField = findViewById(R.id.caloriesField)
        servingSizeField = findViewById(R.id.servingSizeField)
        totalFatField = findViewById(R.id.totalFatField)
        saturatedFatField = findViewById(R.id.saturatedFatField)
        proteinField = findViewById(R.id.proteinField)
        sodiumField = findViewById(R.id.sodiumField)
        potassiumField = findViewById(R.id.potassiumField)
        cholesterolField = findViewById(R.id.cholesterolField)
        carbsField = findViewById(R.id.carbsField)
        fiberField = findViewById(R.id.fiberField)
        sugarField = findViewById(R.id.sugarField)
        tagsField = findViewById(R.id.tagsField)
        imageSpinner = findViewById(R.id.imageSpinner)
        initializeSpinner()
    }

    private fun initializeSpinner() {
        spinnerItems = listOf("None", "Fruit", "Vegetable", "Grain", "Protein", "Dairy")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        imageSpinner.adapter = adapter
    }

    fun onPageExit() {
        clearSearchBar()
        clearFields()
    }

    private fun clearSearchBar() {
        searchBar.setQuery("", false)
        searchBar.clearFocus()
    }

    fun fill(food: Food) {
        nameField.setText(food.name)
        caloriesField.setText(food.calories.toString())
        servingSizeField.setText(food.servingSize.toString())
        totalFatField.setText(food.totalFat.toString())
        saturatedFatField.setText(food.saturatedFat.toString())
        proteinField.setText(food.protein.toString())
        sodiumField.setText(food.sodium.toString())
        potassiumField.setText(food.potassium.toString())
        cholesterolField.setText(food.cholesterol.toString())
        carbsField.setText(food.carbs.toString())
        fiberField.setText(food.fiber.toString())
        sugarField.setText(food.sugar.toString())
        tagsField.setText(food.tagsToString())
        imageSpinner.setSelection(spinnerItems.indexOf(imageIdToName(food.imageId)))
    }

    private fun createFoodFromFields(): Food? {
        try {
            val id = when (mode) {
                EditorMode.Create -> mainView.generateNewId()
                EditorMode.Edit -> workingFood!!.id
            }
            return Food(
                id,
                nameField.text.toString(),
                caloriesField.text.toString().toInt(),
                servingSizeField.text.toString().toDouble(),
                totalFatField.text.toString().toDouble(),
                saturatedFatField.text.toString().toDouble(),
                proteinField.text.toString().toDouble(),
                sodiumField.text.toString().toDouble(),
                potassiumField.text.toString().toDouble(),
                cholesterolField.text.toString().toDouble(),
                carbsField.text.toString().toDouble(),
                fiberField.text.toString().toDouble(),
                sugarField.text.toString().toDouble(),
                tagsStringToTagList(tagsField.text.toString()),
                imageNameToId(imageSpinner.selectedItem.toString()),
            )
        }
        catch (e: Exception) { return null }
    }

    private fun clearFields() {
        nameField.setText("")
        caloriesField.setText("")
        servingSizeField.setText("")
        totalFatField.setText("")
        saturatedFatField.setText("")
        proteinField.setText("")
        sodiumField.setText("")
        potassiumField.setText("")
        cholesterolField.setText("")
        carbsField.setText("")
        fiberField.setText("")
        sugarField.setText("")
        tagsField.setText("")
        imageSpinner.setSelection(spinnerItems.indexOf("None"))
    }

    fun configure(pageAdapter: PageAdapter, asWeb: Boolean) {
        this.mainView = mainView
        this.pageAdapter = pageAdapter
        if (asWeb) {
            searchBar.isIconified = false
            searchBar.visibility = VISIBLE
        }
        else {
            searchBar.isIconified = true
            searchBar.visibility = GONE
        }
    }

    fun setBindings(mainView: MainView) {
        this.mainView = mainView
        val self = this
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                mainView.searchWeb(query, self)
                return true
            }
        })
        mainView.navigation.setSelectionState( { onClickCancel() }, { onClickConfirm() })
    }

    private fun onClickCancel() {
        clearFields()
        pageAdapter.disableEditor()
        mainView.navigation.endSelectionState()
        mode = EditorMode.Create
    }

    private fun onClickConfirm() {
        when (mode) {
            EditorMode.Create -> {
                var food = createFoodFromFields()
                if (food != null) {
                    mainView.addFoodToModel(food!!)
                    clearFields()
                    pageAdapter.disableEditor()
                    mainView.navigation.endSelectionState()
                }
            }
            EditorMode.Edit -> {
                var food = createFoodFromFields()
                if (food != null) {
                    mainView.editFood(food)
                    clearFields()
                    pageAdapter.disableEditor()
                    mainView.navigation.endSelectionState()
                    workingFood = null
                    mode = EditorMode.Create
                }
            }
        }
    }

    enum class EditorMode {
        Create,
        Edit,
    }
}