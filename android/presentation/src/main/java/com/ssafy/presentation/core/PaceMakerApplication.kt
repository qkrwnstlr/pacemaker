package com.ssafy.presentation.core

import android.app.Application
import timber.log.Timber

class PaceMakerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}