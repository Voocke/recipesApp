package com.example.recipes.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recipes.data.local.AppDatabase
import com.example.recipes.data.local.DatabaseSeeder
import com.example.recipes.data.local.dao.RecipeDao
import com.example.recipes.data.local.dao.TagDao
import com.example.recipes.data.repository.RecipeRepository
import com.example.recipes.data.sync.JsonRecipeBackupService
import com.example.recipes.data.sync.RecipeBackupService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        lateinit var database: AppDatabase

        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    DatabaseSeeder.seed(database.recipeDao())
                }
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                CoroutineScope(Dispatchers.IO).launch {
                    DatabaseSeeder.seed(database.recipeDao())
                }
            }
        }

        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "recipe_book_db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

        return database
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

    @Provides
    @Singleton
    fun provideRecipeBackupService(repository: RecipeRepository): RecipeBackupService {
        return JsonRecipeBackupService(repository)
    }
}
