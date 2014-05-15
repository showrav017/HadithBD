package com.ehsan.hadithbd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Muhammad Ehsanul Hoq on 4/3/14.
 */
public class Preloader_Controller {

    ImageView logo;
    public Activity activity;
    Context _c;
    TextView MiddleInstruction;
    TextView BottomInstruction;
    Display display;
    Typeface DeafultFont;

    Button Btt_Start;
    Button Btt_Instruction;

    Layout_Listener listenerInstPage;
    RelativeLayout container;

    public Preloader_Controller() {

    }


    void setupScreen(Activity _activity, Context context)
    {
        this.activity = _activity;
        this._c=context;
        logo = (ImageView)this.activity.findViewById(R.id.logo);
        display = this.activity.getWindowManager().getDefaultDisplay();

        MiddleInstruction = (TextView)this.activity.findViewById(R.id.InstructionText);
        MiddleInstruction.setVisibility(View.GONE);

        container=(RelativeLayout)this.activity.findViewById(R.id.container);

        MiddleInstruction.setText(this.activity.getResources().getString(R.string.loading_instruction));
        DeafultFont= Typeface.createFromAsset(this.activity.getAssets(), "NotoSansBengali-Regular.ttf");
        MiddleInstruction.setTypeface(DeafultFont);

        BottomInstruction = (TextView)this.activity.findViewById(R.id.bottomTexts);
        BottomInstruction.setVisibility(View.GONE);

        BottomInstruction.setText(this.activity.getResources().getString(R.string.copyright_text));
        DeafultFont= Typeface.createFromAsset(this.activity.getAssets(), "NotoSansBengali-Regular.ttf");
        BottomInstruction.setTypeface(DeafultFont);

        Btt_Start = (Button)this.activity.findViewById(R.id.start);
        Btt_Start.setVisibility(View.GONE);
        Btt_Start.setTypeface(DeafultFont);
        Btt_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerInstPage.onFired("home");
            }
        });

        Btt_Instruction = (Button)this.activity.findViewById(R.id.instruction);
        Btt_Instruction.setVisibility(View.GONE);
        Btt_Instruction.setTypeface(DeafultFont);
        Btt_Instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerInstPage.onFired("show_insturction_panel");
            }
        });




        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        try {
            InputStream ims = context.getAssets().open("BanglaHadithLogo.png");
            Drawable d = Drawable.createFromStream(ims, null);

            LayoutParams params = (LayoutParams) logo.getLayoutParams();
            params.width = 200;

            logo.setImageDrawable(d);
            logo.setLayoutParams(params);
        }
        catch(IOException ex) {
            return;
        }


        addWebView();

    }

    void addWebView()
    {
        WebView ExplanationDetails = new WebView(_c);
        ExplanationDetails.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        String CheckDevice="<script type='text/javascript'>var agent=navigator.userAgent;var isWebkit=(agent.indexOf('AppleWebKit')>0);var isIPad=(agent.indexOf('iPad')>0);var isIOS=(agent.indexOf('iPhone')>0||agent.indexOf('iPod')>0);var isAndroid=(agent.indexOf('Android')>0);var isNewBlackBerry=(agent.indexOf('AppleWebKit')>0&&agent.indexOf('BlackBerry')>0);var isWebOS=(agent.indexOf('webOS')>0);var isWindowsMobile=(agent.indexOf('IEMobile')>0);var isSmallScreen=(screen.width<767||(isAndroid&&screen.width<1000));var isUnknownMobile=(isWebkit&&isSmallScreen);var isMobile=(isIOS||isAndroid||isNewBlackBerry||isWebOS||isWindowsMobile||isUnknownMobile);var isTablet=(isIPad||(isMobile&&!isSmallScreen));</script>";

        String DecideDevice="<script type='text/javascript'>function deviceDetails(){if(isMobile){AndroidFunction.deviceDetection('mobile')};if(isTablet){AndroidFunction.deviceDetection('tablet')};}deviceDetails();</script>";

        String customHtml = "<html>"+CheckDevice+"<body>"+DecideDevice+"</body></html>";

        ExplanationDetails.getSettings().setAllowFileAccess(true);
        ExplanationDetails.getSettings().setBuiltInZoomControls(true);
        ExplanationDetails.getSettings().setJavaScriptEnabled(true);
        ExplanationDetails.setWebChromeClient(new WebChromeClient());
        JavaScriptConnunication myJavaScriptInterface = new JavaScriptConnunication(activity);
        myJavaScriptInterface.setListener(new Layout_Listener() {
            @Override
            public void onFired(String item) {
                if(item.matches("mobile")){
                    listenerInstPage.onFired("screen_setup_for_mobile");
                }
                if(item.matches("tablet")){
                    listenerInstPage.onFired("screen_setup_for_tablet");
                }
            }
        });


        ExplanationDetails.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");
        ExplanationDetails.loadDataWithBaseURL(null, customHtml, "text/html", "utf-8", null);
        ExplanationDetails.setVisibility(View.INVISIBLE);

        container.addView(ExplanationDetails);
    }


    void ShowInstructionButton()
    {
        final Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, (((height/2)-150) * -1) );
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);

        logo.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                MiddleInstruction.setVisibility(View.VISIBLE);



                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) MiddleInstruction.getLayoutParams();
                params.width = size.x-50;
                params.setMargins(25, 300, 0, 0);
                MiddleInstruction.setLayoutParams(params);


                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) Btt_Start.getLayoutParams();

                params2.setMargins(0, getDPI((height/3)), 0, 0);
                //params2.width = 480;
                Btt_Start.setVisibility(View.VISIBLE);
                Btt_Start.setLayoutParams(params2);
                Btt_Start.setText(activity.getResources().getString(R.string.start_button));



                RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) Btt_Instruction.getLayoutParams();
                params3.setMargins((width-230), (height-100), 0, 0);
                params3.width = 350;
                Btt_Instruction.setVisibility(View.GONE);
                Btt_Instruction.setLayoutParams(params3);
                Btt_Instruction.setText(Html.fromHtml(activity.getResources().getString(R.string.instruction_button)));


                BottomInstruction.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params9 = (RelativeLayout.LayoutParams) BottomInstruction.getLayoutParams();
                params9.width = size.x-50;
                params9.leftMargin=25;
                params9.bottomMargin=25;
                BottomInstruction.setLayoutParams(params9);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }
        });
    }

    int getDPI(int size){
        DisplayMetrics metrics;
        metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
    }

    void StatusText(String m)
    {
        //BottomTextBox.setText(m);
    }

    public class JavaScriptConnunication {
        Context mContext;
        Activity activity;
        Layout_Listener listenerInstPage;

        JavaScriptConnunication(Activity a) {
            activity=a;
            mContext = a.getBaseContext();
        }

        public void showToast(String toast){
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        public void deviceDetection(final String m)
        {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    listenerInstPage.onFired(m);
                }
            });

        }

        void setListener(Layout_Listener listener) {
            this.listenerInstPage = listener;
        }
    }

    public void setListener(Layout_Listener listener) {
        this.listenerInstPage = listener;
    }
}
