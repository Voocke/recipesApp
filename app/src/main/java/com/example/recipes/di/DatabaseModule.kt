package com.example.recipes.di

import android.content.Context
import androidx.room.Room
import com.example.recipes.data.local.AppDatabase
import com.example.recipes.data.local.dao.RecipeDao
import com.example.recipes.data.local.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context
    ) : AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "recipe_book_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(db: AppDatabase): RecipeDao {
        return db.recipeDao()
    }

    @Provides
    @Singleton
    fun provideTagDao(db: AppDatabase): TagDao {
        return db.tagDao()
    }
}