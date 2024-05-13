package com.proyectointegrador.madridindustria;

import android.content.*;
import com.google.mlkit.nl.translate.*;

public class Traductor {

    public interface OnTranslationComplete {
        void onTranslationComplete(String translatedText);
        void onTranslationFailed(String errorMessage);
    }

    public static Translator getTranslator() {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.SPANISH)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();

        return Translation.getClient(options);
    }

    public static void descargarModeloTraduccion(Context context) {
        getTranslator().downloadModelIfNeeded()
                .addOnSuccessListener(aVoid -> {
                    SharedPreferences preferences = context.getSharedPreferences("ModoApp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("esDescargado", true);
                    editor.apply();
                })
                .addOnFailureListener(e -> {
                    SharedPreferences preferences = context.getSharedPreferences("ModoApp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("esDescargado", false);
                    editor.apply();
                });
    }

    public static void traducirTexto(String texto, OnTranslationComplete callback, Context context) {
        if (context.getSharedPreferences("ModoApp", Context.MODE_PRIVATE).getBoolean("esDescargado", true)) {
            getTranslator().translate(texto)
                    .addOnSuccessListener(callback::onTranslationComplete)
                    .addOnFailureListener(e -> callback.onTranslationFailed(e.getMessage()));
        } else {
            callback.onTranslationFailed("El modelo de traducción no ha sido descargado.");
        }
    }
}
