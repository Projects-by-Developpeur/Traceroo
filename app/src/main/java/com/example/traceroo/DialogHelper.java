package com.example.traceroo;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class DialogHelper {

    /**
     * Affiche une boîte de dialogue personnalisée avec une icône, un titre, un message
     * et des boutons personnalisables (positif et éventuellement négatif).
     *
     * @param context Le contexte de l'activité
     * @param iconResId L'identifiant de l'icône à afficher dans la boîte de dialogue
     * @param titleText Le titre de la boîte de dialogue
     * @param messageText Le message explicatif
     * @param positiveBtnText Le texte du bouton positif
     * @param onPositiveClick L'action à exécuter si l'utilisateur clique sur le bouton positif
     * @param negativeBtnText (Optionnel) Le texte du bouton négatif
     * @param onNegativeClick (Optionnel) L'action à exécuter si l'utilisateur clique sur le bouton négatif
     */
    public static void showDialog(Context context,
                                  int iconResId,
                                  String titleText,
                                  String messageText,
                                  String positiveBtnText,
                                  @Nullable Runnable onPositiveClick,
                                  @Nullable String negativeBtnText,
                                  @Nullable Runnable onNegativeClick) {

        // Charge la vue personnalisée pour la boîte de dialogue
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_cute, null);

        // Récupère les éléments de la vue
        ImageView icon = view.findViewById(R.id.dialogIcon);
        TextView title = view.findViewById(R.id.dialogTitle);
        TextView message = view.findViewById(R.id.dialogMessage);

        // Charge une police personnalisée (dynapuff)
        Typeface font = ResourcesCompat.getFont(context, R.font.dynapuff);
        title.setTypeface(font);
        message.setTypeface(font);

        // Applique les données à la vue
        icon.setImageResource(iconResId);
        title.setText(titleText);
        message.setText(messageText);

        // Crée la boîte de dialogue avec les actions associées
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(positiveBtnText, (dialog, which) -> {
                    SoundHelper.playSound(context, R.raw.bubble);
                    if (onPositiveClick != null) onPositiveClick.run(); // Exécute le bouton positif
                });

        // Si un bouton négatif est défini, l'ajouter à la boîte de dialogue
        if (negativeBtnText != null && onNegativeClick != null) {
            builder.setNegativeButton(negativeBtnText, (dialog, which) -> {
                SoundHelper.playSound(context, R.raw.bubble);
                onNegativeClick.run(); // Exécute le bouton négatif
            });
        }

        // Affiche la boîte de dialogue
        builder.show();
    }

    /**
     * Affiche une boîte de dialogue spécifique quand une lettre est verrouillée.
     *
     * @param context Le contexte d'affichage
     * @param letter La lettre verrouillée (peut être vide si non utilisée)
     */
    public static void showLetterLockedDialog(Context context, String letter) {
        showDialog(
                context,
                R.drawable.ic_lock,
                "Lettre verrouillée 🔒",
                "La lettre " + letter + " est encore verrouillée.\nPratique les lettres précédentes pour la débloquer !",
                "OK",
                null,
                null,
                null
        );
    }
}
