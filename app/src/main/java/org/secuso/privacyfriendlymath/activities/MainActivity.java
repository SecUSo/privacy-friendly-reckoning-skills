package org.secuso.privacyfriendlymath.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import org.secuso.privacyfriendlymath.R;

/**
 * @author Chris
 * @version 20161225
 * This activity is an example for the main menu of gaming applications
 */

public class MainActivity extends BaseActivity {

    private ViewPager mViewPager;
    private ImageView mArrowLeft;
    private ImageView mArrowRight;

    private boolean add = true;
    private boolean sub = true;
    private boolean mul = true;
    private boolean div = true;
    private int activeOperators = 4;

    private Button addButton;
    private Button subButton;
    private Button mulButton;
    private Button divButton;
    private Button gameButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.scroller);
        if(mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        int index = mSharedPreferences.getInt("lastChosenPage", 0);

        mViewPager.setCurrentItem(index);
        mArrowLeft = (ImageView) findViewById(R.id.arrow_left);
        mArrowRight = (ImageView) findViewById(R.id.arrow_right);

        addButton = (Button) findViewById(R.id.button_add);
        subButton = (Button) findViewById(R.id.button_sub);
        mulButton = (Button) findViewById(R.id.button_mul);
        divButton = (Button) findViewById(R.id.button_div);
        gameButton = (Button) findViewById(R.id.button_start_game);

        //care for initial postiton of the ViewPager
        mArrowLeft.setVisibility((index==0)?View.INVISIBLE:View.VISIBLE);
        mArrowRight.setVisibility((index==mSectionsPagerAdapter.getCount()-1)?View.INVISIBLE:View.VISIBLE);

        //Update ViewPager on change
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mArrowLeft.setVisibility((position==0)?View.INVISIBLE:View.VISIBLE);
                mArrowRight.setVisibility((position==mSectionsPagerAdapter.getCount()-1)?View.INVISIBLE:View.VISIBLE);

                //save position in settings
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt("lastChosenPage", position);
                editor.apply();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_example;
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.arrow_left:
                mViewPager.arrowScroll(View.FOCUS_LEFT);
                break;
            case R.id.arrow_right:
                mViewPager.arrowScroll(View.FOCUS_RIGHT);
                break;
            case R.id.button_add:
                if(add==true && activeOperators > 1) {
                    addButton.setBackgroundColor(getResources().getColor(R.color.middlegrey));
                    add = false;
                    activeOperators--;
                } else if(!add) {
                    addButton.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    add = true;
                    activeOperators++;
                } else {
                    oneActiveToast();
                }
                break;
            case R.id.button_sub:
                if(sub==true && activeOperators > 1) {
                    subButton.setBackgroundColor(getResources().getColor(R.color.middlegrey));
                    sub = false;
                    activeOperators--;
                } else if(!sub) {
                    subButton.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    sub = true;
                    activeOperators++;
                } else {
                    oneActiveToast();
                }
                break;
            case R.id.button_mul:
                if(mul==true && activeOperators > 1) {
                    mulButton.setBackgroundColor(getResources().getColor(R.color.middlegrey));
                    mul = false;
                    activeOperators--;
                } else if(!mul) {
                    mulButton.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    mul = true;
                    activeOperators++;
                } else {
                    oneActiveToast();
                }
                break;
            case R.id.button_div:
                if(div==true && activeOperators > 1) {
                    divButton.setBackgroundColor(getResources().getColor(R.color.middlegrey));
                    div = false;
                    activeOperators--;
                } else if(!div) {
                    divButton.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    div = true;
                    activeOperators++;
                } else {
                    oneActiveToast();
                }
                break;
            case R.id.button_start_game:
                Intent intent = new Intent(view.getContext(), ExerciseActivity.class);
                intent.putExtra("add",add);
                intent.putExtra("sub",sub);
                intent.putExtra("mul",mul);
                intent.putExtra("div",div);
                //this ok?
                intent.putExtra("space", mViewPager.getCurrentItem());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default:
        }
    }

    void oneActiveToast(){
        Context context = getApplicationContext();
        CharSequence text = getResources().getString(R.string.main_button_inactive_toast);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PageFragment (defined as a static inner class below).
            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

    public static class PageFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PageFragment newInstance(int sectionNumber) {
            PageFragment fragment = new PageFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PageFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int id = 0;
            if(getArguments() != null) {
                id = getArguments().getInt(ARG_SECTION_NUMBER);
            }

            View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            switch(id) {
                case(0): textView.setText(getResources().getString(R.string.number_range_one));
                    break;
                case(1): textView.setText(getResources().getString(R.string.number_range_two));;
                    break;
                case(2): textView.setText(getResources().getString(R.string.number_range_three));;
                    break;
                default: textView.setText(getResources().getString(R.string.number_range_four));;
                    break;
            }

            return rootView;
        }
    }
}
