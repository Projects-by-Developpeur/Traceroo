package com.example.traceroo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

// Classe principale de l'application, l'écran d'accueil
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Active l'affichage bord à bord (Edge-to-Edge UI)
        EdgeToEdge.enable(this);

        // Lie l'activité à son fichier de layout XML (activity_main.xml)
        setContentView(R.layout.activity_main);

        // Joue le son de démarrage du jeu
        SoundHelper.playSound(this, R.raw.gamestart);

        // Récupère les vues depuis le layout
        CardView cardView = findViewById(R.id.pic);        // Carte qui contient une image (mascotte ?)
        TextView title = findViewById(R.id.title);         // Titre principal
        Button commencer = findViewById(R.id.commencer);   // Bouton "Commencer"

        // Charge l'animation "float_up" depuis les ressources
        Animation floatUp = AnimationUtils.loadAnimation(this, R.anim.float_up);

        // Applique l'animation aux éléments de l'interface
        cardView.startAnimation(floatUp);
        title.startAnimation(floatUp);
        commencer.startAnimation(floatUp);

        // Définit le comportement du bouton "Commencer"
        commencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Joue un son "bubble" au clic
                SoundHelper.playSound(MainActivity.this, R.raw.bubble);

                // Lance l'activité des alphabets
                Intent intent = new Intent(MainActivity.this, AlphabetsActivity.class);
                startActivity(intent);

                // Termine l'activité actuelle pour ne pas revenir en arrière avec le bouton retour
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Joue un son d'avertissement quand l'utilisateur appuie sur "Retour"
        SoundHelper.playSound(this, R.raw.warning);

        // Affiche un dialogue personnalisé pour demander confirmation avant de quitter
        DialogHelper.showDialog(
                this,
                R.drawable.ic_stop,     // Icône du dialogue
                "Attends !",            // Titre
                "Tu veux quitter le jeu ?", // Message
                "Oui",                  // Texte bouton positif
                () -> {
                    // Si l'utilisateur clique sur "Oui", on appelle la méthode parente pour quitter
                    super.onBackPressed();
                },
                "Non",                  // Texte bouton négatif
                null                    // Action si "Non" : rien (le dialogue se ferme)
        );
    }
}
