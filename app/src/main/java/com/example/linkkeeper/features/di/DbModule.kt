package com.example.linkkeeper.features.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.linkkeeper.features.db.MyDb
import com.example.linkkeeper.features.link.data.ContentHtmlDao
import com.example.linkkeeper.features.link.data.LinkDao
import com.example.linkkeeper.features.tag.data.TagDao
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
                        INSERT INTO tag (id, name, amountOfLink, isRead, isDefaultCreated) 
                        VALUES (0, 'Favorite', 0, true, true)
                    """
                )
                db.execSQL(
                    """
                        INSERT INTO tag (id, name, amountOfLink, isRead, isDefaultCreated) 
                        VALUES (1, 'Read later', 0, true, true)
                    """
                )
                db.execSQL(
                    """
                        INSERT INTO tag (id, name, amountOfLink, isRead, isDefaultCreated) 
                        VALUES (2, 'Personal', 0, true, true)
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
}