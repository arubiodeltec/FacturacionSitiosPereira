package com.example.formato;


import android.database.Cursor;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.formato.EjecucionFormatos2;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter {

	List<Fragment> mFragments;
	public TabsAdapter(FragmentManager fm, Cursor cursor, int ef_id, int tf_id, int cuadrilla) {
		super(fm);

		int mf_id;
		mFragments = new ArrayList<Fragment>();
		if (cursor.moveToFirst()) {
			do {
				mf_id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
				EjecucionFormatos2 ef = EjecucionFormatos2.newInstance(null);
				ef.setIds(mf_id, ef_id, tf_id, cuadrilla);
				mFragments.add(ef);
				System.gc();
			} while (cursor.moveToNext());//accessing data upto last row from table
		}
		//this.mFragments = mFragments;
	}

	@Override
	public Fragment getItem(int index) {
		return mFragments.get(index);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

}
