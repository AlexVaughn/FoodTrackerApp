package com.healthapp_av

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.viewpager2.widget.ViewPager2

class MainView(private var controller: Controller) {
    var activity = controller.mainActivity
    var viewPager: ViewPager2
    var navigation: Navigation
    var pageAdapter: PageAdapter
    var selectingMyFood = false
    var currentPage: Pages
    private var startPage = Pages.Overview
    private var commandManager = CommandManager()
    private var proxyMyFoodsSearch: ArrayList<Food>? = null
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