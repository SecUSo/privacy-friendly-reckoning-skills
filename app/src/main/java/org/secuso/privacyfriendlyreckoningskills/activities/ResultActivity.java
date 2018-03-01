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

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlyreckoningskills.R;
import org.secuso.privacyfriendlyreckoningskills.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyreckoningskills.exerciseInstance;
import org.secuso.privacyfriendlyreckoningskills.gameInstance;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    TextView space;
    TextView score;
    TextView solved;
    TextView addSign;
    TextView subSign;
    TextView mulSign;
    TextView divSign;

    gameInstance game;

    Boolean newHighScore;
    String playerName;

    ArrayList<TextView> resultTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        SharedPreferences hs = this.getSharedPreferences("pfa-math-highscore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = hs.edit();
        editor.putBoolean("continue", false);
        editor.commit();

        game = (gameInstance) getIntent().getSerializableExtra("game");
        newHighScore = getIntent().getBooleanExtra("highScoreAchieved",false);
        playerName = getIntent().getStringExtra("name");

        //Ui
        space = (TextView) findViewById(R.id.space);
        score = (TextView) findViewById(R.id.score);
        solved = (TextView) findViewById(R.id.solved);
        space.setText("Up to \n" + game.getUpperBound());
        addSign = (TextView) findViewById(R.id.sign_add);
        subSign = (TextView) findViewById(R.id.sign_sub);
        mulSign = (TextView) findViewById(R.id.sign_mul);
        divSign = (TextView) findViewById(R.id.sign_div);

        //fab
        FloatingActionButton myFab = (FloatingActionButton)  this.findViewById(R.id.myFAB);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveSelectedExercises();
                updateStats();
                updateScore(playerName);
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        if(!game.add) addSign.setTextColor(getResources().getColor(R.color.middlegrey));
        if(!game.sub) subSign.setTextColor(getResources().getColor(R.color.middlegrey));
        if(!game.mul) mulSign.setTextColor(getResources().getColor(R.color.middlegrey));
        if(!game.div) divSign.setTextColor(getResources().getColor(R.color.middlegrey));

        resultTexts = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            TextView exercise = new TextView(this);
            TextView solution = new TextView(this);
            exercise.setTextSize(24);
            solution.setTextSize(24);
            exercise.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            solution.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            exercise.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if(game.exercises.get(i).revisit) {
                exercise.setTextColor(getResources().getColor(R.color.middlegrey));
            } else {
                exercise.setTextColor(getResources().getColor(R.color.lightblue));
            }
            solution.setTextColor(getResources().getColor(R.color.red));

            exercise.setText(game.exercises.get(i).x + " " + game.exercises.get(i).o + " " + game.exercises.get(i).y + " = " + game.exercises.get(i).z);
            if(game.exercises.get(i).solve() == game.exercises.get(i).z){
                solution.setText("\u2713");
                solution.setTextColor(getResources().getColor(R.color.green));
            } else {
                String answer =""+game.exercises.get(i).z;
                final SpannableStringBuilder sb = new SpannableStringBuilder(exercise.getText());
                final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.red));
                sb.setSpan(fcs, exercise.getText().length()-answer.length(), exercise.getText().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                solution.setText(""+game.exercises.get(i).solve());
                exercise.setText(sb);
            }

            exercise.setId(i);
            exercise.setOnClickListener(this);

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.exercises);
            LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.solutions);
            linearLayout.addView(exercise);
            linearLayout2.addView(solution);

            resultTexts.add(exercise);
        }

        if(newHighScore) {
            score.setText(getResources().getString(R.string.result_score) + " " + game.score + " " + getResources().getString(R.string.result_highscore));
        } else {
            score.setText(getResources().getString(R.string.result_score) + " " + game.score);
        }
        solved.setText(getResources().getString(R.string.result_solved) + " " + game.answeredCorrectly() + " "+ getResources().getString(R.string.result_solved_of) + " 10");
    }

    @Override
    public void onBackPressed() {
        updateStats();
        updateScore(playerName);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        for(int i = 0; i < game.exercisesSolved(); i++){
            if((v.getId() == resultTexts.get(i).getId()) && (game.exercises.get(i).solve() != game.exercises.get(i).z)){
                if(!game.exercises.get(i).revisit) {
                    resultTexts.get(i).setTextColor(getResources().getColor(R.color.middlegrey));
                    game.exercises.get(i).revisit = true;
                    Toast.makeText(this, getResources().getString(R.string.result_toast_selected),
                            Toast.LENGTH_SHORT).show();
                } else {
                    resultTexts.get(i).setTextColor(getResources().getColor(R.color.lightblue));
                    game.exercises.get(i).revisit = false;
                }
            }
        }
    }

    private void saveSelectedExercises(){
        PFASQLiteHelper helper = new PFASQLiteHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        for(int i = 0; i < game.exercisesSolved(); i++){
            if(game.exercises.get(i).revisit) {
                ContentValues values = new ContentValues();
                values.put("operator1",game.exercises.get(i).x);
                values.put("operator2",game.exercises.get(i).y);
                values.put("operand",game.exercises.get(i).o);
                values.put("space",game.space);
                db.insert("SAVED_EXERCISES",null,values);
            }
        }
    }

    private void updateStats(){

        int rightAnswersAdd = 0;
        int rightAnswersSub = 0;
        int rightAnswersMul = 0;
        int rightAnswersDiv = 0;
        int wrongAnswersAdd = 0;
        int wrongAnswersSub = 0;
        int wrongAnswersMul = 0;
        int wrongAnswersDiv = 0;


        for(exerciseInstance e: game.exercises){
            switch(e.o) {
                case "+":
                    if(e.solve()==e.z) rightAnswersAdd++; else wrongAnswersAdd++; break;
                case "-":
                    if(e.solve()==e.z) rightAnswersSub++; else wrongAnswersSub++; break;
                case "*":
                    if(e.solve()==e.z) rightAnswersMul++; else wrongAnswersMul++; break;
                case "/":
                    if(e.solve()==e.z) rightAnswersDiv++; else wrongAnswersDiv++; break;
            }
        }

        SharedPreferences hs = this.getSharedPreferences("pfa-math-highscore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = hs.edit();
        editor.putInt("rightadd"+game.space,hs.getInt("rightadd"+game.space,0)+rightAnswersAdd);
        editor.putInt("wrongadd"+game.space,hs.getInt("wrongadd"+game.space,0)+wrongAnswersAdd);
        editor.putInt("rightsub"+game.space,hs.getInt("rightsub"+game.space,0)+rightAnswersSub);
        editor.putInt("wrongsub"+game.space,hs.getInt("wrongsub"+game.space,0)+wrongAnswersSub);
        editor.putInt("rightmul"+game.space,hs.getInt("rightmul"+game.space,0)+rightAnswersMul);
        editor.putInt("wrongmul"+game.space,hs.getInt("wrongmul"+game.space,0)+wrongAnswersMul);
        editor.putInt("rightdiv"+game.space,hs.getInt("rightdiv"+game.space,0)+rightAnswersDiv);
        editor.putInt("wrongdiv"+game.space,hs.getInt("wrongdiv"+game.space,0)+wrongAnswersDiv);
        editor.commit();
    }

    private void updateScore(String name){
        SharedPreferences hs = this.getSharedPreferences("pfa-math-highscore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = hs.edit();

        //saved values
        String[] highscoreNames = new String[5];
        String[] highscoreScores = new String[5];
        for(int i = 0; i <5; i++){
            highscoreNames[i] = hs.getString("hsname"+i+game.space,null);
            highscoreScores[i] = hs.getString("hsscore"+i+game.space,null);
        }

        //update values
        Boolean updated = false;
        String prevScore = "0";
        String prevName = "N";
        for(int i = 0; i < 5; i++){
            if(!updated && (highscoreScores[i] != null) && game.score >= Integer.parseInt(highscoreScores[i])){
                prevScore = highscoreScores[i];
                prevName = highscoreNames[i];
                highscoreScores[i] = ""+game.score;
                highscoreNames[i] = name;
                updated = true;
            } else if(!updated && highscoreScores[i] == null){
                highscoreScores[i] = ""+game.score;
                highscoreNames[i] = name;
                break;
            } else if(updated && highscoreScores[i] == null){
                highscoreScores[i] = prevScore;
                highscoreNames[i] = prevName;
                break;
            } else if(updated){
                String tempScore = prevScore;
                String tempName = prevName;
                prevScore = highscoreScores[i];
                prevName = highscoreNames[i];
                highscoreScores[i] = tempScore;
                highscoreNames[i] = tempName;
            }
        }

        //save new values
        for(int i = 0; i <5; i++){
            if(highscoreScores[i] != null){
                editor.putString("hsscore"+i+game.space, highscoreScores[i]);
                editor.putString("hsname"+i+game.space, highscoreNames[i]);
                editor.commit();
            }
        }
    }
}


