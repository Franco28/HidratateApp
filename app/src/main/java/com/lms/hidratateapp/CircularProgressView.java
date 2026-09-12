package com.lms.hidratateapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CircularProgressView extends View {

    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcBounds = new RectF();

    private int progress = 0;
    private float strokeWidthPx;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        strokeWidthPx = dpToPx(16);

        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidthPx);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setColor(
                ContextCompat.getColor(getContext(), R.color.blue_ring_background)
        );

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidthPx);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(
                ContextCompat.getColor(getContext(), R.color.blue_primary)
        );
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float padding = strokeWidthPx / 2f + dpToPx(3);
        float size = Math.min(getWidth(), getHeight());

        float left = (getWidth() - size) / 2f + padding;
        float top = (getHeight() - size) / 2f + padding;
        float right = (getWidth() + size) / 2f - padding;
        float bottom = (getHeight() + size) / 2f - padding;

        arcBounds.set(left, top, right, bottom);

        canvas.drawArc(arcBounds, 0f, 360f, false, backgroundPaint);

        float sweepAngle = 360f * progress / 100f;
        canvas.drawArc(arcBounds, -90f, sweepAngle, false, progressPaint);
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(progress, 100));
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    private float dpToPx(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
