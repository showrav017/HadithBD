package com.ehsan.hadithbd;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Muhammad Ehsanul Hoq on 4/11/14.
 */
public class By_Book_view {

    Activity activity;
    Context _c;
    ListView lv1;
    Display display;

    ImageView NavLeft;
    ImageView NavRight;

    ImageView explanationWindow_NavLeft;
    ImageView explanationWindow_NavRight;

    int explanationWindow_Pointer=-8;

    TextView NavCenter;

    DBMS DatabaseControl;
    ClipboardManager clipboard;

    RelativeLayout bottomContents;

    ArrayList<ListDecorator> bookLIST;
    ArrayList<ListDecorator> sectionList;
    ArrayList<ListDecorator> hadithList;

    int NoOfSection;
    int NoOfHadith;
    int startFrom=0;
    int totalLimit=0;

    int startFrom_hdl=0;
    int totalLimit_hdl=0;

    String bookid__;
    String BookName__;
    String sectionid__;
    String SectionName__;
    String SelectedHadith_id;
    int Section_startFrom;
    Typeface DeafultFont;
    Typeface ArabicFont;

    Dialog explanationWindow;
    TabHost leftTabHolder;
    TabHost tabs;

    String NavRightNow;

    TextView CopyButton;
    TextView TagButton;
    TextView ShareButton;

    TextView current_status;

    TextView BanglaDescription;
    TextView EnglishDescription;
    TextView ArabicDescription;



    public By_Book_view(){
    }

    void setupScreen(Activity _activity, Context c)
    {
        display = _activity.getWindowManager().getDefaultDisplay();
        this.activity = _activity;
        this._c = c;
        DatabaseControl=new DBMS(_c);
        DatabaseControl.setup(activity, _c);


        NavCenter = (TextView)this.activity.findViewById(R.id.center_nav);
        DeafultFont= Typeface.createFromAsset(this.activity.getAssets(), "NotoSansBengali-Regular.ttf");
        NavCenter.setTypeface(DeafultFont);

        NavRightNow="";

        clipboard = (ClipboardManager) this.activity.getSystemService(Context.CLIPBOARD_SERVICE);

        SetupHadithDetailsWindow();
    }

    void createLookBoolList()
    {
        bookLIST = GetSearchResults();
        NavRightNow="book";


        lv1 = (ListView)this.activity.findViewById(R.id.lvCustomList);
        //lv1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        lv1.setAdapter(new Adapter1(_c, bookLIST, this.activity));
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                createSectionList(bookLIST.get(position).id, bookLIST.get(position).getTittle());
                DatabaseControl.ArrayFYallHadithId(bookLIST.get(position).id);
            }

        });

        bottomContents= (RelativeLayout)this.activity.findViewById(R.id.bottomContents);
        bottomContents.setVisibility(View.INVISIBLE);

        final Point size = new Point();
        display.getSize(size);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lv1.getLayoutParams();
        params.height = size.y-(0);
        lv1.setLayoutParams(params);


    }

    private ArrayList<ListDecorator> GetSearchResults(){
        ArrayList<ListDecorator> results = new ArrayList<ListDecorator>();

        ArrayList<BookDetails> TheBookList=DatabaseControl.GetBookList();

        for (int i=0; i<TheBookList.size() ; i++) {
            ListDecorator sr1 = new ListDecorator();
            sr1.id=TheBookList.get(i).id;
            sr1.setTittle(TheBookList.get(i).bookName);
            sr1.setSubtittle(activity.getResources().getString(R.string.number_of_chapter)+" "+getdateInBangla(TheBookList.get(i).sectionnumber)+activity.getResources().getString(R.string.t)+", "

                    +activity.getResources().getString(R.string.number_of_hadith)+" "+getdateInBangla(TheBookList.get(i).thehadithnumber)+activity.getResources().getString(R.string.t));
            results.add(sr1);
        }

        return results;
    }

    void createSectionList(String bookid, String BookName)
    {
        NoOfSection=DatabaseControl.getNumberOfSection(bookid);
        totalLimit=NoOfSection;
        bottomContents.setVisibility(View.VISIBLE);

        bookid__=bookid;
        BookName__=BookName;

        updateSectionList(bookid,BookName, startFrom);



        final Point size = new Point();
        display.getSize(size);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lv1.getLayoutParams();

        params.height = size.y-Float.valueOf(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (32+48), _c.getResources().getDisplayMetrics())).intValue();

        lv1.setLayoutParams(params);
        setNavigationArrow();
    }

    void updateSectionList(String bookid, String BookName, int startFrom)
    {
        sectionList = GetSectionList(bookid, BookName, startFrom);
        if(sectionList.size()>0){
            waitingNotification();
            NavCenter.setText(activity.getResources().getString(R.string.book_list));
            NavCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createLookBoolList();
                }
            });

            NavRightNow="section";

            lv1.setAdapter(new Adapter1(_c, sectionList, this.activity));
            lv1.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    createHadithList(sectionList.get(position).id, sectionList.get(position).getTittle());
                    waitingNotification();
                }

            });
            Section_startFrom=startFrom;
        }else{
            this.startFrom=this.startFrom-25;
            sectionList = GetSectionList(bookid, BookName, startFrom);
            //lv1.setOnItemClickListener(null);
        }
    }

    private ArrayList<ListDecorator> GetSectionList(String bookid, String BookName, int startFrom){
        ArrayList<ListDecorator> results = new ArrayList<ListDecorator>();

        ArrayList<SectionDetails> TheBookList=DatabaseControl.GetSectionList(bookid, startFrom);

        for (int i=0; i<TheBookList.size() ; i++) {
            ListDecorator sr1 = new ListDecorator();
            sr1.id=TheBookList.get(i).id;
            sr1.setTittle(TheBookList.get(i).sectionName);

            sr1.setSubtittle(activity.getResources().getString(R.string.number_of_hadith) + " " + getdateInBangla(TheBookList.get(i).thehadithnumber) + activity.getResources().getString(R.string.t));

            sr1.setSubtittle2(BookName__);

            results.add(sr1);
        }

        return results;
    }

    void createHadithList(String SectionID, String SectionName)
    {

        NoOfHadith=DatabaseControl.getNumberOfHadith(SectionID);
        totalLimit_hdl=NoOfHadith;
        sectionid__=SectionID;
        SectionName__=SectionName;

        updateHadithList(sectionid__, SectionName__, startFrom_hdl);

        final Point size = new Point();
        display.getSize(size);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lv1.getLayoutParams();
        params.height = size.y-Float.valueOf(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (32+48), _c.getResources().getDisplayMetrics())).intValue();
        lv1.setLayoutParams(params);
    }

    void updateHadithList(String Sectionid, String SectionName, int startFrom)
    {
        hadithList = GetHadithList(Sectionid, SectionName, startFrom);
        NavRightNow="hadith";
        if(hadithList.size()>0){
            lv1.setAdapter(new Adapter1(_c, hadithList, this.activity));
            lv1.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    SelectedHadith_id=hadithList.get(position).id;
                    showHadithDetails(DatabaseControl.GetHadithDetails(hadithList.get(position).id));
                    waitingNotification();
                }

            });

            NavCenter.setText(activity.getResources().getString(R.string.chapter_list));
            NavCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSectionList(bookid__, BookName__,Section_startFrom);
                }
            });

            Log.i("Total Hadith Number", ">"+hadithList.size()+">"+hadithList.get(0).getTittle());
        }else{
            this.startFrom_hdl=this.startFrom_hdl-25;
            lv1.setOnItemClickListener(null);
        }

        Log.i("Section name", ">"+Sectionid+">"+SectionName);
    }

    private ArrayList<ListDecorator> GetHadithList(String sectionid, String SectionName, int startFrom){
        ArrayList<ListDecorator> results = new ArrayList<ListDecorator>();

        ArrayList<HadithDetails> TheBookList=DatabaseControl.GetHadithList(sectionid, startFrom);

        for (int i=0; i<TheBookList.size() ; i++) {
            ListDecorator sr1 = new ListDecorator();
            sr1.id=TheBookList.get(i).id;

            sr1.setTittle(activity.getResources().getString(R.string.hadith_number)+" "+getdateInBangla(TheBookList.get(i).thehadithno));

            String summaryDescrip="";

            if(TheBookList.get(i).BanglaHadith.length()>300){
                summaryDescrip=TheBookList.get(i).BanglaHadith.substring(0, 50)+"......"+
                        TheBookList.get(i).BanglaHadith.substring(100, 150)+"......"+
                        TheBookList.get(i).BanglaHadith.substring(200, 250);
            }else{
                summaryDescrip=TheBookList.get(i).BanglaHadith;
            }

            sr1.setSubtittle(summaryDescrip);
            sr1.setSubtittle2(BookName__+" "+activity.getResources().getString(R.string.er)+" "+SectionName__+" "+activity.getResources().getString(R.string.chapter_from));

            results.add(sr1);
        }

        return results;
    }

    void SetupHadithDetailsWindow()
    {
        explanationWindow = new Dialog(this.activity);
        explanationWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        explanationWindow.setContentView(R.layout.explanation_layout);
        explanationWindow.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        BanglaDescription = (TextView)explanationWindow.findViewById(R.id.banglaHadithDetails);
        EnglishDescription = (TextView)explanationWindow.findViewById(R.id.englishHadithDetails);
        ArabicDescription = (TextView)explanationWindow.findViewById(R.id.arabicHadithDetails);

        tabs=(TabHost)explanationWindow.findViewById(R.id.tabhost);
        //tabs.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        tabs.setup();

        setupTab(R.id.banglaHadith, this.activity.getResources().getString(R.string.BanglaTab));
        setupTab(R.id.arabicHadith, this.activity.getResources().getString(R.string.ArabicTab));
        setupTab(R.id.englishHadith, "English");

        Point size = new Point();
        display.getSize(size);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tabs.getLayoutParams();
        params.height = size.y-(Float.valueOf(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (100), _c.getResources().getDisplayMetrics())).intValue());
        tabs.setLayoutParams(params);

        explanationWindow.setCanceledOnTouchOutside(true);
        explanationWindow.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                explanationWindow_Pointer=-8;
            }
        });

        explanationWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                explanationWindow_Pointer=-8;
            }
        });

        CopyButton = (TextView)explanationWindow.findViewById(R.id.CopyButton);
        CopyButton.setText(activity.getResources().getString(R.string.do_copy));
        CopyButton.setTypeface(DeafultFont);
        CopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabs.getCurrentTab()==0){
                    ClipData clip = ClipData.newPlainText("SP", BanglaDescription.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
                if(tabs.getCurrentTab()==1){
                    ClipData clip = ClipData.newPlainText("SP", ArabicDescription.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
                if(tabs.getCurrentTab()==2){
                    ClipData clip = ClipData.newPlainText("SP", EnglishDescription.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }

                Toast toast = Toast.makeText(_c, activity.getResources().getString(R.string.copied_on_clipboard), Toast.LENGTH_SHORT);
                toast.show();

            }
        });

        TagButton = (TextView)explanationWindow.findViewById(R.id.TagButton);
        TagButton.setVisibility(View.INVISIBLE);
        TagButton.setText(activity.getResources().getString(R.string.do_tagging));
        TagButton.setTypeface(DeafultFont);
        TagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("theTotal", DatabaseControl.getAllHadithId().size()+">");
            }
        });

        ShareButton = (TextView)explanationWindow.findViewById(R.id.ShareButton);
        ShareButton.setText(activity.getResources().getString(R.string.do_share));
        ShareButton.setTypeface(DeafultFont);
        ShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        BookName__+" "+activity.getResources().getString(R.string.er)+" "+SectionName__+" "+activity.getResources().getString(R.string.chapter_from));

                String shareMessage="";

                if(tabs.getCurrentTab()==0){
                    shareMessage = BanglaDescription.getText().toString();
                }
                if(tabs.getCurrentTab()==1){
                    shareMessage = ArabicDescription.getText().toString();
                }
                if(tabs.getCurrentTab()==2){
                    shareMessage = EnglishDescription.getText().toString();
                }

                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getString(R.string.share_with_others)));

            }
        });

        current_status = (TextView)explanationWindow.findViewById(R.id.current_status);


        explanationWindow_NavLeft = (ImageView)explanationWindow.findViewById(R.id.PreviousHadith);
        explanationWindow_NavLeft.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                if(explanationWindow_Pointer==-8){
                    int cp=DatabaseControl.getAllHadithId().indexOf((SelectedHadith_id)+"");
                    if(cp>(-1)){
                        explanationWindow_Pointer=cp-1;
                    }
                }else{
                    if(explanationWindow_Pointer>0){
                        explanationWindow_Pointer=explanationWindow_Pointer-1;
                    }
                }
                Log.i("SearchResult", "" + DatabaseControl.getAllHadithId().indexOf(SelectedHadith_id));
                showHadithDetails(DatabaseControl.GetHadithDetails(DatabaseControl.getAllHadithId().get(explanationWindow_Pointer)+""));
            }
        });

        explanationWindow_NavRight = (ImageView)explanationWindow.findViewById(R.id.NextHadith);
        explanationWindow_NavRight.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                if(explanationWindow_Pointer==-8){
                    int cp=DatabaseControl.getAllHadithId().indexOf((SelectedHadith_id)+"");
                    if(cp>(-1)){
                        explanationWindow_Pointer=cp+1;
                    }
                }else{
                    explanationWindow_Pointer=explanationWindow_Pointer+1;
                }
                showHadithDetails(DatabaseControl.GetHadithDetails(DatabaseControl.getAllHadithId().get(explanationWindow_Pointer)+""));
            }
        });

    }

    private void setupTab(final int view, final String tag) {
        View tabview = createTabView(tabs.getContext(), tag);

        TabSpec setContent = tabs.newTabSpec(tag).setIndicator(tabview).setContent(view);
        tabs.addTab(setContent);
        tabs.setCurrentTab(0);
    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        //tv.setTextColor(Color.parseColor("#000000"));
        tv.setTextSize(20);
        return view;
    }

    void showHadithDetails(HadithDetails hd)
    {

        DeafultFont= Typeface.createFromAsset(this.activity.getAssets(), "SolaimanLipi_20-04-07.ttf");
        ArabicFont= Typeface.createFromAsset(this.activity.getAssets(), "PDMS_Saleem_QuranFontQEShip.ttf");
        BanglaDescription.setTypeface(DeafultFont);

        BanglaDescription.setText(Html.fromHtml("<p align='justify'>"+hd.BanglaHadith+"</p>"));

        current_status.setText(Html.fromHtml(hd.sectionName+"<br>"+hd.bookName));
        current_status.setTypeface(DeafultFont);

        if(hd.EnglishHadith!=null){
            EnglishDescription.setText(Html.fromHtml(hd.EnglishHadith));
        }


        if(hd.EnglishHadith!=null){

            ArabicDescription.setText(Html.fromHtml(hd.ArabicHadith));
            //ArabicDescription.setTypeface(ArabicFont);
        }

        explanationWindow.show();
    }

    void waitingNotification()
    {
        Toast toast = Toast.makeText(_c, activity.getResources().getString(R.string.toast_wait), Toast.LENGTH_SHORT);
        toast.show();
    }

    String getdateInBangla(String string)
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

    void setNavigationArrow()
    {
        NavLeft = (ImageView)this.activity.findViewById(R.id.LeftNav);
        NavLeft.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                if(NavRightNow.matches("section")){
                    if(startFrom_hdl>0){
                        startFrom_hdl=startFrom_hdl-25;
                    }
                    updateSectionList(bookid__, BookName__,startFrom);
                }else{
                    if(startFrom_hdl>0){
                        startFrom_hdl=startFrom_hdl-25;
                    }
                    updateHadithList(sectionid__, SectionName__, startFrom_hdl);
                }
            }
        });

        NavRight = (ImageView)this.activity.findViewById(R.id.RightNav);
        NavRight.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                if(NavRightNow.matches("section")){
                    if(totalLimit > startFrom && (totalLimit-startFrom)>25){
                        startFrom=startFrom+25;
                    }
                    updateSectionList(bookid__, BookName__, startFrom);
                }else{
                    if(totalLimit_hdl > startFrom_hdl && (totalLimit_hdl-startFrom_hdl)>25){
                        startFrom_hdl=startFrom_hdl+25;
                    }
                    updateHadithList(sectionid__, SectionName__, startFrom_hdl);
                }
            }
        });
    }
}
