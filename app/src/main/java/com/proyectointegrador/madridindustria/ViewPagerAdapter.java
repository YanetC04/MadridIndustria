package com.proyectointegrador.madridindustria;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Aquí debes devolver el Fragment correspondiente a cada posición
        // Por ejemplo:
        switch (position) {
            case 0:
                return new QR_Fragment();
            case 1:
                return new NFC_Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
