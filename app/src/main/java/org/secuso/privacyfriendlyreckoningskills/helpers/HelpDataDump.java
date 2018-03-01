package org.secuso.privacyfriendlyreckoningskills.helpers;

import android.content.Context;

import org.secuso.privacyfriendlyreckoningskills.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class structure taken from tutorial at http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * last access 27th October 2016
 */

public class HelpDataDump {

    private Context context;

    public HelpDataDump(Context context) {
        this.context = context;
    }

    public LinkedHashMap<String, List<String>> getDataGeneral() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();

        List<String> general = new ArrayList<String>();
        general.add(context.getResources().getString(R.string.help_whatis_answer));

        expandableListDetail.put(context.getResources().getString(R.string.help_whatis), general);

        List<String> game = new ArrayList<String>();
        game.add(context.getResources().getString(R.string.help_game_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_game), game);

        List<String> pause = new ArrayList<String>();
        pause.add(context.getResources().getString(R.string.help_pause_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_pause), pause);

        List<String> revisit = new ArrayList<String>();
        revisit.add(context.getResources().getString(R.string.help_revisit_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_revisit), revisit);

        List<String> score = new ArrayList<String>();
        score.add(context.getResources().getString(R.string.help_score_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_score), score);

        List<String> privacy = new ArrayList<String>();
        privacy.add(context.getResources().getString(R.string.help_privacy_answer));

        expandableListDetail.put(context.getResources().getString(R.string.help_privacy), privacy);

        List<String> permissions = new ArrayList<String>();
        permissions.add(context.getResources().getString(R.string.help_permission_answer));

        expandableListDetail.put(context.getResources().getString(R.string.help_permission), permissions);

        return expandableListDetail;
    }

}
