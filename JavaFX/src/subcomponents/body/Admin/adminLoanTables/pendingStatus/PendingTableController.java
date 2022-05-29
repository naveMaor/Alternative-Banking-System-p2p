package subcomponents.body.Admin.adminLoanTables.pendingStatus;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import loan.Loan;
import loan.enums.eLoanStatus;
import subcomponents.body.Admin.adminLoanTables.adminLoanTablesMain.adminLoanTablesController;
import subcomponents.body.Admin.adminLoanTables.pendingStatus.innerTable.PendingInnerTableController;
import utills.Engine;

import java.io.IOException;

public class PendingTableController {

    Engine engine =Engine.getInstance();

    private adminLoanTablesController mainTablesController;

    public void setMainController(adminLoanTablesController mainTablesController){
        this.mainTablesController = mainTablesController;
    }
    @FXML
    private TableView<Loan> PendingTable;

    @FXML
    private TableColumn<Loan, String> ColumnId;

    @FXML
    private TableColumn<Loan, String> ColumnName;

    @FXML
    private TableColumn<Loan, eLoanStatus> ColumnStatus;

    @FXML
    private TableColumn<Loan, String> ColumnCategory;

    @FXML
    private TableColumn<Loan, Double> ColumnAmount;

    @FXML
    private TableColumn<Loan, Integer> ColumnTotalYaz;

    @FXML
    private TableColumn<Loan, Integer> ColumnPayEvery;

    @FXML
    private TableColumn<Loan, Double> ColumnInterest;

    @FXML
    private TableColumn<Loan, Button> LendersColumn;





    @FXML
    private TableColumn<Loan, Double> RaisedColmun;

    @FXML
    private TableColumn<Loan, Double> LeftToMakeActive;

    ObservableList<Loan> loanObservableList;




    public void addButtonToTable(TableView<Loan> table) {
        TableColumn<Loan, Void> colBtn = new TableColumn("Button Column");

        Callback<TableColumn<Loan, Void>, TableCell<Loan, Void>> cellFactory = new Callback<TableColumn<Loan, Void>, TableCell<Loan, Void>>() {
            @Override
            public TableCell<Loan, Void> call(final TableColumn<Loan, Void> param) {
                final TableCell<Loan, Void> cell = new TableCell<Loan, Void>() {

                    private final Button btn = new Button("Action");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Loan data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getStatus());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);

        table.getColumns().add(colBtn);

    }

    public void initializeTable() {
        ColumnAmount.setCellValueFactory(new PropertyValueFactory<Loan, Double>("loanOriginalDepth"));
        ColumnInterest.setCellValueFactory(new PropertyValueFactory<Loan, Double>("interestPercentagePerTimeUnit"));
        LeftToMakeActive.setCellValueFactory(new PropertyValueFactory<Loan, Double>("missingMoney"));
        RaisedColmun.setCellValueFactory(new PropertyValueFactory<Loan, Double>("totalRaisedDeposit"));
        ColumnCategory.setCellValueFactory(new PropertyValueFactory<Loan, String>("loanCategory"));
        ColumnId.setCellValueFactory(new PropertyValueFactory<Loan, String>("loanID"));
        ColumnName.setCellValueFactory(new PropertyValueFactory<Loan, String>("borrowerName"));
        ColumnPayEvery.setCellValueFactory(new PropertyValueFactory<Loan, Integer>("paymentFrequency"));
        ColumnTotalYaz.setCellValueFactory(new PropertyValueFactory<Loan, Integer>("originalLoanTimeFrame"));
        ColumnStatus.setCellValueFactory(new PropertyValueFactory<Loan, eLoanStatus>("status"));
        LendersColumn.setCellValueFactory(new PropertyValueFactory<Loan, Button>("infoButton"));


        loanObservableList = engine.getDatabase().o_getAllLoansByStatus(eLoanStatus.PENDING);
        for (Loan loan:loanObservableList){
            loan.getInfoButton().setOnAction(event -> ActiveActionHandle(loan));
        }
        PendingTable.getItems().clear();
        PendingTable.setItems(loanObservableList);
        //mainTablesController.addButtonToTable(PendingTable);

    }
    public void ActiveActionHandle(Loan loan){
        //create stage
        Stage stage = new Stage();
        stage.setTitle("lenders info");
        //load fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("innerTable/PendingInnerTable.fxml"));
        AnchorPane pendingInnerTable = null;
        try {
            pendingInnerTable = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //get the controller
        PendingInnerTableController innerTableController = fxmlLoader.getController();
        innerTableController.initializeTable(loan.getLendersList());
        Scene scene = new Scene(pendingInnerTable);
        stage.setScene(scene);
        stage.show();
    }

}
