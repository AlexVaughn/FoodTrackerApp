package com.healthapp_av

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class FoodListType {
    TodaysFoods,
    MyFoods,
}

class FoodListAdapter(
    private val context: Context,
    val mainView: MainView,
    val foodListType: FoodListType,
    private val foodRecycler: FoodRecycler,
): RecyclerView.Adapter<FoodItem>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItem {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.food_item, parent, false)
        return FoodItem(view, context, foodRecycler)
    }

    override fun getItemCount(): Int {
        return when (foodListType) {
            FoodListType.TodaysFoods -> mainView.getTodaysFoods().size
            FoodListType.MyFoods -> mainView.getMyFoods().size
        }
    }

    override fun onBindViewHolder(item: FoodItem, position: Int) {
        when (foodListType) {
            FoodListType.TodaysFoods -> item.onBind(mainView.getTodaysFoods()[position])
            FoodListType.MyFoods -> item.onBind(mainView.getMyFoods()[position])
        }
    }
}

class FoodItem(itemView: View, context: Context, foodRecycler: FoodRecycler) : RecyclerView.ViewHolder(itemView) {
    lateinit var food: Food
    private var context = context
    private var detailCycler: LinearLayout = itemView.findViewById(R.id.detailCycler)
    private var image: CustomImage = itemView.findViewById(R.id.customImage)
    private var singleFood: SingleFood = itemView.findViewById(R.id.singleFoodCycler)
    private var foodName: TextView = itemView.findViewById(R.id.foodItemName)
    private var editButton: TextView = itemView.findViewById(R.id.editButton)
    private var deleteButton: TextView = itemView.findViewById(R.id.deleteButton)
    private var removeButton: TextView = itemView.findViewById(R.id.removeMyFoodItem)
    private var foodRecycler = foodRecycler
    private val animationDuration: Long = 200
    private var isExpanded = false

    fun onBind(food: Food) {
        this.food = food
        detailCycler.scaleY = 0f
        detailCycler.visibility = View.GONE
        isExpanded = false
        image.setImage(food, foodRecycler.foodListAdapter.mainView)
        if (image.bitmap == null) {
            image.visibility = View.GONE
            singleFood.setPadding(0, 0, 0, 0)
        }
        else {
            image.visibility = View.VISIBLE
            singleFood.setPadding(0, 15, 0, 0)
        }
        singleFood.fill(food)
        foodName.text = food.name
        itemView.setOnClickListener { onClick() }
        editButton.setOnClickListener { onClickEdit() }
        deleteButton.setOnClickListener { onClickDelete() }
        removeButton.setOnClickListener { onClickRemove() }
        foodName.visibility = View.VISIBLE
        editButton.visibility = View.GONE
        deleteButton.visibility = View.GONE
    }

    private fun onClick() {
        if (isExpanded) { collapse() }
        else { expand() }
    }

    fun updateFood(food: Food) {
        singleFood.fill(food)
        image.setImage(food, foodRecycler.foodListAdapter.mainView)
        foodName.text = food.name
    }

    /**
     *  Remove the item from today's Overview.
     *  This button will only be visible in the today's Overview.
     */
    private fun onClickRemove() {
        foodRecycler.foodListAdapter.mainView.removeTodaysFood(food)
        collapse()
        foodRecycler.foodListAdapter.notifyItemRemoved(adapterPosition)
        foodRecycler.foodListAdapter.mainView.getCurrentPageView().findViewById<OverView>(R.id.overview).updateTotal()
    }

    /**
     *  Edit the selected food in the database.
     *  This button will only be visible in MyFoods.
     */
    private fun onClickEdit() {
        var pageAdapter = foodRecycler.foodListAdapter.mainView.pageAdapter
        var viewPager = foodRecycler.foodListAdapter.mainView.viewPager
        pageAdapter.enableEditor()
        viewPager.currentItem = Pages.Editor.ordinal
        viewPager.post {
            var editor = pageAdapter.pages[Pages.Editor.ordinal]!!.itemView.findViewById<Editor>(R.id.editor)
            editor.configure(pageAdapter, false)
            editor.fill(food)
            editor.workingFood = food
            editor.mode = Editor.EditorMode.Edit
        }
    }

    /**
     *  Remove the item from MyFoods and the database.
     *  This button will only be visible in MyFoods.
     */
    private fun onClickDelete() {
        foodRecycler.foodListAdapter.mainView.removeMyFood(food)
        collapse()
        foodRecycler.foodListAdapter.notifyItemRemoved(adapterPosition)
    }

    fun collapse() {
        val collapseAnimation = ObjectAnimator.ofFloat(detailCycler, "scaleY", 1f, 0f)
        collapseAnimation.duration = animationDuration
        val self = this
        collapseAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                foodName.visibility = View.VISIBLE
                detailCycler.visibility = View.GONE
                editButton.visibility = View.GONE
                deleteButton.visibility = View.GONE
                removeButton.visibility = View.GONE
                CoroutineScope(Dispatchers.Main).launch {
                    if (foodRecycler.selectedItem == self) {
                        foodRecycler.foods.layoutManager!!.scrollToPosition(adapterPosition)
                    }
                    isExpanded = false
                    foodRecycler.selectedItem = null
                }
            }
        })
        collapseAnimation.start()
    }

    private fun expand() {
        val expandAnimation = ObjectAnimator.ofFloat(detailCycler, "scaleY", 0f, 1f)
        detailCycler.visibility = View.VISIBLE
        foodName.visibility = View.GONE
        when (foodRecycler.foodListAdapter.foodListType) {
            FoodListType.MyFoods -> {
                if (foodRecycler.foodListAdapter.mainView.selectingMyFood) {
                    foodName.visibility = View.VISIBLE
                }
                else {
                    editButton.visibility = View.VISIBLE
                    deleteButton.visibility = View.VISIBLE
                }
            }
            FoodListType.TodaysFoods -> removeButton.visibility = View.VISIBLE
        }
        val self = this
        expandAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                foodRecycler.foods.layoutManager!!.scrollToPosition(adapterPosition)
                isExpanded = true
                foodRecycler.selectedItem = self
            }
        })
        if (foodRecycler.selectedItem != null) {
            CoroutineScope(Dispatchers.Main).launch {
                foodRecycler.selectedItem!!.collapse()
                expandAnimation.start()
            }
        }
        else {
            expandAnimation.start()
        }
    }
}