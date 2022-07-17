package admin.adminLoanTables.riskStatus;

import admin.adminLoanTables.InnerTablesRefresher;
import admin.adminLoanTables.adminLoanTablesMain.AdminLoanTablesController;
import admin.adminLoanTables.riskStatus.innerTable.riskInnerTableController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import loan.enums.eLoanStatus;
import servletDTO.admin.AdminLoanObj;
import servletDTO.admin.InnerTableObj;
import util.AddJavaFXCell;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RiskTableController {
    public final static int REFRESH_RATE = 2000;
    private final BooleanProperty autoUpdate = new SimpleBooleanProperty(true);
    ObservableList<AdminLoanObj> loanObservableList = FXCollections.observableArrayList();
    private Timer timer;
    private TimerTask listRefresher;
    private riskInnerTableController innerTableController;
    private AdminLoanTablesController mainTablesController;
    @FXML
    private TableColumn<AdminLoanObj, String> ColumnId;

    @FXML
    private TableColumn<AdminLoanObj, String> ColumnName;

    @FXML
    private TableColumn<AdminLoanObj, eLoanStatus> ColumnStatus;

    @FXML
    private TableColumn<AdminLoanObj, String> ColumnCategory;

    @FXML
    private TableColumn<AdminLoanObj, Double> ColumnAmount;

    @FXML
    private TableColumn<AdminLoanObj, Integer> ColumnTotalYaz;

    @FXML
    private TableColumn<AdminLoanObj, Integer> ColumnPayEvery;

    @FXML
    private TableColumn<AdminLoanObj, Double> ColumnInterest;


    @FXML
    private TableColumn<AdminLoanObj, Integer> ActiveStatusYaz;

    @FXML
    private TableColumn<AdminLoanObj, Integer> NextPaymentColumn;

    @FXML
    private TableView<AdminLoanObj> RiskTable;

    public void setMainController(AdminLoanTablesController mainTablesController) {
        this.mainTablesController = mainTablesController;
    }

    public void initialize() {
        ColumnAmount.setCellValueFactory(new PropertyValueFactory<AdminLoanObj, Double>("totalLoanCostInterestPlusOriginalDepth"));
        ColumnInterest.setCellValueFactory(new PropertyValueFactory<AdminLoanObj, Double>("interestPercentagePerTimeUnit"));
        ColumnCategory.setCellValueFactory(new PropertyValueFactory<AdminLoanObj, String>("loanCategory"));
        ColumnId.setCellValueFactory(new PropertyValueFactory<AdminLoanObj, String>("loanID"));
        ColumnName.setCellValueFactory(new PropertyValueFactory<AdminLoanObj, String>("borrowerName"));
        ColumnPayEvery.setCellValueFactory(new PropertyValueFactory<AdminLoanObj, Integer>("paymentFrequency"));
        ColumnTotalYaz.setCellValueFactory(new PropertyValueFactory<AdminLoanObj, Integer>("originalLoanTimeFrame"));
        ColumnStatus.setCellValueFactory(new PropertyValueFactory<AdminLoanObj, eLoanStatus>("status"));
        ActiveStatusYaz.setCellValueFactory(new PropertyValueFactory<AdminLoanObj, Integer>("startLoanYaz"));
        NextPaymentColumn.setCellValueFactory(new PropertyValueFactory<AdminLoanObj, Integer>("nextYazToPay"));
        AddJavaFXCell.addButtonToTable(RiskTable, this::openLoanDetails, "show", "Lenders&Payments");
    }


    public void initializeTable(List<AdminLoanObj> adminLoanObj) {
        loanObservableList.clear();
        RiskTable.getItems().clear();
        for (AdminLoanObj loanObj : adminLoanObj) {
            if (loanObj.getStatus() == eLoanStatus.RISK) {
                loanObservableList.add(loanObj);
            }
        }
        RiskTable.setItems(loanObservableList);
    }


    private void openLoanDetails(AdminLoanObj adminLoanObj) {
        activeActionHandle(adminLoanObj.getLoanID());
    }


    private void activeActionHandle(String loanName) {
        //create stage
        Stage stage = new Stage();
        stage.setTitle("lenders info");
        //load fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("innerTable/riskInnerTable.fxml"));
        AnchorPane riskInnerTable = null;
        try {
            riskInnerTable = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //get the controller
        innerTableController = fxmlLoader.getController();
        startLoanListRefresher(loanName);
        Scene scene = new Scene(riskInnerTable);
        stage.setScene(scene);
        stage.show();
    }

    private void loadInnerTableData(InnerTableObj innerTableObj) {
        innerTableController.loadTableData(innerTableObj);
    }


    public void startLoanListRefresher(String loanName) {
        listRefresher = new InnerTablesRefresher(
                this::loadInnerTableData,
                autoUpdate,
                loanName
        );
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }


}
