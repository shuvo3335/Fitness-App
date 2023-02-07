

package org.dynamicsoft.caloriescope.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.dynamicsoft.caloriescope.R;
import org.dynamicsoft.caloriescope.accelerometerCounter.StepDetector;
import org.dynamicsoft.caloriescope.accelerometerCounter.StepListener;
import org.dynamicsoft.caloriescope.services.BackgroundService;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener, StepListener {

    public static Intent i1, i2, i3, i4, i5, i6, i7, i8,i9,i11,i10;
    @SuppressLint("StaticFieldLeak")
    public static TextView LastBMI, LastWHR, LastBPM;
    private static Intent i0;
    private long numSteps;
    private int waterGlasses = 0, caffeineCups = 0, currentWaterQuantity, currentCaffeineQuantity;
    private float Calories, SensorSensitivityTemp;
    private ImageView ClearFluids, addCaffeine, addWater;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar circularProgressbar;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel, mSensor;
    private boolean isPedometerSensorPresent = false, HeartRateSensorIsPresent = false;
    private Button BtnStart, BtnStop, BtnReset;
    private TextView TvSteps, CalorieView, currentWaterValue, currentCaffeineValue, waterQuantity, caffeineQuantity, stepsInCircle;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calorie_scope_root_nav_drawer);
        i0 = new Intent(this, MainActivity.class);
        i1 = new Intent(this, NewsActivity.class);
        i2 = new Intent(this, AboutActivity.class);
        i3 = new Intent(this, SettingsActivity.class);
        i4 = new Intent(this, CalculatorActivity.class);
        i5 = new Intent(this, HeartRateSensorActivity.class);
        i6 = new Intent(this, HeartRateCameraActivity.class);
        i7 = new Intent(this, HearingWellbeingActivity.class);
        i8 = new Intent(this, VideosActivity.class);
        i9 = new Intent(this, exercise_Activity.class);
        i11= new Intent();
        i10= new Intent(this, MapsActivity.class);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("AppData", 0);
        editor = pref.edit();

        SensorSensitivityTemp = pref.getFloat("CurrentSensitivityValue", 30f);

        startService(new Intent(this, BackgroundService.class));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TvSteps = findViewById(R.id.tv_steps);
        CalorieView = findViewById(R.id.CalorieView);
        BtnStart = findViewById(R.id.btn_start);
        BtnStop = findViewById(R.id.btn_stop);
        BtnReset = findViewById(R.id.btn_reset);
        addWater = findViewById(R.id.addWater);
        addCaffeine = findViewById(R.id.addCaffeine);
        currentWaterValue = findViewById(R.id.currentWaterValue);
        currentCaffeineValue = findViewById(R.id.currentCaffeineValue);
        waterQuantity = findViewById(R.id.waterQuantity);
        caffeineQuantity = findViewById(R.id.caffeineQuantity);
        ClearFluids = findViewById(R.id.btnClearFluidIntake);
        stepsInCircle = findViewById(R.id.stepsInCircle);
        circularProgressbar = findViewById(R.id.circularProgressbar);
        TextView todayDateAndTime = findViewById(R.id.TodayDateAndTime);
        LastBMI = findViewById(R.id.LastBMI);
        LastWHR = findViewById(R.id.LastWHR);
        LastBPM = findViewById(R.id.LastBPM);

        //Set date

        Date full = Calendar.getInstance().getTime();
        todayDateAndTime.setText(full.toString().substring(0, 10) + ", " + full.toString().substring(30, 34));

        waterGlasses = pref.getInt("waterGlasses", 0);
        currentWaterQuantity = pref.getInt("currentWaterQuantity", 0);
        currentWaterValue.setText("" + waterGlasses);
        waterQuantity.setText("( " + currentWaterQuantity + " ml" + " )");
        caffeineCups = pref.getInt("caffeineCups", 0);
        currentCaffeineQuantity = pref.getInt("currentCaffeineQuantity", 0);
        currentCaffeineValue.setText("" + caffeineCups);
        caffeineQuantity.setText("( " + currentCaffeineQuantity + " mg" + " )");

        numSteps = pref.getLong("numSteps", 0);
        Calories = pref.getFloat("Calories", 0);

        //Set personalization

        final TextView NavDrawerUserString = navigationView.getHeaderView(0).findViewById(R.id.NavDrawerUserString);
        NavDrawerUserString.setText(pref.getString("UserName", "Welcome"));

        if (!pref.getString("LastBMI", "").equals("")) {
            LastBMI.setText("Your Body Mass index is " + pref.getString("LastBMI", "Error"));
            LastBMI.setVisibility(View.VISIBLE);
        }
        if (!pref.getString("LastWHR", "").equals("")) {
            LastWHR.setText("Your Waist Hip ratio is " + pref.getString("LastWHR", "Error"));
            LastWHR.setVisibility(View.VISIBLE);
        }
        if (!pref.getString("LastBPM", "").equals("")) {
            LastBPM.setText("Your last heart rate check was " + pref.getString("LastBPM", "Error") + " BPM");
            LastBPM.setVisibility(View.VISIBLE);
        }

        if (pref.getString("personalInfoSet", "").equals("")) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            @SuppressLint("InflateParams") View promptView = layoutInflater.inflate(R.layout.personal_info_alert, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setView(promptView);

            final EditText PersonName = promptView.findViewById(R.id.PersonName);
           /* final RadioGroup GenderRadioGroup = promptView.findViewById(R.id.GenderRadioGroup);

            GenderRadioGroup.clearCheck();
            GenderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rb = group.findViewById(checkedId);
                    if (null != rb && checkedId > -1) {
                        editor.putString("Gender", rb.getText().toString());
                    }
                }
            });*/

            alertDialogBuilder.setCancelable(false).setTitle("Welcome").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    NavDrawerUserString.setText("Welcome " + PersonName.getText().toString());
                    String name = "Welcome " + PersonName.getText().toString();

                    editor.putString("UserName", name);
                    editor.putString("personalInfoSet", "true");
                    editor.apply();
                }
            });

            AlertDialog SaveUserDetails = alertDialogBuilder.create();
            SaveUserDetails.show();
        }

        // Get an instance of the SensorManager

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isPedometerSensorPresent = true;
        } else {
            isPedometerSensorPresent = false;
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            simpleStepDetector = new StepDetector();
            simpleStepDetector.registerListener(this);
        }

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!isPedometerSensorPresent) {
                    sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                } else {
                    sensorManager.registerListener(MainActivity.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }

                BtnStart.setVisibility(View.INVISIBLE);
                BtnReset.setVisibility(View.INVISIBLE);
                BtnStop.setVisibility(View.VISIBLE);
            }
        });

        BtnStop.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View arg0) {
                TvSteps.setText("Paused");
                sensorManager.unregisterListener(MainActivity.this);
                BtnStop.setVisibility(View.INVISIBLE);
                BtnStart.setVisibility(View.VISIBLE);
                if (!isPedometerSensorPresent) {
                    BtnReset.setVisibility(View.VISIBLE);
                } else {
                    BtnReset.setVisibility(View.INVISIBLE);
                }
            }
        });

        BtnReset.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View arg0) {
                TvSteps.setText("Let's Start!");
                CalorieView.setText("");
                sensorManager.unregisterListener(MainActivity.this);
                numSteps = 0;
                Calories = 0;
                editor.putLong("numSteps", 0);
                editor.putFloat("Calories", 0);
                editor.apply();
                BtnReset.setVisibility(View.INVISIBLE);
                BtnStop.setVisibility(View.INVISIBLE);
                BtnStart.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Records cleared", Toast.LENGTH_SHORT).show();
            }
        });

        addWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                waterGlasses++;
                currentWaterQuantity = currentWaterQuantity + 250;
                editor.putInt("waterGlasses", waterGlasses);
                editor.putInt("currentWaterQuantity", currentWaterQuantity);
                editor.apply();
                currentWaterValue.setText("" + waterGlasses);
                waterQuantity.setText("( " + currentWaterQuantity + " ml" + " )");
            }
        });

        addCaffeine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                caffeineCups++;
                currentCaffeineQuantity = currentCaffeineQuantity + 80;
                editor.putInt("caffeineCups", caffeineCups);
                editor.putInt("currentCaffeineQuantity", currentCaffeineQuantity);
                editor.apply();
                currentCaffeineValue.setText("" + caffeineCups);
                caffeineQuantity.setText("( " + currentCaffeineQuantity + " mg" + " )");
            }
        });

        //Handling heart rate activities visibility, this chunk of code must exist in each activity!
        SensorManager mSensorManager;
        Menu nav_Menu = navigationView.getMenu();
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null) {
            nav_Menu.findItem(R.id.nav_heart_rate).setVisible(true);
            HeartRateSensorIsPresent = true;

        } else {
            nav_Menu.findItem(R.id.nav_heart_rate_camera).setVisible(true);
            HeartRateSensorIsPresent = false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_exit) {
            MainActivity.this.moveTaskToBack(true);
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {
        }
        /*else if (id == R.id.nav_news) {
            startActivity(i1);
        }*/

        else if (id == R.id.nav_about) {
            startActivity(i2);
        } else if (id == R.id.nav_settings) {
            startActivity(i3);
        } else if (id == R.id.nav_calculator) {
            startActivity(i4);
        } else if (id == R.id.nav_heart_rate) {
            startActivity(i5);
        } else if (id == R.id.nav_heart_rate_camera) {
            startActivity(i6);
        } else if (id == R.id.nav_hearing_wellbeing) {
            startActivity(i7);
        } /*else if (id == R.id.nav_videos) {
            startActivity(i8);
        }*/
        else if (id == R.id.nav_exercise){
            startActivity(i9);
        }
        else if (id== R.id.nav_maps)
        {
            startActivity(i10);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isPedometerSensorPresent) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                simpleStepDetector.updateAccel(
                        event.timestamp, event.values[0], event.values[1], event.values[2], SensorSensitivityTemp);
            }
        } else {
            String rawCount = String.valueOf(event.values[0]);
            String actualCount = rawCount.substring(0, rawCount.length() - 2);
            Calories = Float.parseFloat(actualCount) / 20; //Algorithm by "Shape Up America!"
            numSteps = Long.parseLong(actualCount);
            TvSteps.setText(actualCount);
            stepsInCircle.setText(numSteps + "/10000");
            circularProgressbar.setProgress((int) numSteps);
            CalorieView.setText(Calories + " cal");
            editor.putLong("numSteps", numSteps);
            editor.putFloat("Calories", Calories);
            editor.apply();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void step(long timeNs) {
        if (!isPedometerSensorPresent) {
            numSteps++; //Stores value of steps
            Calories = numSteps / (float) 20; //Stores calories, algorithm by "Shape Up America!"
            TvSteps.setText("" + numSteps);
            stepsInCircle.setText(numSteps + "/10000");
            circularProgressbar.setProgress((int) numSteps);
            CalorieView.setText(Calories + " cal");
            editor.putLong("numSteps", numSteps);
            editor.putFloat("Calories", Calories);
            editor.apply();
        }
    }

    public void launch_heart_activity_from_home(View view) {
        if (HeartRateSensorIsPresent) {
            startActivity(i5);
        } else {
            startActivity(i6);
        }
    }

    @SuppressLint("SetTextI18n")
    public void clear_fluids(View view) {
        waterGlasses = 0;
        currentWaterQuantity = 0;
        caffeineCups = 0;
        currentCaffeineQuantity = 0;
        editor.putInt("waterGlasses", waterGlasses);
        editor.putInt("currentWaterQuantity", currentWaterQuantity);
        editor.putInt("caffeineCups", caffeineCups);
        editor.putInt("currentCaffeineQuantity", currentCaffeineQuantity);
        editor.apply();
        currentWaterValue.setText("" + waterGlasses);
        waterQuantity.setText(currentWaterQuantity + " ml");
        currentCaffeineValue.setText("" + caffeineCups);
        caffeineQuantity.setText(currentCaffeineQuantity + " mg");
        Toast.makeText(MainActivity.this, "Records cleared", Toast.LENGTH_SHORT).show();
    }
}
