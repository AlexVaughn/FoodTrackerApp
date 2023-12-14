package com.healthapp_av

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *  Manages the commands that should be executed when a new page is brought into view.
 */
class CommandManager {
    /**
     *  A list of commands that should be executed when a specific page is brought into view.
     */
    private var commands: ArrayList<Command> = ArrayList()

    /**
     *  Queue a new command.
     */
    fun newCommand(command: Command) {
        commands.add(command)
    }

    /**
     *  Goes through the stored list of commands, if the new page being brought into view
     *  is the page assigned to the command, that command will be exectued.
     */
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

/**
 *  A specific action is defined in the doExecute function. When execute is called, the doExecute
 *  code will be run in a coroutine. The received page specifies that the execute function should
 *  be called the next time that page is brought into view.
 */
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