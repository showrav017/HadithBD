package com.ehsan.hadithbd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by showrav017 on 5/13/14.
 */
public class By_Number_View {

    Activity activity;
    Context _c;
    ListView lv1;
    Display display;

    DBMS DatabaseControl;

    Typeface DeafultFont;
    TextView topInstruction;
    TextView topTitle;

    Button doSearch;
    Spinner specific_book;

    public By_Number_View(){
    }

    void setupScreen(Activity _activity, Context c)
    {

        display = _activity.getWindowManager().getDefaultDisplay();
        this.activity = _activity;
        this._c = c;
        DatabaseControl=new DBMS(_c);
        DatabaseControl.setup(activity, _c);

        ImageView TopLogo=(ImageView)this.activity.findViewById(R.id.TopLogo);
        display = this.activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        DeafultFont= Typeface.createFromAsset(this._c.getAssets(), "SolaimanLipi_20-04-07.ttf");

        try {
            InputStream ims = this._c.getAssets().open("BanglaHadithLogoSmall.png");
            Drawable d = Drawable.createFromStream(ims, null);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) TopLogo.getLayoutParams();
            params.width = 82;
            params.height=48;
            TopLogo.setLayoutParams(params);
            TopLogo.setImageDrawable(d);
        }
        catch(IOException ex) {
            return;
        }


        specific_book = (Spinner)this.activity.findViewById(R.id.specific_book);


        topInstruction = (TextView)this.activity.findViewById(R.id.topInstruction);
        topInstruction.setTypeface(DeafultFont);

        topTitle = (TextView)this.activity.findViewById(R.id.topTitle);
        topTitle.setTypeface(DeafultFont);

        doSearch = (Button)this.activity.findViewById(R.id.doSearch);
        doSearch.setTypeface(DeafultFont);
        doSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}


class BookListSpinnerModel {

    private  int bookId;
    private  String bookName="";

    public void setbookId(int bkid)
    {
        this.bookId = bkid;
    }

    public void setbookName(String bkname)
    {
        this.bookName = bkname;
    }

    public int getbookId()
    {
        return this.bookId;
    }

    public String getbookName()
    {
        return this.bookName;
    }
}

class BookListSpinner extends ArrayAdapter<BookListSpinnerModel>
{
    private Activity context;
    ArrayList<BookListSpinnerModel> data = null;

    public BookListSpinner(Activity context, int resource, ArrayList<BookListSpinnerModel> data)
    {
        super(context, resource, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {   // Ordinary view in Spinner, we use android.R.layout.simple_spinner_item
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {   // This view starts when we click the spinner.
        View row = convertView;
       /* if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_layout, parent, false);
        }

        CountryInfo item = data.get(position);

        if(item != null)
        {   // Parse the data from each object and set it.
            ImageView myFlag = (ImageView) row.findViewById(R.id.imageIcon);
            TextView myCountry = (TextView) row.findViewById(R.id.countryName);
            if(myFlag != null)
            {
                myFlag.setBackgroundDrawable(getResources().getDrawable(item.getCountryFlag()));
            }
            if(myCountry != null)
                myCountry.setText(item.getCountryName());

        }*/

        return row;
    }
}
