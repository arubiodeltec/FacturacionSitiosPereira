package com.example.verorden;


import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter {

    public static int LOOPS_COUNT = 1000;
    private List<Bundle> mOrdenes;

    public TabsAdapter(FragmentManager fm, List<Bundle> ordenes) {
        super(fm);
        mOrdenes = ordenes;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bolsaR = mOrdenes.get(position);

        //System.out.println("POSITION FRAGMENT " + position );
        return VerLecturaFragment.newInstance(bolsaR);
    }

    @Override
    public int getCount()
    {
        return mOrdenes.size(); // simulate infinite by big number of products
    }

/*
    @Override
    public int getItemPosition(Object item) {

        VerLecturaFragment fragment = (VerLecturaFragment)item;
        int ose_codigo_search = fragment.ose_codigo;
        int position = searchOse_codigo(ose_codigo_search);

        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }
    }*/


    private int searchOse_codigo(int ose_codigo_search) {

        int ose_codigo_tmp = 0;
        int position = -1;
        System.out.println("BUSCAR " + ose_codigo_search );
        if (!mOrdenes.isEmpty()) {
            for (int idx = 0; idx < mOrdenes.size(); idx++) {
                Bundle item = mOrdenes.get(idx);
                ose_codigo_tmp = item.getInt("ose_codigo");
                if (ose_codigo_tmp == ose_codigo_search) {
                    System.out.println("ENCONTRO " + idx );
                    position = idx;
                }
            }
        }
        if(position<0)
            System.out.println("NO ENCONTRO " + position );
        return position;
    }

}
