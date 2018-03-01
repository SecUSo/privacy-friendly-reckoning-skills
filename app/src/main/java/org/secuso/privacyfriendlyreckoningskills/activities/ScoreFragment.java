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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyreckoningskills.R;

public class ScoreFragment extends Fragment{

    int space;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        space = getArguments().getInt("space", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            SharedPreferences sharedPref = getActivity().getSharedPreferences("pfa-math-highscore", getActivity().MODE_PRIVATE);

            TextView add = getView().findViewById(R.id.stat_add);
            TextView sub = getView().findViewById(R.id.stat_sub);
            TextView mul = getView().findViewById(R.id.stat_mul);
            TextView div = getView().findViewById(R.id.stat_div);
            LinearLayout names = getView().findViewById(R.id.namelist);
            LinearLayout scores = getView().findViewById(R.id.scorelist);

            add.setText(""+perc(sharedPref.getInt("rightadd"+space,0),sharedPref.getInt("rightadd"+space,0)+sharedPref.getInt("wrongadd"+space,0))+"%");
            sub.setText(""+perc(sharedPref.getInt("rightsub"+space,0),sharedPref.getInt("rightsub"+space,0)+sharedPref.getInt("wrongsub"+space,0))+"%");
            mul.setText(""+perc(sharedPref.getInt("rightmul"+space,0),sharedPref.getInt("rightmul"+space,0)+sharedPref.getInt("wrongmul"+space,0))+"%");
            div.setText(""+perc(sharedPref.getInt("rightdiv"+space,0),sharedPref.getInt("rightdiv"+space,0)+sharedPref.getInt("wrongdiv"+space,0))+"%");

            for (int i = 0; i < 5; i++) {
                String name = sharedPref.getString("hsname"+i+space,null);
                String score = sharedPref.getString("hsscore"+i+space,null);

                if(name == null){
                    break;
                } else {
                    TextView nameEntry = new TextView(getContext());
                    TextView scoreEntry = new TextView(getContext());
                    nameEntry.setTextSize(18);
                    scoreEntry.setTextSize(18);
                    nameEntry.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    scoreEntry.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    nameEntry.setTextColor(getResources().getColor(R.color.lightblue));
                    scoreEntry.setTextColor(getResources().getColor(R.color.lightblue));

                    nameEntry.setText(name + "\n");
                    scoreEntry.setText(score + " " + getResources().getString(R.string.score_credit) + "\n");

                    names.addView(nameEntry);
                    scores.addView(scoreEntry);
                }
            }
    }

    int perc (int x, int y){
        if(y == 0){
            return 0;
        }
        //int i = (int)((x*100.0f) / y);
        return (int)((x*100.0f) / y);
    }

}
