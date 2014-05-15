package com.ehsan.hadithbd;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    Preloader_Controller preloader;
    Instruction_Controller instruction_window;
    Home_Controller home;

    By_Number_View bnv;

    Context context;
    Activity activity;

    DBMS DatabaseControl;

    String PositionRightnow="preloader";
    String Device="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context=getBaseContext();
        activity=this;

        DatabaseControl=new DBMS(getBaseContext());
        DatabaseControl.setup(activity, context);
        //DatabaseControl.ArrayFYallHadithId();

        setContentView(R.layout.preloader);
        SetupScreen();

        /*if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
    }


    void SetupScreen()
    {
        preloader=new Preloader_Controller();

        bnv=new By_Number_View();

        instruction_window=new Instruction_Controller();
        instruction_window.setupScreen(this, getBaseContext());

        home=new Home_Controller();


        DatabaseControl.setListener(new Layout_Listener() {
            @Override
            public void onFired(String item) {
                boolean TimeOut=new Handler().postDelayed(new Runnable() {public void run() {
                    preloader.ShowInstructionButton();
                }}, 1000);

            }
        });

        preloader.setListener(new Layout_Listener() {

            @Override
            public void onFired(String item) {
                if(item=="show_insturction_panel"){
                    setContentView(R.layout.frame);
                    PositionRightnow="instruction_page";
                    instruction_window.createView();
                }
                if(item=="home"){
                    PositionRightnow="home";
                    setContentView(R.layout.list);
                    home.setupScreen(activity, getBaseContext());
                }

                if(item=="screen_setup_for_mobile"){
                    Device="mobile";
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    DatabaseControl.setUpDatabase();
                }
                if(item=="screen_setup_for_tablet"){
                    Device="tablet";
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    DatabaseControl.setUpDatabase();
                }
                Log.i("preloader>>", "!!"+item);
            }
        });
        preloader.setupScreen(this, getBaseContext());

        instruction_window.setListener(new Layout_Listener() {

            @Override
            public void onFired(String item) {
                //setContentView(R.layout.preloader);
                //preloader.setupScreen(activity, getBaseContext());
            }
        });

        home.setListener(new Layout_Listener() {

            @Override
            public void onFired(String item) {
                if(item=="by_number"){
                    setContentView(R.layout.search_by_number);
                    bnv.setupScreen(activity, context);
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(home.PositionRightnow=="home"){
                finish();
                moveTaskToBack(true);
            }else{
                setContentView(R.layout.list);
                home.setupScreen(activity, getBaseContext());
                home.PositionRightnow="home";
            }

            Log.i(">>", PositionRightnow);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
