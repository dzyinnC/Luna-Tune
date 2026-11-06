#!/bin/bash
set -e

DRAWABLE="app/src/main/res/drawable"
mkdir -p "$DRAWABLE"

EXPR_SRC="public/assets/aistudio/Panduan_Ekspresi_Musik_LunaTune_e336ffc5.png"
KOLASE_SRC="public/assets/aistudio/Kolase_UI_Aplikasi_Musik_LunaTune_Bergaya_Anime_071aaec8.png"
ICON_SRC="public/assets/aistudio/LunaTune__Gadis_Anime_dalam_Malam_Musik-removebg-preview_3d5e93c5.png"

# Copy main Luna character
cp "$ICON_SRC" "$DRAWABLE/luna_character.png"
cp "$ICON_SRC" "$DRAWABLE/luna_avatar.png"

# Crop 18 Expressions from Panduan_Ekspresi_Musik_LunaTune_e336ffc5.png (1536x1024)
# 6 columns: x centers roughly at 145, 390, 635, 880, 1125, 1370 -> size 200x200
# Row 1: y=105
# Row 2: y=340
# Row 3: y=575

# Row 1: Calm, Happy, Energetic, Romantic, Sad, Crying
convert "$EXPR_SRC" -crop 205x205+40+100 +repage "$DRAWABLE/luna_expr_calm.png"
convert "$EXPR_SRC" -crop 205x205+285+100 +repage "$DRAWABLE/luna_expr_happy.png"
convert "$EXPR_SRC" -crop 205x205+535+100 +repage "$DRAWABLE/luna_expr_hype.png"
convert "$EXPR_SRC" -crop 205x205+780+100 +repage "$DRAWABLE/luna_expr_romantic.png"
convert "$EXPR_SRC" -crop 205x205+1025+100 +repage "$DRAWABLE/luna_expr_sad.png"
convert "$EXPR_SRC" -crop 205x205+1270+100 +repage "$DRAWABLE/luna_expr_crying.png"

# Row 2: Angry, Pouting, Surprised, Shy, Confident, Worried
convert "$EXPR_SRC" -crop 205x205+40+335 +repage "$DRAWABLE/luna_expr_angry.png"
convert "$EXPR_SRC" -crop 205x205+285+335 +repage "$DRAWABLE/luna_expr_pouting.png"
convert "$EXPR_SRC" -crop 205x205+535+335 +repage "$DRAWABLE/luna_expr_surprised.png"
convert "$EXPR_SRC" -crop 205x205+780+335 +repage "$DRAWABLE/luna_expr_shy.png"
convert "$EXPR_SRC" -crop 205x205+1025+335 +repage "$DRAWABLE/luna_expr_smug.png"
convert "$EXPR_SRC" -crop 205x205+1270+335 +repage "$DRAWABLE/luna_expr_worried.png"

# Row 3: Sleepy, Bored, Focused, Confused, Touched, Tired
convert "$EXPR_SRC" -crop 205x205+40+570 +repage "$DRAWABLE/luna_expr_sleepy.png"
convert "$EXPR_SRC" -crop 205x205+285+570 +repage "$DRAWABLE/luna_expr_bored.png"
convert "$EXPR_SRC" -crop 205x205+535+570 +repage "$DRAWABLE/luna_expr_focused.png"
convert "$EXPR_SRC" -crop 205x205+780+570 +repage "$DRAWABLE/luna_expr_confused.png"
convert "$EXPR_SRC" -crop 205x205+1025+570 +repage "$DRAWABLE/luna_expr_touched.png"
convert "$EXPR_SRC" -crop 205x205+1270+570 +repage "$DRAWABLE/luna_expr_tired.png"

# Bottom states / music moods
# State Player: Playing, Paused, Loading, Error
convert "$EXPR_SRC" -crop 125x125+1055+830 +repage "$DRAWABLE/luna_state_playing.png"
convert "$EXPR_SRC" -crop 125x125+1175+830 +repage "$DRAWABLE/luna_state_paused.png"
convert "$EXPR_SRC" -crop 125x125+1295+830 +repage "$DRAWABLE/luna_state_loading.png"
convert "$EXPR_SRC" -crop 125x125+1410+830 +repage "$DRAWABLE/luna_state_error.png"

# Music Mood avatars: Chill, Happy, Sad, Romantic, Rock, Galau, Dangdut
convert "$EXPR_SRC" -crop 125x125+150+830 +repage "$DRAWABLE/luna_mood_chill.png"
convert "$EXPR_SRC" -crop 125x125+280+830 +repage "$DRAWABLE/luna_mood_happy.png"
convert "$EXPR_SRC" -crop 125x125+400+830 +repage "$DRAWABLE/luna_mood_sad.png"
convert "$EXPR_SRC" -crop 125x125+520+830 +repage "$DRAWABLE/luna_mood_romantic.png"
convert "$EXPR_SRC" -crop 125x125+645+830 +repage "$DRAWABLE/luna_mood_rock.png"
convert "$EXPR_SRC" -crop 125x125+770+830 +repage "$DRAWABLE/luna_mood_galau.png"
convert "$EXPR_SRC" -crop 125x125+895+830 +repage "$DRAWABLE/luna_mood_dangdut.png"

# Crop UI Assets from Kolase_UI_Aplikasi_Musik_LunaTune_Bergaya_Anime_071aaec8.png (1254x1254)
# Splash screen Luna art
convert "$KOLASE_SRC" -crop 270x420+15+150 +repage "$DRAWABLE/luna_splash_art.png"

# Home screen Favorite Music Banner (sunset lake)
convert "$KOLASE_SRC" -crop 260x120+320+210 +repage "$DRAWABLE/banner_favorite_music.png"

# Recommendation covers from Home screen
convert "$KOLASE_SRC" -crop 80x80+320+370 +repage "$DRAWABLE/cover_chill_vibes.png"
convert "$KOLASE_SRC" -crop 80x80+405+370 +repage "$DRAWABLE/cover_focus_mode.png"
convert "$KOLASE_SRC" -crop 80x80+495+370 +repage "$DRAWABLE/cover_anime_hitz.png"

# Full Player glowing circular Luna art
convert "$KOLASE_SRC" -crop 230x230+650+105 +repage "$DRAWABLE/luna_player_disc.png"

# Playlist screen album covers
convert "$KOLASE_SRC" -crop 60x60+925+140 +repage "$DRAWABLE/cover_better_days.png"
convert "$KOLASE_SRC" -crop 60x60+925+210 +repage "$DRAWABLE/cover_dreaming_together.png"
convert "$KOLASE_SRC" -crop 60x60+925+270 +repage "$DRAWABLE/cover_sunset_memories.png"
convert "$KOLASE_SRC" -crop 60x60+925+330 +repage "$DRAWABLE/cover_focus_on_you.png"
convert "$KOLASE_SRC" -crop 60x60+925+390 +repage "$DRAWABLE/cover_night_sky.png"
convert "$KOLASE_SRC" -crop 60x60+925+450 +repage "$DRAWABLE/cover_lovely_day.png"
convert "$KOLASE_SRC" -crop 60x60+925+510 +repage "$DRAWABLE/cover_starry_night.png"

# Pomodoro screen sleeping Luna on cloud
convert "$KOLASE_SRC" -crop 170x130+360+605 +repage "$DRAWABLE/luna_pomodoro_cloud.png"

# 3 Aesthetic wallpapers from bottom right
convert "$KOLASE_SRC" -crop 120x290+860+710 +repage "$DRAWABLE/wallpaper_night.png"
convert "$KOLASE_SRC" -crop 120x290+985+710 +repage "$DRAWABLE/wallpaper_sunset.png"
convert "$KOLASE_SRC" -crop 120x290+1115+710 +repage "$DRAWABLE/wallpaper_meadow.png"

# Luna cute sleeping cat mascot
convert "$KOLASE_SRC" -crop 90x65+1140+1030 +repage "$DRAWABLE/mascot_cat.png"

echo "Asset extraction complete!"
