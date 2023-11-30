package com.example.hansungmarket.control.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class ViewBindingFragment<VB : ViewBinding> : Fragment() {
    protected abstract val bf: (LayoutInflater, ViewGroup?, Boolean) -> VB
    private var _b: VB? = null
    protected val b: VB get() = requireNotNull(_b)
    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _b = bf.invoke(inflater, container, false)
        return b.root
    }
    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}