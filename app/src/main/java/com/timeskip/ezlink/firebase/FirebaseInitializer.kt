package com.timeskip.ezlink.firebase

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * FirebaseInitializer - A ContentProvider that initializes Firebase early in the app lifecycle.
 *
 * ContentProvider's onCreate() is called before Application.onCreate(), making it ideal for
 * early initialization of libraries like Firebase.
 *
 * This initializer sets up:
 * - Firebase App
 * - Firebase Analytics
 * - Firebase Crashlytics with custom logging
 */
class FirebaseInitializer : ContentProvider() {

    companion object {
        private const val TAG = "FirebaseInitializer"
    }

    override fun onCreate(): Boolean {
        val context = context ?: return false

        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(context)
            Log.d(TAG, "Firebase initialized successfully")

            // Initialize Analytics
            FirebaseAnalytics.getInstance(context)
            Log.d(TAG, "Firebase Analytics initialized")

            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.isCrashlyticsCollectionEnabled = true
            Log.d(TAG, "FirebaseCrashlytics initialized")

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase", e)
            // Even if Firebase fails, we should return true to not crash the app
            return true
        }
    }

    // ContentProvider required methods (not used, but must be implemented)
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}

