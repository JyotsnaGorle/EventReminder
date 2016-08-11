package com.example.jyotsna.switchspike;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

  private TextView switchStatus;
  private Switch mySwitch;
  private long enqueue;
  private DownloadManager dm;
  private String ICSFile;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
          Toast toast = Toast.makeText(getApplicationContext(), "downloaded", Toast.LENGTH_LONG);
          toast.show();
          try {
            getICSFile(null);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    };

    registerReceiver(receiver, new IntentFilter(
        DownloadManager.ACTION_DOWNLOAD_COMPLETE));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  public void onAddEventClicked() {
    Intent intent = new Intent(Intent.ACTION_INSERT);
    intent.setType("vnd.android.cursor.item/event");

    Calendar cal = Calendar.getInstance();
    long startTime = cal.getTimeInMillis();
    long endTime = cal.getTimeInMillis() + 60 * 60 * 1000;

    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
    intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

    intent.putExtra(CalendarContract.Events.TITLE, "Neel Birthday");
    intent.putExtra(CalendarContract.Events.DESCRIPTION, "This is a sample description");
    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Guest House");
    intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");

    startActivity(intent);
  }

  public void onClick(View view) {
    dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    DownloadManager.Request request = new DownloadManager.Request(
        Uri.parse("http://traintimes.org.uk/ical.ics?BUGBTNbMuyIyTTrain_BTNHAVbMuzLACTrain_HAVPMHbMuBlBJTrain"));
    enqueue = dm.enqueue(request);
  }

  public void showDownload(View view) {
    Intent i = new Intent();
    i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
    startActivity(i);
  }

  public void openICS(File file) throws IOException {
    String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ics");

    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setDataAndType(Uri.fromFile(file), mime);
    startActivity(intent);
  }

  public void getICSFile(View view) throws IOException {
    File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    File listAllFiles[] = file.listFiles();
    if (listAllFiles != null && listAllFiles.length > 0) {
      for (File currentFile : listAllFiles) {
        if (currentFile.getName().endsWith(".ics")) {
          openICS(currentFile);
        }
      }
    }
  }

  public void addNewEvent(View view){
    final ContentValues event = new ContentValues();
    Calendar cal = Calendar.getInstance();
    long startTime = cal.getTimeInMillis() ;
    long endTime = cal.getTimeInMillis() ;
    event.put(CalendarContract.Events.CALENDAR_ID, 1);

    event.put(CalendarContract.Events.TITLE, "MIRACULAOUS EVENT");
    event.put(CalendarContract.Events.DESCRIPTION, "bleh bleh bleh");
    event.put(CalendarContract.Events.EVENT_LOCATION, "somewhwere");

    event.put(CalendarContract.Events.DTSTART, startTime);
    event.put(CalendarContract.Events.DTEND, endTime);
    event.put(CalendarContract.Events.ALL_DAY, 0);   // 0 for false, 1 for true
    event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true

    String timeZone = TimeZone.getDefault().getID();
    event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);

    Uri baseUri;
    if (Build.VERSION.SDK_INT >= 8) {
      baseUri = Uri.parse("content://com.android.calendar/events");
    } else {
      baseUri = Uri.parse("content://calendar/events");
    }

    getApplicationContext().getContentResolver().insert(baseUri, event);
  }

  public void insertEvent(View view) {
    Intent intent = new Intent(Intent.ACTION_INSERT);
    intent.setType("vnd.android.cursor.item/event");

    Calendar cal = Calendar.getInstance();
    long startTime = cal.getTimeInMillis() + 2*60*1000;
    long endTime = cal.getTimeInMillis()  + 60 * 60 * 1000;

    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endTime);
    intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);

    intent.putExtra(CalendarContract.Events.TITLE, "Neel Birthday");
    intent.putExtra(CalendarContract.Events.DESCRIPTION,  "This is a sample description");
    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Guest House");
    intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");

    startActivity(intent);
  }
}

