package com.apppubs.bean.webapp;

public class DeptModel {
    private String id;
    private String name;
    private boolean isLeaf;
    private boolean isSelected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static DeptModel createFrom(DeptHttpModel httpModel){
        DeptModel model = new DeptModel();
        model.setId(httpModel.getId());
        model.setLeaf(httpModel.isLeaf());
        model.setName(httpModel.getName());
        return model;
    }
}
