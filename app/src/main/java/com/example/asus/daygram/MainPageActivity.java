package com.example.asus.daygram;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainPageActivity extends AppCompatActivity {
    private  String TAG = "Mainactivity";
    private HashMap<String,ArrayList<Daycontent>> AllDate;
    private ListView listView;
    private ListAdapter adapter;//存储Daycontent数据的数组的适配器
    private ArrayList<Daycontent> DaycontentsList = new ArrayList<>();//存储Daycontent数据的数组
    private ArrayList<Daycontent> FilterList = new ArrayList<>();//存储过滤后的数据
    private int nowYear;//当前年份
    private int nowMonth;//当前月份
    private int nowDay;//当前日期
    private int sellectYear;//选定年份
    private int sellectMonth;//选定月份
    private Listoperate operate=new Listoperate();
    private boolean status=false;//是否显示另外的listview
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
         //时间初始化
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy");
        nowYear = Integer.parseInt(sDateFormat.format(new java.util.Date()));
        sDateFormat = new SimpleDateFormat("MM");
        nowMonth = Integer.parseInt(sDateFormat.format(new java.util.Date()));
        sDateFormat = new SimpleDateFormat("dd");
        nowDay = Integer.parseInt(sDateFormat.format(new java.util.Date()));
        sellectYear = nowYear;
        sellectMonth = nowMonth;

        //数据初始化,即在缓存加载
        dataintialise();

        //数据加载listview
        adapter = new ListAdapter(MainPageActivity.this, this.DaycontentsList);
        listView =(ListView)this.findViewById(R.id.MyListView);
        listView.setAdapter(adapter);
        //listview的点击事件
        listView.setOnItemClickListener(new mItemClick());
       // listView.setOnItemLongClickListener(new lItemClick());
        //添加当天日记
        final RelativeLayout addButton = (RelativeLayout) findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellectYear = nowYear;
                sellectMonth = nowMonth;
                Intent intent = new Intent(MainPageActivity.this, EditPageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("year", nowYear);
                bundle.putInt("month", nowMonth);
                bundle.putInt("day", nowDay);
                if (DaycontentsList.get(nowDay-1).getContent()==null)
                    bundle.putString("diaryContent", null);
                else
                    bundle.putString("diaryContent",DaycontentsList.get(nowDay-1).getContent());
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        //选择月份
        final TextView monthbar = (TextView)findViewById(R.id.month);
        monthbar.setText(getMonthText(sellectMonth));
        monthbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog MonthDialog=new AlertDialog.Builder(MainPageActivity.this,R.style.MonthDialog).create();//创建自定义对话框，默认会遮住整个页面
                MonthDialog.show();
                WindowManager.LayoutParams lp = MonthDialog.getWindow().getAttributes();//获取窗口属性
                //获取屏幕分辨率属性
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                //保持窗口与屏幕的宽度分辨率相同
                lp.width = dm.widthPixels;
                MonthDialog.getWindow().setAttributes(lp);
                //窗口内容绑定
                MonthDialog.getWindow().setContentView(R.layout.month_tabbar);//整个窗口内容看作一个新页面
                MonthDialog.setCancelable(true);//返回键返回
                //点击窗口之外可取消
                MonthDialog.getWindow().findViewById(R.id.month_tabbar)
                        .setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                MonthDialog.dismiss();
                            }
                        });
                //选择项变黑
                int [] buttonId={R.id.Jan_bar,R.id.Feb_bar,R.id.Mar_bar,R.id.Apr_tar,R.id.May_tar,R.id.Jun_bar,R.id.Jul_bar,
                R.id.Aug_bar,R.id.Sep_bar,R.id.Oct_bar,R.id.Nov_bar,R.id.Dec_bar};
                MonthDialog.getWindow().findViewById(buttonId[sellectMonth-1]).setBackgroundResource(R.drawable.blackcircle);
                //各个按钮绑定
                for (int i=0;i<12;i++)
                {
                    MonthDialog.getWindow().findViewById(buttonId[i]).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //sellectMonth=i+1;//内部类不能访问方法中变量
                            for(int j=0;j<12;j++){
                                if(((TextView)view).getText().toString().equals(getMonthText(j)))
                                {
                                    sellectMonth=j++;
                                    break;
                                }
                            }
                            dataintialise();
                            monthbar.setText(getMonthText(sellectMonth));
                            MonthDialog.dismiss();
                        }
                    });
                }
            }
        });

        //选择年份
        final TextView yearbar = (TextView)findViewById(R.id.year);
        yearbar.setText(Integer.toString(sellectYear));
        yearbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog YearDialog=new AlertDialog.Builder(MainPageActivity.this,R.style.MonthDialog).create();//创建自定义对话框，默认会遮住整个页面
                YearDialog.show();
                WindowManager.LayoutParams lp = YearDialog.getWindow().getAttributes();//获取窗口属性
                //获取屏幕分辨率属性
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                //保持窗口与屏幕的宽度分辨率相同
                lp.width = dm.widthPixels;
                YearDialog.getWindow().setAttributes(lp);
                //窗口内容绑定
                YearDialog.getWindow().setContentView(R.layout.year_tabbar);//整个窗口内容看作一个新页面
                YearDialog.setCancelable(true);//返回键返回
                //点击窗口之外可取消
                YearDialog.getWindow().findViewById(R.id.year_tabbar)
                        .setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                YearDialog.dismiss();
                            }
                        });
                //选择项变黑
                int [] buttonId={R.id.first_year,R.id.second_year,R.id.third_year,R.id.fourth_year,R.id.fifth_year,R.id.sixth_year,R.id.seventh_year,
                        R.id.eighth_year};
                ((TextView)YearDialog.getWindow().findViewById(buttonId[sellectYear-2011])).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));
                //各个按钮绑定
                for (int i=0;i<8;i++)
                {
                    YearDialog.getWindow().findViewById(buttonId[i]).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for(int j=0;j<8;j++){
                                if(((TextView)view).getText().toString().equals(Integer.toString(2011+j)))
                                {
                                    sellectYear=2011+j;
                                    break;
                                }
                            }
                            dataintialise();
                            yearbar.setText(Integer.toString(sellectYear));
                            YearDialog.dismiss();
                        }
                    });
                }
            }
        });

        //切换显示样式
        RelativeLayout changeButton = (RelativeLayout) findViewById(R.id.change_button);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterList.clear();
                for (int i=0;i<DaycontentsList.size();i++)
                {
                    if(DaycontentsList.get(i).getContent()!=null)
                        FilterList.add(DaycontentsList.get(i));
                }
                if(adapter.getstatus())
                {
//                   adapter.changestatus();
//                   adapter.refreshdata(DaycontentsList);
//                   adapter.notifyDataSetChanged();
//                   不知道为什么刷新不了，只能重置adapter，重新set回进去
                    adapter = new ListAdapter(MainPageActivity.this, DaycontentsList);
                    listView.setAdapter(adapter);
                }
                else {
                    adapter.changestatus();
                    adapter.refreshdata(FilterList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
    //处理返回值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bundle;
                    bundle = data.getExtras();
                    int getYear = bundle.getInt("year");
                    int getMonth = bundle.getInt("month");
                    int getDay = bundle.getInt("day");
                    String getcontent = bundle.getString("diaryContent");
                    Daycontent tmpcontent=new Daycontent();
                    tmpcontent.setDay(getDay);tmpcontent.setMonth(getMonth);tmpcontent.setYear(getYear);
                    tmpcontent.setContent(getcontent);
                    //DaycontentsList跟着Adapter一起变的，指向同一块堆中
                    if(getcontent!=null)
                        adapter.changedata(tmpcontent,getDay-1);
                    adapter.notifyDataSetChanged();//实现快速刷新
                    //Log.d(TAG,"getMonth:"+getMonth);
                    AllDate.put(Integer.toString(getYear)+Integer.toString(getMonth),DaycontentsList);
                    operate.save(getBaseContext(),AllDate);
                    break;
                }
        }
    }
    private void dataintialise()
    {
        //Log.d(TAG,"month"+sellectMonth+",year"+sellectYear);
        this.DaycontentsList.clear();
        this.AllDate=operate.load(getBaseContext());
        if(AllDate==null)
            AllDate=new HashMap<String, ArrayList<Daycontent>>();
        //如果对应的月份无数据，正常初始化
        if(AllDate.get(Integer.toString(sellectYear)+Integer.toString(sellectMonth))!=null)
        {
            DaycontentsList=AllDate.get(Integer.toString(sellectYear)+Integer.toString(sellectMonth));
        }
        //如果是初始加载，直接加上距上次没有加那些
        if(sellectMonth==nowMonth&&sellectYear==nowYear) {
//            DaycontentsList.clear();//数据清理
//            AllDate.put(Integer.toString(nowYear)+Integer.toString(nowMonth),DaycontentsList);
//            operate.save(getBaseContext(),AllDate);
            for (int i = DaycontentsList.size() + 1; i <= this.nowDay; i++) {
                Daycontent content = new Daycontent();
                content.setYear(nowYear);
                content.setMonth(nowMonth);
                content.setDay(i);
                this.DaycontentsList.add(content);
            }
            if(adapter!=null)//第二次刷新，adapter要重置
            {
//                adapter.refreshdata(DaycontentsList);
//                adapter.notifyDataSetChanged();
                adapter = new ListAdapter(MainPageActivity.this, DaycontentsList);
                listView.setAdapter(adapter);
            }
        }//不是当前月份
        else {
           // Log.d(TAG,"size:"+DaycontentsList.size()+",count:"+getDayCount(time));
            for(int i=DaycontentsList.size() +1;i<=getDayCount(sellectMonth,sellectYear);i++)
            {
                Daycontent content = new Daycontent();
                content.setYear(sellectYear);
                content.setMonth(sellectMonth);
                content.setDay(i);
                this.DaycontentsList.add(content);
            }
            //adapter刷新
//            adapter.refreshdata(DaycontentsList);
//            adapter.notifyDataSetChanged();
            adapter = new ListAdapter(MainPageActivity.this, DaycontentsList);
            listView.setAdapter(adapter);
        }
    }
    //listview的点击事件
    class mItemClick implements AdapterView.OnItemClickListener
    {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Daycontent selectcontent = DaycontentsList.get(i);//获取选中的数据
            Intent intent = new Intent(MainPageActivity.this, EditPageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("year", selectcontent.getYear());
            bundle.putInt("month", selectcontent.getMonth());
            bundle.putInt("day", selectcontent.getDay());
            bundle.putString("diaryContent", selectcontent.getContent());
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
            //Log.d(TAG,"click");
        }
    }

   //获取当前月份的text
   private String getMonthText(int month) {
       switch (month) {
           case 1:
               return "JAN";
           case 2:
               return "FEB";
           case 3:
               return "MAR";
           case 4:
               return "APR";
           case 5:
               return "MAY";
           case 6:
               return "JUN";
           case 7:
               return "JUL";
           case 8:
               return "AUG";
           case 9:
               return "SEP";
           case 10:
               return "OCT";
           case 11:
               return "NOV";
           case 12:
               return "DEC";
           default:
               return null;
       }
   }
   //获取当前月份一共有几天
    private int getDayCount(int month,int year){
        //Calendar calendar = new GregorianCalendar();
        Calendar calendar = Calendar.getInstance();

        // 格式化日期--设置date
        calendar.set(Calendar.YEAR,sellectYear);
        calendar.set(Calendar.MONTH,sellectMonth-1);
        int num2 = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //Log.d(TAG,"days:"+num2);
        return num2;
    }
}
