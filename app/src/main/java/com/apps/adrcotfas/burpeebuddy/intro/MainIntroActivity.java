package com.apps.adrcotfas.burpeebuddy.intro;

import android.os.Bundle;

import com.apps.adrcotfas.burpeebuddy.R;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainIntroActivity extends IntroActivity {

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide(new FragmentSlide.Builder()
                .background(R.color.black)
                .backgroundDark(R.color.gray1000)
                .fragment(WelcomeFragment.newInstance())
                .build());

        IntroSelectExercisesFragment selectExercisesFragment = IntroSelectExercisesFragment.newInstance();
        addSlide(new FragmentSlide.Builder()
                .background(R.color.black)
                .backgroundDark(R.color.gray1000)
                .fragment(selectExercisesFragment)
                .build());

        IntroCreateChallengeFragment introCreateChallengeFragment = IntroCreateChallengeFragment.newInstance();
        addSlide(new FragmentSlide.Builder()
                .background(R.color.black)
                .backgroundDark(R.color.gray1000)
                .fragment(introCreateChallengeFragment)
                .build());
    }
}
