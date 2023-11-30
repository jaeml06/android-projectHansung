package com.example.hansungmarket.control.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting

@VisibleForTesting
const val RR = 25142

private const val M = "snackBarMessage"

fun Activity.setResultRefresh(@StringRes res: Int? = null) {
    val intent = Intent().apply {
        if (res != null) putExtra(M, getString(res))
    }
    setResult(RR, intent)
}

class RefreshStateResult(val message: String?)
class RefreshStateContract : ActivityResultContract<Intent, RefreshStateResult?>() {
    override fun createIntent(context: Context, input: Intent) = input
    override fun parseResult(resultCode: Int, intent: Intent?): RefreshStateResult? {
        if (resultCode == RR) {
            val message = intent?.getStringExtra(M)
            return RefreshStateResult(message)
        }
        return null
    }
}