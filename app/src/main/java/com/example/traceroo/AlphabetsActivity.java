package com.example.traceroo;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Activité qui affiche la grille des lettres de l'alphabet que l'enfant peut tracer
public class AlphabetsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Active l'interface "bord à bord" (Edge to Edge UI)
        EdgeToEdge.enable(this);

        // Définir le layout XML associé à cette activité
        setContentView(R.layout.activity_alphabets);

        // Récupère les éléments de l'interface via leurs ID
        Button reButton = findViewById(R.id.reButton);            // Bouton pour réinitialiser le jeu
        RecyclerView recyclerAlphabet = findViewById(R.id.recyclerView); // Grille des lettres
        TextView title = findViewById(R.id.title);                // Titre en haut de l'écran

        // Charge les animations depuis les fichiers XML
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);    // Apparition douce
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);  // Glissement vers le haut

        // Applique les animations aux vues correspondantes
        title.startAnimation(fadeIn);
        recyclerAlphabet.startAnimation(slideUp);
        reButton.setAnimation(fadeIn);

        // Configure la RecyclerView avec un layout en grille de 3 colonnes
        int numberOfColumns = 3;
        recyclerAlphabet.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        // Initialise l'adaptateur personnalisé pour les lettres
        AlphabetAdapter adapter = new AlphabetAdapter(this);
        recyclerAlphabet.setAdapter(adapter); // Attache l'adaptateur à la grille

        // Gère le clic sur le bouton de réinitialisation
        reButton.setOnClickListener(v -> {
            // Joue un son d'avertissement
            SoundHelper.playSound(this, R.raw.warning);

            // Affiche un dialogue de confirmation
            DialogHelper.showDialog(
                    this,
                    R.drawable.ic_stop, // Icône d'alerte
                    "Réinitialiser le jeu ↺", // Titre du dialogue
                    "Es-tu sûr de vouloir tout recommencer ?\nTous les scores seront perdus !", // Message
                    "Oui, tout réinitialiser", // Texte du bouton positif
                    () -> {
                        // Action si confirmé : réinitialise les scores
                        PrefHelper.resetGame(this);

                        // Recharge l'adaptateur pour actualiser l'affichage
                        recyclerAlphabet.setAdapter(adapter);
                    },
                    "Annuler", // Bouton négatif
                    null // Pas d'action si l'utilisateur annule
            );
        });
    }

    @Override
    public void onBackPressed() {
        // Son d'avertissement lorsqu'on appuie sur le bouton "Retour"
        SoundHelper.playSound(this, R.raw.warning);

        // Dialogue de confirmation pour quitter l’activité
        DialogHelper.showDialog(
                this,
                R.drawable.ic_stop,  // Icône d’alerte
                "Attends !",         // Titre
                "Tu veux quitter le jeu ?", // Message
                "Oui",               // Si oui → quitter l'activité
                () -> super.onBackPressed(),
                "Non",               // Sinon → rester dans l'activité
                null
        );
    }
}
