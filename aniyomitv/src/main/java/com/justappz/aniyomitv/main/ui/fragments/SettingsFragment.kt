package com.justappz.aniyomitv.main.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseFragment
import com.justappz.aniyomitv.constants.PrefsKeys
import com.justappz.aniyomitv.core.util.PrefsManager
import com.justappz.aniyomitv.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment() {

    //region variables
    private val tag = "SettingsFragment"
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var isNsfwEnabled = false
    //endregion

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    //endregion

    //region onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(tag, "onViewCreated")

        init()
    }
    //endregion


    //region init
    private fun init() {
        Log.d(tag, "init")

        isNsfwEnabled = PrefsManager.getBoolean(PrefsKeys.PREFERRED_NSFW_SOURCE, false)

        binding.switchNsfwSources.isChecked = isNsfwEnabled

        if (isNsfwEnabled) {
            binding.tvNSFWSourcesDesc.text = getString(R.string.nsfw_sources_are_enabled)
        } else {
            binding.tvNSFWSourcesDesc.text = getString(R.string.nsfw_sources_are_disabled)
        }

        binding.switchNsfwSources.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                PrefsManager.putBoolean(PrefsKeys.PREFERRED_NSFW_SOURCE, true)
                binding.tvNSFWSourcesDesc.text = getString(R.string.nsfw_sources_are_enabled)
            } else {
                PrefsManager.putBoolean(PrefsKeys.PREFERRED_NSFW_SOURCE, false)
                binding.tvNSFWSourcesDesc.text = getString(R.string.nsfw_sources_are_disabled)
            }
        }
    }
    //endregion


}
