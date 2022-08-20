package kfupm;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.util.*;


public class controller {
    @FXML
    private TableColumn<Section, String> crn;

    @FXML
    private TableColumn<Section, String> day;

    @FXML
    private Button addButtonL;

    @FXML
    private Button addButtonUN;

    @FXML
    private Button removeButtonL;

    @FXML
    private Button removeButtonUN;

    @FXML
    private TableColumn<Section, String> instructor;

    @FXML
    private TableColumn<Section, String> loc;

    @FXML
    private TableColumn<Section, String> section;

    @FXML
    private TableColumn<Section, String> status;

    @FXML
    private TableView<Section> table;

    @FXML
    private TableColumn<Section, String> time;

    @FXML
    private TableColumn<Section, String> wait;
    
    @FXML
    private AnchorPane pane;

    @FXML
    private CheckBox darkCheck;

    @FXML
    private ComboBox<String> deptBox;

    @FXML
    private ComboBox<String> courseBox;

    @FXML
    private ComboBox<String> termBox;

    public boolean isDark = false;

    public static ArrayList<Section> currentSections = new ArrayList<Section>();

    @FXML
    private AnchorPane schedulePane;

    ArrayList<Color> colors = new ArrayList<Color>();
    ArrayList<SectionRectangle> scheduledSections = new ArrayList<SectionRectangle>();

    @FXML
    void initialize() throws Exception {
        pane.getStylesheets().add("style.css");

        darkCheck.selectedProperty().addListener(e->{
            if(!isDark){
                pane.getStylesheets().remove("style.css");
                pane.getStylesheets().add("darkStyle.css");
                isDark = true;
            }
            else{
                pane.getStylesheets().add("style.css");
                pane.getStylesheets().remove("darkStyle.css");
                isDark = false;
            }
        });

        Scanner scanner3 = new Scanner(new File(controller.class.getResource("colors.txt").toURI()));
        while(scanner3.hasNextLine()) {
            colors.add(Color.web(scanner3.nextLine()));
        }
        scanner3.close();
        
        try {
            load();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
            
        section.prefWidthProperty().bind(table.widthProperty().divide(8)); 
        crn.prefWidthProperty().bind(table.widthProperty().divide(12));
        instructor.prefWidthProperty().bind(table.widthProperty().divide(4));
        day.prefWidthProperty().bind(table.widthProperty().divide(8));
        time.prefWidthProperty().bind(table.widthProperty().divide(8));
        loc.prefWidthProperty().bind(table.widthProperty().divide(8));
        wait.prefWidthProperty().bind(table.widthProperty().divide(12));
        status.prefWidthProperty().bind(table.widthProperty().divide(12));

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        section.setCellValueFactory(new PropertyValueFactory<Section, String>("section"));
        crn.setCellValueFactory(new PropertyValueFactory<Section, String>("CRN"));
        status.setCellValueFactory(new PropertyValueFactory<Section, String>("seats"));
        loc.setCellValueFactory(new PropertyValueFactory<Section, String>("loc"));
        instructor.setCellValueFactory(new PropertyValueFactory<Section, String>("instructor"));
        time.setCellValueFactory(new PropertyValueFactory<Section,String>("time"));
        day.setCellValueFactory(new PropertyValueFactory<Section, String>("day"));
        wait.setCellValueFactory(new PropertyValueFactory<Section, String>("wait"));
        


        ArrayList<String> terms = new ArrayList<String>();
        int LatestTerm = 220;
        JSONArray array3 = new JSONArray();
        do {
            LatestTerm += 1;
            array3 = read(String.valueOf(LatestTerm), "SE");
            if(String.valueOf(LatestTerm).charAt(2) == '3') {
                LatestTerm += 7;
            }
            if(array3.size() <= 0) {
                LatestTerm--;
            }
        } while(array3.size() > 0);
        

        for(int i = 201; i <= LatestTerm; i++){
            terms.add(String.valueOf(i));
            if(String.valueOf(i).charAt(2) == '3'){
                i += 7;
            }
        }
        Collections.reverse(terms);
        termBox.setItems(FXCollections.observableArrayList(terms));

        ArrayList<String> depts = new ArrayList<String>();
        Scanner scanner = new Scanner(new File(controller.class.getResource("depts.txt").toURI()));
        while(scanner.hasNextLine()) {
            depts.add(scanner.nextLine());
        }
       deptBox.setItems(FXCollections.observableArrayList(depts));
       scanner.close();

       ArrayList<String> deptSymbols = new ArrayList<String>();
       Scanner scanner2 = new Scanner(new File(controller.class.getResource("deptSymbols.txt").toURI()));
       while(scanner2.hasNextLine()) {
        deptSymbols.add(scanner2.nextLine());
        }
        scanner2.close();

        

        termBox.getSelectionModel().selectedItemProperty().addListener(e-> {
            courseBox.getSelectionModel().clearSelection();
            courseBox.setValue(null);
            JSONArray array;
            if(termBox.getSelectionModel().getSelectedItem() != null && deptBox.getSelectionModel().getSelectedItem() != null) {
            try {
                array = read(termBox.getSelectionModel().getSelectedItem(),deptSymbols.get(deptBox.getSelectionModel().getSelectedIndex()));
                Section[] sections = write(array,false);
                table.setItems(FXCollections.observableArrayList(sections));
                courseBox.setItems(FXCollections.observableArrayList(getCourseList(sections)));
                List<Section> sectionsArrayList = Arrays.asList(sections);
                currentSections.clear();
                currentSections.addAll(sectionsArrayList);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
            
        });

        deptBox.getSelectionModel().selectedItemProperty().addListener(e-> {
            courseBox.getSelectionModel().clearSelection();
            courseBox.setValue(null); 
            JSONArray array;
            if(termBox.getSelectionModel().getSelectedItem() != null && deptBox.getSelectionModel().getSelectedItem() != null) {
            try {
                array = read(termBox.getSelectionModel().getSelectedItem(),deptSymbols.get(deptBox.getSelectionModel().getSelectedIndex()));
                Section[] sections = write(array,false);
                table.setItems(FXCollections.observableArrayList(sections));
                courseBox.setItems(FXCollections.observableArrayList(getCourseList(sections)));
                List<Section> sectionsArrayList = Arrays.asList(sections);
                currentSections.clear();
                currentSections.addAll(sectionsArrayList);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            }
        });

        courseBox.getSelectionModel().selectedItemProperty().addListener(e->{
            String course = courseBox.getSelectionModel().getSelectedItem();
            ArrayList<Section> sections = new ArrayList<Section>(currentSections);
            ArrayList<Section> acceptedSections = new ArrayList<Section>();
            for (Section section : sections) {
                if(section.getSection().split("-")[0].equals(course)) {
                    acceptedSections.add(section);
                }
            }
            table.setItems(FXCollections.observableArrayList(acceptedSections));
        });

        table.getSelectionModel().selectedItemProperty().addListener(e->{
            if(table.getSelectionModel().getSelectedItem() != null){
                addButtonL.setVisible(false);
                addButtonUN.setVisible(true);
                
                boolean exists = false;
                for(SectionRectangle rect : scheduledSections) {
                    if(rect.getSection().getSection().equals(table.getSelectionModel().getSelectedItem().getSection())) {
                        exists = true;
                    }
                }
                if(exists) {
                    removeButtonUN.setVisible(true);
                    removeButtonL.setVisible(false);
                    addButtonL.setVisible(true);
                    addButtonUN.setVisible(false);
                }
                else {
                    removeButtonUN.setVisible(false);
                    removeButtonL.setVisible(true);
                }
            }
            else {
                addButtonL.setVisible(true);
                addButtonUN.setVisible(false);
                removeButtonUN.setVisible(false);
                removeButtonL.setVisible(true);
            }
        });

        addButtonUN.setOnAction(e->{
            Boolean conflict = false;
            Boolean sectionConflict = checkConflict(table.getSelectionModel().getSelectedItem());
            Section connectedSection = findConnectedSection(table.getSelectionModel().getSelectedItem());
            boolean connectedSectionConflict = false;
            if(connectedSection != null) {
                connectedSectionConflict = checkConflict(connectedSection);
            }
            if(sectionConflict || connectedSectionConflict) {
                conflict = true;
            }
            
            int randomIndex = (int) (Math.random() * (20));
            if(table.getSelectionModel().getSelectedItem().getDay() != null && !conflict) {
            for(int i = 0; i< scheduledSections.size(); i++) {
                if(table.getSelectionModel().getSelectedItem().getSection().contains("LAB")) {
                    if(table.getSelectionModel().getSelectedItem().getSection().split("-")[0].equals(scheduledSections.get(i).getSection().getSection().split("-")[0]) && scheduledSections.get(i).getSection().getSection().contains("LAB")) {
                        schedulePane.getChildren().remove(scheduledSections.get(i));
                        scheduledSections.remove(scheduledSections.get(i));
                        i--;
                    }
                }
                else {
                    if(table.getSelectionModel().getSelectedItem().getSection().split("-")[0].equals(scheduledSections.get(i).getSection().getSection().split("-")[0]) && !scheduledSections.get(i).getSection().getSection().contains("LAB")) {
                        schedulePane.getChildren().remove(scheduledSections.get(i));
                        scheduledSections.remove(scheduledSections.get(i));
                        i--;
                    }
                }
            }
            
            for(int i = 0; i < table.getSelectionModel().getSelectedItem().getDay().length(); i++){
                SectionRectangle rect = add(table.getSelectionModel().getSelectedItem(), table.getSelectionModel().getSelectedItem().getDay().charAt(i));
                rect.setFill(colors.get(randomIndex));
                rect.colorIndex = randomIndex;
                schedulePane.getChildren().add(rect);
                scheduledSections.add(rect);
            }

            
            if(connectedSection != null) {
                for(int j = 0; j < connectedSection.getDay().length(); j++){
                    SectionRectangle rect = add(connectedSection, connectedSection.getDay().charAt(j));
                    rect.setFill(colors.get(randomIndex));
                    rect.colorIndex = randomIndex;
                    scheduledSections.add(rect);
                    schedulePane.getChildren().add(rect);
                }
            }
            table.getSelectionModel().clearSelection(); 

            try {
                save();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        });

        removeButtonUN.setOnAction(e-> {
            Section connectedSection = findConnectedSection(table.getSelectionModel().getSelectedItem());
            for(int i = 0; i< scheduledSections.size(); i++) {
                SectionRectangle rect = scheduledSections.get(i);
                if(rect.getSection().getSection().equals(table.getSelectionModel().getSelectedItem().getSection()) || rect.getSection().getSection().equals(connectedSection.getSection())) {
                    scheduledSections.remove(rect);
                    schedulePane.getChildren().remove(rect);
                    i--;
                }
            }
            table.getSelectionModel().clearSelection();
            try {
                save();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        

    }

    JSONArray read(String termCode, String deptCode) throws Exception{
        URL url = new URL("https://registrar.kfupm.edu.sa/api/course-offering?term_code=20" + termCode + "0&department_code=" + deptCode);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET"); 
        Scanner url_input = new Scanner(url.openStream());
        String output = "";
        while (url_input.hasNext()) {
            output += url_input.nextLine();
        }
        JSONParser parser = new JSONParser();
        JSONObject array = (JSONObject) parser.parse(output);
        JSONArray array2 = (JSONArray) array.get("data");
        url_input.close();
        return array2;
    }

    Section[] write(JSONArray array, Boolean specificCourse) {
        ArrayList<HashMap<String,String>> sections = new ArrayList<HashMap<String,String>>();
        for(int i = 0; i < array.size(); i++) {
            sections.add((HashMap<String,String>) array.get(i));
        }

        Section[] section_list = new Section[sections.size()];

        for(int i = 0; i < section_list.length; i++) {
            Section section = new Section(sections.get(i).get("course_number") + "-" + sections.get(i).get("section_number") + " (" + sections.get(i).get("class_type") + ")",
                                          sections.get(i).get("crn"), sections.get(i).get("start_time") + "-" + sections.get(i).get("end_time"),
                                          sections.get(i).get("class_days"), sections.get(i).get("building") + "-" + sections.get(i).get("room"), 
                                          String.valueOf(sections.get(i).get("available_seats")),sections.get(i).get("instructor_name"), String.valueOf(sections.get(i).get("waiting_list_count")));
            section_list[i] = section;
        }
        return section_list;
    }

    ArrayList<String> getCourseList(Section[] array) {
       ArrayList<String> courses = new ArrayList<String>();
       for(int i = 0; i < array.length; i++){
            courses.add(array[i].getSection().split("-")[0]);
       }
       HashSet<String> uniqueCourses = new HashSet<String>(courses);
       courses.clear();
       courses.addAll(uniqueCourses);
       Collections.sort(courses);
       return courses;
    }

    Double calculateHeight(Section section) {
        String start_time = section.getTime().split("-")[0].substring(0,2) + ":" + section.getTime().split("-")[0].substring(2);
        String end_time = section.getTime().split("-")[1].substring(0,2) + ":" + section.getTime().split("-")[1].substring(2);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("kk:mm");
        LocalTime t1 = LocalTime.parse(start_time, fmt);
        LocalTime t2 = LocalTime.parse(end_time, fmt);
        Long totalMinutes = ChronoUnit.MINUTES.between(t1, t2);
        Double height = ((totalMinutes)/60.0) * 41.8;
        return height;
    }

    Double[] calculatePosition(Section section, char day) {
        Double leftAnchor;
        Double topAnchor;
        String start_time = section.getTime().split("-")[0];
        int multiplier = 0;

        if(day == 'U' || day == 'u') {
            leftAnchor = 1.0;
        }
        else if(day == 'M' || day == 'm') {
            leftAnchor = 241.0;
        }
        else if(day == 'T' || day == 't') {
            leftAnchor = 481.0;
        }
        else if(day == 'W' || day == 't') {
            leftAnchor = 721.0;
        }
        else {
            leftAnchor = 961.0;
        }

        for(int i = 7; i < 19; i++ ) {
            if(Integer.valueOf(start_time.substring(0,2)) == i) {
                multiplier = i-7;
            }
        }
        topAnchor = 41.8 * multiplier + (Integer.valueOf(start_time.substring(2))/60.0) * 41.8;
        Double[] position = new Double[2];
        position[0] = leftAnchor;
        position[1] = topAnchor;
        return position;
    }

    SectionRectangle add(Section section, char day) {
        SectionRectangle rect = new SectionRectangle(section);
        rect.setWidth(239);
        rect.setArcWidth(4);
        rect.setArcHeight(3);
        rect.setHeight(calculateHeight(section));
        Double[] position = calculatePosition(section, day);
        AnchorPane.setLeftAnchor(rect, position[0]);
        AnchorPane.setTopAnchor(rect, position[1]);
        rect.setLeftAnchor(position[0]);
        rect.setTopAnchor(position[1]);
        return rect;
    }

    public boolean isOverlapping(SectionRectangle section1, Section section2, char day) {
        String start_time1 = section1.getSection().getTime().split("-")[0].substring(0,2) + ":" + section1.getSection().getTime().split("-")[0].substring(2);
        String end_time1 = section1.getSection().getTime().split("-")[1].substring(0,2) + ":" + section1.getSection().getTime().split("-")[1].substring(2);
        String start_time2 = section2.getTime().split("-")[0].substring(0,2) + ":" + section2.getTime().split("-")[0].substring(2);
        String end_time2 = section2.getTime().split("-")[1].substring(0,2) + ":" + section2.getTime().split("-")[1].substring(2);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("kk:mm");
        LocalTime start1 = LocalTime.parse(start_time1, fmt);
        LocalTime end1 = LocalTime.parse(end_time1, fmt);
        LocalTime start2 = LocalTime.parse(start_time2, fmt);
        LocalTime end2 = LocalTime.parse(end_time2, fmt);
        Boolean conflictHours = start1.isBefore(end2)  && end1.isAfter(start2);
        Boolean conflictDays = false;
        for(int i = 0; i < section1.getSection().getDay().length(); i++) {
            if(section1.getSection().getDay().charAt(i) == day) {
                conflictDays = true;
            }
        }
        return conflictHours && conflictDays;
    }

    Section findConnectedSection(Section section) {
        if(section.getSection().contains("(LEC)")) {
            for(int i=0; i< currentSections.size(); i++) {
                if(currentSections.get(i).getSection().split(" ")[0].equals(section.getSection().split(" ")[0]) && 
                currentSections.get(i).getSection().contains("(REC)")) {
                    return currentSections.get(i);
                }
            }
        }   
        else if(table.getSelectionModel().getSelectedItem().getSection().contains("(REC)")) {
            for(int i=0; i< currentSections.size(); i++) {
                if(currentSections.get(i).getSection().split(" ")[0].equals(section.getSection().split(" ")[0]) && 
                currentSections.get(i).getSection().contains("(LEC)")) {
                    return currentSections.get(i);
                }
            }
        }
    
        return null;
    }

    boolean checkConflict(Section section) {
        Boolean conflict = false;
        Boolean brokenLoop = false;
        for(SectionRectangle sectionRect: scheduledSections) {
            if(brokenLoop) {
                break;
            }
            for(int i = 0; i < section.getDay().length(); i++) {
                if(section.getSection().contains("(LAB)")) {
                    if(isOverlapping(sectionRect,section, section.getDay().charAt(i))
                    && !(section.getSection().split("-")[0].equals(sectionRect.getSection().getSection().split("-")[0])
                        && sectionRect.getSection().getSection().contains("(LAB)"))){
                            conflict = true;
                            brokenLoop = true;
                            break;
                    }
                }
                else {
                    if(isOverlapping(sectionRect,section, section.getDay().charAt(i))
                    && !(section.getSection().split("-")[0].equals(sectionRect.getSection().getSection().split("-")[0])
                        && !sectionRect.getSection().getSection().contains("(LAB)"))){
                            conflict = true;
                            brokenLoop = true;
                            break;
                            
                    }
                }
            }
        }
        return conflict;
    }

    void save() throws Exception{
        FileOutputStream fos = new FileOutputStream(new File(controller.class.getResource("schedule.ser").toURI()));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(scheduledSections);
        oos.close();
        fos.close();
    }
    void load() throws Exception {
        FileInputStream fis = new FileInputStream(new File(controller.class.getResource("schedule.ser").toURI()));
        ObjectInputStream ois = new ObjectInputStream(fis);
        scheduledSections = (ArrayList<SectionRectangle>) ois.readObject();
        ois.close();
        fis.close();
        for(SectionRectangle rect:scheduledSections) {
            Double[] position = rect.getAnchors();
            AnchorPane.setLeftAnchor(rect,position[0]);
            AnchorPane.setTopAnchor(rect, position[1]);
            rect.setWidth(239);
            rect.setHeight(calculateHeight(rect.getSection()));
            try {
                rect.setFill(colors.get(rect.colorIndex));}
            catch (IndexOutOfBoundsException ex ) {
                rect.setFill(colors.get(0));
            }
            schedulePane.getChildren().add(rect);
        }
    }
}
