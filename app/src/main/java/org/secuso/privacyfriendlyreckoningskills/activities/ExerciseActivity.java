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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.pfacore.model.dialog.ValueSelectionDialog;
import org.secuso.pfacore.ui.dialog.DialogKt;
import org.secuso.privacyfriendlyreckoningskills.databinding.DialogPlayerNameBinding;

import org.secuso.privacyfriendlyreckoningskills.R;
import org.secuso.privacyfriendlyreckoningskills.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyreckoningskills.exerciseInstance;
import org.secuso.privacyfriendlyreckoningskills.gameInstance;
import org.secuso.privacyfriendlyreckoningskills.PFApplicationData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import kotlin.Unit;

public class ExerciseActivity extends AppCompatActivity {

    private static final int MAX_EXERCISE_GENERATION_ATTEMPTS = 100;
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
        if(!game.add)addsign.setTextColor(ContextCompat.getColor(this, R.color.middlegrey)); else
            addsign.setTextColor(ContextCompat.getColor(this, R.color.red));
        if(!game.sub)subsign.setTextColor(ContextCompat.getColor(this, R.color.middlegrey)); else
            subsign.setTextColor(ContextCompat.getColor(this, R.color.green));
        if(!game.mul)mulsign.setTextColor(ContextCompat.getColor(this, R.color.middlegrey)); else
            mulsign.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
        if(!game.div)divsign.setTextColor(ContextCompat.getColor(this, R.color.middlegrey)); else
            divsign.setTextColor(ContextCompat.getColor(this, R.color.lightblue));

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
                String name = PFApplicationData.instance(this).defaultPlayerNameOrNull();
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
                            input.setTextColor(ContextCompat.getColor(this, R.color.green));
                            s = s + "" + " \u2713";
                        } else {
                            if (PFApplicationData.instance(this).isCorrectAnswerEnabled()) {
                                s = s + " (" + exercise.solve() + ")";
                                input.setTextColor(ContextCompat.getColor(this, R.color.red));
                            } else {
                                input.setTextColor(ContextCompat.getColor(this, R.color.red));
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
                PFApplicationData appData = PFApplicationData.instance(this);

                if (appData.isDirectFeedbackEnabled()) {
                    exercise.z = inputtemp;
                    if(!exercise.pausedOn) {
                        game.putExercise2(exercise);
                    }
                    String s = ""+exercise.z;
                    if(exercise.z == exercise.solve()){
                        input.setTextColor(ContextCompat.getColor(this, R.color.green));
                        s = s + "" + " \u2713";
                    } else {
                        if (appData.isCorrectAnswerEnabled()) {
                            s = s + " (" + exercise.solve() + ")";
                            input.setTextColor(ContextCompat.getColor(this, R.color.red));
                        } else {
                            input.setTextColor(ContextCompat.getColor(this, R.color.red));
                        }
                    }
                    input.setText(s);

                    if(exercise.pausedOn) {
                        miliElapsed =SystemClock.elapsedRealtime();
                        timer.setBase(SystemClock.elapsedRealtime() - game.timeElapsed);
                        timer.start();
                        game.exercises.get(game.exercises.size()-1).pausedOn = false;
                        input.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
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
                        input.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
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
                String name = PFApplicationData.instance(this).defaultPlayerNameOrNull();
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

    @SuppressLint("Range")
    private exerciseInstance newExercise() {
        String op = game.randomOperator();

        boolean excludeZeroAndOne =
                PFApplicationData.instance(this)
                        .shouldExcludeZeroAndOne();

        PFASQLiteHelper helper = new PFASQLiteHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = new String[]{
                "id",
                "operator1",
                "operator2",
                "operand",
                "space"
        };

        String selection = "space = ? AND operand = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(game.space),
                op
        };

        try (Cursor cursor = db.query(
                "SAVED_EXERCISES",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        )) {
            /*
             * Search all saved exercises for the selected operation.
             * Exercises containing 0 or 1 are skipped while the option
             * is enabled, but they remain stored for later use.
             */
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndex("id")
                );

                String operand = cursor.getString(
                        cursor.getColumnIndex("operand")
                );

                int x = cursor.getInt(
                        cursor.getColumnIndex("operator1")
                );

                int y = cursor.getInt(
                        cursor.getColumnIndex("operator2")
                );

                exerciseInstance savedExercise =
                        new exerciseInstance(x, y, 0, operand);

                if (
                        !excludeZeroAndOne ||
                                !containsZeroOrOne(savedExercise)
                ) {
                    db.delete(
                            "SAVED_EXERCISES",
                            "id = ?",
                            new String[]{
                                    String.valueOf(id)
                            }
                    );

                    return savedExercise;
                }
            }
        } finally {
            helper.close();
        }

        return createGeneratedExercise(excludeZeroAndOne);
    }
    /**
     * Generates an exercise that respects the zero-and-one setting.
     */
    private exerciseInstance createGeneratedExercise(
            boolean excludeZeroAndOne
    ) {
        exerciseInstance generatedExercise =
                game.createNewExercise();

        int attempts = 1;

        while (
                excludeZeroAndOne &&
                        containsZeroOrOne(generatedExercise) &&
                        attempts < MAX_EXERCISE_GENERATION_ATTEMPTS
        ) {
            generatedExercise = game.createNewExercise();
            attempts++;
        }

        /*
         * A valid exercise should normally be generated after only a
         * few attempts. This fallback prevents a theoretical endless
         * generation loop.
         */
        if (
                excludeZeroAndOne &&
                        containsZeroOrOne(generatedExercise)
        ) {
            return createFallbackExercise();
        }

        return generatedExercise;
    }
    /**
     * Checks whether an exercise contains zero or one as an operand
     * or as its result.
     */
    private boolean containsZeroOrOne(
            exerciseInstance exercise
    ) {
        int result = exercise.solve();

        return exercise.x == 0 ||
                exercise.x == 1 ||
                exercise.y == 0 ||
                exercise.y == 1 ||
                result == 0 ||
                result == 1;
    }
    /**
     * Creates a guaranteed valid exercise without zero or one.
     */
    private exerciseInstance createFallbackExercise() {
        String operator = game.randomOperator();

        switch (operator) {
            case "-":
                return new exerciseInstance(
                        4,
                        2,
                        0,
                        operator
                );

            case "/":
                return new exerciseInstance(
                        4,
                        2,
                        0,
                        operator
                );

            case "*":
            case "+":
            default:
                return new exerciseInstance(
                        2,
                        2,
                        0,
                        operator
                );
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

    /**
     * Displays a PFA-Core dialog for entering the high-score player name.
     */
    private void displayNameInput() {
        final String defaultName =
                PFApplicationData.instance(this)
                        .defaultPlayerNameOrNull();

        final SharedPreferences highscorePreferences =
                getSharedPreferences(
                        "pfa-math-highscore",
                        Context.MODE_PRIVATE
                );

        final String initialName;

        if (defaultName != null) {
            initialName = defaultName;
        } else {
            initialName = highscorePreferences.getString(
                    "previousname",
                    ""
            );
        }

        ValueSelectionDialog<String> dialog =
                ValueSelectionDialog.Companion.<String>build(
                        (AppCompatActivity) this,
                        builder -> {
                            builder.setTitle(
                                    () -> getString(R.string.alert_title)
                            );

                            builder.setAcceptLabel(
                                    getString(android.R.string.ok)
                            );

                            builder.setAbortLabel(
                                    getString(android.R.string.cancel)
                            );

                            builder.setRequired(false);

                            /*
                             * Prevents the abort callback from also being
                             * called after a successful confirmation.
                             */
                            builder.setHandleDismiss(false);

                            builder.setOnConfirmation(enteredName -> {
                                String name = enteredName == null
                                        ? ""
                                        : enteredName.trim();

                                highscorePreferences.edit()
                                        .putString("previousname", name)
                                        .apply();

                                startResultActivity(name);

                                return Unit.INSTANCE;
                            });

                            builder.setOnAbort(() -> {
                                startResultActivity(
                                        defaultName == null
                                                ? ""
                                                : defaultName
                                );

                                return Unit.INSTANCE;
                            });

                            return Unit.INSTANCE;
                        }
                );

        DialogKt.show(
                DialogKt.content(
                        dialog,

                        () -> {
                            DialogPlayerNameBinding binding =
                                    DialogPlayerNameBinding.inflate(
                                            getLayoutInflater()
                                    );

                            binding.playerNameInput.setText(initialName);
                            binding.playerNameInput.setSelection(
                                    initialName.length()
                            );

                            return binding;
                        },

                        binding -> {
                            CharSequence enteredText =
                                    binding.playerNameInput.getText();

                            return enteredText == null
                                    ? ""
                                    : enteredText.toString().trim();
                        }
                )
        );
    }

}
