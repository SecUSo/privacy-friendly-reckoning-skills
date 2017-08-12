package org.secuso.privacyfriendlymath;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class gameInstance implements Serializable {
    
    public int score = 0;
    public int space = 0;
    public Boolean mul,add,sub,div = true;

    //do this properly
    public ArrayList<exerciseInstance> exercises = new ArrayList<>();

    public void putExercise(int x, int y, int z, String op){
        exercises.add(new exerciseInstance(x,y,z,op));
    }

    public int exercisesSolved(){
        return exercises.size();
    }

    public int calculateScore(int seconds){
        score =(int) ((1.0 + (1.0/100.0) + Math.max(100-seconds,0)) * (answeredCorrectly())*answeredCorrectly());
        return score;
    }

    public int answeredCorrectly(){
        int c = 0;
        for(exerciseInstance e : exercises){
             if(e.solve() == e.z) c++;
        }
        return c;
    }

    public exerciseInstance createNewExercise(){

        Random rand = new Random();
        int randomX = 0;
        int randomY = 0;

        ArrayList<String> choose = new ArrayList<>();
        if(add) choose.add("+");
        if(sub) choose.add("-");
        if(mul) choose.add("*");
        if(div) choose.add("/");

        String op =  choose.get(rand.nextInt(((choose.size()-1) - 0) + 1) + 0);

        switch(op){
            case "+":
                randomX = rand.nextInt(((getUpperBound() - getLowerBound()) - getLowerBound()) + 1) + getLowerBound();
                randomY = rand.nextInt(((getUpperBound() -randomX) - getLowerBound()) + 1) + getLowerBound();
                break;
            case "-":
                randomX = rand.nextInt((getUpperBound() - getLowerBound() *2) + 1) + getLowerBound() *2;
                randomY = rand.nextInt(((randomX- getLowerBound()) - getLowerBound()) + 1) + getLowerBound();
                break;
            case "*":

                float f = rand.nextFloat(); //incl0.0f,excl.1.0f

                if(f <= 0.9) {
                    f = (float) Math.tanh(f) / 2;
                } else {
                    f = (float) rand.nextFloat();
                    f = f * (1.0f-0.35f)+0.35f;
                }
                randomX = (int) Math.round(f* getUpperBound());
                int up;
                if(randomX == 0) up = getUpperBound(); else
                    up = (int) Math.floor(getUpperBound() /randomX);

                float f2 = rand.nextFloat();
                f2 = (float) (Math.pow(f2,1.0/3.0));
                randomY = (int) Math.round(f2*up);

                break;
            case "/":
                randomX = rand.nextInt((getUpperBound() - getLowerBound())+1) + getLowerBound();
                int lb = getLowerBound();
                if(lb == 0) lb = 1;

                randomY = 1;
                ArrayList<Integer> divisors = new ArrayList<Integer>();
                if(randomX == 0) {
                    divisors.add(rand.nextInt((getUpperBound() - lb)+1) + lb);
                } else {
                    for (int i = lb; i <= randomX; i++) {
                        if (randomX % i == 0) {
                            divisors.add(i);
                        }
                    }
                }
                int fint = rand.nextInt(((divisors.size()-1) - 0) + 1) + 0;
                float fdiv = rand.nextFloat();
                //fdiv = (float) Math.pow(fdiv,2.0);
                randomY = divisors.get(Math.round(fdiv*fint));

                break;
        }

        exerciseInstance e = new exerciseInstance(randomX,randomY,0,op.toString());
        return e;

    }

    public int getUpperBound(){
        switch (space){
            case 3:
                return 10000;
            case 2:
                return 1000;
            case 1:
                return 100;
            default:
                return 10;
        }
    }

    public int getLowerBound(){
        switch (space){
            case 3:
                return 1000;
            case 2:
                return 100;
            case 1:
                return 10;
            default:
                return 0;
        }
    }

}
