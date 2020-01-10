package com.example.randomtimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText roundMin, roundMax, restMin, restMax, repeat;
    private TextView countDown, test;
    private Button goBtn;

    private long roundTimeLeftInMillis = 0;
    private long restTimeLeftMillis = 0;

    private List<Integer> roundTimes = new ArrayList<>();
    private List<Integer> restTimes = new ArrayList<>();

    private CountDownTimer countDownTimer;
    private CountDownTimer countDownTimer2;

    //ყველაფერი უნდა იწყებოდეს დასვენების დროით
    //სანამ ახლიდან დაიწყება ლისტები უნდა დავაქელარო

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = findViewById(R.id.rest_count_down);

        initUI();
        clickListener();

    }

    private void initUI() {
        roundMin = findViewById(R.id.round_timer_min_id);
        roundMax = findViewById(R.id.round_timer_max_id);
        restMin = findViewById(R.id.rest_timer_min_id);
        restMax = findViewById(R.id.rest_timer_max_id);
        repeat = findViewById(R.id.repeat_counter_id);
        goBtn = findViewById(R.id.goBtn);
        countDown = findViewById(R.id.count_down_id);
    }

    private void takeInputs() {
        String rndMin = roundMin.getText().toString().trim();
        String rndMax = roundMax.getText().toString().trim();
        String rstMin = restMin.getText().toString().trim();
        String rstMax = restMax.getText().toString().trim();
        String rpt = repeat.getText().toString().trim();

        int intRoundMin = Integer.parseInt(rndMin);
        int intRoundMax = Integer.parseInt(rndMax);
        int intRestMin = Integer.parseInt(rstMin);
        int intRestMax = Integer.parseInt(rstMax);

        int intRepeat = Integer.parseInt(rpt);

        int roundTime = getRandomNumberInRange(intRoundMin, intRoundMax);
        int restTime = getRandomNumberInRange(intRestMin, intRestMax);

        //შემოწმება რომ მინიმალური არ იყოს მეტი მაქსიმალურზე
        if (roundTime == -1 || restTime == -1 || intRepeat > 20) {
            if (roundTime == -1) {
                Toast.makeText(this, "Round max time must be greater than min", Toast.LENGTH_SHORT).show();
            }
            if (restTime == -1) {
                Toast.makeText(this, "Rest max time must be greater than min", Toast.LENGTH_SHORT).show();
            }
            if (intRepeat > 20) {
                Toast.makeText(this, "Repeat cant be set more than 20 times", Toast.LENGTH_SHORT).show();
            }

        } else {
            //თუ შეყვანილი ინფორმაცია სწორია ლისტებში ჩაყრის დროებს
            for (int i = 0; i < intRepeat; i++) {
                restTimes.add(getRandomNumberInRange(intRoundMin, intRoundMax));
                roundTimes.add(getRandomNumberInRange(intRoundMin, intRoundMax));
            }
        }

        //აქ პირველი უნდა გავუშვა დასვენების დრო და დასვენების დროიდან უნდა გამოვიძახო რაუნდის დრო
//        timeLeftInMillis = roundTimes.get(0) * 1000;
//        startRoundTimer();

        restTimeLeftMillis = restTimes.get(0);
        startRestTimer();

    }

    private int getRandomNumberInRange(int min, int max) {
        if (min > max) {
            // ამ ციფრზე რამეს დავუწერ users რო სწორად შეიყვანოს - ის შეყვანა არ შეუძლია მაინც და ამას შემთხვევით ვერ შეიყვანს
            return -1;
        }
        Random random = new Random();
        //გადასცემ მაქსიმალუს გამოკლებული მინიმალური და უმატებ 1 ს რადგან მიიღო range სადაც უნდა ამოყაროს ციფრები, შემდეგ უმატებ მინიმალურს და იღებ ზუსტად იმ ranges რავ გჭირდება
        return random.nextInt((max - min) + 1) + min;
    }

    private void clickListener() {
        goBtn.setOnClickListener(view -> {
            takeInputs();
        });
    }

    int roundRepeatCounter = 1;
    private void startRoundTimer() {
        countDownTimer = new CountDownTimer(roundTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                roundTimeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                if(roundRepeatCounter < roundTimes.size()){
                    roundTimeLeftInMillis = roundTimes.get(roundRepeatCounter) * 1000;
                    startRestTimer();
                    roundRepeatCounter++;
                }else{
                    roundRepeatCounter = 0;
                }
            }
        }.start();
    }

    int restRepeatCounter = 1;
    private void startRestTimer(){
        countDownTimer2 = new CountDownTimer(restTimeLeftMillis, 1000) {
            @Override
            public void onTick(long l) {
                restTimeLeftMillis = l;
                //აქ უნდა გავაკეთ რაღაც ანიმაცია რომელიც კითხვის ნიშნებს აანთებს და ჩააქრობს
                testCountdown();
            }

            @Override
            public void onFinish() {
                //აედან უნდა გამოვიძახო startRoundTimer;
                if(restRepeatCounter < restTimes.size()){
                    roundTimeLeftInMillis = restTimes.get(restRepeatCounter) * 1000;
                    startRoundTimer();
                    roundRepeatCounter++;
                }else{
                    roundRepeatCounter = 0;
                }
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (roundTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (roundTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        countDown.setText(timeLeftFormatted);
    }

    private void testCountdown() {
        int minutes = (int) (restTimeLeftMillis / 1000) / 60;
        int seconds = (int) (restTimeLeftMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        test.setText(timeLeftFormatted);
    }
}
