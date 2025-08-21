package com.justappz.aniyomitv.main.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.databinding.ActivityMainBinding
import com.justappz.aniyomitv.main.ui.adapters.TabAdapter
import com.justappz.aniyomitv.main.ui.viewmodel.MainViewModel
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MainActivity : BaseActivity() {

    //region variables
    private lateinit var binding: ActivityMainBinding
    private var tabAdapter: TabAdapter? = null
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory { MainViewModel(Injekt.get()) }
    }
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
        binding.tabs.layoutManager = object : LinearLayoutManager(ctx, HORIZONTAL, false) {
            override fun canScrollHorizontally(): Boolean = false
        }
        binding.tabs.isNestedScrollingEnabled = false

        // Observe tabs from ViewModel
        mainViewModel.tabs.observe(this) { tabs ->
            tabAdapter = TabAdapter(tabs.toMutableList()).apply {
                onItemClick = { tab, index ->
                    // Only do click when that tab is not already selected
                    if (!tab.isSelected) {

                        // Find the last selected tab and update it to not selected
                        tabs.forEachIndexed { idx, item ->
                            if (item.isSelected) {
                                item.isSelected = false
                                updateItemAt(idx, item)
                            }
                        }

                        // Select the current tab
                        tab.isSelected = true
                        updateItemAt(index, tab)
                        loadFragment(tab.fragment)
                    }
                }
                tabs.forEach { if (it.isSelected) loadFragment(it.fragment) }
            }
            binding.tabs.adapter = tabAdapter
        }

        // Load tabs from ViewModel
        mainViewModel.loadTabs()
    }
    //endregion

    //region loadFragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
    //endregion

}
