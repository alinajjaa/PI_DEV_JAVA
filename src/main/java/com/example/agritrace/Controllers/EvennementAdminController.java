package com.example.agritrace.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class EvennementAdminController implements Initializable {

    //pages
    @FXML
     VBox ActivitesPage;
    @FXML
     Pane AddEventPage;
    @FXML
     VBox EventsInterface;
    @FXML
     VBox ParticipantsPage;
    @FXML
    Pane UpdateEventPage,ADDAcitivitesPage,UpdateAcitivitesPage;



    //Crud Actions Button
    @FXML
    Button AddEventBtn;
    @FXML
    Button UpdateEvent;


    @FXML
    Button AddImageBtn;


// table view avis
    @FXML
     TableView<?> ActivitesTableView;
    @FXML
     TableColumn<?, ?> idAvisColumn;
    @FXML
     TableColumn<?, ?> ideventcolumnnA;
    @FXML
     TableColumn<?, ?> iduserrColumnA;
    @FXML
     TableColumn<?, ?> idcommentaireColumn;
    @FXML
     TableColumn<?, ?> actionsColumnA;




    // table view Participants
    @FXML
     TableView<?> participantsTableView;
    @FXML
    TableColumn<?, ?> idColumnParticipants;
    @FXML
    TableColumn<?, ?> ideventcolumnn;
    @FXML
    TableColumn<?, ?> iduserrColumn;
    @FXML
     TableColumn<?, ?> dateColumnP;




    @FXML
     TextField ImageEventInput;

    @FXML
     TextField LieuIEventnput;
    @FXML
     ChoiceBox<?> TypeEventInput;


    @FXML
     DatePicker dateEvent1;

    @FXML
     DatePicker dateEventInput;

    @FXML
     DatePicker dateEventInput2;

    @FXML
     TextArea desEvent1;

    @FXML
     TextField imageEvent1;

    @FXML
     TextField localisation1;

    @FXML
     TextField nomEvent1;

    @FXML
     TextField nomEventIInput;

    @FXML
     TextField typeEvent1;




    @FXML
    public void GoToEventPagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);


        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(true);
        EventsInterface.setManaged(true);
    }

    @FXML
    public void GoToAddEventPagee(){
        AddEventPage.setVisible(true);
        AddEventPage.setManaged(true);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }

    @FXML
    public void GoToUpdateEventPagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(true);
        UpdateEventPage.setManaged(true);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }

    @FXML
    public void GoToParticipantPagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(true);
        ParticipantsPage.setManaged(true);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }

    @FXML
    public void GoToActivitesPagePagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(true);
        ActivitesPage.setManaged(true);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }

    @FXML
    public void GoToAddActivitesPagePagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(true);
        ADDAcitivitesPage.setManaged(true);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }

    @FXML
    public void GoToUpdateActivitesPagePagee(){
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(true);
        UpdateAcitivitesPage.setManaged(true);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(false);
        EventsInterface.setManaged(false);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AddEventPage.setVisible(false);
        AddEventPage.setManaged(false);

        UpdateEventPage.setVisible(false);
        UpdateEventPage.setManaged(false);

        ADDAcitivitesPage.setVisible(false);
        ADDAcitivitesPage.setManaged(false);

        UpdateAcitivitesPage.setVisible(false);
        UpdateAcitivitesPage.setManaged(false);

        ActivitesPage.setVisible(false);
        ActivitesPage.setManaged(false);

        ParticipantsPage.setVisible(false);
        ParticipantsPage.setManaged(false);

        EventsInterface.setVisible(true);
        EventsInterface.setManaged(true);


        
    }
}
