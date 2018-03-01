package org.secuso.privacyfriendlyreckoningskills;

import java.io.Serializable;

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
public class exerciseInstance implements Serializable {

    public exerciseInstance(int x_,int y_,int z_,String o_){
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
    public Boolean revisit = false;
    public Boolean pausedOn = false;

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
