package com.example.traceroo;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundHelper {

    // MediaPlayer statique pour gérer les sons à travers l'application
    private static MediaPlayer mediaPlayer;

    /**
     * Joue un son à partir d'une ressource audio.
     * @param context Le contexte actuel (nécessaire pour accéder aux ressources)
     * @param resId L'identifiant de la ressource audio à jouer (ex: R.raw.success)
     */
    public static void playSound(Context context, int resId) {
        // Si un son est déjà en cours, l'arrêter proprement
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();    // Stoppe la lecture
            mediaPlayer.release(); // Libère les ressources
        }

        // Crée un nouveau MediaPlayer avec la ressource fournie
        mediaPlayer = MediaPlayer.create(context, resId);
        mediaPlayer.setLooping(false); // Ne pas répéter le son
        mediaPlayer.start();           // Lance la lecture
    }

    /**
     * Arrête la lecture du son en cours s’il y en a un.
     */
    public static void stopSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();       // Stoppe le son
            mediaPlayer.release();    // Libère les ressources
            mediaPlayer = null;       // Réinitialise le MediaPlayer
        }
    }
}
