package kfupm;

import java.io.Serializable;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

public class SectionRectangle extends Rectangle implements Serializable {
    Section section;
    String endTimeLabel;
    String beginTimeLabel;
    String instructorLabel;
    String sectionLabel;
    Double topAnchor;
    Double leftAnchor;
    int colorIndex;
    private static final long serialVersionUID = -9028681586303153548L;

    SectionRectangle(Section section) {
        this.section = section;
        String beginTime = section.getTime().split("-")[0];
        String endTime = section.getTime().split("-")[1];
        this.beginTimeLabel = beginTime;
        this.endTimeLabel = endTime;
        this.instructorLabel = section.getInstructor();
        this.sectionLabel = section.getSection() + section.getLoc();

    }

    Section getSection() {
        return section;
    }

    void setTopAnchor(Double topAnchor) {
        this.topAnchor = topAnchor;
    }

    void setLeftAnchor(Double leftAnchor) {
        this.leftAnchor = leftAnchor;
    }
    
    Double[] getAnchors() {
        Double[] anchors = new Double[2];
        anchors[0] = this.leftAnchor;
        anchors[1] = this.topAnchor;
        return anchors;
    }
    
    

}