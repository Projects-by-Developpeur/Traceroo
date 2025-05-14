package com.example.traceroo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TraceActivity extends AppCompatActivity {

    private DrawView drawView;
    private Button pointsButton, previousButton, nextButton, exitButton, resetButton, validateButton;
    private String letter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace);

        // Initialisation des éléments de l'interface utilisateur
        drawView = findViewById(R.id.drawView);
        exitButton = findViewById(R.id.exitButton);
        resetButton = findViewById(R.id.resetButton);
        validateButton = findViewById(R.id.validateButton);
        previousButton = findViewById(R.id.previouseButton);
        nextButton = findViewById(R.id.nextButton);
        pointsButton = findViewById(R.id.pointsButton);
        pointsButton.setText("🏆 " + PrefHelper.getScore(this));

        // Application de l'animation "zoomIn" aux boutons
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        previousButton.setAnimation(zoomIn);
        nextButton.setAnimation(zoomIn);
        pointsButton.setAnimation(zoomIn);
        validateButton.setAnimation(zoomIn);
        resetButton.setAnimation(zoomIn);
        exitButton.setAnimation(zoomIn);

        // Récupération de la lettre sélectionnée depuis l'Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selected_letter")) {
            letter = intent.getStringExtra("selected_letter");
            drawView.setLetter(letter); // Définit la lettre à dessiner dans la vue
        }

        // Action pour le bouton "Exit" (Quitter)
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundHelper.playSound(TraceActivity.this, R.raw.warning);
                DialogHelper.showDialog(
                        TraceActivity.this,
                        R.drawable.ic_stop,
                        "Attends !",
                        "Tu veux retourner à l'accueil ?",
                        "Oui",
                        () -> {
                            startActivity(new Intent(TraceActivity.this, AlphabetsActivity.class));
                            finish(); // Ferme l'activité actuelle
                        },
                        "Non",
                        null
                );
            }
        });

        // Action pour le bouton "Reset" (Réinitialiser)
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clearCanvas(); // Efface le dessin
                SoundHelper.playSound(TraceActivity.this, R.raw.bubble);
            }
        });

        // Action pour le bouton "Validate" (Valider)
        validateButton.setOnClickListener(v -> {
            SoundHelper.playSound(TraceActivity.this, R.raw.bubble);
            if (drawView.isEnoughDrawing()) { // Vérifie si la trace est suffisante
                if (!PrefHelper.hasScored(this, letter.charAt(0))) {
                    // Ajoute le score si la lettre n'a pas encore été scorée
                    PrefHelper.incrementScore(this);
                    PrefHelper.markScored(this, letter.charAt(0)); // Marque la lettre comme scorée
                    pointsButton.setText("🏆 " + PrefHelper.getScore(this)); // Mise à jour du score affiché

                    // Déverrouille la lettre suivante si elle existe
                    if (letter.charAt(0) < 'Z') {
                        char nextLetter = (char) (letter.charAt(0) + 1);
                        PrefHelper.unlockLetter(this, nextLetter);
                    }
                }

                // Affiche un message de réussite
                SoundHelper.playSound(this, R.raw.success);
                DialogHelper.showDialog(
                        this,
                        R.drawable.bravo,
                        "Bravo !",
                        "Tu as bien tracé la lettre !",
                        "👉 Lettre suivante",
                        () -> {
                            if (letter.charAt(0) < 'Z') {
                                letter = String.valueOf((char) (letter.charAt(0) + 1));
                                drawView.setLetter(letter);
                                drawView.clearCanvas(); // Efface le dessin pour la nouvelle lettre
                                pointsButton.setText("🏆 " + PrefHelper.getScore(this));
                            } else if (letter.charAt(0) == 'Z') {
                                SoundHelper.playSound(TraceActivity.this, R.raw.applause);
                                DialogHelper.showDialog(
                                        this,
                                        R.drawable.ic_trophy, // Icône de trophée
                                        "Bravo ! 🎉",
                                        "Tu as fini toutes les lettres ! \nTon score= " +(PrefHelper.getScore(this) + " 🏆")+ "  \nExcellent travail 👏",
                                        "Retour",
                                        () -> {
                                            startActivity(new Intent(this, AlphabetsActivity.class));
                                        },
                                        null,
                                        null
                                );
                            }
                        },
                        "↺ Réessayer",
                        () -> drawView.clearCanvas() // Réinitialise la zone de dessin
                );
            } else {
                SoundHelper.playSound(this, R.raw.fail);
                // Affiche un message d'erreur si la trace est insuffisante
                DialogHelper.showDialog(
                        this,
                        R.drawable.ic_tryagain, // Icône pour l'erreur
                        "Essayer encore 😅",
                        "Tu dois mieux tracer la lettre.\nRéessaie encore une fois !",
                        "↺ Réessayer",
                        () -> drawView.clearCanvas(), // Efface le dessin pour une nouvelle tentative
                        null,
                        null
                );
            }
        });

        // Action pour le bouton "Previous" (Précédent)
        previousButton.setOnClickListener(v -> {
            changeLetter(false); // Va à la lettre précédente
            drawView.clearCanvas(); // Efface le dessin pour la nouvelle lettre
        });

        // Action pour le bouton "Next" (Suivant)
        nextButton.setOnClickListener(v -> {
            changeLetter(true); // Va à la lettre suivante
            drawView.clearCanvas(); // Efface le dessin pour la nouvelle lettre
        });
    }

    // Fonction pour changer la lettre actuelle (suivant ou précédent)
    private void changeLetter(boolean isNext) {
        if (isNext) {
            letter = getNextLetter(letter); // Va à la lettre suivante
        } else {
            letter = getPreviousLetter(letter); // Va à la lettre précédente
        }

        drawView.setLetter(letter); // Met à jour la lettre dans DrawView
    }

    // Récupère la lettre suivante déverrouillée
    private String getNextLetter(String currentLetter) {
        SoundHelper.playSound(TraceActivity.this, R.raw.bubble);
        char letterChar = currentLetter.charAt(0);

        // Recherche la prochaine lettre déverrouillée
        while (letterChar < 'Z') {
            letterChar++;
            if (PrefHelper.isUnlocked(this, letterChar)) {
                return String.valueOf(letterChar);
            } else {
                // Affiche un message si la lettre suivante est verrouillée
                DialogHelper.showLetterLockedDialog(
                        this,
                        ""
                );
                break; // Arrête la recherche dès qu'une lettre verrouillée est trouvée
            }
        }
        if (letterChar == 'Z') {
            SoundHelper.playSound(TraceActivity.this, R.raw.applause);
            DialogHelper.showDialog(
                    this,
                    R.drawable.ic_trophy, // Icône de trophée
                    "Bravo ! 🎉",
                    "Tu as fini toutes les lettres !\nExcellent travail 👏",
                    "Retour",
                    () -> {
                        SoundHelper.stopSound();
                        startActivity(new Intent(this, AlphabetsActivity.class)); // Retour à l'écran principal
                        finish();
                    },
                    null,
                    null
            );
        }

        return currentLetter;
    }

    // Récupère la lettre précédente déverrouillée
    private String getPreviousLetter(String currentLetter) {
        SoundHelper.playSound(TraceActivity.this, R.raw.bubble);
        char letterChar = currentLetter.charAt(0);

        // Recherche la lettre précédente déverrouillée
        while (letterChar > 'A') {
            letterChar--;
            if (PrefHelper.isUnlocked(this, letterChar)) {
                return String.valueOf(letterChar);
            }
        }
        if (letterChar == 'A') {
            DialogHelper.showDialog(
                    this,
                    R.drawable.ic_lock,
                    "Oups ! 😅",
                    "Tu ne peux pas revenir en arrière.\nContinue à apprendre !",
                    "OK",
                    null,
                    null,
                    null
            );
        }
        return currentLetter;
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        SoundHelper.stopSound();
        startActivity(new Intent(this, AlphabetsActivity.class)); // Retour à l'écran principal
        finish(); // Ferme l'activité actuelle
    }

}
