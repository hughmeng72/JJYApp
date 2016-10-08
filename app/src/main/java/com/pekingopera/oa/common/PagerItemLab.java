package com.pekingopera.oa.common;

import android.content.Context;

import java.util.List;

/**
 * Created by wayne on 10/3/2016.
 */

public class PagerItemLab<T extends IPager> {
    private static final String TAG = "PagerItemLab";

    private static PagerItemLab sPagerItemLab;

    private List<T> mItems;

    private PagerItemLab(Context context) {
    }

    public static PagerItemLab get(Context context) {
        if (sPagerItemLab == null) {
            sPagerItemLab = new PagerItemLab(context);
        }

        return sPagerItemLab;
    }

    public List<T> getItems() {
        return mItems;
    }

    public void setItems(List<T> items) {
        mItems = items;
    }

    public void Remove(int id) {
        for (T item : mItems) {
            if (item.getId() == id) {
                mItems.remove(item);
                break;
            }
        }
    }
}