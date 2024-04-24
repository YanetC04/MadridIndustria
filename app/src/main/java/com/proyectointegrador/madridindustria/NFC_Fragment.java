package com.proyectointegrador.madridindustria;

import android.animation.*;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class NFC_Fragment extends Fragment {

    private ImageView imageView;
    private boolean isScaled = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nfc, container, false);

        imageView = root.findViewById(R.id.imageView);
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            Glide.with(NFC_Fragment.this)
                    .load(R.drawable.whitemadi)
                    .into(imageView);
        } else {
            Glide.with(NFC_Fragment.this)
                    .load(R.drawable.redmadi)
                    .into(imageView);
        }
        imageView.setOnClickListener(v -> {
            animacionImagen();
            openPlayStore();
        });

        return root;
    }

    private void openPlayStore() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.compTit));
        String shareMessage = getResources().getString(R.string.compCont);
        shareMessage = shareMessage + " https://play.google.com/store/apps/details?id=" + requireContext().getPackageName();
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        requireContext().startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.compVia)));
    }

    private void animacionImagen() {
        if (!isScaled) {
            imageView.animate()
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isScaled = true;
                        }
                    })
                    .start();
        } else {
            imageView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isScaled = false;
                        }
                    })
                    .start();
        }
    }
}