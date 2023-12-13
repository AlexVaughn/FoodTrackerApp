package com.healthapp_av

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *  Used for matching a page layout to an index.
 */
enum class Pages {
    Overview,
    MyFoods,
    CreateNew,
    Editor,
}

class PageAdapter(
    private var context: Context,
    private var mainView: MainView,
):RecyclerView.Adapter<Page>() {

    var pages: MutableList<Page?> = MutableList(Pages.entries.size) { null }
    private var allowEditor = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Page {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = when (viewType) {
            Pages.Overview.ordinal -> inflater.inflate(R.layout.page0, parent, false)
            Pages.MyFoods.ordinal -> inflater.inflate(R.layout.page1, parent, false)
            Pages.CreateNew.ordinal -> inflater.inflate(R.layout.page2, parent, false)
            Pages.Editor.ordinal -> inflater.inflate(R.layout.page3, parent, false)
            else -> null
        }!!
        return Page(view)
    }

    override fun getItemCount(): Int {
        if (!allowEditor) { return pages.size - 1 }
        return pages.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(item: Page, position: Int) {
        pages[position] = item
        when (position) {
            Pages.Overview.ordinal -> item.itemView.findViewById<OverView>(R.id.overview).onBind(mainView)
            Pages.MyFoods.ordinal -> item.itemView.findViewById<MyFoods>(R.id.myFoods).onBind(mainView)
            Pages.CreateNew.ordinal -> item.itemView.findViewById<CreateNew>(R.id.createNew)
                .setViewPagerBindings(mainView.viewPager, this)
            Pages.Editor.ordinal -> item.itemView.findViewById<Editor>(R.id.editor)
                .setBindings(mainView)
        }
    }

    fun enableEditor() {
        allowEditor = true
        notifyItemInserted(Pages.Editor.ordinal)
    }

    fun disableEditor() {
        allowEditor = false
        notifyItemRemoved(Pages.Editor.ordinal)
    }

    fun leavePage(currentPage: Pages) {
        when (currentPage) {
            Pages.MyFoods -> pages[currentPage.ordinal]!!.itemView.findViewById<MyFoods>(R.id.myFoods).onPageExit()
            Pages.Editor -> pages[currentPage.ordinal]!!.itemView.findViewById<Editor>(R.id.editor).onPageExit()
            else -> {}
        }
    }
}

class Page(itemView: View): RecyclerView.ViewHolder(itemView) {}
