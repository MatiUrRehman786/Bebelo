package com.buzzware.bebelo.Model;

import com.buzzware.bebelo.retrofit.DetailModel.BarBottleItem;
import com.intrusoft.sectionedrecyclerview.Section;
import java.util.List;

public class SectionHeader implements Section<BarBottleItem>, Comparable<SectionHeader> {

    List<BarBottleItem> childList;
    String sectionText;
    int index;

    public SectionHeader(List<BarBottleItem> childList, String sectionText, int index) {
        this.childList = childList;
        this.sectionText = sectionText;
        this.index = index;
    }

    @Override
    public List<BarBottleItem> getChildItems() {
        return childList;
    }

    public String getSectionText() {
        return sectionText;
    }

    @Override
    public int compareTo(SectionHeader another) {
        if (this.index > another.index) {
            return -1;
        } else {
            return 1;
        }
    }
}