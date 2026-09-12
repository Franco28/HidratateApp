package com.lms.hidratateapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CircularProgressView circularProgress;
    private TextView tvCurrentWater;
    private TextView tvGoal;
    private TextView tvPercentage;
    private EditText etCustomAmount;
    private LinearLayout historyContainer;

    private static final int GOAL_WATER = 2000;

    private int currentWater = 0;
    private final List<WaterRecord> recordList = new ArrayList<>();

    private static class WaterRecord {
        final int amount;
        final String time;

        WaterRecord(int amount, String time) {
            this.amount = amount;
            this.time = time;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvDate = findViewById(R.id.tvDate);
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE, d 'de' MMMM", Locale.forLanguageTag("es-AR"));
        String rawDate = sdfDate.format(new Date());
        String finalDate = rawDate.isEmpty() ? "" : rawDate.substring(0, 1).toUpperCase(Locale.forLanguageTag("es-AR")) + rawDate.substring(1);
        tvDate.setText(finalDate);

        circularProgress = findViewById(R.id.circularProgress);
        tvCurrentWater = findViewById(R.id.tvCurrentWater);
        tvGoal = findViewById(R.id.tvGoal);
        tvPercentage = findViewById(R.id.tvPercentage);
        etCustomAmount = findViewById(R.id.etCustomAmount);
        historyContainer = findViewById(R.id.historyContainer);

        Button btn250 = findViewById(R.id.btn250);
        Button btn500 = findViewById(R.id.btn500);
        Button btn750 = findViewById(R.id.btn750);
        Button btnAdd = findViewById(R.id.btnAdd);

        btn250.setOnClickListener(v -> addWater(250));
        btn500.setOnClickListener(v -> addWater(500));
        btn750.setOnClickListener(v -> addWater(750));

        btnAdd.setOnClickListener(v -> {
            String text = etCustomAmount.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_amount, Toast.LENGTH_SHORT).show();
                return;
            }
            int amount;
            try {
                amount = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.error_invalid_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(this, R.string.error_invalid_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount > 5000) {
                Toast.makeText(this, R.string.error_amount_too_large, Toast.LENGTH_SHORT).show();
                return;
            }

            addWater(amount);
            etCustomAmount.setText("");
        });

        ScrollView mainScroll = findViewById(R.id.mainScroll);
        TextView todayNavItem = findViewById(R.id.todayNavItem);
        TextView settingsNavItem = findViewById(R.id.settingsNavItem);

        todayNavItem.setOnClickListener(v -> mainScroll.smoothScrollTo(0, 0));
        settingsNavItem.setOnClickListener(v -> Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show());

        updateUI();
    }

    private void addWater(int amount) {
        int previousWater = currentWater;
        currentWater += amount;
        String timeStr = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        WaterRecord record = new WaterRecord(amount, timeStr);
        recordList.add(0, record);

        playWaterPourSound();

        if (previousWater < GOAL_WATER && currentWater >= GOAL_WATER) {
            Toast.makeText(this, R.string.goal_reached, Toast.LENGTH_LONG).show();
        }

        updateUI();
        rebuildHistoryViews();
    }

    private void removeRecord(WaterRecord record) {
        if (recordList.remove(record)) {
            currentWater = Math.max(0, currentWater - record.amount);
            playWaterEraseSound();
            updateUI();
            rebuildHistoryViews();
            Toast.makeText(this, R.string.record_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    private void playWaterPourSound() {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.water_pour);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error al reproducir el sonido water_pour", e);
        }
    }

    private void playWaterEraseSound() {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.water_erase);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error al reproducir el sonido water_erase", e);
        }
    }

    private void updateUI() {
        tvCurrentWater.setText(getString(R.string.water_amount, String.valueOf(currentWater)));
        tvGoal.setText(getString(R.string.goal_amount, String.valueOf(GOAL_WATER)));

        int percentage = (int) Math.round(((double) currentWater / GOAL_WATER) * 100);
        tvPercentage.setText(getString(R.string.percentage, percentage));

        circularProgress.setProgress(percentage);
    }

    private void rebuildHistoryViews() {
        historyContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (WaterRecord record : recordList) {
            View rowView = inflater.inflate(R.layout.item_history, historyContainer, false);
            TextView tvRecordAmount = rowView.findViewById(R.id.tvRecordAmount);
            TextView tvRecordTime = rowView.findViewById(R.id.tvRecordTime);
            ImageView btnDeleteRecord = rowView.findViewById(R.id.btnDeleteRecord);

            tvRecordAmount.setText(getString(R.string.water_amount, String.valueOf(record.amount)));
            tvRecordTime.setText(record.time);

            btnDeleteRecord.setOnClickListener(v -> removeRecord(record));

            historyContainer.addView(rowView);
        }
    }
}