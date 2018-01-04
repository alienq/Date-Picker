package com.android.mylibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by JanD4rk on 12/29/2017 4:11 PM .
 */

public class LibraryDialog extends FrameLayout {
    NumberPicker day,month,year;
    List<String> listYears;
    String[] months;
    RelativeLayout confirm;
    String[] years;
    TextView backToday;
    boolean todayNotPressed=true;
    Calendar calendar;
    TextPaint paint;
    ConfirmListener confirmListener;
    private static final int START_YEAR=1990;

    public interface ConfirmListener{
        void onConfirm(int year,int month,int day);
    }

    public LibraryDialog(@NonNull Context context) {
        super(context);
        onCreate(null);
    }

    public LibraryDialog(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate(attrs);}

    public LibraryDialog(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate(attrs);
    }

    public LibraryDialog(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreate(attrs);
    }


    private void onCreate(AttributeSet attributeSet) {
        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        LayoutInflater.from(getContext()).inflate(R.layout.main_layout, this);
        listYears= new ArrayList<>();
        confirm= findViewById(R.id.confirm);
        day= findViewById(R.id.np_days);
        month = findViewById(R.id.np_months);
        year= findViewById(R.id.np_years);
        backToday= findViewById(R.id.back_today);
        Log.e(TAG, "onCreate: "+attributeSet.getAttributeCount());
        TypedArray ta = getContext().obtainStyledAttributes(attributeSet, R.styleable.LibraryDialog);
        if (ta != null) {

            float f=   ta.getDimension(R.styleable.LibraryDialog_fontSize,10f);
              if (f!=10f){
//                  day.setSelectedTextSize(f);
              }

        }


        calendar= Calendar.getInstance();
        int a= calendar.get(Calendar.YEAR)-1990+2;
        months= new String[]{"Jan","Feb","Mart","Apr","May","Jun","July","Aug","Sent","Oct","Nov","Dec"};
        years= new String[a];
        for (int b=0;b<a;b++){
            years[b] = 1990+b+"";
        }
        year.setMaxValue(a);
        year.setValue(a-1);
        year.setWrapSelectorWheel(false);
        year.setDisplayedValues(years);
        month.setMaxValue(12);
        month.setValue(calendar.get(Calendar.MONTH)+1);
        month.setDisplayedValues(months);
        day.setMaxValue(getMonthDays(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1));
        day.setValue(calendar.get(Calendar.DAY_OF_MONTH));
        month.setOnValueChangedListener(monthListener);
        year.setOnValueChangedListener(yearListener);
        backToday.setOnClickListener(backButtonListener);
        confirm.setOnClickListener(confirmButtonClickListener);
    }
    /**Back button listeners */
    private OnClickListener backButtonListener= new OnClickListener() {
        @Override
        public void onClick(View v) {
            int selectedYear= Integer.valueOf(years[year.getValue()-1]);
            int thisYear = calendar.get(Calendar.YEAR);
            int thisMonth= calendar.get(Calendar.MONTH);
            int selectedMonth= month.getValue()-1;
            int selectedDay=  day.getValue();
            int thisDay= calendar.get(Calendar.DAY_OF_MONTH);

            int monthDayCount=getMonthDays(selectedYear,month.getValue());

            day.setMaxValue(monthDayCount);

            int difm=thisMonth-selectedMonth;
            int dify=thisYear-selectedYear;
            int difd=thisDay-selectedDay;

            todayNotPressed= false;
            int cDifM,cDifY,cDifD;
            cDifM=   Math.abs(difm);
            cDifY= Math.abs(dify);
            cDifD=Math.abs(difd);
            int lastDif;
            if (cDifD>cDifY&cDifD>cDifM)
                lastDif=cDifD;
            else if (cDifY>cDifM&cDifY>cDifD)
                lastDif=cDifY;
            else
                lastDif = cDifM;

            Handler handler= new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    todayNotPressed=true;
                }
            },lastDif*35);


            IncreaseValue increasePicker2 = new IncreaseValue(month, difm);
            increasePicker2.run(0);
            IncreaseValue increasePicker1 = new IncreaseValue(year, dify);
            increasePicker1.run(0);
            IncreaseValue increasePicker = new IncreaseValue(day, difd);
            increasePicker.run(0);
        }
    };
 /**Number Picker listeners */
    private NumberPicker.OnValueChangeListener monthListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            if (todayNotPressed) {
                int selectedYear = Integer.valueOf(years[year.getValue() - 1]);
                int selectedDay = getMonthDays(selectedYear, newVal);
                int oldDay = day.getValue();
                day.setMaxValue(selectedDay);
                day.setValue(getSelectedDayValue(selectedDay, oldDay));
            }
        }
    };

    private NumberPicker.OnValueChangeListener yearListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            if (todayNotPressed) {
                int selectedYear= Integer.valueOf(years[year.getValue()-1]);
                int selectedDay=getMonthDays(selectedYear,month.getValue());
                int oldDay= day.getValue();

                day.setMaxValue(selectedDay);
                day.setValue(getSelectedDayValue(selectedDay,oldDay));
            }
        }
    };

    private int getMonthDays(int year,int month){
        int a=0;

        switch (month) {
            case 1:
                a = 31;
                break;
            case 2:
                a = (year % 4 == 0 ? 29 : 28);
                break;
            case 3:
                a = 31;
                break;
            case 4:
                a = 30;
                break;
            case 5:
                a = 31;
                break;
            case 6:
                a = 30;
                break;
            case 7:
                a = 31;
                break;
            case 8:
                a = 31;
                break;
            case 9:
                a = 30;
                break;
            case 10:
                a = 31;
                break;
            case 11:
                a = 30;
                break;
            case 12:
                a = 31;
                break;
        }


        return a;
    }
    private int getSelectedDayValue(int monthDays,int oldDay){
        return (monthDays>=oldDay ? oldDay:monthDays);
    }
    private View.OnClickListener confirmButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (confirmListener!=null){
                confirmListener.onConfirm(year.getValue(),month.getValue(),day.getValue());
            }
        }
    };
    private class IncreaseValue {
        private int counter = 0;
        private final NumberPicker picker;
        private final int incrementValue;
        private final boolean increment;

        private final Handler handler = new Handler();
        private final Runnable fire = new Runnable() { @Override public void run() { fire(); } };

        /*public*/ IncreaseValue(final NumberPicker picker, final int incrementValue) {
            this.picker = picker;
            if (incrementValue > 0) {
                increment = true;
                this.incrementValue = incrementValue;
            } else {
                increment = false;
                this.incrementValue = -incrementValue;
            }
        }

        /*public*/ void run(final int startDelay) {
            handler.postDelayed(fire, startDelay);  // This will execute the runnable passed to it (fire)
            // after [startDelay in milliseconds], ASYNCHRONOUSLY.
        }

        private void fire() {
            ++counter;
            if (counter > incrementValue) return;

            try {
                // refelction call for
                // picker.changeValueByOne(true);
                final Method method = picker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
                method.setAccessible(true);
                method.invoke(picker, increment);

            } catch (final NoSuchMethodException | InvocationTargetException |
                    IllegalAccessException | IllegalArgumentException e) {

            }

            handler.postDelayed(fire, 35);  // This will execute the runnable passed to it (fire)
            // after 120 milliseconds, ASYNCHRONOUSLY. Customize this value if necessary.
        }
    }

    public void  setOnConfirmListener(ConfirmListener confirmListener){
        this.confirmListener=confirmListener;
    }

    private float pxToSp(float px) {
        return px / getResources().getDisplayMetrics().scaledDensity;
    }
}
