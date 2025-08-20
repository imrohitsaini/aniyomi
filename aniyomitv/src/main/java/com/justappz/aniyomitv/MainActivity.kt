package com.justappz.aniyomitv

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.databinding.ActivityMainBinding
import com.justappz.aniyomitv.navigation.adapter.TabAdapter
import com.justappz.aniyomitv.navigation.model.MainScreenTab

class MainActivity : BaseActivity() {

    //region variables
    private lateinit var binding: ActivityMainBinding
    private var tabAdapter: TabAdapter? = null
    private var tabs = listOf(
        MainScreenTab(0, "Home", true),
        MainScreenTab(1, "Discover"),
        MainScreenTab(2, "Search"),
        MainScreenTab(3, "Settings")
    )
    //endregion

    //region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyPaddingToMainView(binding.main)

        init()
    }
    //endregion

    //region init
    private fun init() {
        tabAdapter = TabAdapter(tabs).apply {
            onItemClick = { tab, _ ->
                if (!tab.isSelected) {
                    tabs.forEach { it.isSelected = false }
                    tab.isSelected = true
                    updateTabs()
                }
            }
        }
        binding.tabs.isNestedScrollingEnabled = false
        binding.tabs.adapter = tabAdapter
    }
    //endregion

    //region updateTabs
    private fun updateTabs() {
        tabAdapter?.updateList(tabs)
        binding.tabs.adapter = tabAdapter
    }
    //endregion
}
