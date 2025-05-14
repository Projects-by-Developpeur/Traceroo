package com.example.traceroo;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefHelper {
    private static final String PREF_NAME = "TraceAppPrefs";  // Nom du fichier SharedPreferences
    private static final String KEY_SCORE = "score";          // Clé pour stocker le score
    private static final String KEY_UNLOCK_PREFIX = "unlocked_"; // Préfixe de la clé pour savoir si une lettre est déverrouillée

    // Récupérer le score actuel depuis SharedPreferences
    public static int getScore(Context context) {
        // Récupère le score stocké, ou 0 si aucun score n'est trouvé
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(KEY_SCORE, 0);
    }

    // Incrémenter le score dans SharedPreferences
    public static void incrementScore(Context context) {
        // Récupérer le score actuel
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int score = prefs.getInt(KEY_SCORE, 0);
        // Mettre à jour le score en l'incrémentant de 1
        prefs.edit().putInt(KEY_SCORE, score + 1).apply();
    }

    // Vérifier si une lettre a été déverrouillée
    public static boolean isUnlocked(Context context, char letter) {
        // La lettre 'A' est toujours déverrouillée par défaut
        if (letter == 'A') return true;
        // Vérifie dans SharedPreferences si la lettre spécifiée est déverrouillée
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_UNLOCK_PREFIX + letter, false);
    }

    // Déverrouiller une lettre et la sauvegarder dans SharedPreferences
    public static void unlockLetter(Context context, char letter) {
        // Marque la lettre comme déverrouillée en la sauvegardant
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_UNLOCK_PREFIX + letter, true)
                .apply();
    }

    // Réinitialiser toutes les données du jeu (score, lettres déverrouillées, etc.)
    public static void resetGame(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Supprime toutes les données enregistrées
        editor.apply(); // Applique les modifications
    }

    private static final String KEY_SCORED_PREFIX = "scored_";  // Préfixe de la clé pour marquer si une lettre a été scorée (pour éviter de scorer plusieurs fois)

    // Marquer une lettre comme "scorée" dans SharedPreferences
    public static void markScored(Context context, char letter) {
        // Indique que la lettre a été tracée correctement et que le score a été comptabilisé
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_SCORED_PREFIX + letter, true)
                .apply();
    }

    // Vérifier si une lettre a déjà été scorée
    public static boolean hasScored(Context context, char letter) {
        // Vérifie si l'utilisateur a déjà gagné un score pour cette lettre
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_SCORED_PREFIX + letter, false);
    }
}
