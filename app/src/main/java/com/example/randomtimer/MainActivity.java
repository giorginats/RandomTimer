package com.example.randomtimer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
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
    private TextView countDown, restTimeIndicator;
    private Button goBtn;

    private long countDownTimeLeftInMillis = 0;

    private List<Integer> roundTimes = new ArrayList<>();
    private List<Integer> restTimes = new ArrayList<>();

    private CountDownTimer countDownTimer;

    private boolean restTimeIsRunning = false;

    private SoundPool soundPool;
    private int soundStart, soundRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initUI();
        createSoundPools();
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
        restTimeIndicator = findViewById(R.id.rest_time_indicator);
    }

    private void createSoundPools() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder().setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        soundStart = soundPool.load(this, R.raw.start, 1);
        soundRest = soundPool.load(this, R.raw.rest, 1);
    }

    private void takeInputs() {

        String rndMin = roundMin.getText().toString().trim();
        String rndMax = roundMax.getText().toString().trim();
        String rstMin = restMin.getText().toString().trim();
        String rstMax = restMax.getText().toString().trim();
        String rpt = repeat.getText().toString().trim();


        try {
            int intRoundMin = Integer.parseInt(rndMin);
            int intRoundMax = Integer.parseInt(rndMax);
            int intRestMin = Integer.parseInt(rstMin);
            int intRestMax = Integer.parseInt(rstMax);

            int intRepeat = Integer.parseInt(rpt);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "All field must be filled correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        int intRoundMin = Integer.parseInt(rndMin);
        int intRoundMax = Integer.parseInt(rndMax);
        int intRestMin = Integer.parseInt(rstMin);
        int intRestMax = Integer.parseInt(rstMax);

        int intRepeat = Integer.parseInt(rpt);


        //ამით ვამოწმებ შემოსული რიცხვები ხო სწორია და მერე აღარ ვიყენებ არაფერში
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
                restTimes.add(getRandomNumberInRange(intRestMin, intRestMax));
                roundTimes.add(getRandomNumberInRange(intRoundMin, intRoundMax));
            }
        }

        restTimeIsRunning = true;
        restTimeIndicator.setVisibility(View.VISIBLE);
        startCountDown();

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

    int roundRepeatCounter = 0;
    int restRepeatCounter = 0;

    private void startCountDown() {
        if (roundRepeatCounter >= roundTimes.size()) {
            roundTimes.clear();
            roundRepeatCounter = 0;
            restTimes.clear();
            restRepeatCounter = 0;
            restTimeIndicator.setVisibility(View.INVISIBLE);
            soundPool.play(soundRest, 1, 1, 0, 0, 1);
            return;
        }

        if (restTimeIsRunning) {
            if (restRepeatCounter != 0) {
                soundPool.play(soundRest, 1, 1, 0, 0, 1);
            }
        } else {
            soundPool.play(soundStart, 1, 1, 0, 0, 1);
        }

        if (restTimeIsRunning) {
            countDownTimeLeftInMillis = restTimes.get(restRepeatCounter) * 1000;
            restRepeatCounter++;
        } else {
            countDownTimeLeftInMillis = roundTimes.get(roundRepeatCounter) * 1000;
            roundRepeatCounter++;
        }

        countDownTimer = new CountDownTimer(countDownTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                if (restTimeIsRunning) {
                    countDownTimeLeftInMillis = l;
                    //აქ უნდა გავაკეთ რაღაც ანიმაცია რომელიც კითხვის ნიშნებს აანთებს და ჩააქრობს
//                    updateRestTimeCountDown();
                } else {
                    countDownTimeLeftInMillis = l;
                    updateRoundTimeCountDown();
                }
            }

            @Override
            public void onFinish() {
                if (restTimeIsRunning) {
                    restTimeIsRunning = false;
                    restTimeIndicator.setVisibility(View.INVISIBLE);
                    startCountDown();
                } else {
                    restTimeIsRunning = true;
                    restTimeIndicator.setVisibility(View.VISIBLE);
                    startCountDown();
                }
            }
        }.start();
    }


    private void updateRoundTimeCountDown() {
        int minutes = (int) (countDownTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (countDownTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        countDown.setText(timeLeftFormatted);
    }

    //ამ დროის Ui ში დახატვა არ დამჭირდება სატესტოა
//    private void updateRestTimeCountDown() {
//        int minutes = (int) (countDownTimeLeftInMillis / 1000) / 60;
//        int seconds = (int) (countDownTimeLeftInMillis / 1000) % 60;
//        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
//        test.setText(timeLeftFormatted);
//    }
}
