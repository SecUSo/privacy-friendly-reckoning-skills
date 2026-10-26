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

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.secuso.privacyfriendlyreckoningskills.R
import org.secuso.privacyfriendlyreckoningskills.database.PFASQLiteHelper
import org.secuso.privacyfriendlyreckoningskills.gameInstance

class ResultActivity : AppCompatActivity(), View.OnClickListener {
    val space: TextView by lazy { findViewById<TextView>(R.id.space)}
    val score: TextView by lazy { findViewById<TextView>(R.id.score)}
    val solved: TextView by lazy { findViewById<TextView>(R.id.solved)}
    val addSign: TextView by lazy { findViewById<TextView>(R.id.sign_add)}
    val subSign: TextView by lazy { findViewById<TextView>(R.id.sign_sub)}
    val mulSign: TextView by lazy { findViewById<TextView>(R.id.sign_mul)}
    val divSign: TextView by lazy { findViewById<TextView>(R.id.sign_div)}
    var game: gameInstance? = null
    var newHighScore: Boolean = false
    var playerName: String? = null

    var resultTexts: ArrayList<TextView?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    updateStats()
                    updateScore(playerName)

                    val intent = Intent(
                        this@ResultActivity,
                        MainActivity::class.java
                    )

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }
            }
        )

        val hs = this.getSharedPreferences("pfa-math-highscore", MODE_PRIVATE)
        val editor = hs.edit()
        editor.putBoolean("continue", false)
        editor.commit()
        
        game = intent.getSerializableExtra("game") as gameInstance?
        newHighScore = intent.getBooleanExtra("highScoreAchieved", false)
        playerName = intent.getStringExtra("name")

        //Ui
        space.text = "Up to \n" + game!!.getUpperBound()

        //fab
        val myFab = this.findViewById<View?>(R.id.myFAB) as FloatingActionButton
        myFab.setOnClickListener { v ->
            saveSelectedExercises()
            updateStats()
            updateScore(playerName)
            val intent = Intent(v.getContext(), MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        if (!game!!.add) addSign.setTextColor(ResourcesCompat.getColor(resources,R.color.middlegrey, theme))
        if (!game!!.sub) subSign.setTextColor(ResourcesCompat.getColor(resources,R.color.middlegrey, theme))
        if (!game!!.mul) mulSign.setTextColor(ResourcesCompat.getColor(resources,R.color.middlegrey, theme))
        if (!game!!.div) divSign.setTextColor(ResourcesCompat.getColor(resources,R.color.middlegrey, theme))

        resultTexts = ArrayList<TextView?>()

        for (i in 0..9) {
            val exercise = TextView(this)
            val solution = TextView(this)
            exercise.textSize = 24f
            solution.textSize = 24f
            exercise.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            solution.textAlignment = View.TEXT_ALIGNMENT_CENTER
            exercise.setLayoutParams(
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            if (game!!.exercises[i].revisit) {
                exercise.setTextColor(getResources().getColor(R.color.middlegrey))
            } else {
                exercise.setTextColor(getResources().getColor(R.color.lightblue))
            }
            solution.setTextColor(ContextCompat.getColor(this, R.color.red))

            exercise.text = game!!.exercises[i].x.toString() + " " + game!!.exercises[i].o + " " + game!!.exercises[i].y + " = " + game!!.exercises[i].z
            if (game!!.exercises[i].solve() == game!!.exercises[i].z) {
                solution.text = "\u2713"
                solution.setTextColor(getResources().getColor(R.color.green))
            } else {
                val answer = "" + game!!.exercises[i].z
                val sb = SpannableStringBuilder(exercise.getText())
                val fcs = ForegroundColorSpan(ContextCompat.getColor(this, R.color.red))
                sb.setSpan(
                    fcs,
                    exercise.getText().length - answer.length,
                    exercise.getText().length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                solution.text = "${game!!.exercises[i].solve()}"
                exercise.text = sb
            }

            exercise.setId(i)
            exercise.setOnClickListener(this)

            val linearLayout = findViewById<View?>(R.id.exercises) as LinearLayout
            val linearLayout2 = findViewById<View?>(R.id.solutions) as LinearLayout
            linearLayout.addView(exercise)
            linearLayout2.addView(solution)

            resultTexts!!.add(exercise)
        }

        if (newHighScore) {
            score.text = getResources().getString(R.string.result_score) + " " + game!!.score + " " + getResources().getString(
                R.string.result_highscore
            )
        } else {
            score.text = getResources().getString(R.string.result_score) + " " + game!!.score
        }
        solved.text = getResources().getString(R.string.result_solved) + " " + game!!.answeredCorrectly() + " " + getResources().getString(
            R.string.result_solved_of
        ) + " 10"
    }

    override fun onClick(v: View) {
        for (i in 0..<game!!.exercisesSolved()) {
            if ((v.id == resultTexts!![i]!!.id) && (game!!.exercises[i].solve() != game!!.exercises[i].z)
            ) {
                if (!game!!.exercises[i].revisit) {
                    resultTexts!![i]!!.setTextColor(ResourcesCompat.getColor(resources, R.color.middlegrey, theme))
                    game!!.exercises[i].revisit = true
                    Toast.makeText(
                        this, getResources().getString(R.string.result_toast_selected),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    resultTexts!![i]!!.setTextColor(ResourcesCompat.getColor(resources,R.color.lightblue, theme))
                    game!!.exercises[i].revisit = false
                }
            }
        }
    }

    private fun saveSelectedExercises() {
        val helper = PFASQLiteHelper(this)
        val db = helper.writableDatabase
        for (i in 0..<game!!.exercisesSolved()) {
            if (game!!.exercises[i].revisit) {
                val values = ContentValues()
                values.put("operator1", game!!.exercises[i].x)
                values.put("operator2", game!!.exercises[i].y)
                values.put("operand", game!!.exercises[i].o)
                values.put("space", game!!.space)
                db.insert("SAVED_EXERCISES", null, values)
            }
        }
    }

    private fun updateStats() {
        var rightAnswersAdd = 0
        var rightAnswersSub = 0
        var rightAnswersMul = 0
        var rightAnswersDiv = 0
        var wrongAnswersAdd = 0
        var wrongAnswersSub = 0
        var wrongAnswersMul = 0
        var wrongAnswersDiv = 0


        for (e in game!!.exercises) {
            when (e.o) {
                "+" -> if (e.solve() == e.z) rightAnswersAdd++ else wrongAnswersAdd++
                "-" -> if (e.solve() == e.z) rightAnswersSub++ else wrongAnswersSub++
                "*" -> if (e.solve() == e.z) rightAnswersMul++ else wrongAnswersMul++
                "/" -> if (e.solve() == e.z) rightAnswersDiv++ else wrongAnswersDiv++
            }
        }

        val hs = this.getSharedPreferences("pfa-math-highscore", MODE_PRIVATE)
        val editor = hs.edit()
        editor.putInt(
            "rightadd" + game!!.space,
            hs.getInt("rightadd" + game!!.space, 0) + rightAnswersAdd
        )
        editor.putInt(
            "wrongadd" + game!!.space,
            hs.getInt("wrongadd" + game!!.space, 0) + wrongAnswersAdd
        )
        editor.putInt(
            "rightsub" + game!!.space,
            hs.getInt("rightsub" + game!!.space, 0) + rightAnswersSub
        )
        editor.putInt(
            "wrongsub" + game!!.space,
            hs.getInt("wrongsub" + game!!.space, 0) + wrongAnswersSub
        )
        editor.putInt(
            "rightmul" + game!!.space,
            hs.getInt("rightmul" + game!!.space, 0) + rightAnswersMul
        )
        editor.putInt(
            "wrongmul" + game!!.space,
            hs.getInt("wrongmul" + game!!.space, 0) + wrongAnswersMul
        )
        editor.putInt(
            "rightdiv" + game!!.space,
            hs.getInt("rightdiv" + game!!.space, 0) + rightAnswersDiv
        )
        editor.putInt(
            "wrongdiv" + game!!.space,
            hs.getInt("wrongdiv" + game!!.space, 0) + wrongAnswersDiv
        )
        editor.commit()
    }

    private fun updateScore(name: String?) {
        val hs = this.getSharedPreferences("pfa-math-highscore", MODE_PRIVATE)
        val editor = hs.edit()

        //saved values
        val highscoreNames = arrayOfNulls<String>(5)
        val highscoreScores = arrayOfNulls<String>(5)
        for (i in 0..4) {
            highscoreNames[i] = hs.getString("hsname" + i + game!!.space, null)
            highscoreScores[i] = hs.getString("hsscore" + i + game!!.space, null)
        }

        //update values
        var updated = false
        var prevScore: String? = "0"
        var prevName: String? = "N"
        for (i in 0..4) {
            if (!updated && (highscoreScores[i] != null) && game!!.score >= highscoreScores[i]!!.toInt()) {
                prevScore = highscoreScores[i]
                prevName = highscoreNames[i]
                highscoreScores[i] = "" + game!!.score
                highscoreNames[i] = name
                updated = true
            } else if (!updated && highscoreScores[i] == null) {
                highscoreScores[i] = "" + game!!.score
                highscoreNames[i] = name
                break
            } else if (updated && highscoreScores[i] == null) {
                highscoreScores[i] = prevScore
                highscoreNames[i] = prevName
                break
            } else if (updated) {
                val tempScore = prevScore
                val tempName = prevName
                prevScore = highscoreScores[i]
                prevName = highscoreNames[i]
                highscoreScores[i] = tempScore
                highscoreNames[i] = tempName
            }
        }

        //save new values
        for (i in 0..4) {
            if (highscoreScores[i] != null) {
                editor.putString("hsscore" + i + game!!.space, highscoreScores[i])
                editor.putString("hsname" + i + game!!.space, highscoreNames[i])
                editor.commit()
            }
        }
    }
}


