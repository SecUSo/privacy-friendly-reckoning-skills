package org.secuso.privacyfriendlyreckoningskills;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class gameInstance implements Serializable {
    
    public int score = 0;
    public int space = 0;
    public Boolean mul,add,sub,div = true;
    public long timeElapsed = 0;

    public int e_x = 0;
    public int e_y = 0;
    public int e_z = 0;
    public String e_input = "0";
    public String e_op = "+";
    public Boolean e_commited = false;
    public Boolean e_paused = false;
    public Boolean gameFinished = false;

    public ArrayList<exerciseInstance> exercises = new ArrayList<>();

    public void putExercise(int x, int y, int z, String op){
        exercises.add(new exerciseInstance(x,y,z,op));
    }

    public void putExercise2(exerciseInstance e){
        exercises.add(e);
    }

    public int exercisesSolved(){
        return exercises.size();
    }

    public int calculateScore(int seconds){
        score =(int) ((1.0 + (1.0/100.0) * Math.max(100-seconds,0)) * (answeredCorrectly())*answeredCorrectly());
        return score;
    }

    public int answeredCorrectly(){
        int c = 0;
        for(exerciseInstance e : exercises){
             if(e.solve() == e.z) c++;
        }
        return c;
    }

    public String randomOperator(){
        Random rand = new Random();
        ArrayList<String> choose = new ArrayList<>();
        if(add) choose.add("+");
        if(sub) choose.add("-");
        if(mul) choose.add("*");
        if(div) choose.add("/");
        return choose.get(rand.nextInt(((choose.size()-1) - 0) + 1) + 0);
    }

    public exerciseInstance createNewExercise(){

        Random rand = new Random();
        int randomX = 0;
        int randomY = 0;

        String op =  randomOperator();

        switch(op){
            case "+":
                int lowerboundadd = 0;
                randomX = rand.nextInt(getUpperBound() + 1 - lowerboundadd) + lowerboundadd;
                randomY = rand.nextInt((getUpperBound() - randomX) + 1 - lowerboundadd) + lowerboundadd;
                break;
            case "-":
                int lowerboundsub = 0;
                randomX = rand.nextInt((getUpperBound() + 1 - lowerboundsub)) + lowerboundsub;
                randomY = rand.nextInt((randomX + 1 - lowerboundsub)) + lowerboundsub;
                break;
            case "*":

                float f = rand.nextFloat(); //incl0.0f,excl.1.0f
                if(f <= 0.9) {
                    f = (float) Math.tanh(f) / 2;
                } else {
                    f = rand.nextFloat();
                    f = f * (1.0f-0.35f)+0.35f;
                }

                randomX = Math.round(f * getUpperBound());
                int up;
                if(randomX == 0)
                    up = getUpperBound();
                else up = (int) Math.floor(getUpperBound() /randomX);

                float f2 = rand.nextFloat();
                f2 = (float) (Math.pow(f2,1.0/3.0));
                randomY = Math.round(f2*up);

                break;
            case "/":
                randomX = rand.nextInt(getUpperBound() + 1 - 0) + 0;
                int lb = 1;

                ArrayList<Integer> divisors = new ArrayList<>();
                if(randomX == 0) {
                    divisors.add(rand.nextInt(getUpperBound() - lb + 1) + lb);
                } else {
                    for (int i = lb; i <= randomX; i++) {
                        if (randomX % i == 0) {
                            divisors.add(i);
                        }
                    }
                }
                int fint = rand.nextInt((divisors.size()-1) +1 - 0) + 0;
                //float fdiv = rand.nextFloat();
                //fdiv = (float) Math.pow(fdiv,2.0);
                randomY = divisors.get(fint);

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
