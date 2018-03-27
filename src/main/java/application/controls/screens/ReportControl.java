package application.controls.screens;

import application.controls.MainPanelControl;
import application.controls.ScreenLoader;
import application.reports.AppointmentByMonth;
import application.reports.AppointmentCountByConsultant;
import application.reports.ConsultantSchedule;
import application.services.IApplicationState;
import application.services.IReportContext;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import models.Appointment;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;

public class ReportControl extends MainPanelControl {

    private final IReportContext reportContext;
    private final IApplicationState state;
    private final Label label = new Label();
    private final HBox hbox = new HBox();
    private final ComboBox cboReports = new ComboBox();

    public ReportControl(IReportContext reportContext, IApplicationState state) {

        this.reportContext = reportContext;
        this.state = state;

        ScreenLoader.load("/views/ReportView.fxml", this, this);

    }

    @FXML
    public void initialize() {

        label.setText("Select Report: ");


        ObservableList<String> reports = FXCollections.observableArrayList();
        reports.add("Appointments By Month");
        reports.add("Consultant Schedule");
        reports.add("Consultant Appointment Totals");
        cboReports.setItems(reports);

        cboReports.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (null == newValue) {
                    loadAppointmentCountByConsultant();
                } else {
                    switch (newValue) {
                        case "Appointments By Month":
                            loadAppointmentsByMonth();
                            break;
                        case "Consultant Schedule":
                            loadConsultantSchedule();
                            break;
                        default:
                            loadAppointmentCountByConsultant();
                            break;
                    }
                }
            }
        });

        hbox.getChildren().add(label);
        hbox.getChildren().add(cboReports);
        this.setTop(hbox);
    }
    
    public void load(){
        
        cboReports.getSelectionModel().select(0);
    }

    private void loadAppointmentsByMonth() {

        this.setCenter(null);
        this.setCenter(getAppointmentByMonth());
    }

    private void loadConsultantSchedule() {
        this.setCenter(null);
        this.setCenter(getConsultantSchedule());
    }

    private void loadAppointmentCountByConsultant() {
        this.setCenter(null);
        this.setCenter(getAppointmentCountByConsultant());
    }

    private void createColumns(TableView tableView, ArrayList<String> columnList, boolean isSortable) {

        final int col = columnList.size();

        for (int i = 0; i < col; i++) {
            String name = columnList.get(i);
            TableColumn<Map<Integer, String>, String> tableColumn = new TableColumn<>(name);
            tableColumn.setCellValueFactory(new PropertyValueFactory(name));
            tableColumn.setSortable(isSortable);
            tableView.getColumns().add(tableColumn);
        }
    }

    private TableView getAppointmentByMonth() {

        TableView tableView = new TableView();
        createColumns(tableView, AppointmentByMonth.getColumns(), true);
        ObservableList<AppointmentByMonth> dataRows = getAppointmentByMonthRows();
        tableView.setItems(dataRows);

        return tableView;
    }

    private TableView getConsultantSchedule() {
        TableView tableView = new TableView();

        createColumns(tableView, ConsultantSchedule.getColumns(), true);

        ObservableList<ConsultantSchedule> dataRows = getConsultantAppointmentsRows();
        tableView.setItems(dataRows);

        return tableView;
    }

    private TableView getAppointmentCountByConsultant() {
        TableView tableView = new TableView();

        createColumns(tableView, AppointmentCountByConsultant.getColumns(), false);

        ObservableList<AppointmentCountByConsultant> dataRows = getAppointmentCountByConsultantRows();
        tableView.setItems(dataRows);

        return tableView;
    }

    private ObservableList<AppointmentByMonth> getAppointmentByMonthRows() {

        ObservableList<AppointmentByMonth> dataList = FXCollections.observableArrayList();

        SortedSet<Appointment> list = reportContext.getAppointments();

        Map<Month, List<Appointment>> groupedList = list.stream().collect(Collectors.groupingBy(w -> w.getStart().getMonth()));
        for (Map.Entry<Month, List<Appointment>> entry : groupedList.entrySet()) {
            Month month = entry.getKey();
            List<Appointment> apps = entry.getValue();

            for (Appointment app : apps) {
                dataList.add(new AppointmentByMonth(app));
            }
        }

        return dataList;
    }

    private ObservableList<ConsultantSchedule> getConsultantAppointmentsRows() {

        ObservableList<ConsultantSchedule> dataList = FXCollections.observableArrayList();

        SortedSet<Appointment> list = reportContext.getAppointments();
        for (Appointment app : list) {
            if (state.getLoggedInUser().getName().equals(app.getAudit().getCreatedBy())) {
                dataList.add(new ConsultantSchedule(app));
            }
        }

        return dataList;
    }

    private ObservableList<AppointmentCountByConsultant> getAppointmentCountByConsultantRows() {

        ObservableList<AppointmentCountByConsultant> dataList = FXCollections.observableArrayList();

        SortedSet<Appointment> list = reportContext.getAppointments();
        Map<String, List<Appointment>> consultantList
                = list.stream().collect(Collectors.groupingBy(w -> w.getAudit().getCreatedBy()));

        for (Map.Entry<String, List<Appointment>> conMap : consultantList.entrySet()) {
            String consultant = conMap.getKey();
            int total = conMap.getValue().size();
            dataList.add(new AppointmentCountByConsultant(consultant, total));

            Map<Integer, List<Appointment>> yearList
                    = conMap.getValue().stream().collect(Collectors.groupingBy(w -> w.getStart().getYear()));

            for (Map.Entry<Integer, List<Appointment>> yearMap : yearList.entrySet()) {
                int year = yearMap.getKey();
                int yearTotal = yearMap.getValue().size();

                dataList.add(new AppointmentCountByConsultant(consultant, year, yearTotal));

                Map<Month, List<Appointment>> monthList
                        = yearMap.getValue().stream().collect(Collectors.groupingBy(m -> m.getStart().getMonth()));
                for (Map.Entry<Month, List<Appointment>> monthMap : monthList.entrySet()) {
                    Month month = monthMap.getKey();
                    int monthTotal = monthMap.getValue().size();

                    dataList.add(new AppointmentCountByConsultant(consultant, month, monthTotal));

                }
            }
        }

        return dataList;
    }

    @Override
    public void setContentSize(ReadOnlyObjectProperty<Bounds> readOnlyBounds) {

    }
}
