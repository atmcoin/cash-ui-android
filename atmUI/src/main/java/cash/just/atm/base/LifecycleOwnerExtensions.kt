package com.square.project.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

val LifecycleOwner.context : Context
    get() = (this as? Fragment)?.requireContext() ?: this as Context
