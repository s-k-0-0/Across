package com.kax.across

import android.app.Application
import com.kax.across.data.local.AppDatabase
import com.kax.across.data.repository.SavedItemRepository

/**
 * Application class for OmniSave managing application-wide singletons.
 */
class OmniSaveApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    val repository: SavedItemRepository by lazy {
        SavedItemRepository(database.savedItemDao(), applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
