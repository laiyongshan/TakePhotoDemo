package com.suncere.app.takephotodemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * ClassName：
 * Description:
 * Author：LYS
 * Createtime：2019/7/8
 * Modified By：
 * Fixtime：2019/7/8
 * FixDescription：
 */

public class FragmentTestActivity extends AppCompatActivity {

    private TestFragment mTestFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);

        if (savedInstanceState != null) {
            mTestFragment = (TestFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, "TestFragment");
        } else {
            mTestFragment = TestFragment.newInstance();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, mTestFragment)
                .commit();
    }

    /**
     * 当活动被回收时，存储当前的状态。
     *
     * @param outState 状态数据。
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 存储 Fragment 的状态。
        getSupportFragmentManager().putFragment(outState, "TestFragment", mTestFragment);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
