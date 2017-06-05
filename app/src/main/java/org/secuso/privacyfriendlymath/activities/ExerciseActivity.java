package org.secuso.privacyfriendlymath.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlymath.R;

import java.util.Random;
import java.util.ArrayList;

class NumberSpace {
    int upperBound;
    int lowerBound;
}

enum Op {
    ADD,SUB,MUL,DIV;
    @Override
    public String toString() {
        String str;
        switch (this.name()) {
            case "ADD": str = "+"; break;
            case "SUB": str = "-"; break;
            case "MUL": str = "*"; break;
            case "DIV": str = "/"; break;
            default:
                str = "+";
                break;
        }
        return str;
    }
}

public class ExerciseActivity extends AppCompatActivity {

    StringBuilder sb = new StringBuilder();
    TextView textInput;
    TextView textX;
    TextView textY;
    TextView operator;
    TextView feedback;

    Op activeOperator  = Op.ADD;
    NumberSpace nS = new NumberSpace();

    boolean mul,add,sub,div = true;
    int x,y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        textInput = (TextView) findViewById(R.id.inputfield);
        textX = (TextView) findViewById(R.id.valuex);
        textY = (TextView) findViewById(R.id.valuey);
        operator = (TextView) findViewById(R.id.operator);
        feedback = (TextView) findViewById(R.id.feedback);


        add = getIntent().getBooleanExtra("add",true);
        sub = getIntent().getBooleanExtra("sub",true);
        mul = getIntent().getBooleanExtra("mul",true);
        div = getIntent().getBooleanExtra("div",true);
        int space = getIntent().getIntExtra("space",1);

        switch (space){
            case 3:
                nS.upperBound = 10000;
                nS.lowerBound = 1000;
                break;
            case 2:
                nS.upperBound = 1000;
                nS.lowerBound = 100;
                break;
            case 1:
                nS.upperBound = 100;
                nS.lowerBound = 10;
                break;
            default:
                nS.upperBound = 10;
                nS.lowerBound = 1;
                break;
        }

        this.createNewExercise();

    }


    private void createNewExercise(){

        Random rand = new Random();
        int randomX = 0;
        int randomY = 0;

        ArrayList<Op> choose = new ArrayList<Op>();
        if(add) choose.add(Op.ADD);
        if(sub) choose.add(Op.SUB);
        if(mul) choose.add(Op.MUL);
        if(div) choose.add(Op.DIV);

        activeOperator =  choose.get(rand.nextInt(((choose.size()-1) - 0) + 1) + 0);

        switch(activeOperator){
            case ADD:
                randomX = rand.nextInt(((nS.upperBound -nS.lowerBound) - nS.lowerBound) + 1) + nS.lowerBound;
                randomY = rand.nextInt(((nS.upperBound -randomX) - nS.lowerBound) + 1) + nS.lowerBound;
                break;
            case SUB:
                randomX = rand.nextInt((nS.upperBound - nS.lowerBound *2) + 1) + nS.lowerBound *2;
                randomY = rand.nextInt(((randomX-nS.lowerBound) - nS.lowerBound) + 1) + nS.lowerBound;
                break;
            case MUL:

                float f = rand.nextFloat(); //incl0.0f,excl.1.0f

                if(f <= 0.9) {
                    f = (float) Math.tanh(f) / 2;
                } else {
                    f = (float) rand.nextFloat();
                    f = f * (1.0f-0.35f)+0.35f;
                }
                randomX = (int) Math.round(f* nS.upperBound);
                int up;
                if(randomX == 0) up =nS.upperBound; else
                up = (int) Math.floor(nS.upperBound /randomX);

                float f2 = rand.nextFloat();
                f2 = (float) (Math.pow(f2,1.0/3.0));
                randomY = (int) Math.round(f2*up);

                break;
            case DIV:
                randomX = rand.nextInt((nS.upperBound - nS.lowerBound)+1) + nS.lowerBound;

                randomY = 1;
                ArrayList<Integer> divisors = new ArrayList<Integer>();
                for(int i = nS.lowerBound; i <= randomX; i++){
                    if(randomX % i == 0){
                        divisors.add(i);
                    }
                }
                int fint = rand.nextInt(((divisors.size()-1) - 0) + 1) + 0;
                float fdiv = rand.nextFloat();
                fdiv = (float) Math.pow(fdiv,2.0);
                randomY = divisors.get(Math.round(fdiv*fint));

                break;
        }

        x = randomX;
        y = randomY;
        textX.setText(Integer.toString(x));
        textY.setText(Integer.toString(y));
        operator.setText(activeOperator.toString());

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

        textInput.setText(sb.toString());
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
        boolean solution = false;
        switch (activeOperator) {
            case ADD: solution = ((x + y) == input) ? true: false; break;
            case SUB: solution = ((x - y) == input) ? true: false; break;
            case MUL: solution = ((x * y) == input) ? true: false; break;
            case DIV: solution = ((x / y) == input) ? true: false; break;
        }
        sb.setLength(0);
        sb.append("0");
        if (solution) feedback.setText(getResources().getString(R.string.feedback_positive)); else feedback.setText(getResources().getString(R.string.feedback_negative));
        createNewExercise();
    }

}
