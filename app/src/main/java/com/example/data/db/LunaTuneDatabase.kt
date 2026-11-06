package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Playlist
import com.example.data.model.PomodoroSession
import com.example.data.model.Song

@Database(
    entities = [Song::class, Playlist::class, PomodoroSession::class],
    version = 2,
    exportSchema = false
)
abstract class LunaTuneDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun pomodoroDao(): PomodoroDao

    companion object {
        @Volatile
        private var INSTANCE: LunaTuneDatabase? = null

        fun getDatabase(context: Context): LunaTuneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LunaTuneDatabase::class.java,
                    "lunatune_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
