package com.lms.hidratateapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars =
                            insets.getInsets(WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        TextView tvDate = findViewById(R.id.tvDate);

        SimpleDateFormat sdfDate =
                new SimpleDateFormat(
                        "EEEE, d 'de' MMMM",
                        Locale.forLanguageTag("es-ES")
                );

        String formattedDate = sdfDate.format(new Date());

        if (!formattedDate.isEmpty()) {
            formattedDate =
                    formattedDate.substring(0, 1)
                            .toUpperCase(Locale.forLanguageTag("es-ES"))
                            + formattedDate.substring(1);
        }

        tvDate.setText(formattedDate);

        circularProgress = findViewById(R.id.circularProgress);
        tvCurrentWater = findViewById(R.id.tvCurrentWater);
        tvGoal = findViewById(R.id.tvGoal);
        tvPercentage = findViewById(R.id.tvPercentage);
        etCustomAmount = findViewById(R.id.etCustomAmount);
        historyContainer = findViewById(R.id.historyContainer);

        updateUI();
    }
}