package org.secuso.privacyfriendlymath.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlymath.R;
import org.secuso.privacyfriendlymath.exerciseInstance;
import org.secuso.privacyfriendlymath.gameInstance;

public class ExerciseActivity extends AppCompatActivity {

    //Ui
    TextView input;
    TextView operand1;
    TextView operand2;
    TextView operator;
    TextView lastinput;

    StringBuilder sb = new StringBuilder();
    long nanoElapsed = 0;
    gameInstance game;
    exerciseInstance exercise;
    Boolean highScoreAchieved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        //Ui
        input = (TextView) findViewById(R.id.inputfield);
        operand1 = (TextView) findViewById(R.id.valuex);
        operand2 = (TextView) findViewById(R.id.valuey);
        operator = (TextView) findViewById(R.id.operator);
        lastinput = (TextView) findViewById(R.id.lastinput);

        game = (gameInstance) getIntent().getSerializableExtra("game");

        //start timer and first exercise
        nanoElapsed = System.nanoTime();
        exercise = game.createNewExercise();
        operand1.setText(""+exercise.x);
        operand2.setText(""+exercise.y);
        operator.setText(exercise.o);
    }

    public void calcbuttonClicked(View view) {
        if(sb.toString().equals("0")) sb.setLength(0);
        int maxLength = 7;

        switch(view.getId()){
            case R.id.calcbutton_confirm:
                if(sb.length() == 0){
                    sb.append("0");
                }
                commitAnswer();break;
            case R.id.calcbutton_00:
                if(sb.length() < maxLength)
                     sb.append("0");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_01:
                if(sb.length() < maxLength)
                    sb.append("1");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_02:
                if(sb.length() < maxLength)
                    sb.append("2");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_03:
                if(sb.length() < maxLength)
                    sb.append("3");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_04:
                if(sb.length() < maxLength)
                    sb.append("4");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_05:
                if(sb.length() < maxLength)
                    sb.append("5");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_06:
                if(sb.length() < maxLength)
                    sb.append("6");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_07:
                if(sb.length() < maxLength)
                    sb.append("7");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_08:
                if(sb.length() < maxLength)
                    sb.append("8");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_09:
                if(sb.length() < maxLength)
                    sb.append("9");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_trash:
                sb.setLength(0);
                sb.append("0");
                break;
        }

        input.setText(sb.toString());
    }

    void maxInputToast(){
        Context context = getApplicationContext();
        CharSequence text = getResources().getString(R.string.game_max_length_toast);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void commitAnswer(){
        int input = Integer.parseInt(sb.toString());

        game.putExercise(exercise.x,exercise.y,input,exercise.o.toString());

        //direct feedback
        /*
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPref.getBoolean("pref_example_switch", false)){
             feedback.setText(getResources().getString(R.string.feedback_positive));
        }
        if (solution)  feedback.setText(getResources().getString(R.string.feedback_positive)); else  feedback.setText(getResources().getString(R.string.feedback_negative));
        */

        if(game.exercisesSolved() >= 5){
            highScoreAchieved = achievedHighscore(game.calculateScore((int)((System.nanoTime() - nanoElapsed)/1000000000.0)),game.space);
            if(highScoreAchieved){
                displayNameInput();
            } else {
                startResultActivity("defaultname");
            }
        } else {
            sb.setLength(0);
            sb.append("0");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            if(sharedPref.getBoolean("pref_switch_feedback", false)){
                if(input == exercise.solve()){
                    lastinput.setTextColor(getResources().getColor(R.color.green));
                } else {
                    lastinput.setTextColor(getResources().getColor(R.color.red));
                }
                String s = ""+exercise.x+" "+exercise.o+" "+exercise.y+" = " + input;
                if(sharedPref.getBoolean("pref_switch_answer", false)){
                    s = s + "(" + exercise.solve() + ")";
                }
                lastinput.setText(s);
            }
            exercise = game.createNewExercise();
            operand1.setText(""+exercise.x);
            operand2.setText(""+exercise.y);
            operator.setText(exercise.o);
        }
    }

    private Boolean achievedHighscore(int score, int space){
        SharedPreferences hs = this.getSharedPreferences("pfa-math-highscore", Context.MODE_PRIVATE);
        for(int i = 0; i < 5; i++){
            String s = hs.getString("hsscore"+i+space,null);
            if(s != null){
                if (score >= Integer.parseInt(s)){
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void startResultActivity(String name){
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        intent.putExtra("highScoreAchieved", highScoreAchieved);
        intent.putExtra("game", game);
        intent.putExtra("name", name);

        startActivity(intent);
    }

    private void displayNameInput(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alert_title));
        final EditText inputs = new EditText(this);

        //check if name has been set
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPref.getString("weight",null);
        //otherwise use previous input
        SharedPreferences hs = this.getSharedPreferences("pfa-math-highscore", Context.MODE_PRIVATE);
        if(name != null){
            inputs.setText(name);
        } else {
            inputs.setText(hs.getString("previousname",""));
        }

        inputs.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputs);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = inputs.getText().toString();

                //save name
                SharedPreferences hs = getSharedPreferences("pfa-math-highscore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = hs.edit();
                editor.putString("previousname", text);
                editor.commit();

                startResultActivity(text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startResultActivity("defaultname");
            }
        });
        builder.show();
    }

}
