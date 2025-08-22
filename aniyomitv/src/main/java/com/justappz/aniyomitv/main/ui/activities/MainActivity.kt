package com.justappz.aniyomitv.main.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.databinding.ActivityMainBinding
import com.justappz.aniyomitv.main.domain.model.MainScreenTab
import com.justappz.aniyomitv.main.ui.adapters.TabAdapter
import com.justappz.aniyomitv.main.ui.fragments.SettingsFragment
import com.justappz.aniyomitv.main.ui.viewmodel.MainViewModel
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MainActivity : BaseActivity(), View.OnClickListener {

    //region variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var tabAdapter: TabAdapter
    private lateinit var tabs: List<MainScreenTab>
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory { MainViewModel(Injekt.get()) }
    }
    //endregion

    //region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        applyPaddingToMainView(binding.main)
        init()
    }
    //endregion

    //region init
    private fun init() {
        setTabsProperties()

        // observers
        observeTabs()

        val tintList = ContextCompat.getColorStateList(ctx, R.color.icon_tint_selector)
        ImageViewCompat.setImageTintList(binding.ivSettings, tintList)
        binding.ivSettings.setOnClickListener(this)
    }
    //endregion

    //region onResume
    override fun onResume() {
        super.onResume()
        // start fetching time
        mainViewModel.startUpdatingTime()
    }
    //endregion

    //region setTabsProperties
    private fun setTabsProperties() {
        //tabs
        binding.tabs.layoutManager = object : LinearLayoutManager(ctx, HORIZONTAL, false) {
            override fun canScrollHorizontally(): Boolean = false
        }
        binding.tabs.isNestedScrollingEnabled = false
        binding.tabs.itemAnimator = null

        //adapter
        tabAdapter = TabAdapter(listOf()).apply {
            onItemClick = { tab, _ -> onTabClicked(tab) }
        }
        tabAdapter.setHasStableIds(true)

        binding.tabs.adapter = tabAdapter
    }
    //endregion

    //region observeTabs
    private fun observeTabs() {
        // Observe tabs from ViewModel
        mainViewModel.tabs.observe(this) { tabs ->
            this.tabs = tabs
            tabAdapter.updateList(tabs)

            // Load the initially selected tab fragment
            tabs.firstOrNull { it.isSelected }?.let { tab ->
                loadFragment(tab.fragment, tab.tag)
            }
        }
    }
    //endregion

    //region loadFragment
    private fun loadFragment(fragment: Fragment, tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, fragment, tag)
                .commit()
        }
    }
    //endregion

    //region onStop
    override fun onStop() {
        super.onStop()
        mainViewModel.stopUpdatingTime()
    }
    //endregion

    //region onTabClicked
    private fun onTabClicked(tab: MainScreenTab) {
        binding.ivSettings.isSelected = false
        loadFragment(tab.fragment, tab.tag)
    }
    //endregion

    //region onClick
    override fun onClick(view: View?) {
        view?.let {
            when (it) {
                binding.ivSettings -> settingIconClicked()
            }
        }
    }
    //endregion

    //region settingIconClicked
    private fun settingIconClicked() {
        if (!binding.ivSettings.isSelected) {
            deselectPreviousTab()
            binding.ivSettings.isSelected = true
            loadFragment(SettingsFragment(), "settings_fragmentx")
        }
    }
    //ending

    //region deselectPreviousTab
    private fun deselectPreviousTab() {
        val prevIndex = tabs.indexOfFirst { it.isSelected }
        if (prevIndex != -1) {
            tabs[prevIndex].isSelected = false
            tabAdapter.notifyItemChanged(prevIndex)
        }
    }
    //endregion
}
