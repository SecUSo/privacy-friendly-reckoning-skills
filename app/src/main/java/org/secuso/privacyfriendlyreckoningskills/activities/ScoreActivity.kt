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

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.secuso.privacyfriendlyreckoningskills.R

class ScoreActivity : AppCompatActivity() {
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        mSectionsPagerAdapter = ScoreActivity.SectionsPagerAdapter(resources,supportFragmentManager)

        mViewPager = findViewById<View?>(R.id.container) as ViewPager
        mViewPager!!.setAdapter(mSectionsPagerAdapter)

        val tabLayout = findViewById<View?>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)
    }

    class SectionsPagerAdapter(val resources: Resources, fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            val args = Bundle()
            args.putInt("space", position)
            val frag = ScoreFragment()
            frag.setArguments(args)
            return frag
        }

        override fun getCount(): Int {
            return 4
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return resources.getString(R.string.number_range_one)
                1 -> return resources.getString(R.string.number_range_two)
                2 -> return resources.getString(R.string.number_range_three)
                3 -> return resources.getString(R.string.number_range_four)
            }
            return null
        }
    }
}
