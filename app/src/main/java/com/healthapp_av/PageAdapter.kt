package com.healthapp_av

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *  This enum is used for matching a page layout to an index.
 */
enum class Pages {
    Overview,
    MyFoods,
    CreateNew,
    Editor,
}

/**
 *  Adapter for the ViewPager in MainView. The user may swipe left or right or use the navigation
 *  bar to cycle through the pages. 1 page takes up the whole screen, except for the navigation bar.
 *  The ViewHolder items for this recyclerView is a Page class. There are 4 pages. Overview, MyFoods,
 *  and CreateNew are accessible by swiping or via the navigation bar. The 4th page, the editor, is
 *  locked by default, it is only accessible through the CreateNew page or by clicking edit on a FoodItem.
 */
class PageAdapter(
    private var context: Context,
    private var mainView: MainView,
):RecyclerView.Adapter<Page>() {

    /**
     *  Holds references to each page. Initialized to null. When a page is created or bound, the
     *  reference will be updated in this list.
     */
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

    /**
     *  Unlocks the editor page.
     */
    fun enableEditor() {
        allowEditor = true
        notifyItemInserted(Pages.Editor.ordinal)
    }

    /**
     *  Locks the editor page.
     */
    fun disableEditor() {
        allowEditor = false
        notifyItemRemoved(Pages.Editor.ordinal)
    }

    /**
     *  Called by MainView when a new page is being moved into view. This function is typically used
     *  to cleanup the page that is being left (such as emptying fields in the editor).
     */
    fun leavePage(currentPage: Pages) {
        when (currentPage) {
            Pages.MyFoods -> pages[currentPage.ordinal]!!.itemView.findViewById<MyFoods>(R.id.myFoods).onPageExit()
            Pages.Editor -> pages[currentPage.ordinal]!!.itemView.findViewById<Editor>(R.id.editor).onPageExit()
            else -> {}
        }
    }
}

/**
 *  ViewHolder used in the ViewPager recycler/adapter.
 */
class Page(itemView: View): RecyclerView.ViewHolder(itemView) {}
