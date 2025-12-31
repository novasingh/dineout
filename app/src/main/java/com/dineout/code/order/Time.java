package com.dineout.code.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dineout.R;
import com.dineout.code.billing.ConfirmPayment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class Time extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 1200000; // 20 minutes

    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private String orderId = "1"; // Assuming a static order ID for now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_time);

        mTextViewCountDown = findViewById(R.id.countdown);
        startTimer();
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mTextViewCountDown.setText("00:00");
                Toast.makeText(Time.this, "Your order should be ready!", Toast.LENGTH_SHORT).show();
            }
        }.start();

        mTimerRunning = true;
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    // Update Order: Navigates back to the main menu to place a new order
    public void onClickReg100(View v) {
        Intent intent = new Intent(Time.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish this activity
    }

    // Cancel Order: Deletes the order from the database and returns to the welcome screen
    public void onClickReg101(View v) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderId);
        DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance().getReference("OrderDetails");

        orderRef.removeValue(); // Delete the main order

        // Query and delete order details (assuming orderId is a direct child for simplicity)
        orderDetailsRef.orderByChild("orderid").equalTo(orderId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (com.google.firebase.database.DataSnapshot snapshot : task.getResult().getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }
        });

        Toast.makeText(this, "Order Cancelled", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Time.this, WelcomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish this activity
    }

    // View Bill: Navigates to the payment screen with the correct order ID
    public void onClickReg102(View v) {
        Intent i = new Intent(this, ConfirmPayment.class);
        i.putExtra("orderid", orderId);
        i.putExtra("customerview", "true");
        startActivity(i);
    }
}
