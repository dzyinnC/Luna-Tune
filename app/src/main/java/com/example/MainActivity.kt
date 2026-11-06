package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.character.EmotionState
import com.example.data.model.LunaExpression
import com.example.data.model.Song
import com.example.ui.components.BackgroundWallpaper
import com.example.ui.components.MiniPlayer
import com.example.ui.screens.EqualizerScreen
import com.example.ui.screens.ExpressionSelectorDialog
import com.example.ui.screens.FullPlayerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PomodoroScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SleepTimerDialog
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.Lavender
import com.example.ui.theme.LunaTuneTheme
import com.example.ui.theme.MutedGray
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SoftWhite
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as LunaTuneApp

        setContent {
            val wallpaper by app.preferences.selectedWallpaper.collectAsState(initial = "dreamy_purple")
            val isDynamicAlbumEnabled by app.preferences.isDynamicAlbumEnabled.collectAsState(initial = false)
            val playbackState by app.playerManager.playbackState.collectAsState()

            val effectiveWallpaper = if (isDynamicAlbumEnabled) "dynamic_album" else wallpaper
            val currentMood = playbackState.currentSong?.mood

            LunaTuneTheme(
                wallpaperKey = effectiveWallpaper,
                currentSongMood = currentMood
            ) {
                LunaTuneMainApp(app = app)
            }
        }
    }
}

enum class MainScreen(val route: String, val title: String) {
    HOME("home", "Beranda"),
    LIBRARY("library", "Playlist"),
    POMODORO("pomodoro", "Pomodoro"),
    SETTINGS("settings", "Profil")
}

@Composable
fun LunaTuneMainApp(app: LunaTuneApp) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showSplash by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf(MainScreen.HOME) }
    var isFullPlayerOpen by remember { mutableStateOf(false) }
    var isEqualizerOpen by remember { mutableStateOf(false) }
    var showExpressionDialog by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }

    // State Collection
    val playbackState by app.playerManager.playbackState.collectAsState()
    val emotionState by app.emotionEngine.emotionState.collectAsState()
    val pomodoroState by app.pomodoroEngine.pomodoroState.collectAsState()
    val allSongs by app.musicRepository.allSongs.collectAsState(initial = emptyList())
    val recentSongs by app.musicRepository.recentSongs.collectAsState(initial = emptyList())
    val playlists by app.musicRepository.allPlaylists.collectAsState(initial = emptyList())
    val wallpaper by app.preferences.selectedWallpaper.collectAsState(initial = "dreamy_purple")
    val performance by app.preferences.performanceMode.collectAsState(initial = "PREMIUM")
    val isDynamicAlbumEnabled by app.preferences.isDynamicAlbumEnabled.collectAsState(initial = false)
    val lockedExprId by app.preferences.lockedExpression.collectAsState(initial = null)

    var onlineSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    LaunchedEffect(Unit) {
        onlineSongs = app.musicRepository.getOnlineTracks()
    }

    // Predictive / system back handling
    BackHandler(enabled = isFullPlayerOpen) {
        isFullPlayerOpen = false
    }
    BackHandler(enabled = !isFullPlayerOpen && isEqualizerOpen) {
        isEqualizerOpen = false
    }
    BackHandler(enabled = !isFullPlayerOpen && !isEqualizerOpen && currentScreen != MainScreen.HOME) {
        currentScreen = MainScreen.HOME
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                val count = app.musicRepository.scanDeviceAudio()
                Toast.makeText(context, "$count lagu lokal ditemukan!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Izin akses media ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    if (showSplash) {
        SplashScreen(onTimeout = { showSplash = false })
        return
    }

    val effectiveWallpaper = if (isDynamicAlbumEnabled) "dynamic_album" else wallpaper

    Box(modifier = Modifier.fillMaxSize()) {
        // Aesthetic dynamic background wallpaper
        BackgroundWallpaper(
            wallpaperKey = effectiveWallpaper
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (!isFullPlayerOpen && !isEqualizerOpen) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        // Mini Player
                        if (playbackState.currentSong != null) {
                            MiniPlayer(
                                playbackState = playbackState,
                                onTogglePlayPause = { app.playerManager.togglePlayPause() },
                                onNext = { app.playerManager.next() },
                                onPrevious = { app.playerManager.previous() },
                                onClick = { isFullPlayerOpen = true }
                            )
                        }

                        // Bottom Navigation Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(Color(0xE617132F))
                                .border(1.dp, GlassBorder, RoundedCornerShape(26.dp))
                        ) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .testTag("bottom_nav_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == MainScreen.HOME,
                                    onClick = { currentScreen = MainScreen.HOME },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = "Beranda",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("Beranda", fontSize = 11.5.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SoftWhite,
                                        selectedTextColor = SoftWhite,
                                        indicatorColor = PrimaryPurple,
                                        unselectedIconColor = MutedGray,
                                        unselectedTextColor = MutedGray
                                    ),
                                    modifier = Modifier.testTag("nav_home")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == MainScreen.LIBRARY,
                                    onClick = { currentScreen = MainScreen.LIBRARY },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.QueueMusic,
                                            contentDescription = "Playlist",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("Playlist", fontSize = 11.5.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SoftWhite,
                                        selectedTextColor = SoftWhite,
                                        indicatorColor = PrimaryPurple,
                                        unselectedIconColor = MutedGray,
                                        unselectedTextColor = MutedGray
                                    ),
                                    modifier = Modifier.testTag("nav_library")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == MainScreen.POMODORO,
                                    onClick = { currentScreen = MainScreen.POMODORO },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = "Pomodoro",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("Pomodoro", fontSize = 11.5.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SoftWhite,
                                        selectedTextColor = SoftWhite,
                                        indicatorColor = PrimaryPurple,
                                        unselectedIconColor = MutedGray,
                                        unselectedTextColor = MutedGray
                                    ),
                                    modifier = Modifier.testTag("nav_pomodoro")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == MainScreen.SETTINGS,
                                    onClick = { currentScreen = MainScreen.SETTINGS },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Profil",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("Profil", fontSize = 11.5.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SoftWhite,
                                        selectedTextColor = SoftWhite,
                                        indicatorColor = PrimaryPurple,
                                        unselectedIconColor = MutedGray,
                                        unselectedTextColor = MutedGray
                                    ),
                                    modifier = Modifier.testTag("nav_settings")
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    MainScreen.HOME -> {
                        HomeScreen(
                            emotionState = emotionState,
                            isPlaying = playbackState.playerState == com.example.data.model.PlayerState.PLAYING,
                            recentSongs = if (recentSongs.isNotEmpty()) recentSongs else allSongs.take(5),
                            playlists = playlists,
                            onSongClick = { song ->
                                app.playerManager.playSong(song, allSongs)
                                scope.launch { app.musicRepository.recordSongPlayed(song.id) }
                            },
                            onPlaylistClick = { currentScreen = MainScreen.LIBRARY },
                            onToggleFavorite = { song ->
                                scope.launch { app.musicRepository.toggleFavorite(song.id, song.isFavorite) }
                            },
                            onTapLuna = { app.emotionEngine.onUserTapLuna() },
                            onOpenMoodSelector = { showExpressionDialog = true },
                            onNavigateToPlaylist = { currentScreen = MainScreen.LIBRARY },
                            onPlayFavoriteBanner = {
                                val favorite = allSongs.firstOrNull { it.isFavorite } ?: allSongs.firstOrNull()
                                favorite?.let {
                                    app.playerManager.playSong(it, allSongs)
                                    scope.launch { app.musicRepository.recordSongPlayed(it.id) }
                                }
                            }
                        )
                    }

                    MainScreen.LIBRARY -> {
                        val combinedSongs = remember(allSongs, onlineSongs) {
                            val list = mutableListOf<Song>()
                            list.addAll(allSongs)
                            onlineSongs.forEach { online ->
                                if (!list.any { it.id == online.id }) {
                                    list.add(online)
                                }
                            }
                            list
                        }
                        LibraryScreen(
                            offlineSongs = allSongs,
                            onlineSongs = onlineSongs,
                            currentSongId = playbackState.currentSong?.id,
                            isPlaying = playbackState.playerState == com.example.data.model.PlayerState.PLAYING,
                            onSongClick = { song ->
                                app.playerManager.playSong(song, combinedSongs)
                                scope.launch { app.musicRepository.recordSongPlayed(song.id) }
                            },
                            onToggleFavorite = { song ->
                                scope.launch { app.musicRepository.toggleFavorite(song.id, song.isFavorite) }
                            }
                        )
                    }

                    MainScreen.POMODORO -> {
                        PomodoroScreen(
                            pomodoroState = pomodoroState,
                            emotionState = emotionState,
                            onStartOrResume = {
                                app.pomodoroEngine.startOrResume()
                                // Auto play focus track if idle
                                if (playbackState.playerState != com.example.data.model.PlayerState.PLAYING) {
                                    val focusTrack = allSongs.firstOrNull { it.id == "track_focus_on_you" } ?: allSongs.firstOrNull()
                                    focusTrack?.let { app.playerManager.playSong(it, allSongs) }
                                }
                            },
                            onPause = { app.pomodoroEngine.pause() },
                            onReset = { app.pomodoroEngine.reset() },
                            onSkipSession = { app.pomodoroEngine.skipSession() },
                            onSelectMode = { app.pomodoroEngine.setMode(it) },
                            onSetCustomDurations = { focus, sBreak, lBreak ->
                                app.pomodoroEngine.setCustomDurations(focus, sBreak, lBreak)
                                scope.launch {
                                    app.preferences.setPomodoroDurations(focus, sBreak, lBreak)
                                }
                            },
                            onPlayFocusMusic = {
                                val focusTrack = allSongs.firstOrNull { it.id == "track_focus_on_you" } ?: allSongs.firstOrNull()
                                focusTrack?.let { app.playerManager.playSong(it, allSongs) }
                            },
                            onTapLuna = { app.emotionEngine.onUserTapLuna() }
                        )
                    }

                    MainScreen.SETTINGS -> {
                        SettingsScreen(
                            currentWallpaper = wallpaper,
                            currentPerformance = performance,
                            isDynamicAlbumEnabled = isDynamicAlbumEnabled,
                            emotionState = emotionState,
                            onSelectWallpaper = { key ->
                                scope.launch { app.preferences.setWallpaper(key) }
                            },
                            onToggleDynamicAlbum = { enabled ->
                                scope.launch { app.preferences.setDynamicAlbumEnabled(enabled) }
                            },
                            onSelectPerformance = { mode ->
                                scope.launch { app.preferences.setPerformanceMode(mode) }
                            },
                            onOpenExpressionSelector = { showExpressionDialog = true },
                            onScanAudio = {
                                val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    Manifest.permission.READ_MEDIA_AUDIO
                                } else {
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                }
                                permissionLauncher.launch(perm)
                            },
                            onOpenSleepTimer = { showSleepTimerDialog = true }
                        )
                    }
                }
            }
        }

        // Full Player Screen Overlay with Slide Transition
        AnimatedVisibility(
            visible = isFullPlayerOpen,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            FullPlayerScreen(
                playbackState = playbackState,
                emotionState = emotionState,
                onBack = { isFullPlayerOpen = false },
                onTogglePlayPause = { app.playerManager.togglePlayPause() },
                onSeekTo = { app.playerManager.seekTo(it) },
                onNext = { app.playerManager.next() },
                onPrevious = { app.playerManager.previous() },
                onToggleShuffle = { app.playerManager.toggleShuffle() },
                onToggleRepeat = { app.playerManager.toggleRepeat() },
                onToggleFavorite = { song ->
                    scope.launch { app.musicRepository.toggleFavorite(song.id, song.isFavorite) }
                },
                onOpenEqualizer = { isEqualizerOpen = true },
                onOpenSleepTimer = { showSleepTimerDialog = true },
                onTapLuna = { app.emotionEngine.onUserTapLuna() }
            )
        }

        // Equalizer Screen Overlay with Slide Transition
        AnimatedVisibility(
            visible = isEqualizerOpen,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            EqualizerScreen(
                currentPreset = playbackState.equalizerPreset,
                bassLevel = playbackState.bassLevel,
                trebleLevel = playbackState.trebleLevel,
                onPresetChange = { app.playerManager.setEqualizerPreset(it) },
                onBassTrebleChange = { bass, treble -> app.playerManager.setBassTreble(bass, treble) },
                onBack = { isEqualizerOpen = false }
            )
        }

        // Expression Selector Modal Bottom Sheet
        if (showExpressionDialog) {
            val lockedExpr = lockedExprId?.let { LunaExpression.fromId(it) }
            ExpressionSelectorDialog(
                currentLockedExpression = lockedExpr,
                onSelectExpression = { expr ->
                    scope.launch {
                        app.preferences.setLockedExpression(expr?.id)
                        app.emotionEngine.setLockedExpression(expr)
                    }
                },
                onDismiss = { showExpressionDialog = false }
            )
        }

        // Sleep Timer Dialog
        if (showSleepTimerDialog) {
            SleepTimerDialog(
                currentMinutesRemaining = playbackState.sleepTimerMinutesRemaining,
                onSetTimer = { app.playerManager.setSleepTimer(it) },
                onDismiss = { showSleepTimerDialog = false }
            )
        }
    }
}
