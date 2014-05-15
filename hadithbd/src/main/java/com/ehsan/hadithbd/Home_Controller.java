package com.ehsan.hadithbd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Muhammad Ehsanul Hoq on 4/11/14.
 */
public class Home_Controller {

    Activity activity;
    Context _c;
    Layout_Listener listenerInstPage;
    Display display;
    ListView lv1;

    RelativeLayout bottomContents;


    By_Book_view bookView;
    By_Number_View numberView;
    String PositionRightnow="home";

    public Home_Controller() {}

    void setupScreen(Activity _activity, Context context)
    {
        this.activity = _activity;
        this._c = context;

        PositionRightnow="home";

        bottomContents= (RelativeLayout)this.activity.findViewById(R.id.bottomContents);
        bottomContents.setVisibility(View.INVISIBLE);


        bookView=new By_Book_view();
        bookView.setupScreen(_activity, context);

        numberView=new By_Number_View();


        ArrayList<ListDecorator> searchResults = GetSearchResults();

        ImageView TopLogo=(ImageView)this.activity.findViewById(R.id.TopLogo);
        display = this.activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

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

        lv1 = (ListView)this.activity.findViewById(R.id.lvCustomList);
        lv1.setAdapter(new Adapter1(_c, searchResults, this.activity));
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                if(position==0){
                    removeListener();

                    bookView.createLookBoolList();
                    PositionRightnow="bookview";
                }

               if(position==1){
                   removeListener();
                   listenerInstPage.onFired("by_number");
                   numberView.setupScreen(activity, _c);
                }
                /* if(position==0){
                    listenerInstPage.onFired("by_type");
                }
                if(position==0){
                    listenerInstPage.onFired("by_rabi");
                }*/

            }

        });
    }

    private ArrayList<ListDecorator> GetSearchResults(){
        ArrayList<ListDecorator> results = new ArrayList<ListDecorator>();

        ListDecorator sr1 = new ListDecorator();
        sr1.setTittle(activity.getResources().getString(R.string.way_of_search__by_book));
        sr1.setSubtittle(activity.getResources().getString(R.string.way_of_search__by_book_inst));
        results.add(sr1);

        ListDecorator sr2 = new ListDecorator();
        sr2.setTittle(activity.getResources().getString(R.string.way_of_search__by_number));
        sr2.setSubtittle(activity.getResources().getString(R.string.way_of_search__by_number_inst));
        results.add(sr2);

       // ListDecorator sr3 = new ListDecorator();
       // sr3.setTittle(activity.getResources().getString(R.string.way_of_search__by_type));
     //   sr3.setSubtittle(activity.getResources().getString(R.string.way_of_search__by_type_inst));
      //  results.add(sr3);

        /*ListDecorator sr4 = new ListDecorator();
        sr4.setTittle(activity.getResources().getString(R.string.way_of_search__by_rabi));
        sr4.setSubtittle(activity.getResources().getString(R.string.way_of_search__by_rabi_inst));
        results.add(sr4);
*/
        return results;
    }

    public void removeListener() {
        lv1.setOnItemClickListener(null);
    }

    public String getdateInBangla(String string)
    {
        Character bangla_number[]=new Character[]{'০','১','২','৩','৪','৫','৬','৭','৮','৯'};
        Character eng_number[]={'0','1','2','3','4','5','6','7','8','9'};

        String values = "";
        char[] character = string.toCharArray();
        for (int i=0; i<character.length ; i++) {
            Character c='0';
            for (int j = 0; j < eng_number.length; j++) {
                if(character[i]==eng_number[j])
                {
                    c=bangla_number[j];
                    break;
                }else {
                    c=character[i];
                }
            }
            values=values+c;
        }
        return values;
    }


    public void setListener(Layout_Listener listener) {
        this.listenerInstPage = listener;
    }
}


class Adapter1 extends BaseAdapter {

    LayoutInflater mInflater;
    private static ArrayList<ListDecorator> searchArrayList;
    Activity activity;
    Typeface DeafultFont;

    public Adapter1(Context context, ArrayList<ListDecorator> results, Activity _activity) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
        this.activity=_activity;
    }

    @Override
    public int getCount() {
        return searchArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_pattern, null);
            holder = new ViewHolder();
            holder.tittle = (TextView) convertView.findViewById(R.id.list_content_tittle);
            holder.subtittle = (TextView) convertView.findViewById(R.id.list_content_subtittle);
            holder.subtittle2 = (TextView) convertView.findViewById(R.id.list_content_subtittle2);
            holder.subtittle2Holder = (LinearLayout) convertView.findViewById(R.id.subtittle2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tittle.setText(Html.fromHtml(searchArrayList.get(position).getTittle()));
        holder.subtittle.setText(Html.fromHtml(searchArrayList.get(position).getSubtittle()));

        if(!searchArrayList.get(position).getSubtittle2().matches("blank")){
            holder.subtittle2Holder.setVisibility(View.VISIBLE);
            holder.subtittle2.setText(Html.fromHtml(searchArrayList.get(position).getSubtittle2()));
        }else{
            holder.subtittle2Holder.setVisibility(View.GONE);
        }

        DeafultFont= Typeface.createFromAsset(this.activity.getAssets(), "SolaimanLipi_20-04-07.ttf");
        holder.tittle.setTypeface(DeafultFont);
        holder.subtittle.setTypeface(DeafultFont);
        holder.subtittle2.setTypeface(DeafultFont);

        return convertView;
    }

    static class ViewHolder {
        TextView tittle;
        TextView subtittle;
        TextView subtittle2;

        LinearLayout subtittle2Holder;
    }

}

class ListDecorator {
    String id;
    private String tittle = "";
    private String subtittle = "";
    private String subtittle2 = "blank";

    public void setTittle(String name) {
        this.tittle = name;
    }

    public String getTittle() {
        return tittle;
    }

    public void setSubtittle(String _subtittle) {
        this.subtittle = _subtittle;
    }

    public String getSubtittle() {
        return subtittle;
    }

    public void setSubtittle2(String _subtittle2) {
        this.subtittle2 = _subtittle2;
    }

    public String getSubtittle2() {
        return subtittle2;
    }
}