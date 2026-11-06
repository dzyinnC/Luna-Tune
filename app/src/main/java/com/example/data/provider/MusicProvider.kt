package com.example.data.provider

import com.example.R
import com.example.data.model.Song

interface MusicProvider {
    suspend fun getDiscoverTracks(): List<Song>
    suspend fun searchOnlineTracks(query: String): List<Song>
    suspend fun getTracksByGenre(genre: String): List<Song>
    suspend fun getTracksByMood(mood: String): List<Song>
}

class LegalOnlineMusicProvider : MusicProvider {

    private val onlineCatalog = listOf(
        Song(
            id = "online_placid_ambient",
            title = "Placid Ambient",
            artist = "MusicLFiles",
            album = "Chill & Relaxing Ambient",
            durationMs = 141000L,
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/5/53/Placid_Ambient_by_MusicLFiles.ogg",
            streamUrl = "https://upload.wikimedia.org/wikipedia/commons/5/53/Placid_Ambient_by_MusicLFiles.ogg",
            albumArtRes = R.drawable.cover_chill_vibes,
            genre = "Lo-Fi / Ambient",
            mood = "CHILL",
            isOnline = true,
            license = "Creative Commons Attribution 4.0 (CC BY 4.0)",
            attribution = "MusicLFiles via Wikimedia Commons • CC BY 4.0",
            lyrics = "[00:10] Instrumental piano & relaxing synth waves...\n[00:45] Mengalun lembut menenangkan pikiran\n[01:10] Rasakan ketenangan malam bersama Luna ♡"
        ),
        Song(
            id = "online_warm_sunset",
            title = "Warm Sunset",
            artist = "MusicLFiles",
            album = "Acoustic Sunset Melodies",
            durationMs = 224000L,
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/4/45/Warm_Sunset_by_MusicLFiles.ogg",
            streamUrl = "https://upload.wikimedia.org/wikipedia/commons/4/45/Warm_Sunset_by_MusicLFiles.ogg",
            albumArtRes = R.drawable.cover_sunset_memories,
            genre = "Acoustic Folk",
            mood = "HAPPY",
            isOnline = true,
            license = "Creative Commons Attribution 4.0 (CC BY 4.0)",
            attribution = "MusicLFiles via Wikimedia Commons • CC BY 4.0",
            lyrics = "[00:15] Petikan gitar akustik yang hangat di kala senja\n[00:45] Menemani langkah pulang dengan senyuman\n[01:15] Hari ini penuh warna dan harapan baru!"
        ),
        Song(
            id = "online_gymnopedie",
            title = "Gymnopédie No. 1",
            artist = "Erik Satie",
            album = "Classical Melancholy",
            durationMs = 195000L,
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/9/90/Erik_Satie_-_gymnopedies_-_la_1_ere._lent_et_douloureux.ogg",
            streamUrl = "https://upload.wikimedia.org/wikipedia/commons/9/90/Erik_Satie_-_gymnopedies_-_la_1_ere._lent_et_douloureux.ogg",
            albumArtRes = R.drawable.cover_night_sky,
            genre = "Classical Piano",
            mood = "SAD",
            isOnline = true,
            license = "Public Domain (CC0)",
            attribution = "Erik Satie (1888) • Performance via Wikimedia Commons • Public Domain",
            lyrics = "[00:20] Melodi piano klasik yang menyentuh kalbu\n[00:50] Mengiringi heningnya malam dan renungan hati\n[01:25] Ketenangan abadi dalam setiap nada..."
        ),
        Song(
            id = "online_soft_corporate",
            title = "Sunrise Inspiration",
            artist = "MusicLFiles",
            album = "Uplifting Morning Vibes",
            durationMs = 170000L,
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/5/5f/Soft_Corporate_by_MusicLFiles.ogg",
            streamUrl = "https://upload.wikimedia.org/wikipedia/commons/5/5f/Soft_Corporate_by_MusicLFiles.ogg",
            albumArtRes = R.drawable.cover_lovely_day,
            genre = "Upbeat Pop",
            mood = "HAPPY",
            isOnline = true,
            license = "Creative Commons Attribution 4.0 (CC BY 4.0)",
            attribution = "MusicLFiles via Wikimedia Commons • CC BY 4.0",
            lyrics = "[00:10] Semangat baru di pagi hari!\n[00:30] Melangkah maju meraih cita-cita bersama Luna\n[01:00] Segala rintangan pasti bisa terlewati!"
        ),
        Song(
            id = "online_anime_hitz",
            title = "Starlight Euphoria",
            artist = "LunaTune Studio",
            album = "Anime Hitz Vol. 1",
            durationMs = 210000L,
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/5/5f/Soft_Corporate_by_MusicLFiles.ogg",
            streamUrl = "https://upload.wikimedia.org/wikipedia/commons/5/5f/Soft_Corporate_by_MusicLFiles.ogg",
            albumArtRes = R.drawable.cover_anime_hitz,
            genre = "Anime Upbeat",
            mood = "ROCK",
            isOnline = true,
            license = "Creative Commons Attribution 4.0 (CC BY 4.0)",
            attribution = "LunaTune Studio & CC Community",
            lyrics = "[00:12] Irama bersemangat membakar semangat belajar!\n[00:40] Cahaya bintang menuntun jalan kemenangan"
        ),
        Song(
            id = "online_lofi_rain",
            title = "Midnight Coffee Lo-Fi",
            artist = "LunaTune Studio",
            album = "Rainy Day Study",
            durationMs = 185000L,
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/5/53/Placid_Ambient_by_MusicLFiles.ogg",
            streamUrl = "https://upload.wikimedia.org/wikipedia/commons/5/53/Placid_Ambient_by_MusicLFiles.ogg",
            albumArtRes = R.drawable.cover_focus_mode,
            genre = "Lo-Fi Beats",
            mood = "CHILL",
            isOnline = true,
            license = "Creative Commons Attribution 4.0 (CC BY 4.0)",
            attribution = "LunaTune Studio & MusicLFiles",
            lyrics = "[00:15] Gemericik hujan dan kopi hangat di meja belajar\n[00:50] Fokus maksimal bersama Luna..."
        )
    )

    override suspend fun getDiscoverTracks(): List<Song> {
        return onlineCatalog
    }

    override suspend fun searchOnlineTracks(query: String): List<Song> {
        if (query.isBlank()) return onlineCatalog
        return onlineCatalog.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.artist.contains(query, ignoreCase = true) ||
            it.album.contains(query, ignoreCase = true) ||
            it.genre.contains(query, ignoreCase = true) ||
            it.mood.contains(query, ignoreCase = true)
        }
    }

    override suspend fun getTracksByGenre(genre: String): List<Song> {
        return onlineCatalog.filter { it.genre.contains(genre, ignoreCase = true) }
    }

    override suspend fun getTracksByMood(mood: String): List<Song> {
        return onlineCatalog.filter { it.mood.equals(mood, ignoreCase = true) }
    }
}
