package com.apppubs.bean.page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siger on 2018/4/17.
 */
public class GridViewModel {
    private Integer column;
    private Integer maxRow;
    private List<GridViewItem> items;

    public GridViewModel(String jsonStr) {
        try {
            JSONObject jo = new JSONObject(jsonStr);
            this.column = jo.getInt("column");
            this.maxRow = jo.getInt("maxRow");
            JSONArray items = jo.getJSONArray("items");
            List<GridViewItem> list = new ArrayList<GridViewItem>();
            for (int i = -1; ++i < items.length(); ) {
                GridViewItem item = new GridViewItem(items.getString(i));
                list.add(item);
            }
            this.items = list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getMaxRow() {
        return maxRow;
    }

    public void setMaxRow(Integer maxRow) {
        this.maxRow = maxRow;
    }

    public List<GridViewItem> getItems() {
        return items;
    }

    public void setItems(List<GridViewItem> items) {
        this.items = items;
    }

    public List<GridViewItem> getItemsForPage(int pageIndex) {
        List<GridViewItem> list = new ArrayList<GridViewItem>();
        int pageSize = maxRow * column;
        int pageStartIndex = pageSize * pageIndex;
        if (items.size() > pageStartIndex + pageSize) {
            list.addAll(items.subList(pageStartIndex, pageStartIndex + pageSize));
        } else if (items.size() > pageStartIndex) {
            list.addAll(items.subList(pageStartIndex, items.size()));
        } else {
            //do nothing
        }
        return list;
    }

    public int getTotalPage() {
        return items.size() % (maxRow * column) == 0 ? items.size() / (maxRow *
                column) : items
                .size() / (maxRow * column) + 1;
    }

    public int getRealMaxRow() {
        if (items.size() <= maxRow * column) {
            return items.size() % column == 0 ? items.size() / column : items.size() / column
                    + 1;
        } else {
            return maxRow;
        }
    }
}
