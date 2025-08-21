package com.justappz.aniyomitv.main.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseFragment
import com.justappz.aniyomitv.databinding.FragmentExtensionBinding

class ExtensionFragment : BaseFragment() {

    //region variables
    private var _binding: FragmentExtensionBinding? = null
    private val binding get() = _binding!!
    //endregion

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_extension, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
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
    }
    //endregion
}
