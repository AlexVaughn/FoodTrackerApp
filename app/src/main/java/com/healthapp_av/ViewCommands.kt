package com.healthapp_av

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CommandManager {
    private var commands: ArrayList<Command> = ArrayList()

    fun newCommand(command: Command) {
        commands.add(command)
    }

    fun executeRelevantCommands(newPagePosition: Int) {
        var newCommands: ArrayList<Command> = ArrayList()
        for (command in commands) {
            if (command.pageToExecute.ordinal == newPagePosition) {
                command.execute(newPagePosition)
            }
            else {
                newCommands.add(command)
            }
        }
        commands = newCommands
    }
}


abstract class Command(
    private var pageAdapter: PageAdapter,
    var pageToExecute: Pages,
) {

    abstract fun doExecute(position: Int, view: View)

    /**
     *  Waits for the page to be fully loaded by the adapter and then executes the command.
     */
    fun execute(newPagePosition: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            while (pageAdapter.pages[newPagePosition] == null) { delay(30) }
            doExecute(newPagePosition, pageAdapter.pages[newPagePosition]!!.itemView)
        }
    }
}


class UpdateMyFoods(pageAdapter: PageAdapter, pageToExecute: Pages,
                    private var updateIndex: Int
): Command(pageAdapter, pageToExecute) {

    override fun doExecute(position: Int, view: View) {
        val myFoods = view.findViewById<FoodRecycler>(R.id.myFoodsRecycler)
        myFoods.collapseIfSelected()
        myFoods.foodListAdapter.notifyItemInserted(updateIndex)
    }
}


class ClearTodaysFoods(pageAdapter: PageAdapter, pageToExecute: Pages
): Command(pageAdapter, pageToExecute) {

    override fun doExecute(position: Int, view: View) {
        val overview = view.findViewById<OverView>(R.id.overview)
        overview.foodRecycler.foodListAdapter.notifyDataSetChanged()
        overview.updateTotal()
    }
}


class RemoveSingleTodaysFoods(pageAdapter: PageAdapter, pageToExecute: Pages
): Command(pageAdapter, pageToExecute) {

    override fun doExecute(position: Int, view: View) {
        val overviewRecycler = view.findViewById<FoodRecycler>(R.id.foodRecyclerOverview)
        overviewRecycler.collapseIfSelected()
        overviewRecycler.foodListAdapter.notifyDataSetChanged()
    }
}


class EditSingleOverview(pageAdapter: PageAdapter, pageToExecute: Pages,
                         private var food: Food
): Command(pageAdapter, pageToExecute) {

    override fun doExecute(position: Int, view: View) {
        var overview = view.findViewById<OverView>(R.id.overview)
        overview.foodRecycler.updateItemDetailIfBound(food)
        overview.updateTotal()
    }
}

class EditSingleMyFoods(pageAdapter: PageAdapter, pageToExecute: Pages,
                        private var food: Food
): Command(pageAdapter, pageToExecute) {

    override fun doExecute(position: Int, view: View) {
        var myFoods = view.findViewById<MyFoods>(R.id.myFoods)
        myFoods.foodRecycler.updateItemDetailIfBound(food)
    }
}