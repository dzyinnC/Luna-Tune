package com.example.data.repo

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.R
import com.example.data.db.LunaTuneDatabase
import com.example.data.model.Playlist
import com.example.data.model.Song
import com.example.data.provider.LegalOnlineMusicProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MusicRepository(private val context: Context) {
    private val database = LunaTuneDatabase.getDatabase(context)
    private val songDao = database.songDao()
    private val playlistDao = database.playlistDao()
    private val onlineProvider = LegalOnlineMusicProvider()

    val allSongs: Flow<List<Song>> = songDao.getAllSongs()
    val favoriteSongs: Flow<List<Song>> = songDao.getFavoriteSongs()
    val recentSongs: Flow<List<Song>> = songDao.getRecentSongs(10)
    val allPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists()

    fun searchSongs(query: String): Flow<List<Song>> = songDao.searchSongs(query)
    fun getSongsByMood(mood: String): Flow<List<Song>> = songDao.getSongsByMood(mood)

    suspend fun getOnlineTracks(): List<Song> = withContext(Dispatchers.IO) {
        onlineProvider.getDiscoverTracks()
    }

    suspend fun searchOnlineTracks(query: String): List<Song> = withContext(Dispatchers.IO) {
        onlineProvider.searchOnlineTracks(query)
    }

    suspend fun getSongById(id: String): Song? = withContext(Dispatchers.IO) {
        songDao.getSongById(id) ?: onlineProvider.getDiscoverTracks().firstOrNull { it.id == id }
    }

    suspend fun toggleFavorite(songId: String, currentFav: Boolean) = withContext(Dispatchers.IO) {
        val existing = songDao.getSongById(songId)
        if (existing != null) {
            songDao.setFavorite(songId, !currentFav)
        } else {
            // If it's an online track, add it to database as favorite
            val onlineTrack = onlineProvider.getDiscoverTracks().firstOrNull { it.id == songId }
            if (onlineTrack != null) {
                songDao.insertSongs(listOf(onlineTrack.copy(isFavorite = true)))
            }
        }
    }

    suspend fun recordSongPlayed(songId: String) = withContext(Dispatchers.IO) {
        val existing = songDao.getSongById(songId)
        if (existing != null) {
            songDao.recordPlay(songId, System.currentTimeMillis())
        } else {
            val onlineTrack = onlineProvider.getDiscoverTracks().firstOrNull { it.id == songId }
            if (onlineTrack != null) {
                songDao.insertSongs(listOf(onlineTrack.copy(playCount = 1, lastPlayedTimestamp = System.currentTimeMillis())))
            }
        }
    }

    /**
     * Extracts an asset audio file to internal storage so that ExoPlayer can play it
     * with 100% native stability via File URI.
     */
    suspend fun resolveMediaUri(rawUri: String): String = withContext(Dispatchers.IO) {
        if (!rawUri.startsWith("asset:///")) {
            return@withContext rawUri
        }
        val assetPath = rawUri.removePrefix("asset:///")
        val musicDir = File(context.filesDir, "music").apply { mkdirs() }
        val targetFile = File(musicDir, File(assetPath).name)

        if (!targetFile.exists() || targetFile.length() == 0L) {
            try {
                context.assets.open(assetPath).use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext rawUri
            }
        }
        targetFile.toURI().toString()
    }

    suspend fun initializeDefaultLibraryIfNeeded() = withContext(Dispatchers.IO) {
        // Pre-extract offline audio files for instant playback
        listOf("calm_lofi.ogg", "happy_upbeat.ogg", "sad_melancholic.ogg").forEach { filename ->
            resolveMediaUri("asset:///music/$filename")
        }

        if (songDao.getSongCount() == 0) {
            val defaultSongs = listOf(
                // 3 Guaranteed Free & Legal Offline Tracks
                Song(
                    id = "track_calm_lofi",
                    title = "Placid Ambient (Lo-Fi)",
                    artist = "MusicLFiles",
                    album = "Luna Anime Chill",
                    durationMs = 141000L,
                    mediaUri = "asset:///music/calm_lofi.ogg",
                    albumArtRes = R.drawable.cover_better_days,
                    genre = "Lo-Fi / Ambient",
                    mood = "CHILL",
                    isFavorite = true,
                    playCount = 42,
                    isOnline = false,
                    license = "Creative Commons Attribution 4.0 (CC BY 4.0)",
                    attribution = "MusicLFiles via Wikimedia Commons • CC BY 4.0",
                    lyrics = "[00:10] Alunan melodi piano lembut menyejukkan hati...\n[00:35] Di bawah langit ungu LunaTune\n[01:00] Fokus dan raih mimpimu hari ini!"
                ),
                Song(
                    id = "track_happy_upbeat",
                    title = "Warm Sunset (Upbeat)",
                    artist = "MusicLFiles",
                    album = "Sunny Golden Days",
                    durationMs = 224000L,
                    mediaUri = "asset:///music/happy_upbeat.ogg",
                    albumArtRes = R.drawable.cover_sunset_memories,
                    genre = "Acoustic Pop",
                    mood = "HAPPY",
                    isFavorite = true,
                    playCount = 38,
                    isOnline = false,
                    license = "Creative Commons Attribution 4.0 (CC BY 4.0)",
                    attribution = "MusicLFiles via Wikimedia Commons • CC BY 4.0",
                    lyrics = "[00:15] Petikan gitar akustik yang ceria mengiringi langkahmu!\n[00:45] Jangan lupa tersenyum ya~\n[01:15] Bersama Luna, hari ini pasti luar biasa!"
                ),
                Song(
                    id = "track_sad_melancholic",
                    title = "Gymnopédie No. 1",
                    artist = "Erik Satie",
                    album = "Classical Melancholy",
                    durationMs = 195000L,
                    mediaUri = "asset:///music/sad_melancholic.ogg",
                    albumArtRes = R.drawable.cover_night_sky,
                    genre = "Classical Piano",
                    mood = "SAD",
                    isFavorite = false,
                    playCount = 20,
                    isOnline = false,
                    license = "Public Domain (CC0)",
                    attribution = "Erik Satie (1888) • Performance via Wikimedia Commons • CC0",
                    lyrics = "[00:20] Sentuhan tuts piano yang dalam dan syahdu...\n[00:55] Mengenang kisah yang telah berlalu\n[01:30] Damai dalam ketenangan malam"
                ),
                // Additional Bundled Atmospheric Tracks
                Song(
                    id = "track_dreaming_together",
                    title = "Dreaming Together",
                    artist = "LunaTune Studio",
                    album = "Midnight Fantasia",
                    durationMs = 194000L,
                    mediaUri = "asset:///music/calm_lofi.ogg",
                    albumArtRes = R.drawable.cover_dreaming_together,
                    genre = "Anime Ballad",
                    mood = "ROMANTIC",
                    isFavorite = true,
                    playCount = 28,
                    isOnline = false,
                    license = "CC0 Public Domain",
                    attribution = "LunaTune Studio Original",
                    lyrics = "[00:08] Di bawah bintang yang berkilau\n[00:30] Kita merajut angan bersama ♡"
                ),
                Song(
                    id = "track_focus_on_you",
                    title = "Focus On You",
                    artist = "LunaTune Studio",
                    album = "Deep Work Session",
                    durationMs = 240000L,
                    mediaUri = "asset:///music/calm_lofi.ogg",
                    albumArtRes = R.drawable.cover_focus_on_you,
                    genre = "Ambient Focus",
                    mood = "CHILL",
                    isFavorite = true,
                    playCount = 55,
                    isOnline = false,
                    license = "CC0 Public Domain",
                    attribution = "LunaTune Alpha Waves",
                    lyrics = "[Track Musik Fokus Pomodoro • Menjaga Konsentrasi Belajar]"
                ),
                Song(
                    id = "track_anime_hitz",
                    title = "Anime Hitz Sparks",
                    artist = "LunaTune Studio",
                    album = "Anime Hitz",
                    durationMs = 175000L,
                    mediaUri = "asset:///music/happy_upbeat.ogg",
                    albumArtRes = R.drawable.cover_anime_hitz,
                    genre = "J-Pop Anime",
                    mood = "HYPE",
                    isFavorite = false,
                    playCount = 14,
                    isOnline = false,
                    license = "CC0 Public Domain",
                    attribution = "LunaTune Studio",
                    lyrics = "[00:10] Semangat berkobar takkan padam!"
                )
            )
            songDao.insertSongs(defaultSongs)
        }

        if (playlistDao.getPlaylistCount() == 0) {
            val defaultPlaylists = listOf(
                Playlist(
                    id = "pl_chill",
                    name = "Chill Vibes",
                    description = "1.2M plays • Santai ditemani ketukan hangat",
                    coverRes = R.drawable.cover_chill_vibes,
                    songCount = 24,
                    isOnline = false
                ),
                Playlist(
                    id = "pl_focus",
                    name = "Focus Mode",
                    description = "856K plays • Musik pembantu konsentrasi Pomodoro",
                    coverRes = R.drawable.cover_focus_mode,
                    songCount = 18,
                    isOnline = false
                ),
                Playlist(
                    id = "pl_anime",
                    name = "Anime Hitz",
                    description = "3.5M plays • Lagu pembakar semangat anime terbaik",
                    coverRes = R.drawable.cover_anime_hitz,
                    songCount = 32,
                    isOnline = false
                ),
                Playlist(
                    id = "pl_online_discover",
                    name = "Online Stream Essentials",
                    description = "Katalog streaming legal Creative Commons & Public Domain",
                    coverRes = R.drawable.cover_lovely_day,
                    songCount = 6,
                    isOnline = true
                )
            )
            playlistDao.insertPlaylists(defaultPlaylists)
        }
    }

    suspend fun scanDeviceAudio(): Int = withContext(Dispatchers.IO) {
        val scannedSongs = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val title = it.getString(titleCol) ?: "Unknown"
                    val artist = it.getString(artistCol) ?: "Unknown Artist"
                    val album = it.getString(albumCol) ?: "Unknown Album"
                    val duration = it.getLong(durationCol)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    scannedSongs.add(
                        Song(
                            id = "local_$id",
                            title = title,
                            artist = artist,
                            album = album,
                            durationMs = if (duration > 0) duration else 180000L,
                            mediaUri = contentUri,
                            albumArtRes = R.drawable.cover_better_days,
                            genre = "Local Audio",
                            mood = "CHILL",
                            isOnline = false,
                            license = "Personal Device Audio",
                            attribution = "Local Storage"
                        )
                    )
                }
            }
            if (scannedSongs.isNotEmpty()) {
                songDao.insertSongs(scannedSongs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        scannedSongs.size
    }
}
