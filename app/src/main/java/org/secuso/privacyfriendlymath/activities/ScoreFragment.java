package org.secuso.privacyfriendlymath.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlymath.R;

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

            TextView tmp;
            Context context = getActivity();
            SharedPreferences sharedPref = context.getSharedPreferences(
                    "pfa-math-highscore", Context.MODE_PRIVATE);

            TextView add = (TextView) getView().findViewById(R.id.stat_add);
            TextView sub = (TextView) getView().findViewById(R.id.stat_sub);
            TextView mul = (TextView) getView().findViewById(R.id.stat_mul);
            TextView div = (TextView) getView().findViewById(R.id.stat_div);

            add.setText(""+(int)(100.0/(((float)sharedPref.getInt("rightadd"+space,1)+(float)sharedPref.getInt("wrongadd"+space,1))/(float)sharedPref.getInt("rightadd"+space,1)))+"%");
            sub.setText(""+(int)(100.0/(((float)sharedPref.getInt("rightsub"+space,1)+(float)sharedPref.getInt("wrongsub"+space,1))/(float)sharedPref.getInt("rightsub"+space,1)))+"%");
            mul.setText(""+(int)(100.0/(((float)sharedPref.getInt("rightmul"+space,1)+(float)sharedPref.getInt("wrongmul"+space,1))/(float)sharedPref.getInt("rightmul"+space,1)))+"%");
            div.setText(""+(int)(100.0/(((float)sharedPref.getInt("rightdiv"+space,1)+(float)sharedPref.getInt("wrongdiv"+space,1))/(float)sharedPref.getInt("rightdiv"+space,1)))+"%");

            for (int i = 0; i < 5; i++) {
                String name = sharedPref.getString("hsname"+i+space,null);
                String score = sharedPref.getString("hsscore"+i+space,null);

                if(name == null){
                    break;
                } else {
                    tmp = new TextView(getContext());
                    TextView tmp2 = new TextView(getContext());
                    tmp.setTextSize(18);
                    tmp2.setTextSize(18);
                    tmp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tmp2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    tmp.setText(name + "\n");
                    tmp2.setText(score + " " + getResources().getString(R.string.score_credit) + "\n");
                    tmp.setTextColor(getResources().getColor(R.color.lightblue));
                    tmp2.setTextColor(getResources().getColor(R.color.lightblue));

                    tmp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tmp2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scorelist);
                    LinearLayout linearLayout2 = (LinearLayout) getView().findViewById(R.id.scorelist2);
                    linearLayout.addView(tmp);
                    linearLayout2.addView(tmp2);
                }
            }
    }

}
