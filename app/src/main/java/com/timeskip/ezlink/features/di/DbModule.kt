package com.timeskip.ezlink.features.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.timeskip.ezlink.features.contentHtml.ContentHtmlDao
import com.timeskip.ezlink.features.db.DatabaseTransactionRunner
import com.timeskip.ezlink.features.db.MyDb
import com.timeskip.ezlink.features.db.RoomDatabaseTransactionRunner
import com.timeskip.ezlink.features.link.data.LinkDao
import com.timeskip.ezlink.features.tag.data.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        MyDb::class.java,
        "link_keeper"
    )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(
                    """
                        INSERT INTO tag (name, amountOfLink, isRead, isDefaultCreated) 
                        VALUES ('Favorite', 0, true, true)
                    """
                )
                db.execSQL(
                    """
                        INSERT INTO tag (name, amountOfLink, isRead, isDefaultCreated) 
                        VALUES ('Read later', 0, true, true)
                    """
                )
                db.execSQL(
                    """
                        INSERT INTO tag (name, amountOfLink, isRead, isDefaultCreated) 
                        VALUES ('Personal', 0, true, true)
                    """
                )
            }
        })
        .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideTagDao(db: MyDb): TagDao = db.getTagDao()

    @Singleton
    @Provides
    fun provideLinkDao(db: MyDb): LinkDao = db.getLinkDao()

    @Singleton
    @Provides
    fun provideContentHtmlDao(db: MyDb): ContentHtmlDao = db.getContentHtmlDao()

    @Singleton
    @Provides
    fun provideRoomDatabaseTransactionRunner(db: MyDb): DatabaseTransactionRunner =
        RoomDatabaseTransactionRunner(db)
}