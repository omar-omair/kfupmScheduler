package kfupm;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SectionRectangle extends Rectangle {
    Section section;
    Label endTimeLabel;
    Label beginTimeLabel;
    Label instructorLabel;
    Label sectionLabel;

    SectionRectangle(Section section) {
        this.section = section;
        String beginTime = section.getTime().split("-")[0];
        String endTime = section.getTime().split("-")[1];
        this.beginTimeLabel = new Label(beginTime);
        this.endTimeLabel = new Label(endTime);
        this.instructorLabel = new Label(section.getInstructor());
        this.sectionLabel = new Label(section.getSection() + section.getLoc());

    }

    Section getSection() {
        return section;
    }
    
    

}