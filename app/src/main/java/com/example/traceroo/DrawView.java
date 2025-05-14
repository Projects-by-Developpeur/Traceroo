package com.example.traceroo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

/**
 * Cette classe personnalisée représente une vue sur laquelle l’enfant peut tracer
 * une lettre affichée à l’écran. Elle gère le dessin, les interactions tactiles,
 * et la vérification du traçage correct.
 */
public class DrawView extends View {

    private Paint drawPaint;         // Outil pour dessiner les traits de l’enfant
    private Path drawPath;           // Chemin tracé par l’enfant

    private TextPaint textPaint;     // Outil pour dessiner la lettre de référence
    private Path letterPath;         // Chemin de la lettre à tracer

    private String letter = "A";     // Lettre par défaut à tracer
    private boolean isDrawing;       // Indique si l’enfant est en train de tracer

    // Constructeur : initialise les outils de dessin et charge la police
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Charger la police personnalisée dynapuff
        Typeface typeface = ResourcesCompat.getFont(context, R.font.dynapuff);

        // Configuration du pinceau pour le tracé de l’enfant
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(Color.parseColor("#E1CCFB"));  // Couleur douce pour les enfants
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeWidth(90f); // Trait large et lisible
        drawPath = new Path();

        // Configuration du pinceau pour dessiner la lettre en arrière-plan
        textPaint = new TextPaint();
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(800f);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(typeface);

        letterPath = new Path();
    }

    // Méthode appelée automatiquement pour dessiner la vue
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dessiner la lettre de référence au centre de l’écran
        if (letter != null) {
            letterPath.reset();
            textPaint.getTextPath(letter, 0, letter.length(),
                    getWidth() / 2f,
                    getHeight() / 2f + 150f,
                    letterPath);
            canvas.drawPath(letterPath, textPaint);
        }

        // Dessiner le chemin tracé par l’utilisateur
        canvas.drawPath(drawPath, drawPaint);
    }

    // Gestion des événements tactiles (tracer avec le doigt)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(); float y = event.getY();
        // Ignorer les points hors de la lettre
        if (!isInsideLetterPath(x, y)) {return false;}
        // Réagir en fonction de l’action tactile
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(x, y);  // Commencer un nouveau trait
                isDrawing = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDrawing) drawPath.lineTo(x, y);  // Continuer le trait
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;  // Fin du trait
                break;
        }
        invalidate(); // Redessiner la vue
        return true;
    }

    // Vérifie si un point est à l’intérieur de la lettre à tracer
    private boolean isInsideLetterPath(float x, float y) {
        RectF bounds = new RectF();
        letterPath.computeBounds(bounds, true);
        Region region = new Region();
        region.setPath(letterPath, new Region(
                (int) bounds.left,
                (int) bounds.top,
                (int) bounds.right,
                (int) bounds.bottom));
        return region.contains((int) x, (int) y);
    }

    // Efface le dessin de l’utilisateur
    public void clearCanvas() {
        drawPath.reset();
        invalidate();
    }

    // Change la lettre à tracer
    public void setLetter(String letter) {
        this.letter = letter;
        invalidate();
    }

    // Vérifie si le traçage de l’utilisateur couvre suffisamment la lettre affichée
    public boolean isEnoughDrawing() {
        if (letterPath == null || drawPath == null) return false;
        int width = getWidth();
        int height = getHeight();
        // Créer deux bitmaps pour comparer les pixels
        Bitmap letterBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap drawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas letterCanvas = new Canvas(letterBitmap);
        Canvas drawCanvas = new Canvas(drawBitmap);
        Paint tempPaint = new Paint();
        tempPaint.setColor(Color.BLACK);
        tempPaint.setStyle(Paint.Style.FILL);
        tempPaint.setAntiAlias(true);
        // Dessiner les chemins sur les bitmaps
        letterCanvas.drawPath(letterPath, tempPaint);
        drawCanvas.drawPath(drawPath, tempPaint);
        int totalPixels = 0;
        int matchedPixels = 0;
        int widthStep = 2;
        int heightStep = 2;
        // Comparer les pixels entre le chemin cible et celui dessiné
        for (int x = 0; x < width; x += widthStep) {
            for (int y = 0; y < height; y += heightStep) {
                if (letterBitmap.getPixel(x, y) != 0) {
                    totalPixels++;
                    if (drawBitmap.getPixel(x, y) != 0) {
                        matchedPixels++;
                    }
                }
            }
        }

        // Libérer la mémoire
        letterBitmap.recycle();
        drawBitmap.recycle();

        if (totalPixels == 0) return false;

        // Vérifier si l’utilisateur a dessiné au moins 40% du chemin de la lettre
        float matchPercentage = (matchedPixels * 1f) / totalPixels;
        return matchPercentage > 0.4f;
    }
}
