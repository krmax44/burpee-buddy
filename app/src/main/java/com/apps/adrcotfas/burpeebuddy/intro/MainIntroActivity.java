package com.apps.adrcotfas.burpeebuddy.intro;

import android.os.Bundle;

import com.apps.adrcotfas.burpeebuddy.R;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainIntroActivity extends IntroActivity {

    private IntroCreateChallengeFragment introCreateChallengeFragment;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        IntroSelectExercisesFragment selectExercisesFragment = IntroSelectExercisesFragment.newInstance();

        setButtonBackVisible(false);
        setButtonNextVisible(false);
        //setFullscreen(true);
        addSlide(new FragmentSlide.Builder()
                .background(R.color.black)
                .backgroundDark(R.color.gray1000)
                .fragment(selectExercisesFragment)
                .build());

        introCreateChallengeFragment = IntroCreateChallengeFragment.newInstance();
        addSlide(new FragmentSlide.Builder()
                .background(R.color.black)
                .backgroundDark(R.color.gray1000)
                .fragment(introCreateChallengeFragment)
                .build());
    }
}
