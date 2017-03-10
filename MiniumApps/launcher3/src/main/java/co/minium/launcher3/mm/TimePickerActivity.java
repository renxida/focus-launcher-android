package co.minium.launcher3.mm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import co.minium.launcher3.R;


public class TimePickerActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_picker_custom);

        final AbstractWheel hours = (AbstractWheel) findViewById(R.id.hour_horizontal);
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(this, 1, 12, "%01d");
        hourAdapter.setItemResource(R.layout.wheel_text_centered);
        hourAdapter.setItemTextResource(R.id.text);
        hours.setViewAdapter(hourAdapter);

        hours.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
                Log.e("TKB","onScrollingStarted");
            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                Log.e("TKB","onScrollingFinished");

            }
        });

        final AbstractWheel mins = (AbstractWheel) findViewById(R.id.mins);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(this, 0, 59, "%02d");
        minAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        minAdapter.setItemTextResource(R.id.text);
        mins.setViewAdapter(minAdapter);
        mins.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {

            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {

            }
        });

        final AbstractWheel ampm = (AbstractWheel) findViewById(R.id.ampm);
        ArrayWheelAdapter<String> ampmAdapter =
            new ArrayWheelAdapter<>(this, new String[] {"AM", "PM"});
        ampmAdapter.setItemResource(R.layout.wheel_text_centered_am_pm);
        ampmAdapter.setItemTextResource(R.id.text);
        ampm.setViewAdapter(ampmAdapter);
        ampm.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {

            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {

            }
        });
        // set current time
        Calendar calendar = Calendar.getInstance(Locale.US);
        hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(calendar.get(Calendar.MINUTE));
        ampm.setCurrentItem(calendar.get(Calendar.AM_PM));
        
        /*final AbstractWheel day = (AbstractWheel) findViewById(R.id.day);
        DayArrayAdapter dayAdapter = new DayArrayAdapter(this, calendar);
        day.setViewAdapter(dayAdapter);
        day.setCurrentItem(dayAdapter.getToday());*/
    }
    
    /**
     * Day adapter
     *
     */
   /* private class DayArrayAdapter extends AbstractWheelTextAdapter {
        // Count of days to be shown
        private final int daysCount = 4;
        
        // Calendar
        Calendar calendar;
        
        *//**
         * Constructor
         *//*
        protected DayArrayAdapter(Context context, Calendar calendar) {
            super(context, R.layout.time_picker_custom_day, NO_RESOURCE);
            this.calendar = calendar;
            
            setItemTextResource(R.id.time2_monthday);
        }
        public int getToday() {
            return daysCount / 2;
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent, int currentItemIdx) {
            int day = -daysCount/2 + index;
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.roll(Calendar.DAY_OF_YEAR, day);
            
            View view = super.getItem(index, cachedView, parent, currentItemIdx);

            TextView weekday = (TextView) view.findViewById(R.id.time2_weekday);
            if (day == 0) {
                weekday.setText("");
            } else {
                DateFormat format = new SimpleDateFormat("EEE");
                weekday.setText(format.format(newCalendar.getTime()));
            }

            TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
            if (day == 0) {
                monthday.setText("Today");
                monthday.setTextColor(0xFF0000F0);
            } else {
                DateFormat format = new SimpleDateFormat("MMM d");
                monthday.setText(format.format(newCalendar.getTime()));
                monthday.setTextColor(0xFF111111);
            }
            DateFormat dFormat = new SimpleDateFormat("MMM d");
            view.setTag(dFormat.format(newCalendar.getTime()));
            return view;
        }
        
        @Override
        public int getItemsCount() {
            return daysCount + 1;
        }
        
        @Override
        protected CharSequence getItemText(int index) {
            return "";
        }
    }*/
}
