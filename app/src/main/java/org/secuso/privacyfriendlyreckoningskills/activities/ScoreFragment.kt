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
package org.secuso.privacyfriendlyreckoningskills.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.secuso.privacyfriendlyreckoningskills.R

class ScoreFragment : Fragment() {
    var space: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        space = requireArguments().getInt("space", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref =
            requireActivity().getSharedPreferences("pfa-math-highscore", Context.MODE_PRIVATE)

        val add = requireView().findViewById<TextView>(R.id.stat_add)
        val sub = requireView().findViewById<TextView>(R.id.stat_sub)
        val mul = requireView().findViewById<TextView>(R.id.stat_mul)
        val div = requireView().findViewById<TextView>(R.id.stat_div)
        val names = requireView().findViewById<LinearLayout>(R.id.namelist)
        val scores = requireView().findViewById<LinearLayout>(R.id.scorelist)

        add.text = "" + perc(
            sharedPref.getInt("rightadd$space", 0),
            sharedPref.getInt("rightadd$space", 0) + sharedPref.getInt("wrongadd$space", 0)
        ) + "%"
        sub.text = "" + perc(
            sharedPref.getInt("rightsub$space", 0),
            sharedPref.getInt("rightsub$space", 0) + sharedPref.getInt("wrongsub$space", 0)
        ) + "%"
        mul.text = "" + perc(
            sharedPref.getInt("rightmul$space", 0),
            sharedPref.getInt("rightmul$space", 0) + sharedPref.getInt("wrongmul$space", 0)
        ) + "%"
        div.text = "" + perc(
            sharedPref.getInt("rightdiv$space", 0),
            sharedPref.getInt("rightdiv$space", 0) + sharedPref.getInt("wrongdiv$space", 0)
        ) + "%"

        for (i in 0..4) {
            val name = sharedPref.getString("hsname$i$space", null)
            val score = sharedPref.getString("hsscore$i$space", null)

            if (name == null) {
                break
            } else {
                val nameEntry = TextView(context)
                val scoreEntry = TextView(context)
                nameEntry.textSize = 18f
                scoreEntry.textSize = 18f
                nameEntry.textAlignment = View.TEXT_ALIGNMENT_CENTER
                scoreEntry.textAlignment = View.TEXT_ALIGNMENT_CENTER
                nameEntry.setTextColor(ContextCompat.getColor(requireContext(), R.color.lightblue))
                scoreEntry.setTextColor(ContextCompat.getColor(requireContext(), R.color.lightblue))

                nameEntry.text = name + "\n"
                scoreEntry.text = score + " " + resources.getString(R.string.score_credit) + "\n"

                names.addView(nameEntry)
                scores.addView(scoreEntry)
            }
        }
    }

    fun perc(x: Int, y: Int): Int {
        if (y == 0) {
            return 0
        }
        return ((x * 100.0f) / y).toInt()
    }
}
