package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String yourAPIKey = "4eb61da84756466e8b0cb1276d05167a";
    //private ActivityMainBinding binding;

    ArrayList<CountryCode> countryCodeArrayList = new ArrayList<>();
    ArrayList<LanguageCode> languageCodeArrayList = new ArrayList<>();
    private final HashMap<String, String> countryCodeNameMap = new HashMap<>();

    private ArrayAdapter<Source> arrayAdapter;

    final ArrayList<Source> sourceArrayList = new ArrayList<>();
    ArrayList<Article> articleArrayList = new ArrayList<>();
    private ViewPager2 viewPager;
    ArticleAdapter articleAdapter;


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Menu opt_menu;
    private ConstraintLayout mConstraintLayout;

    HashSet<String> topicsHSet = new HashSet<String>();
    HashSet<String> countriesHSet = new HashSet<String>();
    HashSet<String> languagesHSet = new HashSet<String>();
    ArrayList<Source> sourceArrayListTemp = new ArrayList<>();
    HashSet<Source> sourceHshListTemp = new HashSet<>();
    String currentTopic = null;
    String currentCountry = null;
    String currentLanguage = null;
    String recentMenu = null;
    private String savingArticleID = null;
    private Source savingSourceOBJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mConstraintLayout = findViewById(R.id.c_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        new Thread(new CountryCodesLoader(this)).start();
        new Thread(new LanguageCodesLoader(this)).start();



        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    //selectItem(position);
                    viewPager.setCurrentItem(0);
                    ArticlesDownloader.performDownload(this,sourceArrayListTemp.get(position).getId());
                    savingArticleID = sourceArrayListTemp.get(position).getId();
                    savingSourceOBJ = sourceArrayListTemp.get(position);
                    setTitle(sourceArrayListTemp.get(position).getName());
                    mDrawerLayout.closeDrawer(mConstraintLayout);
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,            /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );



        SourcesDownloader.performDownload(this);

        articleAdapter = new ArticleAdapter(this, articleArrayList);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(articleAdapter);


        // 4eb61da84756466e8b0cb1276d05167a



    }

    // You need this to set up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;

        //ArrayList<String> topicsSubMenuArrList = new ArrayList<>();

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        if (item.hasSubMenu()){
            Log.d(TAG, "onOptionsItemSelected: ITEM: " + item.getItemId());
            recentMenu = item.getTitle().toString();
            return true;
        }

        // HARD CODING
        if(item.getTitle().toString().equals("Clear All")) {
            Log.d(TAG, "onOptionsItemSelected: CLEAR ALL CLICKED!");

            currentTopic = null;
            currentCountry = null;
            currentLanguage = null;

            arrayAdapter.clear();
            arrayAdapter.addAll(sourceArrayList);
            arrayAdapter.notifyDataSetChanged();
            return super.onOptionsItemSelected(item);
        }

        int parentSubmenuID = item.getGroupId();
        int menuItemID = item.getItemId();

        Log.d(TAG, "onOptionsItemSelected: ParentID: " + parentSubmenuID +  " MenuItemID: " + menuItemID);
        arrayAdapter.clear();

        //-- HARD CODING --
        if(recentMenu.equals("Topics")){
            currentTopic = item.getTitle().toString();
            Log.d(TAG, "onOptionsItemSelected: RECENT MENU: " + recentMenu);
            Log.d(TAG, "onOptionsItemSelected: CURRENT TOPIC: " + currentTopic);
        }

        if(recentMenu.equals("Countries")){
            currentCountry = getCountryCodeFromHashSet(item.getTitle().toString());
            Log.d(TAG, "onOptionsItemSelected: RECENT MENU: " + recentMenu);
            Log.d(TAG, "onOptionsItemSelected: CURRENT COUNTRY: " + currentCountry);
        }

        if(recentMenu.equals("Languages")){
            Log.d(TAG, "onOptionsItemSelected: " + item.getTitle().toString());
            currentLanguage = getLanguageCodeFromHashSet(item.getTitle().toString());
            Log.d(TAG, "onOptionsItemSelected: " + currentLanguage);

            Log.d(TAG, "onOptionsItemSelected: RECENT MENU: " + recentMenu);
            Log.d(TAG, "onOptionsItemSelected: CURRENT LANGUAGE: " + currentLanguage);
        }


        filterSource();


        arrayAdapter.addAll(sourceHshListTemp);
        arrayAdapter.notifyDataSetChanged();
        sourceHshListTemp.clear();



        return super.onOptionsItemSelected(item);
    }

    private void filterSource() {
        for(Source source : sourceArrayList) {
            if ((source.getCategory().equals(currentTopic) || currentTopic == null)
                    && (source.getCountry().equals(currentCountry) || currentCountry == null)
                    && (source.getLanguage().equals(currentLanguage) || currentLanguage == null)) {
                sourceHshListTemp.add(source);
                //Log.d(TAG, "filterSource: TOPIC: " + source.getCategory() + " COUNTRY: " + source.getCountry() + " Language: " + source.getLanguage());
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }



    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    public void updateSourceArrayList(ArrayList<Source> _sourceArrayList) {
        this.sourceArrayList.addAll(_sourceArrayList);

        Log.d(TAG, "updateSourceArrayList: " +  sourceArrayList.size());

        if(savingArticleID == null){
            ArticlesDownloader.performDownload(this,sourceArrayList.get(0).getId());
            setTitle(sourceArrayList.get(0).getName());
        } else {
            ArticlesDownloader.performDownload(this, savingArticleID);
            //setTitle(articleArrayList.get(0).);
        }

        sourceArrayListTemp.addAll(sourceArrayList);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, sourceArrayListTemp);
        mDrawerList.setAdapter(arrayAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


        makeSubMenu();

    }

    private void makeSubMenu() {
        SubMenu topicsSubMenu = opt_menu.addSubMenu("Topics");
        SubMenu countriesSubMenu = opt_menu.addSubMenu("Countries");
        SubMenu languagesSubMenu = opt_menu.addSubMenu("Languages");
        opt_menu.add("Clear All");

        //Topics SubMenu
        //HashSet<String> topicsHSet = new HashSet<String>();
        for(int i = 0; i < sourceArrayList.size(); i++) {
            topicsHSet.add(sourceArrayList.get(i).getCategory());
        }

        for (String s :topicsHSet) {
            topicsSubMenu.add(s);
        }

        //Countries SubMenu
        //HashSet<String> countriesHSet = new HashSet<String>();
        for(int i = 0; i < sourceArrayList.size(); i++) {
            countriesHSet.add(sourceArrayList.get(i).getCountry());
        }

//        for (String s :countriesHSet) {
//            countriesSubMenu.add(s);
//        }
//

//        for (String s :countriesHSet) {
//            countriesSubMenu.add(getCountryName(s));
//        }
        //Log.d(TAG, "makeSubMenu: " + countryCodeArrayList.toString() );



        Log.d(TAG, "makeSubMenu: " + countriesHSet);

        for(CountryCode countryCode: countryCodeArrayList){
            if(countriesHSet.contains(countryCode.getCode())) {
                countriesSubMenu.add(countryCode.getName());
                //Log.e(TAG, "makeSubMenu: " + countryCode.getCode() + " " + countryCode.getName() );
            }
        }

        Log.d(TAG, "makeSubMenu: " + countriesSubMenu.size());
        Log.d(TAG, "makeSubMenu: " + countriesHSet.size());
        //Languages Submenu

        for(int i = 0; i < sourceArrayList.size(); i++) {
            languagesHSet.add(sourceArrayList.get(i).getLanguage());
        }

//        for (String s :languagesHSet) {
//            languagesSubMenu.add(s);
//        }

        for(LanguageCode languageCode: languageCodeArrayList){
            if(languagesHSet.contains(languageCode.getCode())) {
                languagesSubMenu.add(languageCode.getName());
                //Log.e(TAG, "makeSubMenu: " + countryCode.getCode() + " " + countryCode.getName() );
            }
        }

        Log.d(TAG, "onCreateOptionsMenu: " + topicsHSet);
        Log.d(TAG, "onCreateOptionsMenu: " + countriesHSet);
        Log.d(TAG, "onCreateOptionsMenu: " + languagesHSet);
    }

    public void updateArticleArrayList(ArrayList<Article> _articlesArrayList) {
        this.articleArrayList.clear();
        this.articleArrayList.addAll(_articlesArrayList);

        Log.d(TAG, "updateArticleArrayList: " +  articleArrayList.size());
        articleAdapter.notifyItemRangeChanged(0,articleAdapter.getItemCount());

    }

    public void acceptResults(ArrayList<CountryCode> countryCodeArrayList) {

        if(this.countryCodeArrayList != null){
            this.countryCodeArrayList.clear();
        }

        this.countryCodeArrayList.addAll(countryCodeArrayList);
//        for (CountryCode countryCode: this.countryCodeArrayList) {
//
//            Log.d(TAG, "acceptResults: " + countryCode.getCode());
//            Log.d(TAG, "acceptResults: " + countryCode.getName());
//            Log.d(TAG, "acceptResults: CODE_FROM_NAME " + getCountryCode(countryCode.getName()));
//            Log.d(TAG, "acceptResults: NAME_FROM_CODE " + getCountryName(countryCode.getCode()));
//        }



    }

    public String getCountryCode(String countryName){
        for (CountryCode countryCode: countryCodeArrayList) {
            if (countryCode.getName().equals(countryName)) {
                return countryCode.getCode();
            }
        }
        return null;
    }
    public String getCountryCodeFromHashSet(String countryName){
        for (CountryCode countryCode: countryCodeArrayList) {
            if (countryCode.getName().equals(countryName)) {
                if(countriesHSet.contains(countryCode.getCode()))
                    return countryCode.getCode();
            }
        }
        return null;
    }

    public String getLanguageCode(String languageName){
        for (LanguageCode languageCode: languageCodeArrayList) {
            if (languageCode.getName().equals(languageName)) {
                return languageCode.getCode();
            }
        }
        return null;
    }

    public String getLanguageCodeFromHashSet(String languageName){
        for (LanguageCode languageCode: languageCodeArrayList) {
            if (languageCode.getName().equals(languageName)) {
                if(languagesHSet.contains(languageCode.getCode()))
                    return languageCode.getCode();
            }
        }
        return null;
    }

    public String getCountryName(String _countryCode){
        for (CountryCode countryCode: countryCodeArrayList) {
            if (countryCode.getCode().equals(_countryCode)) {
                return countryCode.getName();
            }
        }
        return null;
    }

    public void acceptResultsLCD(ArrayList<LanguageCode> languageCodeArrayList) {
        if(this.languageCodeArrayList != null){
            this.languageCodeArrayList.clear();
        }

        this.languageCodeArrayList.addAll(languageCodeArrayList);
        for(LanguageCode lc : languageCodeArrayList) {
            Log.d(TAG, "acceptResultsLCD: " + lc.getCode());
            Log.d(TAG, "acceptResultsLCD: " + lc.getName());
        }
        Log.d(TAG, "acceptResultsLCD: " + languageCodeArrayList.toString());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("SOURCE",savingSourceOBJ);
        outState.putSerializable("SOURCE_HL",sourceHshListTemp);
        Log.d(TAG, "onSaveInstanceState: " + savingArticleID);



        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        Log.d(TAG, "onRestoreInstanceState: " + savedInstanceState.getInt("CURRENT_ITEM"));
        savingSourceOBJ = (Source) savedInstanceState.getSerializable("SOURCE");
        if(savingSourceOBJ != null) {
            setTitle(savingSourceOBJ.getName());
            savingArticleID = savingSourceOBJ.getId();
            //viewPager.setCurrentItem(savedInstanceState.getInt("CURRENT_ITEM"));

            //viewPager.setCurrentItem(8);
            Log.d(TAG, "onRestoreInstanceState: " + viewPager.getCurrentItem());
        }

    }
}