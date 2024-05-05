package com.nustfruta.dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nustfruta.R;
import com.nustfruta.authentication.ProfileActivity;
import com.nustfruta.menu_fragments.MenuFragmentAdapter;
import com.nustfruta.utility.Constants;
import com.nustfruta.utility.FirebaseUtil;

import java.util.Random;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    public void onClick(View v) {

        if (v.getId()==optionsIcon.getId())
        {
            openDrawer();
        } else if (v.getId()==profile.getId()) {
            navigateOut(ProfileActivity.class);
        }

        //TODO: navigate out for other buttons.

    }

    DrawerLayout drawerLayout;
    LinearLayout orders, profile, about, logout;

    TabLayout tabLayout;

    ViewPager2 viewPager;
    MenuFragmentAdapter fragmentAdapter;

    ImageView optionsIcon;

    // TODO: remove this, should always be enabled
    boolean factChangeEnabled = false;

    Handler factChangeHandler;

    // the runnable is saved to be able to remove it in onPause.
    Runnable factChangeRunnable;
    Random random = new Random();

    CardView cardA, cardB, currentCard;
    TextView textA, textB;

    // number of fruit facts stored in database
    int fruitFactNumber;

    String fetchedFact;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fetchedFact = getString(R.string.sample_fact);
        initializeViews();
        attachListeners();
        setupMenuFragments();
        syncFruitFactNumber();
        factChangeHandler = new Handler();
    }
    @Override
    protected void onResume() {

        /*
        * the drawer is left open when user navigates to another activity (this activity is set to pause) so if its open when user comes back, close it.
        * i found this to be a better placement then putting it in pause, because in pause the system first waits for the drawer to close, which is jittery
        * in on resume its not jittery at all.
        * */
        closeDrawer();
        super.onResume();
        factChangeHandler.postDelayed( factChangeRunnable = new Runnable() {
            public void run() {
                changeFact();
                factChangeHandler.postDelayed(factChangeRunnable, Constants.FACT_CHANGE_DELAY);
            }
        }, Constants.FACT_CHANGE_DELAY);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // stop handler when activity is paused.
        factChangeHandler.removeCallbacks(factChangeRunnable);

    }

    public void openDrawer()
    {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer()
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void navigateOut(Class activity)
    {
        Intent intent = new Intent(MenuActivity.this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void attachListeners()
    {
        optionsIcon.setOnClickListener(this);
        orders.setOnClickListener(this);
        profile.setOnClickListener(this);
        about.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    public void syncFruitFactNumber()
    {
        FirebaseUtil.getFruitFactReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                fruitFactNumber = (int) snapshot.getChildrenCount();
                setFruitFact(textA);
                setFruitFact(textB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("dbError",error.getMessage());
            }
        });
    }

    public void initializeViews()
    {
        optionsIcon = findViewById(R.id.hamburgerIcon);
        drawerLayout = findViewById(R.id.drawerLayout);
        cardA = findViewById(R.id.cardA);
        currentCard = cardA;
        cardB = findViewById(R.id.cardB);
        textA = findViewById(R.id.textA);
        textB = findViewById(R.id.textB);
        orders=findViewById(R.id.orderRow); profile=findViewById(R.id.profileRow);
        about=findViewById(R.id.aboutRow); logout=findViewById(R.id.logoutRow);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    public void setupMenuFragments()
    {
        fragmentAdapter = new MenuFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(fragmentAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position)->{

           switch (position)
           {
               case 0:
                   //fruit tab
                   tab.setIcon(R.drawable.fruit_apple);
                    break;
               case 1:
                   // salad tab
                   tab.setIcon(R.drawable.fruit_bowl);
                   break;
               case 2:
                   // bundle tab
                   tab.setIcon(R.drawable.fruit_bundle);
           }

        }).attach();

    }

    public void setFruitFact(TextView v)
    {
        if (fruitFactNumber == 0)
        {
            return;
        }

        int randomPos = random.nextInt(fruitFactNumber);
        FirebaseUtil.getFruitFactReference().child(String.valueOf(randomPos)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                v.setText(task.getResult().getValue(String.class));

            }
        });
    }

    public void changeFact() {

        if (!factChangeEnabled)
        {
            return;
        }

        if (currentCard.getId() == cardA.getId())
        {
            animateCardOut(cardA);
            animateCardIn(cardB);

            currentCard = cardB;
        }
        else
        {
            animateCardOut(cardB);
            animateCardIn(cardA);

            currentCard = cardA;
        }
    }

    private void animateCardOut(final View view) {
        ViewPropertyAnimator animator = view.animate()
                .translationX(view.getWidth())
                .alpha(0)
                .setDuration(500);

        animator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);

                // change text of the card that becomes out of view.
                if (view.getId() == cardA.getId())
                {
                    setFruitFact(textA);
                }
                else
                {
                    setFruitFact(textB);
                }
            }
        });
    }

    private void animateCardIn(final View view) {
        view.setTranslationX(-view.getWidth());
        view.setVisibility(View.VISIBLE);

        view.animate()
                .translationX(0)
                .alpha(1)
                .setDuration(500)
                .setListener(null);
    }
}