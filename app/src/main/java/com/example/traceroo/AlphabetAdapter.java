package com.example.traceroo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Adaptateur personnalisé pour la RecyclerView qui affiche les lettres de l'alphabet.
// Gère l'affichage des lettres, le verrouillage/déverrouillage et les interactions.
public class AlphabetAdapter extends RecyclerView.Adapter<AlphabetAdapter.AlphabetViewHolder> {
    private final List<String> alphabetList;    // Liste des lettres de A à Z
    private final Context context;              // Contexte de l'application, utilisé pour accéder aux ressources et démarrer les activités
    private final List<String> unlockedLetters; // Liste des lettres actuellement déverrouillées
    Activity activity;                          // Activité dans laquelle la RecyclerView est affichée

    // Constructeur de l'adaptateur : initialise la liste des lettres et l'activité
    public AlphabetAdapter(Context context) {
        this.context = context;
        this.alphabetList = new ArrayList<>();

        // Ajoute toutes les lettres de l'alphabet à la liste
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            alphabetList.add(String.valueOf(letter));
        }

        // Cast du contexte en activité (attention : s'assurer que le contexte passé est bien une activité)
        this.activity = (Activity) context;

        // Charge les lettres déverrouillées depuis les préférences partagées
        unlockedLetters = loadUnlockedLetters();
    }

    // Fonction de test (ou temporaire) qui retourne une liste de lettres déverrouillées
    private List<String> loadUnlockedLetters() {
        List<String> unlocked = new ArrayList<>();
        unlocked.add("A"); // Par défaut, seule la lettre A est accessible
        return unlocked;
    }

    // Crée une nouvelle vue (item) à afficher dans la RecyclerView
    @NonNull
    @Override
    public AlphabetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // On "inflate" le layout XML d'un item (item_alphabet.xml) pour l'afficher dans la liste
        View view = LayoutInflater.from(context).inflate(R.layout.item_alphabet, parent, false);
        return new AlphabetViewHolder(view);
    }

    // Lie les données (lettre) à une vue existante (ViewHolder)
    @Override
    public void onBindViewHolder(@NonNull AlphabetViewHolder holder, int position) {
        String letter = alphabetList.get(position);  // Récupère la lettre à cette position
        holder.tvLetter.setText(letter);            // Affiche la lettre dans la TextView

        // Applique une animation à l'apparition de l'item (effet visuel)
        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_item);
        holder.itemView.startAnimation(fadeIn);

        // Vérifie dans les préférences si la lettre est déverrouillée
        boolean isUnlocked = PrefHelper.isUnlocked(context, letter.charAt(0));

        if (!isUnlocked) {
            // Si la lettre est verrouillée :
            holder.lockIcon.setVisibility(View.VISIBLE); // Affiche le cadenas

            // On empêche l'accès à l'activité de traçage et on affiche un message
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Joue un son d'avertissement
                    SoundHelper.playSound(context, R.raw.warning);

                    // Affiche un dialogue personnalisé pour prévenir que la lettre est verrouillée
                    DialogHelper.showLetterLockedDialog(
                            context,
                            letter  // Passe la lettre à afficher dans le dialogue
                    );
                }
            });
        } else {
            // Si la lettre est déverrouillée :
            holder.lockIcon.setVisibility(View.GONE); // Cache le cadenas

            // Supprime les éventuels icônes associés à la TextView (par sécurité)
            holder.tvLetter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            // Clic sur une lettre déverrouillée : lance l'activité de traçage
            holder.itemView.setOnClickListener(v -> {
                // Joue un son agréable (bulle, succès)
                SoundHelper.playSound(context, R.raw.bubble);

                // Prépare et démarre l'activité de traçage avec la lettre sélectionnée
                Intent intent = new Intent(context, TraceActivity.class);
                intent.putExtra("selected_letter", letter); // On passe la lettre à tracer à la nouvelle activité
                context.startActivity(intent);

                // Ferme l'activité actuelle (optionnel selon le design de navigation)
                FinishActivity.finishAct(activity);
            });
        }
    }

    // Retourne le nombre total d'éléments à afficher (toujours 26 ici)
    @Override
    public int getItemCount() {
        return alphabetList.size();
    }

    // Classe interne ViewHolder : représente chaque case de la grille avec une lettre
    static class AlphabetViewHolder extends RecyclerView.ViewHolder {
        TextView tvLetter;  // Zone de texte pour afficher la lettre
        ImageView lockIcon; // Icône de cadenas si la lettre est verrouillée

        // Constructeur : on récupère les composants graphiques depuis la vue
        public AlphabetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLetter = itemView.findViewById(R.id.tvLetter);     // Récupère la TextView qui affiche la lettre
            lockIcon = itemView.findViewById(R.id.lockIcon);     // Récupère l'ImageView du cadenas
        }
    }
}
