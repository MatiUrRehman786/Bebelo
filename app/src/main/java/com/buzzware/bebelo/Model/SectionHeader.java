package com.buzzware.bebelo.Model;

import com.intrusoft.sectionedrecyclerview.Section;
import java.util.List;

public class SectionHeader implements Section<Child>, Comparable<SectionHeader> {

    List<Child> childList;
    String sectionText;
    int index;

    public SectionHeader(List<Child> childList, String sectionText, int index) {
        this.childList = childList;
        this.sectionText = sectionText;
        this.index = index;
    }

    @Override
    public List<Child> getChildItems() {
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