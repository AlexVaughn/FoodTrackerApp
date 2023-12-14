package com.healthapp_av

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 *  Controls how information is presented in the UI. Makes use of drawables and layout XML's
 *  in the res folder.
 */
class MainView(private var controller: Controller) {
    var activity = controller.mainActivity
    /**
     *  Information in the view is presented in pages. The list of pages can be found in the Pages enum.
     */
    var viewPager: ViewPager2
    var navigation: Navigation
    var pageAdapter: PageAdapter
    /**
     *  This is true when the myFoods page is visible and the user is adding a new food to todays log.
     */
    var selectingMyFood = false
    var currentPage: Pages
    private var startPage = Pages.Overview
    private var commandManager = CommandManager()
    /**
     *  When a food is being searched for in the myFoods page, this list will contain
     *  the foods that match the search (tag or name). When getMyFoods is called (by the
     *  foodListAdapter or any other place) if this list is not null, the data within this list
     *  will be return instead of the actual list of myFoods retrieved from the model.
     */
    private var proxyMyFoodsSearch: ArrayList<Food>? = null
    /**
     *  Cached bitmaps to be reused by FoodItem ViewHolders in recyclers.
     */
    lateinit var images: HashMap<Int, Bitmap>

    init {
        controller.mainActivity.setContentView(R.layout.main)
        loadImages(controller.mainActivity)
        viewPager = controller.mainActivity.findViewById(R.id.viewPager)
        pageAdapter = PageAdapter(controller.mainActivity, this)
        viewPager.adapter = pageAdapter
        viewPager.currentItem = startPage.ordinal
        currentPage = startPage
        setOnPageChange()
        navigation = controller.mainActivity.findViewById(R.id.navigation)
        navigation.setBindings(viewPager)
    }

    /**
     *  Initializes the cached bitmaps.
     */
    private fun loadImages(context: Context) {
        images = hashMapOf(
            R.drawable.fruit to BitmapFactory.decodeResource(
                context.resources,
                R.drawable.fruit,
                BitmapFactory.Options()),
            R.drawable.vegetables to BitmapFactory.decodeResource(
                context.resources,
                R.drawable.vegetables,
                BitmapFactory.Options()),
            R.drawable.dairy to BitmapFactory.decodeResource(
                context.resources,
                R.drawable.dairy,
                BitmapFactory.Options()),
            R.drawable.grain to BitmapFactory.decodeResource(
                context.resources,
                R.drawable.grain,
                BitmapFactory.Options()),
            R.drawable.protein to BitmapFactory.decodeResource(
                context.resources,
                R.drawable.protein,
                BitmapFactory.Options()),
        )
    }

    /**
     *  Set the callback for when a page is changed. Calls leavePage for the current page.
     *  Tells the command manager to make any updates to the new page. Then sets the current page.
     */
    private fun setOnPageChange() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                commandManager.executeRelevantCommands(position)
                pageAdapter.leavePage(currentPage)
                currentPage = Pages.values()[position]
            }
        })
    }

    /**
     *  Returns the view inside of the current Page.
     */
    fun getCurrentPageView(): View {
        return pageAdapter.pages[viewPager.currentItem]!!.itemView
    }

    fun searchWeb(query: String, editor: Editor) {
        controller.searchWeb(query) { food: Food? ->
            if (food != null) {
                editor.fill(food)
            }
        }
    }

    /**
     *  Returns myFoods from the model, or the proxyMyFoods if its not null.
     */
    fun getMyFoods(): ArrayList<Food> {
        if (proxyMyFoodsSearch != null) { return proxyMyFoodsSearch!! }
        return controller.getMyFoods()
    }

    fun getTodaysFoods(): ArrayList<Food> {
        return controller.getTodaysFoods()
    }

    fun generateNewId(): Int {
        return controller.generateNewId()
    }

    fun addFoodToModel(food: Food) {
        controller.addFoodToModel(food)
        commandManager.newCommand(UpdateMyFoods(pageAdapter, Pages.MyFoods,
            controller.getMyFoods().size - 1))
    }

    fun addFoodToTodaysFood(food: Food) {
        controller.addFoodToTodaysFood(food)
    }

    fun removeAll() {
        controller.removeAll()
        commandManager.newCommand(ClearTodaysFoods(pageAdapter, Pages.Overview))
    }

    fun removeMyFood(food: Food) {
        controller.removeMyFood(food)
        controller.removeTodaysFood(food)
        commandManager.newCommand(RemoveSingleTodaysFoods(pageAdapter, Pages.Overview))
    }

    fun editFood(editedFood: Food) {
        controller.editFood(editedFood)
        commandManager.newCommand(EditSingleOverview(pageAdapter, Pages.Overview, editedFood))
        commandManager.newCommand(EditSingleMyFoods(pageAdapter, Pages.MyFoods, editedFood))
    }

    fun populate(myFoodsRecycler: FoodRecycler) {
        controller.populate()
        myFoodsRecycler.foodListAdapter.notifyDataSetChanged()
    }

    fun queryNameOrTag(query: String, myFoodsRecycler: FoodRecycler) {
        proxyMyFoodsSearch = controller.queryNameOrTag(query)
        myFoodsRecycler.foodListAdapter.notifyDataSetChanged()
    }

    fun endProxyMyFoodsSearch(myFoodsRecycler: FoodRecycler) {
        proxyMyFoodsSearch = null
        myFoodsRecycler.foodListAdapter.notifyDataSetChanged()
    }

    fun removeTodaysFood(food: Food) {
        controller.removeTodaysFood(food)
    }
}