package org.secuso.privacyfriendlymath.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlymath.R;
import org.secuso.privacyfriendlymath.database.PFASQLiteHelper;
import org.secuso.privacyfriendlymath.exerciseInstance;
import org.secuso.privacyfriendlymath.gameInstance;

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
    String name;

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
        name = getIntent().getStringExtra("name");

        //Ui
        space = (TextView) findViewById(R.id.space);
        score = (TextView) findViewById(R.id.score);
        solved = (TextView) findViewById(R.id.solved);
        space.setText("Up to \n" + game.getUpperBound());
        addSign = (TextView) findViewById(R.id.sign_add);
        subSign = (TextView) findViewById(R.id.sign_sub);
        mulSign = (TextView) findViewById(R.id.sign_mul);
        divSign = (TextView) findViewById(R.id.sign_div);

        updateStats();
        updateScore(name);

        if(!game.add) addSign.setTextColor(getResources().getColor(R.color.middlegrey));
        if(!game.sub) subSign.setTextColor(getResources().getColor(R.color.middlegrey));
        if(!game.mul) mulSign.setTextColor(getResources().getColor(R.color.middlegrey));
        if(!game.div) divSign.setTextColor(getResources().getColor(R.color.middlegrey));

        resultTexts = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            TextView tmp = new TextView(this);
            TextView tmp2 = new TextView(this);
            tmp.setTextSize(24);
            tmp2.setTextSize(24);
            tmp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tmp2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tmp.setTextColor(getResources().getColor(R.color.lightblue));
            tmp.setTypeface(Typeface.MONOSPACE);

            tmp.setText(game.exercises.get(i).x + " " + game.exercises.get(i).o + " " + game.exercises.get(i).y + " = " + game.exercises.get(i).z);
            if(game.exercises.get(i).solve() == game.exercises.get(i).z){
                tmp2.setText("\u2713");
                tmp2.setTextColor(getResources().getColor(R.color.green));
            } else {
                tmp2.setText(""+game.exercises.get(i).solve());
                tmp2.setTextColor(getResources().getColor(R.color.lightblue));
                tmp.setTextColor(getResources().getColor(R.color.red));
            }

            tmp.setId(i);
            tmp.setOnClickListener(this);

            tmp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tmp.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.exercises);
            LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.exercises2);
            linearLayout.addView(tmp);
            linearLayout2.addView(tmp2);

            resultTexts.add(tmp);
        }

        if(newHighScore) {
            score.setText(getResources().getString(R.string.result_score) + " " + game.score + " " + getResources().getString(R.string.result_highscore));
        } else {
            score.setText(getResources().getString(R.string.result_score) + " " + game.score);
        }
        solved.setText(getResources().getString(R.string.result_solved) + " " + game.exercisesSolved() + " "+ getResources().getString(R.string.result_solved_of) + " 10");
    }

    @Override
    public void onClick(View v) {
        for(int i = 0; i < game.exercisesSolved(); i++){
            if((v.getId() == resultTexts.get(i).getId()) && (game.exercises.get(i).solve() != game.exercises.get(i).z) && !game.exercises.get(i).revisit){
                resultTexts.get(i).setTextColor(getResources().getColor(R.color.middlegrey));
                game.exercises.get(i).revisit = true;

                ContentValues values = new ContentValues();
                values.put("operator1",game.exercises.get(i).x);
                values.put("operator2",game.exercises.get(i).y);
                values.put("operand",game.exercises.get(i).o);
                values.put("space",game.space);

                PFASQLiteHelper helper = new PFASQLiteHelper(this);
                SQLiteDatabase db = helper.getWritableDatabase();
                db.insert("SAVED_EXERCISES",null,values);

            }
        }
    }

    private void updateStats(){

        int rightAnswersAdd = 0;
        int rightAnswersSub = 0;
        int rightAnswersMul = 0;
        int rightAnswersDiv = 0;

        for(exerciseInstance e: game.exercises){
            switch(e.o) {
                case "+":
                    if(e.solve()==e.z) rightAnswersAdd++; break;
                case "-":
                    if(e.solve()==e.z) rightAnswersSub++; break;
                case "*":
                    if(e.solve()==e.z) rightAnswersMul++; break;
                case "/":
                    if(e.solve()==e.z) rightAnswersDiv++; break;
            }
        }

        SharedPreferences hs = this.getSharedPreferences("pfa-math-highscore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = hs.edit();
        editor.putInt("rightadd"+game.space,hs.getInt("rightadd"+game.space,0)+rightAnswersAdd);
        editor.putInt("wrongadd"+game.space,hs.getInt("wrongadd"+game.space,0)+game.exercises.size() - rightAnswersAdd);
        editor.putInt("rightsub"+game.space,hs.getInt("rightsub"+game.space,0)+rightAnswersSub);
        editor.putInt("wrongsub"+game.space,hs.getInt("wrongsub"+game.space,0)+game.exercises.size() - rightAnswersSub);
        editor.putInt("rightmul"+game.space,hs.getInt("rightmul"+game.space,0)+rightAnswersMul);
        editor.putInt("wrongmul"+game.space,hs.getInt("wrongmul"+game.space,0)+game.exercises.size() - rightAnswersMul);
        editor.putInt("rightdiv"+game.space,hs.getInt("rightdiv"+game.space,0)+rightAnswersDiv);
        editor.putInt("wrongdiv"+game.space,hs.getInt("wrongdiv"+game.space,0)+game.exercises.size() - rightAnswersDiv);
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


