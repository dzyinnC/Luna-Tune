package com.example.data.model

import com.example.R

enum class LunaExpression(
    val id: String,
    val displayName: String,
    val iconRes: Int,
    val defaultDialogue: String
) {
    CALM("calm", "Calm / Tenang", R.drawable.luna_expr_calm, "Suasana yang tenang ya... Rasanya damai sekali."),
    HAPPY("happy", "Bahagia", R.drawable.luna_expr_happy, "Hehe, musiknya bikin hati ceria dan tersenyum!"),
    HYPE("hype", "Bersemangat", R.drawable.luna_expr_hype, "Wah, ketukan melodinya seru banget! Ayo semangat!"),
    ROMANTIC("romantic", "Romantis", R.drawable.luna_expr_romantic, "Lagu ini manis banget... Bikin deg-degan lembut~"),
    SAD("sad", "Sedih", R.drawable.luna_expr_sad, "Lagunya menyentuh hati... Luna ada di sini buat kamu."),
    CRYING("crying", "Menangis", R.drawable.luna_expr_crying, "Hiks... Liriknya sangat dalam, menyentuh jiwa..."),
    ANGRY("angry", "Marah", R.drawable.luna_expr_angry, "Grrr! Musiknya bertenaga dan penuh gejolak emosi!"),
    POUTING("pouting", "Cemberut", R.drawable.luna_expr_pouting, "Hmph! Jangan lupa istirahat juga ya, jangan forsir!"),
    SURPRISED("surprised", "Terkejut", R.drawable.luna_expr_surprised, "Waa! Transisi melodinya keren banget!"),
    SHY("shy", "Malu / Tersipu", R.drawable.luna_expr_shy, "E-eh... Kamu dengerin lagu ini sambil memikirkan siapa?"),
    SMUG("smug", "Jahil / Percaya Diri", R.drawable.luna_expr_smug, "Hehe, playlist pilihanmu selalu berkualitas!"),
    WORRIED("worried", "Khawatir / Cemas", R.drawable.luna_expr_worried, "Kamu baik-baik saja kan? Tarik nafas perlahan yuk."),
    SLEEPY("sleepy", "Mengantuk", R.drawable.luna_expr_sleepy, "Hoammm... Melodinya lembut sekali, bikin ngantuk..."),
    BORED("bored", "Bosan / Kesepian", R.drawable.luna_expr_bored, "Yuk cari lagu baru yang bikin harimu lebih berwarna!"),
    FOCUSED("focused", "Fokus / Serius", R.drawable.luna_expr_focused, "Waktunya konsentrasi penuh. Kamu pasti bisa selesaikan!"),
    CONFUSED("confused", "Bingung", R.drawable.luna_expr_confused, "Ara? Ritme lagunya unik dan tidak terduga ya?"),
    TOUCHED("touched", "Terharu", R.drawable.luna_expr_touched, "Terima kasih sudah selalu mendengarkan musik bersama Luna ♡"),
    TIRED("tired", "Lelah", R.drawable.luna_expr_tired, "Kerja kerasmu hari ini luar biasa. Istirahat yang cukup ya!"),
    ENJOYING_MUSIC("enjoying", "Menikmati Musik", R.drawable.luna_state_playing, "Tetap semangat ya! Kamu pasti bisa! 💖");

    companion object {
        fun fromId(id: String): LunaExpression {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: ENJOYING_MUSIC
        }
    }
}

enum class MusicMood(val label: String, val iconRes: Int, val expression: LunaExpression) {
    CHILL("Chill (Lo-Fi)", R.drawable.luna_mood_chill, LunaExpression.CALM),
    HAPPY("Happy (Pop)", R.drawable.luna_mood_happy, LunaExpression.HAPPY),
    SAD("Sad (Ballad)", R.drawable.luna_mood_sad, LunaExpression.SAD),
    ROMANTIC("Romantic (Love)", R.drawable.luna_mood_romantic, LunaExpression.ROMANTIC),
    ROCK("Rock (Rock)", R.drawable.luna_mood_rock, LunaExpression.HYPE),
    GALAU("Galau (Dramatic)", R.drawable.luna_mood_galau, LunaExpression.CRYING),
    DANGDUT("Dangdut (Fun)", R.drawable.luna_mood_dangdut, LunaExpression.HYPE)
}

enum class PlayerState {
    IDLE,
    LOADING,
    PLAYING,
    PAUSED,
    ERROR
}

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

enum class PerformanceMode(val label: String, val description: String) {
    LOW("Hemat Baterai", "Animasi sederhana & background statis"),
    BALANCED("Seimbang", "Animasi lembut & visualizer efisien"),
    PREMIUM("Premium Aesthetic", "Glow magical, aura partikel, visualizer responsif")
}

enum class EqualizerPreset(
    val displayName: String,
    val bass: Float, // -10 to +10
    val treble: Float, // -10 to +10
    val bands: List<Float> // 5 bands in dB: -10f to +10f
) {
    DEFAULT("Default", 0f, 0f, listOf(0f, 0f, 0f, 0f, 0f)),
    ROCK("Rock", 4.5f, 5.0f, listOf(4f, 2f, -1f, 3f, 5f)),
    POP("Pop", 2.0f, 3.5f, listOf(-1f, 2f, 4f, 2f, -1f)),
    JAZZ("Jazz", 3.0f, 2.0f, listOf(3f, 2f, -2f, 2f, 3f)),
    CLASSICAL("Classical", 1.0f, 4.0f, listOf(4f, 3f, 2f, 2f, -1f)),
    BASS_BOOST("Bass Boost", 8.0f, 1.0f, listOf(8f, 6f, 2f, 0f, 0f)),
    VOCAL("Vocal", -2.0f, 4.0f, listOf(-2f, 0f, 5f, 3f, 1f)),
    CUSTOM("Custom", 0f, 0f, listOf(0f, 0f, 0f, 0f, 0f))
}
