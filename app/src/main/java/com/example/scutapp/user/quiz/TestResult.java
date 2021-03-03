package com.example.scutapp.user.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.scutapp.R;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

public class TestResult extends AppCompatActivity {
    Intent intent;
    int result;
    TextView infectedText, notInfectedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        intent = getIntent();
        result = Integer.parseInt(intent.getStringExtra("result"));

        infectedText = findViewById(R.id.infectedText);
        notInfectedText = findViewById(R.id.notInfectedText);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Test Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        if (result > 49) {
            infectedText.setVisibility(View.VISIBLE);
        } else {
            notInfectedText.setVisibility(View.VISIBLE);
        }
        setPercentageChart(result);
    }

    private void setPercentageChart(int result) {
        DecoView arcView = (DecoView) findViewById(R.id.dynamicArcView);
        final String format = "%.0f%%";
        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);

// Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(100f)
                .build());

//Create data series track
        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 255, 0, 0))
                .setRange(0, 100, 0)
                .setLineWidth(100f)
                .build();

        int series1Index = arcView.addSeries(seriesItem1);
        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        arcView.addEvent(new DecoEvent.Builder(result).setIndex(series1Index).setDelay(2000).build());

        seriesItem1.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (format.contains("%%")) {
                    float percentFilled = ((currentPosition - seriesItem1.getMinValue()) / (seriesItem1.getMaxValue() - seriesItem1.getMinValue()));
                    textPercentage.setText(String.format(format, percentFilled * 100f));
                } else {
                    textPercentage.setText(String.format(format, currentPosition));
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {
            }
        });
    }
}
