package org.secuso.privacyfriendlymath;

import java.io.Serializable;

public class exerciseInstance implements Serializable {

    exerciseInstance(int x_,int y_,int z_,String o_){
        x=x_;
        y=y_;
        z=z_;
        o=o_;
    }

    public int x = 0;
    public int y = 0;
    public int z = 0;
    //operator
    public String o = "+";

    public int solve(){
        switch (o) {
            case "+": return (x + y);
            case "-": return (x - y);
            case "*": return (x * y);
            case "/": return (x / y);
        }
        return 0;
    }

}
