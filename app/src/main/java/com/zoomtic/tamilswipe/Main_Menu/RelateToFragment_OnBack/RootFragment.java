package com.zoomtic.tamilswipe.Main_Menu.RelateToFragment_OnBack;

import android.support.v4.app.Fragment;

/**
 * Created by AQEEL on 3/30/2018.
 */

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImplimentation(this).onBackPressed();
    }
}