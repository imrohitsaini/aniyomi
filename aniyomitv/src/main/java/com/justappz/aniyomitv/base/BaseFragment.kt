package com.justappz.aniyomitv.base

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

/**
 * BaseFragment is a simple base class for all fragments in the application.
 * It provides easy access to context and activity, so child fragments can use them directly.
 */
open class BaseFragment : Fragment() {

    //region variables
    protected val ctx: Context get() = requireContext()
    protected val act: Activity get() = requireActivity()
    //endregion

}
