package com.rafhaanshah.studyassistant.schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rafhaanshah.studyassistant.R;
import com.rafhaanshah.studyassistant.notifications.Notifier;
import com.rafhaanshah.studyassistant.utils.HelperUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class ScheduleItemActivity extends AppCompatActivity {

    public static final String BUNDLE_TIME = "BUNDLE_TIME";
    public static final String BUNDLE_DATE = "BUNDLE_DATE";
    public static final String BUNDLE_DUE_TIME = "BUNDLE_DUE_TIME";
    public static final String BUNDLE_DUE_DATE = "BUNDLE_DUE_DATE";
    public static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";

    private String title, dueDate, dueTime, notes;
    private ScheduleItem.ScheduleItemType type;
    private long epochTime;
    private int day, month, year, hour, minute, itemID;
    private boolean newItem;
    private Realm realm;
    private ScheduleItem item;
    private SimpleDateFormat timeFormat, dateFormat, dateTimeFormat;
    private TextView timeText, dateText;
    private CheckBox checkBox;

    public static Intent getStartIntent(Context context, int ID) {
        Intent intent = new Intent(context, ScheduleItemActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, ID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        realm = Realm.getDefaultInstance();
        setSpinner();

        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        timeText = findViewById(R.id.tv_time);
        dateText = findViewById(R.id.tv_date);
        checkBox = findViewById(R.id.checkBox);

        itemID = getIntent().getIntExtra(EXTRA_ITEM_ID, 0);
        if (itemID == 0) {
            newItem = true;
            toolbar.setTitle(getString(R.string.new_event));
            findViewById(R.id.btn_delete_event).setVisibility(View.GONE);
            findViewById(R.id.et_title).requestFocus();
        } else {
            newItem = false;
            setFields(itemID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_schedule_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_btn_save_event:
                saveItem();
                return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_to_bottom, R.anim.slide_from_top);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_to_bottom, R.anim.slide_from_top);
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putString(BUNDLE_TIME, timeText.getText().toString());
        out.putString(BUNDLE_DATE, dateText.getText().toString());
        out.putString(BUNDLE_DUE_TIME, dueTime);
        out.putString(BUNDLE_DUE_DATE, dueDate);
    }

    @Override
    protected void onRestoreInstanceState(Bundle in) {
        super.onRestoreInstanceState(in);
        timeText.setText(in.getString(BUNDLE_TIME));
        dateText.setText(in.getString(BUNDLE_DATE));
        dueTime = in.getString(BUNDLE_DUE_TIME);
        dueDate = in.getString(BUNDLE_DUE_DATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void setFields(int ID) {
        item = realm.where(ScheduleItem.class).equalTo(ScheduleItem.ScheduleItem_ID, ID).findFirst();

        ((EditText) findViewById(R.id.et_title)).setText(item.getTitle());
        ((EditText) findViewById(R.id.et_notes)).setText(item.getNotes());

        Spinner spinner = findViewById(R.id.spinner);
        switch (item.getType()) {
            case HOMEWORK:
                spinner.setSelection(0);
                break;
            case COURSEWORK:
                spinner.setSelection(1);
                break;
            case TEST:
                spinner.setSelection(2);
                break;
            case EXAM:
                spinner.setSelection(3);
                break;
        }

        checkBox.setChecked(item.isCompleted());

        dueTime = timeFormat.format(new Date(item.getTime()));
        dueDate = dateFormat.format(new Date(item.getTime()));

        hour = Integer.parseInt(dueTime.substring(0, 2));
        minute = Integer.parseInt(dueTime.substring(3, 5));

        day = Integer.parseInt(dueDate.substring(0, 2));
        month = Integer.parseInt(dueDate.substring(3, 5)) - 1;
        year = Integer.parseInt(dueDate.substring(6, 10));

        timeText = findViewById(R.id.tv_time);
        dateText = findViewById(R.id.tv_date);
        try {
            timeText.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(timeFormat.parse(dueTime)));
            dateText.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateFormat.parse(dueDate)));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            timeText.setText(dueTime);
            dateText.setText(dueDate);
        }
    }

    public void setSpinner() {
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        type = ScheduleItem.ScheduleItemType.HOMEWORK;
                        ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit_black_24dp, 0, 0, 0);
                        break;
                    case 1:
                        type = ScheduleItem.ScheduleItemType.COURSEWORK;
                        ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_computer_black_24dp, 0, 0, 0);
                        break;
                    case 2:
                        type = ScheduleItem.ScheduleItemType.TEST;
                        ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chrome_reader_mode_black_24dp, 0, 0, 0);
                        break;
                    case 3:
                        type = ScheduleItem.ScheduleItemType.EXAM;
                        ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_event_note_black_24dp, 0, 0, 0);
                        break;
                }
                ((TextView) view).setCompoundDrawablePadding(50);
                //HelperUtils.setDrawableColour(((TextView) view).getCompoundDrawables()[0], ContextCompat.getColor(ScheduleItemActivity.this, R.color.textGrey));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void saveItem() {
        title = ((EditText) findViewById(R.id.et_title)).getText().toString().trim();
        notes = ((EditText) findViewById(R.id.et_notes)).getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(dueDate) || TextUtils.isEmpty(dueTime)) {
            Toast.makeText(getApplicationContext(), getString(R.string.fill_event), Toast.LENGTH_SHORT).show();
            return;
        }
        epochTime = parseDateTime(dueDate, dueTime);

        Number num = realm.where(ScheduleItem.class).max(ScheduleItem.ScheduleItem_ID);
        final int maxID;
        if (num != null) {
            maxID = num.intValue();
        } else {
            maxID = 0;
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                ScheduleItem scheduleItem = item;
                if (newItem) {
                    scheduleItem = realm.createObject(ScheduleItem.class);
                    scheduleItem.setID(maxID + 1);
                }
                scheduleItem.setCompleted(checkBox.isChecked());
                scheduleItem.setTitle(title);
                scheduleItem.setNotes(notes);
                scheduleItem.setTime(epochTime);
                scheduleItem.setType(type);
            }
        });
        finish();
        overridePendingTransition(R.anim.slide_to_bottom, R.anim.slide_from_top);
    }

    public void deleteEvent(View view) {
        new AlertDialog.Builder(ScheduleItemActivity.this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.delete_event))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                item.deleteFromRealm();
                            }
                        });
                        finish();
                        overridePendingTransition(R.anim.slide_to_bottom, R.anim.slide_from_top);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.ic_delete_black_24dp)
                .show();
    }

    public void setNotification() {
        Notifier.setNotification(ScheduleItemActivity.this, itemID, title, "Event is in XXX hours", 1);
        finish();
    }

    public void pickDate(View view) {
        HelperUtils.hideSoftKeyboard(ScheduleItemActivity.this, view);

        if (newItem) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleItemActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int pickedYear, int monthOfYear, int dayOfMonth) {
                        dueDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + pickedYear;
                        year = pickedYear;
                        month = monthOfYear;
                        day = dayOfMonth;
                        try {
                            dateText.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateFormat.parse(dueDate)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            dateText.setText(dueDate);
                        }
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public void pickTime(View view) {
        HelperUtils.hideSoftKeyboard(ScheduleItemActivity.this, view);

        if (newItem) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleItemActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                        dueTime = (String.valueOf(hourOfDay) + ":" + String.valueOf(minuteOfHour));
                        hour = hourOfDay;
                        minute = minuteOfHour;
                        try {
                            timeText.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(timeFormat.parse(dueTime)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            timeText.setText(dueTime);
                        }
                    }
                }, hour, minute, false);

        timePickerDialog.show();
    }

    private long parseDateTime(String date, String time) {
        Date epochDate = null;
        try {
            epochDate = dateTimeFormat.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        if (epochDate != null) return epochDate.getTime();
        else return Calendar.getInstance().getTimeInMillis();
    }
}
