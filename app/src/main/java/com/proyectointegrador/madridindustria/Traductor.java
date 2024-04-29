package com.proyectointegrador.madridindustria;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class Traductor {
    public interface OnTranslationComplete {
        void onTranslationComplete(String translatedText);
        void onTranslationFailed(String errorMessage);
    }

    public static void traducirTexto(String texto, final OnTranslationComplete callback) {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.SPANISH)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();
        final Translator englishGermanTranslator =
                Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        englishGermanTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(aVoid -> {
                    englishGermanTranslator.translate(texto)
                            .addOnSuccessListener(s -> {
                                callback.onTranslationComplete(s);
                            })
                            .addOnFailureListener(e -> {
                                callback.onTranslationFailed(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    callback.onTranslationFailed(e.getMessage());
                });
    }
}
