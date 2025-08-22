package com.justappz.aniyomitv.main.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
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

class MainActivity : BaseActivity(), View.OnFocusChangeListener{

    //region variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var tabAdapter: TabAdapter
    private lateinit var tabs: List<MainScreenTab>
    private val tag = "MainActivity"
    private var lastSelectedPosition: Int = RecyclerView.NO_POSITION
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
        binding.ivSettings.onFocusChangeListener = this
    }
    //endregion

    //region onResume
    override fun onResume() {
        super.onResume()
        // start fetching time
        mainViewModel.startUpdatingTime()
        mainViewModel.loadTabs()
    }
    //endregion

    //region setTabsProperties
    private fun setTabsProperties() {
        //adapter
        // 1. Create adapter
        tabAdapter = TabAdapter()

        // 2. Wrap in ItemBridgeAdapter for Leanback
        val itemBridgeAdapter = ItemBridgeAdapter(tabAdapter)
        binding.tabs.adapter = itemBridgeAdapter


        //tabs
        // Handle tab click
        binding.tabs.setOnChildViewHolderSelectedListener(
            object : OnChildViewHolderSelectedListener() {
                override fun onChildViewHolderSelected(
                    parent: RecyclerView,
                    child: RecyclerView.ViewHolder?,
                    position: Int,
                    subposition: Int
                ) {
                    Log.i(tag, "onChildViewHolderSelected")
                    if (position == RecyclerView.NO_POSITION || position == lastSelectedPosition) return

                    lastSelectedPosition = position


                    binding.tabs.post {
                        // Update tab UI
                        tabAdapter.selectTab(position)

                        // Avoid reloading same fragment
                        val tab = tabAdapter.currentList()[position]
                        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                        if (currentFragment?.tag != tab.tag) {
                            Log.i(tag, "loadFragment from tab selection")
                            loadFragment(tab.fragment, tab.tag)
                        }
                    }
                }
            }
        )

        // Assuming ivSettings is your settings icon
        binding.tabs.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        // Get the last selected tab's view
                        val lastSelectedViewHolder =
                            binding.tabs.findViewHolderForAdapterPosition(lastSelectedPosition)
                        if (lastSelectedViewHolder != null) {
                            val lastView = lastSelectedViewHolder.itemView
                            // Check if the focused tab is the last one
                            if (binding.tabs.getChildAdapterPosition(lastView) == tabAdapter.getItems().size - 1) {
                                // Move focus to settings icon
                                binding.ivSettings.requestFocus()
                                return@setOnKeyListener true // consume event
                            }
                        }
                    }
                }
            }
            false
        }

        binding.ivSettings.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        lastSelectedPosition = -1
                        tabAdapter.selectLastTab()
                    }
                }
            }
            false
        }



    }
    //endregion

    //region observeTabs
    private fun observeTabs() {
        // Observe tabs from ViewModel
        mainViewModel.tabs.observe(this) { tabs ->
            this.tabs = tabs
            Log.i(tag, "setTabs")
            tabAdapter.setTabs(tabs)

            // Load the initially selected tab fragment
            tabs.firstOrNull { it.isSelected }?.let { tab ->
                Log.i(tag, "First fragment load")
                loadFragment(tab.fragment, tab.tag)
            }
        }
    }
    //endregion

    //region loadFragment
    private fun loadFragment(fragment: Fragment, tag: String) {
        Log.i(tag, "loadFragment()")
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

    //region onFocused
    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        view?.let {
            when(view) {
                binding.ivSettings -> {
                    if (hasFocus) {
                        settingIconFocused()
                    } else {
                        binding.ivSettings.isSelected = false
                    }
                }
            }
        }
    }
    //endregion

    //region settingIconClicked
    private fun settingIconFocused() {
        Log.i(tag, "settingIconClicked")
        if (!binding.ivSettings.isSelected) {
            binding.ivSettings.isSelected = true
            tabAdapter.deselectLastTab()
            loadFragment(SettingsFragment(), "settings_fragments")
        }
    }
    //ending
}
