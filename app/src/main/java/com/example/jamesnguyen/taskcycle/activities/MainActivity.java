package com.example.jamesnguyen.taskcycle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.jamesnguyen.taskcycle.R;
import com.example.jamesnguyen.taskcycle.fragments.NewItemFragment;
import com.example.jamesnguyen.taskcycle.fragments.ReminderFragment;
import com.example.jamesnguyen.taskcycle.fragments.SettingFragment;
import com.example.jamesnguyen.taskcycle.room.ItemDatabase;
import com.example.jamesnguyen.taskcycle.room.ItemEntity;

import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
        NewItemFragment.OnNewItemCreated{

    FloatingActionButton fab;
    //mock ItemDatabase
//    ReminderDatabaseMock database;
    ItemDatabase database;
    private static final int ADD_FLAG = 0;
    private static final int REPLACE_FLAG = 1;

    private static final String FRAGMENT_CODE_EXTRA = "fragment_code_extra";
    public static final int START_DEFAULT_FRAGMENT = 0;
    public static final int START_NEW_ITEM_FRAGMENT = 1;
    public static final int START_SETTING_FRAGMNENT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
        database = ItemDatabase.getInstance(this);
        //populate the database
        //database.getItemDao().deleteAll();
        //populateDb();
        fab = findViewById(R.id.fab);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ReminderFragment.TAG);
        if(fragment==null) {
            fragment = ReminderFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_container, fragment, ReminderFragment.TAG)
                    .commit();
        }

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startFragmentWithBackStack(START_NEW_ITEM_FRAGMENT, ADD_FLAG, null );
            }
        });
    }

    public ItemDatabase getDatabase(){
        return database;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       if (id == R.id.action_settings) {
            startFragmentWithBackStack(START_SETTING_FRAGMNENT, REPLACE_FLAG, null );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNewItemCreated(String itemName, Calendar calendar, boolean hasDate, boolean hasTime) {
        //ReminderMock newItem = new ReminderMock(itemName, calendar, hasDate, hasTime);
        //testEncapsulation(newItem);
        ItemEntity item = new ItemEntity(itemName, calendar.getTimeInMillis(), hasDate, hasTime);
        database.getItemDao().insert(item);

        ReminderFragment fragment = (ReminderFragment)getSupportFragmentManager()
                .findFragmentByTag(ReminderFragment.TAG);
        fragment.updateDatabase();
    }

    public static Intent createIntent(Context context, int fragmentCode){
        Intent intent = new Intent(context, MainActivity.class);
        intent .putExtra(FRAGMENT_CODE_EXTRA, fragmentCode);
        return intent;
    }

    private void startFragmentWithBackStack(int fragmentCode, int stackCode, Bundle argument){
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;
        String tag;

        switch(fragmentCode){
            default:
                fragment = ReminderFragment.newInstance();
                tag = ReminderFragment.TAG;
                break;
            case 1:
                fragment = NewItemFragment.newInstance();
                tag = NewItemFragment.TAG;
                break;
            case 2:
                fragment = SettingFragment.newInstance();
                tag = SettingFragment.TAG;
                break;
        }

        if(argument!=null){
            fragment.setArguments(argument);
        }
        switch(stackCode){
            default: // default back stack, ReminderFragment is the final element in the stack
                fm.beginTransaction()
                        .add(R.id.main_activity_container, fragment, tag)
                        .addToBackStack(ReminderFragment.TAG)
                        .commit();
                break;
            case 1:
                fm.beginTransaction()
                        .replace(R.id.main_activity_container, fragment, tag)
                        .addToBackStack(ReminderFragment.TAG)
                        .commit();
                break;
        }
    }

    public void populateDb(){
        ItemEntity item;
        String title;
        long millis;
        Random rand =new Random();

        for(int i =0;i<5;i++){
            title = "Item #"+Integer.toString(i);
            millis = rand.nextInt(9999999)+1000000;
            item = new ItemEntity(title, millis, true, false);
            database.getItemDao().insert(item);
        }
    }

}
