package com.girgin.wordest.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.girgin.wordest.Adapters.AdapterActivityMainWordsList;
import com.girgin.wordest.App;
import com.girgin.wordest.Fragments.FragmentActivityMainAddWord;
import com.girgin.wordest.Fragments.FragmentActivityMainWordInfo;
import com.girgin.wordest.Fragments.FragmentExercise;
import com.girgin.wordest.Fragments.FragmentPlaySelector;
import com.girgin.wordest.Fragments.FragmentProfile;
import com.girgin.wordest.Fragments.FragmentStart;
import com.girgin.wordest.Fragments.FragmentTest;
import com.girgin.wordest.R;
import com.girgin.wordest.SQLite.SQLite;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {


    //***
    SQLite sqLite;
    AdapterActivityMainWordsList adapterActivityMainWordsList;
    ArrayList<AsyncTask> asyncTasksArrayList = new ArrayList<>();

    //*** FRAGMENTS
    FragmentStart fragmentStart = null;
    FragmentPlaySelector fragmentPlaySelector = null;
    FragmentExercise fragmentExercise = null;
    FragmentTest fragmentTest = null;
    FragmentProfile fragmentProfile = null;
    FragmentActivityMainAddWord fragmentActivityMainAddWord = null;
    FragmentActivityMainWordInfo fragmentActivityMainWordInfo = null;

    //*** LISTS
    ArrayList<ArrayList<String>> words_list = new ArrayList<>();
    ArrayList<ArrayList<String>> favorite_words_list = new ArrayList<>();

    //*** VARIABLES
    boolean isFavoriteWordsList;
    int lastPageIndex = 0;

    //*** COMPONENTS
    SwipeRefreshLayout
            SWIPEREFRESHLAYOUT;
    RecyclerView
            RECYCLERVIEW_MAIN_WORDS;
    SpaceNavigationView
            SPACENAVIGATIONVIEW;
    FrameLayout
            frameLayout_main,
            frameLayout_playSelector;
    ImageButton
            imageButton_add,
            imageButton_favorite;
    TextView
            textView_wordCount,
            textView_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //***
        isFavoriteWordsList = false;
        //***
        loadFragmentStart();
        //***
        loadTextViews();
        //***
        loadImageButtons();
        //***
        loadBottomNavigation();
        //***
        loadSwipeRefreshLayout();
        //***
        loadRecyclerViews();
        //***
        loadFrameLayout();
        //***
        sqLite = new SQLite(this);
        //***
        asyncTasksArrayList.add(new loadWords().execute());
        //***
    }

    @Override
    public void onBackPressed() {
        if (isFragmentOpen()) {
            if (fragmentActivityMainAddWord != null) {
                removeFragmentAddWord();
            }
            if (fragmentActivityMainWordInfo != null) {
                removeFragmentWordInfo();
            }
            if (fragmentStart != null) {
                finish();
            }
            if (fragmentExercise != null && fragmentPlaySelector == null) {
                removeFragmentExercise();
                SPACENAVIGATIONVIEW.changeCurrentItem(lastPageIndex);
                refreshRecyclerView();
            } else if (fragmentTest != null && fragmentPlaySelector == null) {
                removeFragmentTest();
                SPACENAVIGATIONVIEW.changeCurrentItem(lastPageIndex);
                refreshRecyclerView();
            } else if (fragmentProfile != null && fragmentPlaySelector == null) {
                removeFragmentProfile();
                SPACENAVIGATIONVIEW.changeCurrentItem(0);
            } else if (fragmentPlaySelector != null){
                removeFragmentPlaySelector();
                SPACENAVIGATIONVIEW.changeCurrentItem(lastPageIndex);
            }
            if (fragmentPlaySelector != null && (fragmentExercise != null || fragmentTest != null)) {
                removeFragmentPlaySelector();
            }
        } else {
            finish();
        }
    }

    //**********************************************************************************************
    //FUNCTIONS

    boolean isFragmentOpen() {
        return getSupportFragmentManager().getFragments().size() > 0;
    }

    public void refreshRecyclerView() {
        asyncTasksArrayList.add(new loadWords().execute());
    }

    private void addElementToAdd() {
        ArrayList<String> list = new ArrayList<>();
        list.add(0, "0");
        list.add(1, "+");
        list.add(2, "0");
        int position = words_list.size();
        words_list.add(position, list);
    }

    public void setFavoriteWordsList() {
        favorite_words_list.clear();
        for (ArrayList<String> item : words_list) {
            if (item.get(2).equals("1")) {
                favorite_words_list.add(item);
            }
        }
    }

    //**********************************************************************************************
    //BACKGROUND PROCESSES

    @SuppressLint("StaticFieldLeak")
    private class loadWords extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            words_list = sqLite.getWords();
            if (words_list.size() == 0) {
                addElementToAdd();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (words_list.get(0).get(0).equals("0")) {
                textView_wordCount.setText("0");
            } else {
                textView_wordCount.setText(String.valueOf(words_list.size()));
            }
            adapterActivityMainWordsList = new AdapterActivityMainWordsList(ActivityMain.this, words_list);
            RECYCLERVIEW_MAIN_WORDS.setAdapter(adapterActivityMainWordsList);
            RECYCLERVIEW_MAIN_WORDS.startLayoutAnimation();
            SPACENAVIGATIONVIEW.setCentreButtonSelectable(!words_list.get(0).get(0).equals("0"));
        }
    }

    //**********************************************************************************************
    //FRAGMENTS

    //*** REMOVE FRAGMENTS

    public void removeFragmentStart() {
        if (fragmentStart != null) {
            getSupportFragmentManager().beginTransaction().remove(fragmentStart).commitAllowingStateLoss();
            fragmentStart = null;
        }
    }

    public void removeFragmentPlaySelector() {
        if (fragmentPlaySelector != null) {
            frameLayout_playSelector.removeAllViews();
            getSupportFragmentManager().beginTransaction().remove(fragmentPlaySelector).commitAllowingStateLoss();
            fragmentPlaySelector = null;
            frameLayout_playSelector.setVisibility(View.INVISIBLE);
        }
    }

    public void removeFragmentExercise() {
        if (fragmentExercise != null) {
            frameLayout_main.removeAllViews();
            getSupportFragmentManager().beginTransaction().remove(fragmentExercise).commitAllowingStateLoss();
            fragmentExercise = null;
            frameLayout_main.setVisibility(View.INVISIBLE);
        }
    }

    public void removeFragmentTest() {
        if (fragmentTest != null) {
            frameLayout_main.removeAllViews();
            getSupportFragmentManager().beginTransaction().remove(fragmentTest).commitAllowingStateLoss();
            fragmentTest = null;
            frameLayout_main.setVisibility(View.INVISIBLE);
        }
    }

    public void removeFragmentProfile() {
        if (fragmentProfile != null) {
            frameLayout_main.removeAllViews();
            getSupportFragmentManager().beginTransaction().remove(fragmentProfile).commitAllowingStateLoss();
            fragmentProfile = null;
            frameLayout_main.setVisibility(View.INVISIBLE);
        }
    }

    public void removeFragmentAddWord() {
        if (fragmentActivityMainAddWord != null) {
            getSupportFragmentManager().beginTransaction().remove(fragmentActivityMainAddWord).addToBackStack("fragmentActivityMainAddWord").commitAllowingStateLoss();
            fragmentActivityMainAddWord = null;
        }
    }

    public void removeFragmentWordInfo() {
        if (fragmentActivityMainWordInfo != null) {
            getSupportFragmentManager().beginTransaction().remove(fragmentActivityMainWordInfo).addToBackStack("fragmentActivityMainWordInfo").commitAllowingStateLoss();
            fragmentActivityMainWordInfo = null;
        }
    }

    //*** LOAD FRAGMENTS

    public void loadFragmentStart() {
        fragmentStart = new FragmentStart();
        App.loadFragment(getSupportFragmentManager(), R.id.page_main, fragmentStart, "fragmentStart", null);
    }

    public void loadFragmentPlaySelector() {
        fragmentPlaySelector = new FragmentPlaySelector();
        App.loadFragment(getSupportFragmentManager(), R.id.activityMain_frameLayout_playSelector, fragmentPlaySelector, "fragmentPlaySelector", null);
        frameLayout_playSelector.setVisibility(View.VISIBLE);
    }

    public void loadFragmentExercise() {
        fragmentExercise = new FragmentExercise();
        App.loadFragment(getSupportFragmentManager(), R.id.activityMain_frameLayout, fragmentExercise, "fragmentPlay", null);
        frameLayout_main.setVisibility(View.VISIBLE);
    }

    public void loadFragmentTest() {
        fragmentTest = new FragmentTest();
        App.loadFragment(getSupportFragmentManager(), R.id.activityMain_frameLayout, fragmentTest, "fragmentTest", null);
        frameLayout_main.setVisibility(View.VISIBLE);
    }

    public void loadFragmentProfile() {
        fragmentProfile = new FragmentProfile();
        App.loadFragment(getSupportFragmentManager(), R.id.activityMain_frameLayout, fragmentProfile, "fragmentProfile", null);
        frameLayout_main.setVisibility(View.VISIBLE);
    }

    public void loadFragmentAddWord() {
        removeFragmentAddWord();
        fragmentActivityMainAddWord = new FragmentActivityMainAddWord();
        App.loadFragment(getSupportFragmentManager(), R.id.page_main, fragmentActivityMainAddWord, "fragmentActivityMainAddWord", null);
    }

    public void loadFragmentWordInfo(int WORD_ID, String WORD) {
        removeFragmentWordInfo();
        fragmentActivityMainWordInfo = new FragmentActivityMainWordInfo();
        Bundle bundle = new Bundle();
        bundle.putInt("WORD_ID", WORD_ID);
        bundle.putString("WORD", WORD);
        fragmentActivityMainWordInfo.setArguments(bundle);
        App.loadFragment(getSupportFragmentManager(), R.id.page_main, fragmentActivityMainWordInfo, "fragmentActivityMainWordInfo", null);
    }

    //**********************************************************************************************
    //LOAD COMPONENTS

    @SuppressLint("ClickableViewAccessibility")
    void loadRecyclerViews() {
        RECYCLERVIEW_MAIN_WORDS = findViewById(R.id.activity_main_recyclerview);
    }

    void loadTextViews() {
        textView_wordCount = findViewById(R.id.activityMain_textView_wordCount);
        textView_header = findViewById(R.id.activityMain_textView_header);
    }

    void loadFrameLayout() {
        frameLayout_main = findViewById(R.id.activityMain_frameLayout);
        frameLayout_playSelector = findViewById(R.id.activityMain_frameLayout_playSelector);
    }

    void loadSwipeRefreshLayout() {
        SWIPEREFRESHLAYOUT = findViewById(R.id.activity_main_swipeRefreshLayout);
        SWIPEREFRESHLAYOUT.setOnRefreshListener(() -> {
            if (isFavoriteWordsList) {
                adapterActivityMainWordsList = new AdapterActivityMainWordsList(ActivityMain.this, favorite_words_list);
                RECYCLERVIEW_MAIN_WORDS.setAdapter(adapterActivityMainWordsList);
                RECYCLERVIEW_MAIN_WORDS.startLayoutAnimation();
            } else {
                refreshRecyclerView();
            }
            SWIPEREFRESHLAYOUT.setRefreshing(false);
        });
    }

    void loadImageButtons() {
        imageButton_add = findViewById(R.id.activityMain_imageButton_add);
        imageButton_add.setOnClickListener(v -> loadFragmentAddWord());
        imageButton_favorite = findViewById(R.id.activityMain_imageButton_favorite);
        imageButton_favorite.setOnClickListener(v -> {
            if (!isFavoriteWordsList) {
                setFavoriteWordsList();
                if (favorite_words_list.size() < 1) {
                    App.toastShowLong(this, getString(R.string.Your_favorite_list_is_empty));
                } else {
                    adapterActivityMainWordsList = new AdapterActivityMainWordsList(ActivityMain.this, favorite_words_list);
                    RECYCLERVIEW_MAIN_WORDS.setAdapter(adapterActivityMainWordsList);
                    RECYCLERVIEW_MAIN_WORDS.startLayoutAnimation();
                    imageButton_favorite.setImageResource(R.drawable.icon_favorite_filled_light);
                    isFavoriteWordsList = true;
                    imageButton_add.setVisibility(View.INVISIBLE);
                    textView_header.setText(getString(R.string.MY_FAVORITE_WORDS_LIST));
                    textView_wordCount.setText(String.valueOf(favorite_words_list.size()));
                }
            } else {
                adapterActivityMainWordsList = new AdapterActivityMainWordsList(ActivityMain.this, words_list);
                RECYCLERVIEW_MAIN_WORDS.setAdapter(adapterActivityMainWordsList);
                RECYCLERVIEW_MAIN_WORDS.startLayoutAnimation();
                imageButton_favorite.setImageResource(R.drawable.icon_favorite_empty_light);
                isFavoriteWordsList = false;
                imageButton_add.setVisibility(View.VISIBLE);
                textView_header.setText(getString(R.string.MY_WORDS_LIST));
                if (words_list.get(0).get(0).equals("0")) {
                    textView_wordCount.setText("0");
                } else {
                    textView_wordCount.setText(String.valueOf(words_list.size()));
                }
            }
        });
    }

    @SuppressLint({"WrongConstant", "ResourceAsColor"})
    void loadBottomNavigation() {
        Typeface typeface = ResourcesCompat.getFont(this, R.font.bahnschrift);
        SPACENAVIGATIONVIEW = findViewById(R.id.activity_main_bottom_navigation);
        SPACENAVIGATIONVIEW.addSpaceItem(new SpaceItem("HOME", R.drawable.icon_home));
        SPACENAVIGATIONVIEW.addSpaceItem(new SpaceItem("PROFILE", R.drawable.icon_profile));
        SPACENAVIGATIONVIEW.setFont(typeface);
        SPACENAVIGATIONVIEW.setCentreButtonSelectable(true);
        SPACENAVIGATIONVIEW.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                if (fragmentPlaySelector == null && !words_list.get(0).get(0).equals("0")) {
                    loadFragmentPlaySelector();
                } else {
                    App.toastShowLong(getApplicationContext(), getString(R.string.Your_words_list_is_empty));
                    SPACENAVIGATIONVIEW.changeCurrentItem(lastPageIndex);
                }
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    lastPageIndex = 0;
                    if (isFragmentOpen()) {
                        if (fragmentExercise != null) {
                            removeFragmentExercise();
                            refreshRecyclerView();
                        } else if (fragmentPlaySelector != null) {
                            removeFragmentPlaySelector();
                        } else if (fragmentProfile != null) {
                            removeFragmentProfile();
                        } else if (fragmentTest != null) {
                            removeFragmentTest();
                            refreshRecyclerView();
                        }
                    }
                } else if (itemIndex == 1) {
                    lastPageIndex = 1;
                    if (isFragmentOpen()) {
                        if (fragmentExercise != null) {
                            removeFragmentExercise();
                        } else if (fragmentPlaySelector != null) {
                            removeFragmentPlaySelector();
                        }
                    }
                    loadFragmentProfile();
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    lastPageIndex = 0;
                } else if (itemIndex == 1) {
                    lastPageIndex = 1;
                }
            }
        });
    }
}