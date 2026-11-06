import os
import wave
import struct
import math

os.makedirs("app/src/main/assets/music", exist_ok=True)
SAMPLE_RATE = 44100

def generate_track(filename, chords, duration_sec=30):
    total_samples = int(SAMPLE_RATE * duration_sec)
    wav_file = wave.open(filename, 'w')
    wav_file.setnchannels(2) # stereo
    wav_file.setsampwidth(2) # 16-bit
    wav_file.setframerate(SAMPLE_RATE)
    
    # Each chord plays for a measure
    chord_len = int(SAMPLE_RATE * 2.5)
    
    frames = bytearray()
    for i in range(total_samples):
        chord_idx = (i // chord_len) % len(chords)
        current_chord = chords[chord_idx]
        t = i / SAMPLE_RATE
        
        # Soft envelope per chord
        t_in_chord = (i % chord_len) / chord_len
        env = math.sin(math.pi * t_in_chord) ** 0.5
        
        # Subtle lo-fi vinyl warmth
        left = 0.0
        right = 0.0
        for note_idx, freq in enumerate(current_chord):
            # Electric piano / lo-fi bell harmonics
            h1 = math.sin(2 * math.pi * freq * t)
            h2 = 0.35 * math.sin(2 * math.pi * freq * 2 * t)
            h3 = 0.15 * math.sin(2 * math.pi * freq * 3 * t)
            val = (h1 + h2 + h3) * env * 0.25
            
            # Subtle stereo pan
            pan = 0.5 + 0.2 * math.sin(note_idx * 1.5)
            left += val * (1 - pan)
            right += val * pan
            
        # Soft limiter
        left = max(-0.8, min(0.8, left))
        right = max(-0.8, min(0.8, right))
        
        left_int = int(left * 32767)
        right_int = int(right * 32767)
        frames.extend(struct.pack('<hh', left_int, right_int))
        
    wav_file.writeframes(frames)
    wav_file.close()
    print(f"Generated {filename}")

# Track 1: Better Days (Lofi Cmaj7 - Am7 - Dm7 - G7)
generate_track("app/src/main/assets/music/better_days.wav", [
    [261.63, 329.63, 392.00, 493.88], # Cmaj7
    [220.00, 261.63, 329.63, 392.00], # Am7
    [293.66, 349.23, 440.00, 523.25], # Dm7
    [196.00, 246.94, 293.66, 349.23], # G7
], duration_sec=20)

# Track 2: Dreaming Together (Anime ballad Fmaj7 - G - Em7 - Am)
generate_track("app/src/main/assets/music/dreaming_together.wav", [
    [349.23, 440.00, 523.25, 659.25], # Fmaj7
    [392.00, 493.88, 587.33, 783.99], # G
    [329.63, 392.00, 493.88, 587.33], # Em7
    [220.00, 261.63, 329.63, 440.00], # Am
], duration_sec=20)

# Track 3: Focus On You (Binaural Chill Lo-fi Dmaj9 - A/C# - Bm7 - Gmaj7)
generate_track("app/src/main/assets/music/focus_on_you.wav", [
    [293.66, 369.99, 440.00, 554.37], # Dmaj9
    [277.18, 329.63, 440.00, 554.37], # A/C#
    [246.94, 293.66, 369.99, 440.00], # Bm7
    [196.00, 246.94, 293.66, 369.99], # Gmaj7
], duration_sec=20)

# Track 4: Sunset Memories
generate_track("app/src/main/assets/music/sunset_memories.wav", [
    [261.63, 329.63, 392.00, 523.25], # C
    [196.00, 246.94, 293.66, 392.00], # G
    [220.00, 261.63, 329.63, 440.00], # Am
    [174.61, 220.00, 261.63, 349.23], # F
], duration_sec=20)

# Track 5: Night Sky (Celesta Starry tones)
generate_track("app/src/main/assets/music/night_sky.wav", [
    [392.00, 493.88, 587.33, 739.99], # Gmaj7
    [329.63, 392.00, 493.88, 659.25], # Em7
    [261.63, 329.63, 392.00, 523.25], # C
    [293.66, 369.99, 440.00, 587.33], # D
], duration_sec=20)
