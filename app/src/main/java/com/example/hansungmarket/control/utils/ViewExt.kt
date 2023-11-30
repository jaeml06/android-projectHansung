package com.example.hansungmarket.control.utils

import android.graphics.drawable.InsetDrawable
import android.widget.LinearLayout
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addDividerDecoration(@DimenRes horizontalPaddingDimen: Int? = null) {
    val a = intArrayOf(android.R.attr.listDivider)
    val ta = context.obtainStyledAttributes(a)
    val d = ta.getDrawable(0)
    ta.recycle()
    val i = horizontalPaddingDimen?.let { resources.getDimensionPixelSize(it) } ?: 0
    val id = InsetDrawable(d, i, 0, i, 0)
    val dec = DividerItemDecoration(context, LinearLayout.VERTICAL)
    dec.setDrawable(id)
    addItemDecoration(dec)
}