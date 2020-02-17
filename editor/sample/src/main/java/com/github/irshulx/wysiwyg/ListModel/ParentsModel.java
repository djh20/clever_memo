package com.github.irshulx.wysiwyg.ListModel;

/*Like a Category!*/

import java.util.ArrayList;
import java.util.List;

public class ParentsModel {
    String title;
    int resource = -1;
    boolean isNew = false; // is it a new category?
    boolean hasChild = false;
    boolean isSelected = false;

    List<ChildModel> childModelList = new ArrayList<>();
    List<ParentsModel> parentsModelList = new ArrayList<>();

    public ParentsModel(String title){
        this.title = title;
    }

    public ParentsModel(String title, int resource){
        this.title = title;
        this.resource = resource;
    }

    public ParentsModel(String title, int resource, boolean hasChild){
        this.title = title;
        this.resource = resource;
        this.hasChild = hasChild;
    }

    public ParentsModel(String title, int resource, boolean hasChild, boolean isNew
            , boolean isSelected
    ){
        this.title = title;
        this.resource = resource;
        this.isNew = isNew;
        this.hasChild = hasChild;
        this.isSelected = isSelected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ParentsModel addChildModel(ChildModel childModel){
        this.childModelList.add(childModel);

        return this;
    }

    public List<ChildModel> getChildModelList() {
        return childModelList;
    }

    public void setChildModelList(List<ChildModel> childModelList) {
        this.childModelList = childModelList;
    }


}