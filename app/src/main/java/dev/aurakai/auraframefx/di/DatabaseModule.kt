package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.data.room.AgentMemoryDao
import dev.aurakai.auraframefx.data.room.AppDatabase
import dev.aurakai.auraframefx.data.room.TaskHistoryDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "aura_frame_fx_database"
        )
        // Add migrations here if/when schema changes:
        // .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .fallbackToDestructiveMigration() // Placeholder: Consider proper migration strategies for production
        .build()
    }

    @Provides
    fun provideAgentMemoryDao(database: AppDatabase): AgentMemoryDao {
        return database.agentMemoryDao()
    }

    @Provides
    fun provideTaskHistoryDao(database: AppDatabase): TaskHistoryDao {
        return database.taskHistoryDao()
    }
}
