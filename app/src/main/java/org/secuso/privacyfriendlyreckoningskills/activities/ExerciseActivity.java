/*
 This file is part of Privacy Friendly Reckoning Skills.
 Privacy Friendly Reckoning Skills is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.
 Privacy Friendly Reckoning Skills is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Reckoning Skills. If not, see <http://www.gnu.org/licenses/>.
 */

package org.secuso.privacyfriendlyreckoningskills.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlyreckoningskills.R;
import org.secuso.privacyfriendlyreckoningskills.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyreckoningskills.exerciseInstance;
import org.secuso.privacyfriendlyreckoningskills.gameInstance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ExerciseActivity extends AppCompatActivity {

    //Ui
    TextView input;
    TextView operand1;
    TextView operand2;
    TextView operator;
    TextView lastinput;
    Toolbar toolbar;
    Chronometer timer;
    TextView addsign;
    TextView subsign;
    TextView mulsign;
    TextView divsign;
    TextView progress;
    TextView currentspace;

    StringBuilder sb = new StringBuilder();
    long miliElapsed = 0;
    long miliElapsed2 = 0;
    gameInstance game;
    exerciseInstance exercise;
    Boolean highScoreAchieved = false;
    Boolean exerciseAnswered = false;
    long miliElapsedOnPause = 0;

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
        timer = (Chronometer) findViewById(R.id.chronometer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        addsign = (TextView) findViewById(R.id.sign_add);
        subsign = (TextView) findViewById(R.id.sign_sub);
        mulsign = (TextView) findViewById(R.id.sign_mul);
        divsign = (TextView) findViewById(R.id.sign_div);
        progress = (TextView) findViewById(R.id.progress);
        currentspace = (TextView) findViewById(R.id.space);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        game = (gameInstance) getIntent().getSerializableExtra("game");

        exercise = newExercise();

        miliElapsed2 =SystemClock.elapsedRealtime();
        updateLabels();

        if(!getIntent().getBooleanExtra("continue",false)){
            timer.setBase(SystemClock.elapsedRealtime()  - game.timeElapsed);
            timer.start();
        }
        operand1.setText(""+exercise.x);
        operand2.setText(""+exercise.y);
        operator.setText(exercise.o);
    }

    protected void updateLabels(){
        if(!game.add)addsign.setTextColor(getResources().getColor(R.color.middlegrey)); else
            addsign.setTextColor(getResources().getColor(R.color.red));
        if(!game.sub)subsign.setTextColor(getResources().getColor(R.color.middlegrey)); else
            subsign.setTextColor(getResources().getColor(R.color.green));
        if(!game.mul)mulsign.setTextColor(getResources().getColor(R.color.middlegrey)); else
            mulsign.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        if(!game.div)divsign.setTextColor(getResources().getColor(R.color.middlegrey)); else
            divsign.setTextColor(getResources().getColor(R.color.lightblue));

        progress.setText(""+game.exercises.size()+"/"+"10");

        switch (game.space){
            case 3:
                currentspace.setText(""+10000);
                break;
            case 2:
                currentspace.setText(""+1000);
                break;
            case 1:
                currentspace.setText(""+100);
                break;
            default:
                currentspace.setText(""+10);
                break;
        }

        operand1.setText(""+exercise.x);
        operand2.setText(""+exercise.y);
        operator.setText(exercise.o);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(exercise.pausedOn){
            miliElapsed =SystemClock.elapsedRealtime();
            timer.setBase(SystemClock.elapsedRealtime() - game.timeElapsed);
        }
        long diff = (SystemClock.elapsedRealtime()- miliElapsed);
        game.timeElapsed = game.timeElapsed + diff;
        saveGameToStorage();
    }

    @Override
    protected void onResume(){
        super.onResume();
        miliElapsed =SystemClock.elapsedRealtime();
        if(getIntent().getBooleanExtra("continue",false) || game.timeElapsed > 0) {
            loadGameFromStorage();
            //this can happen if orientation changes during name input
            if (game.gameFinished) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                String name = sharedPref.getString("weight",null);
                if(name == null){
                    startResultActivity("");
                } else {
                    startResultActivity(name);
                }
            } else {
                if (game.exercises.size() > 0) {
                    if (exercise.pausedOn) {
                        timer.stop();
                        exercise = game.exercises.get(game.exercises.size() - 1);
                        timer.setBase(SystemClock.elapsedRealtime() - game.timeElapsed);
                        input.setText("" + exercise.z);
                        sb.setLength(0);
                        sb.append("" + exercise.z);
                        String s = "" + exercise.z;
                        if (exercise.z == exercise.solve()) {
                            input.setTextColor(getResources().getColor(R.color.green));
                            s = s + "" + " \u2713";
                        } else {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                            if (sharedPref.getBoolean("pref_switch_answer", false)) {
                                s = s + " (" + exercise.solve() + ")";
                                input.setTextColor(getResources().getColor(R.color.red));
                            } else {
                                input.setTextColor(getResources().getColor(R.color.red));
                            }
                        }
                        input.setText(s);
                        updateLabels();
                    } else {
                        timer.setBase(SystemClock.elapsedRealtime() - game.timeElapsed);
                        timer.start();
                        //exercise = newExercise();
                        input.setText(sb);
                        updateLabels();
                    }
                } else {
                    timer.setBase(SystemClock.elapsedRealtime() - game.timeElapsed);
                    timer.start();
                    //exercise = newExercise();
                    input.setText(sb);
                    updateLabels();
                }
            }
        }
    }

    private void saveGameToStorage(){

        //save current exercise
        game.e_x = exercise.x;
        game.e_y = exercise.y;
        if(sb.length() == 0){
            game.e_input = "";
        } else {
            game.e_input = sb.toString();
        }
        game.e_op = exercise.o;
        game.e_paused = exercise.pausedOn;


        SharedPreferences hs = this.getSharedPreferences("pfa-math-highscore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = hs.edit();
        editor.putBoolean("continue", true);
        editor.commit();

        try {
            FileOutputStream fos = this.openFileOutput("gameinstance", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(game);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGameFromStorage(){
        try {
            FileInputStream fis = this.openFileInput("gameinstance");
            ObjectInputStream is = new ObjectInputStream(fis);
            game = (gameInstance) is.readObject();
            is.close();
            fis.close();

            //retrieve current exercise
            exercise.x = game.e_x;
            exercise.y = game.e_y;
            exercise.o = game.e_op;
            sb.setLength(0);
            sb.append(game.e_input);
            exercise.pausedOn = game.e_paused;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void calcbuttonClicked(View view) {
        if(sb.toString().equals("0")) sb.setLength(0);
        int maxLength = 7;

        switch(view.getId()){
            case R.id.calcbutton_confirm:
                int inputtemp = 0;
                if(sb.length() > 0) {
                    inputtemp = Integer.parseInt(sb.toString());
                }
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                if(sharedPref.getBoolean("pref_switch_feedback", false)) {
                    exercise.z = inputtemp;
                    if(!exercise.pausedOn) {
                        game.putExercise2(exercise);
                    }
                    String s = ""+exercise.z;
                    if(exercise.z == exercise.solve()){
                        input.setTextColor(getResources().getColor(R.color.green));
                        s = s + "" + " \u2713";
                    } else {
                        if(sharedPref.getBoolean("pref_switch_answer", false)) {
                            s = s + " (" + exercise.solve() + ")";
                            input.setTextColor(getResources().getColor(R.color.red));
                        } else {
                            input.setTextColor(getResources().getColor(R.color.red));
                        }
                    }
                    input.setText(s);

                    if(exercise.pausedOn) {
                        miliElapsed =SystemClock.elapsedRealtime();
                        timer.setBase(SystemClock.elapsedRealtime() - game.timeElapsed);
                        timer.start();
                        game.exercises.get(game.exercises.size()-1).pausedOn = false;
                        input.setTextColor(getResources().getColor(R.color.colorPrimary));
                        commitAnswer();
                    }else {
                        long diff = (SystemClock.elapsedRealtime()- miliElapsed);
                        game.timeElapsed = game.timeElapsed + diff;
                        timer.stop();
                        updateLabels();
                        exercise.pausedOn = true;
                    }
                } else {
                    if(exercise.pausedOn){
                        miliElapsed =SystemClock.elapsedRealtime();
                        timer.setBase(SystemClock.elapsedRealtime() - game.timeElapsed);
                        input.setTextColor(getResources().getColor(R.color.colorPrimary));
                        timer.start();
                    }
                    game.putExercise(exercise.x,exercise.y,inputtemp,exercise.o.toString());
                    commitAnswer();
                }
                break;
            case R.id.calcbutton_00:
                if(!exercise.pausedOn)
                if(sb.length() < maxLength)
                     sb.append("0");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_01:
                if(!exercise.pausedOn)
                if(sb.length() < maxLength)
                    sb.append("1");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_02:
                if(!exercise.pausedOn)
                if(sb.length() < maxLength)
                    sb.append("2");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_03:
                if(!exercise.pausedOn)
                if(sb.length() < maxLength)
                    sb.append("3");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_04:
                if(!exercise.pausedOn)
                if(sb.length() < maxLength)
                    sb.append("4");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_05:
                if(!exercise.pausedOn)
                if(sb.length() < maxLength)
                    sb.append("5");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_06:
                if(!exercise.pausedOn)
                if(sb.length() < maxLength)
                    sb.append("6");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_07:
                if(!exercise.pausedOn)
                if(sb.length() < maxLength)
                    sb.append("7");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_08:
                if(!exercise.pausedOn)
                if(sb.length() < maxLength)
                    sb.append("8");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_09:
                if(!exercise.pausedOn)
                if(sb.length() < maxLength)
                    sb.append("9");
                else
                    maxInputToast();
                break;
            case R.id.calcbutton_trash:
                if(!exercise.pausedOn)
                if(sb.length()>0) sb.setLength(sb.length() - 1);
                break;
        }
        if(!exercise.pausedOn){
            input.setText(sb);
        }

    }

    void maxInputToast(){
        Context context = getApplicationContext();
        CharSequence text = getResources().getString(R.string.game_max_length_toast);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void commitAnswer(){
        int input = 0;
        if(sb.length() > 0) {
            input = Integer.parseInt(sb.toString());
        }

        if(game.exercisesSolved() >= 10){
            long diff = (SystemClock.elapsedRealtime()- miliElapsed);
            game.timeElapsed = game.timeElapsed + diff;
            timer.stop();
            highScoreAchieved = achievedHighscore(game.calculateScore((int)((game.timeElapsed)/1000.0)),game.space);
            if(highScoreAchieved){
                game.gameFinished = true;
                displayNameInput();
            } else {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                String name = sharedPref.getString("weight",null);
                if(name == null){
                    startResultActivity("");
                } else {
                    startResultActivity(name);
                }
                startResultActivity(name);
            }
        } else {
            game.e_commited = true;
            sb.setLength(0);
            exercise = newExercise();
            operand1.setText(""+exercise.x);
            operand2.setText(""+exercise.y);
            operator.setText(exercise.o);
            updateLabels();
        }
    }

    private exerciseInstance newExercise(){

        String op = game.randomOperator();

        exerciseInstance savedExercise;
        PFASQLiteHelper helper = new PFASQLiteHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] cols = new String[] {"id", "operator1", "operator2", "operand", "space"};
        Cursor cursor = db.query("SAVED_EXERCISES", cols, "space = " + game.space + " AND operand = '" + op + "'", null, null, null, null);

        if (cursor.moveToFirst()) {
            String operand = cursor.getString(cursor.getColumnIndex("operand"));
            int x = cursor.getInt(cursor.getColumnIndex("operator1"));
            int y = cursor.getInt(cursor.getColumnIndex("operator2"));
            savedExercise = new exerciseInstance(x,y,0,operand);
            db.delete("SAVED_EXERCISES","id = "+cursor.getInt(cursor.getColumnIndex("id")), null);
            return savedExercise;
        }
        return game.createNewExercise();
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
        final String name = sharedPref.getString("weight",null);

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
                if(name == null){
                    startResultActivity("");
                } else {
                    startResultActivity(name);
                }
            }
        });
        builder.show();
    }

}
