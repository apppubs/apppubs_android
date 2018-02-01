package com.apppubs.d20.page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by siger on 2018/1/31.
 */

public class AddressModel implements Serializable{
    private String name;
    private String code;
    private List<AddressModel> items;

    public AddressModel(String json) throws JSONException {
        JSONObject jo = new JSONObject(json);
        this.name = jo.getString("name");
        this.code = jo.getString("code");

        if (jo.has("items")){
            JSONArray ja = jo.getJSONArray("items");
            List<AddressModel> list = new ArrayList<AddressModel>();
            for (int i=-1;++i<ja.length();){
                AddressModel item = new AddressModel(ja.getString(i));
                list.add(item);
            }
            this.items = list;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AddressModel> getItems() {
        return items;
    }

    public void setItems(List<AddressModel> items) {
        this.items = items;
    }
}
