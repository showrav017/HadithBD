package com.ehsan.hadithbd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Muhammad Ehsanul Hoq on 4/11/14.
 */
public class Instruction_Controller {

    Activity activity;
    Context _c;
    Layout_Listener listenerInstPage;
    Display display;
    Typeface DeafultFont;
    Utility _util;

    public Instruction_Controller() {}

    void setupScreen(Activity _activity, Context context)
    {
        this.activity = _activity;
        this._c = context;
        _util=new Utility(this.activity);
    }

    void createView()
    {

        ImageView TopLogo=(ImageView)this.activity.findViewById(R.id.TopLogo);
        display = this.activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        try {
            InputStream ims = this._c.getAssets().open("BanglaHadithLogoSmall.png");
            Drawable d = Drawable.createFromStream(ims, null);

            LayoutParams params = (LayoutParams) TopLogo.getLayoutParams();
            params.width = 82;
            params.height=48;
            TopLogo.setLayoutParams(params);
            TopLogo.setImageDrawable(d);
        }
        catch(IOException ex) {
            return;
        }


        ScrollView ch =(ScrollView)this.activity.findViewById(R.id.contentHolder);
        LayoutParams ch_params = (LayoutParams) ch.getLayoutParams();
        ch_params.height=size.y-48-100;

        ch.setLayoutParams(ch_params);


        TextView Contents =  (TextView) this.activity.findViewById(R.id.contents);

        try {
            Contents.setText(Html.fromHtml(_util.readTxtFromFile("instruction/howto.html"), _util.imgGetter, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Typeface font= Typeface.createFromAsset(this._c.getAssets(), "NotoSansBengali-Regular.ttf");
        Contents.setTypeface(font);


        Button Btt_Back_Inst = (Button) this.activity.findViewById(R.id.back);
        Btt_Back_Inst.setTypeface(font);
        Btt_Back_Inst.setText(this._c.getResources().getString(R.string.btt_back));
        Btt_Back_Inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                listenerInstPage.onFired("back");
            }
        });

    }

    public void setListener(Layout_Listener listener) {
        this.listenerInstPage = listener;
    }

}
