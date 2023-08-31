package com.mycompany.csc311.homework4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

/**
 * 
 * @author Elan
 */
public class PrimaryController implements Initializable {
    @FXML
    private Label labelAmount;
    @FXML
    private Label labelTotal;     
    @FXML
    private TextField textQuantity;
    @FXML
    private ListView listViewSales;
    @FXML
    private MenuItem loadDBMenuItem;
    @FXML    
    private MenuItem saveJSONMenuItem;
    @FXML
    private MenuItem ClearDBJSONMenuItem;    
    @FXML
    private MenuItem exitMenuItem;    
    @FXML
    private Button addSaleButton;
    @FXML
    private Button buttonDescOrder;   
    @FXML
    private Button getTotalButton;    
    @FXML
    private ComboBox<String> DishCombobox;
    @FXML
    private ComboBox<String> PaymentCombobox;   

    //Initalize variables
    String payment = "";
    double price = 0;
    double quantity = 0;
    double amount = 0;
    String databaseURL = "";
    Connection conn = null;
    List<Double> saleAmount = new ArrayList<>();

    //Program Spec - This uses JDBC to communicate with database.
    
    /**
     * This connects to Access Database
     */
    public void connectDB() {
        try {
            databaseURL = "jdbc:ucanaccess://.//Data.accdb";
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }        
    }
    
    //Program Spec - This displays data to the user in the GUI And imports data from JSON file into database.
    //Program Spec - This uses Two Arraylists as collections, one for the JSON data and one for the sales data.
    
    /**
     * This loads the database from JSON file
     * Calls connectDB() method for Database Connection.
     * @throws FileNotFoundException
     * @throws SQLException 
     */
    public void loadDB() throws FileNotFoundException, SQLException {
        //Connects to Access Database
        connectDB();

        //Creates List to store the JSON data in.
        List<Sale> list = new ArrayList<>();
        //Collection of data that is used by the Listview to view the data.
        ObservableList sales = listViewSales.getItems();
        //Clears Listview to prevent duplicates.
        sales.clear();
        //Clears Sales Amount Arraylist to prevent duplicates calculations.
        saleAmount.clear();
        
        //Reads Json file
        FileReader fr = new FileReader("Sale.json");
        //Creates Gson object
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        //Loads Json data into ArrayList
        list = gson.fromJson(fr, new TypeToken<ArrayList<Sale>>() {}.getType());
        
        //Deletes Database Data prior to loading Json data into Database.
        String sql = "DELETE FROM Sale";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        int rowsDeleted = preparedStatement.executeUpdate();
        System.out.println("Rows deleted " + rowsDeleted);
    
        //This loop puts the Json data into the variables using Sale class.
        for (int i = 0; i < list.size(); i++) {
            //Created a Sale class object to call getters for Date, Amount, and Payment Type.
            Sale s = new Sale();
            //Gets elements from list and put into class object
            s = list.get(i);
            //Create variables to store Sale Objects
            String dates = s.getDate();
            double amount = s.getAmount();
            String payment = s.getPayment();
            
            //Inserts into the Database using the variables username, message, and likes.
            try {
                sql = "INSERT INTO Sale(Dates, Amount, PaymentType) VALUES (?, ?, ?)";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, dates);
                preparedStatement.setDouble(2, amount);
                preparedStatement.setString(3, payment);

                int row = preparedStatement.executeUpdate();
                if (row > 0) {
                    System.out.println("Row inserted");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String saleEntry = dates + "\t\t" + String.format("$%.2f", amount) + "\t\t" + (payment);
            //Add entry to Listview
            sales.add(saleEntry);  
            //Adds sales to Arraylist
            saleAmount.add(amount);      
        }
        System.out.println("Successfully Loaded From JSON");   
        //Sets Total label back to default position and value.
        labelTotal.setLayoutX(0);
        labelTotal.setLayoutY(612);
        labelTotal.setScaleX(1);
        labelTotal.setScaleY(1);
        labelTotal.setText("Total Sales: $0.00");
    }
    
    /**
     * Clears the Database.
     * Calls connectDB() method for Database Connection.
     * @throws SQLException 
     */
    public void clearDB() throws SQLException {
        //Connects to Access Database
        connectDB();
        //Deletes Database Data
        String sql = "DELETE FROM Sale";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        int rowsDeleted = preparedStatement.executeUpdate();
        System.out.println("Rows deleted " + rowsDeleted);          
    }
    
    //Program Spec - This exports sale entry data to the database.
    
    /**
     * Adds sale entry to Database and Listview.
     * Calls connectDB() method for Database Connection.
     */
    public void addSale() {
        //Getting the current date value
        LocalDate currentdate = LocalDate.now();
        //Getting the current day
        int currentDay = currentdate.getDayOfMonth();
        //Getting the current month
        Month currentMonth = currentdate.getMonth();
        //Getting the current year
        int currentYear = currentdate.getYear();
        //Puts date format into variable date
        String date = currentMonth + " " + currentDay + "," + currentYear;
        
        //Collection of data that is used by the Listview to view the data.
        ObservableList sales = listViewSales.getItems();
        
        //Connects to Access Database
        connectDB();
        
        //Inserts into database the date, amount, and payment.
        try {
            String sql = "INSERT INTO Sale(Dates, Amount, PaymentType) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            //Current date generated upon call of this method.
            preparedStatement.setString(1, date);
            //Amount generated from calculations made by textQuantity method.
            preparedStatement.setDouble(2, amount);
            //Payment type generated from item selected in Payment Combobox.
            preparedStatement.setString(3, (payment));
            //Adds the calculated amount to Global ArrayList saleAmount.
            saleAmount.add(amount);
            System.out.println("the amount is: " + amount);
            //Updates the database.
            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                System.out.println("Row inserted");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //Adds entry into ListView.
        String saleEntry = date + "\t\t" + String.format("$%.2f", amount) + "\t\t" + payment;
        sales.add(saleEntry);
        //Reset counters upon adding a entry.
        resetCounters(); 
    }
    
    //Program Spec - This exports JSON data to the database.
    
    /**
     * This saves to the JSON from the database.
     * Calls connectDB() method for Database Connection.
     * @throws FileNotFoundException 
     */
    public void saveToJson() throws FileNotFoundException {
        //Connects to Access Database
        connectDB();
        
        //Creates ArrayList to store the Database data in.
        ArrayList<Sale> list = new ArrayList<>();
        
        //Creates Gson object
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        //Creates or Writes to a JSON file.
        PrintStream ps = new PrintStream("Sale.json");

        //Loads from database and puts data into result
        try {
            String tableName = "Sale";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("Select * from " + tableName);

            //while there is data in result
            //Adds data to ArrayList
            while (result.next()) {
                Sale s = new Sale();
                String dates = result.getString("Dates");
                double amount = result.getDouble("Amount");
                String payment = result.getString("PaymentType");
                s.setDate(dates);
                s.setAmount(amount);
                s.setPayment(payment);
                list.add(s);
            }
            //Creates string variable converting gson to JSON
            String jsonString = gson.toJson(list);
            //Writes to JSON file
            ps.println(jsonString);
            System.out.println("jsonstring is " + jsonString);
            //Clears Arraylist to prevent duplicates
            list.clear();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println("Successfully Saved To JSON");        
    }
    
    /**
     * Resets all counters and labels.
     */
    public void resetCounters() {
        quantity = 0;
        textQuantity.setText("");
        amount = 0;
        labelAmount.setText("$0.00");
    }
    
    //Program Spec - This uses Streams and Lambda for the calculation of sales and adds them up.
    
    /**
     * Uses a Stream to do add up all the sales and sets total to label.
     */
    public void sumTotal() {
        //Performs Reduction on sales and adds it all up.
        double sum = saleAmount.stream().mapToDouble(x -> x).sum();
        labelTotal.setText("Total Sales: " + String.format("$%.2f", sum));
    }
    
    //Program Spec - This uses Animations to make the label move to the right, fade, and scale.
    
    /**
     * Sets default position for label for Total.
     * Performs a ParallelTransition making it fade, move to the right and scale at the same time.
     */
    public void runAnimation() {
        //Default position
        labelTotal.setLayoutX(0);
        labelTotal.setLayoutY(612);
        //Makes the label really small
        labelTotal.setScaleX(0);
        labelTotal.setScaleY(0);     
        //Makes the label invisible then appears
        FadeTransition ft = new FadeTransition(Duration.seconds(1), labelTotal);
        ft.setFromValue(1);
        ft.setToValue(0.0);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        //Moves the label to the right
        KeyValue firstKey = new KeyValue(labelTotal.layoutXProperty(), 550);
        KeyFrame firstFrame = new KeyFrame(Duration.seconds(2), firstKey);
        Timeline firstTime = new Timeline(firstFrame);
        //Makes the label bigger
        ScaleTransition sc = new ScaleTransition(Duration.seconds(1), labelTotal);
        sc.setByX(3);
        sc.setByY(3);
        //Performs FadeTransition, then moves it to the right, and scales it at the same time.
        ParallelTransition pt = new ParallelTransition(ft, firstTime, sc);
        pt.play();          
    }
    
    //Program Spec - This uses thread to load the database.
    
    /**
     * Uses thread to process the database load.
     * Calls loadDB() method to load the database from JSON.
     */
    public void longRunningOperation() 
    {
        System.out.println("Long running operation started");
        Platform.runLater(() -> 
        {
            try 
            {
                loadDB();
                String tName = Thread.currentThread().getName();
                System.out.printf("Message from %s\n", tName);
            } catch (FileNotFoundException | SQLException ex) {
            }
        });
        System.out.println("Long running operation ended");
    }
    /**
     * Gets user input for quantity then uses a listener for the textfield.
     * Does calculation for quantity and price for the amount.
     * If focus is lost from the textfield then it updates the label for amount.
     */
    public void getTextQuantity() {
        try {
        //Converts userinput from textfield into integer.
        quantity = Double.parseDouble(textQuantity.getText().trim());
        //Does calculation for amount
        amount = price * quantity;
        //Converts format into 2 decimal positions
        String str = String.format("$%.2f", amount);
        System.out.println(str);
        //Listener for textfield, if focus is lost from textfield then it updates the label.
        textQuantity.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) 
            {
                if (!"".equals(textQuantity.getText())) 
                {
                    labelAmount.setText((str));
                } else {
                    labelAmount.setText("");
                }
            }
        });   
        }catch (Exception e) {
            resetCounters();
        }         
    }
    
    /**
     * Initalizes the Dish Combobox with 5 items.
     * Initalizes the Payment Combobox with 3 items.
     * @param url
     * @param resourceBundle 
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DishCombobox.setItems(FXCollections.observableArrayList("Chicken Wings", "Ribeye Steak", "Chicken Tenders", "Cheese Burger", "Grilled Chicken Salad"));        
        PaymentCombobox.setItems(FXCollections.observableArrayList("Debit", "Credit", "Cash"));
    }    
    
    /**
     * If item is selected in Combobox then update value of price to the Price for the Dish.
     * @param event 
     */
    @FXML
    private void DishComboboxHandler(ActionEvent event) {
        if (DishCombobox.getSelectionModel().getSelectedItem().equals("Chicken Wings")) {
            price = 12;
            System.out.println("Chicken Wings is selected, price is: " + price);
            resetCounters();
        } else if (DishCombobox.getSelectionModel().getSelectedItem().equals("Ribeye Steak")) {
            price = 23.99;
            System.out.println("Ribeye Steak is selected, price is: " + price);
            resetCounters();
        } else if (DishCombobox.getSelectionModel().getSelectedItem().equals("Chicken Tenders")) {
            price = 12.79;
            System.out.println("Chicken Tenders is selected, price is: " + price);
            resetCounters();
        } else if (DishCombobox.getSelectionModel().getSelectedItem().equals("Cheese Burger")) {
            price = 12.79;
            System.out.println("Cheese Burger is selected, price is: " + price);
            resetCounters();
        } else if (DishCombobox.getSelectionModel().getSelectedItem().equals("Grilled Chicken Salad")) {
            price = 12.49;
            System.out.println("Grilled Chicken Salad is selected, price is: " + price);
            resetCounters();
        }
        System.out.println(price);
        System.out.println(quantity);
        System.out.println(amount);
    }
    
    /**
     * If item is selected in Combobox then update value of payment to the Payment Type.
     * @param event 
     */
    @FXML
    private void PaymentComboboxHandler(ActionEvent event) {
        if (PaymentCombobox.getSelectionModel().getSelectedItem().equals("Debit")) {
            payment = "Debit";
            System.out.println("Debit is selected, Payment Type is: " + payment);
        } else if (PaymentCombobox.getSelectionModel().getSelectedItem().equals("Credit")) {
            payment = "Credit";
            System.out.println("Credit is selected, Payment Type is: " + payment);
        } else if (PaymentCombobox.getSelectionModel().getSelectedItem().equals("Cash")) {
            payment = "Cash";
            System.out.println("Cash is selected, Payment Type is: " + payment);
        }     
    }       
    
    /**
     * User Input for Quantity Textfield Handler
     * Calls getTextQuantity() method to update quantity and do calculation for amount.
     * Updates label with amount.
     * @param event 
     */
    @FXML
    private void textQuantityHandler(KeyEvent event) {
        getTextQuantity();
    }
    
    /**
     * Add Sale Entry Button Handler
     * Calls addSale() method to add a Sale Entry to Database and Listview.
     * @param event 
     */
    @FXML
    private void addSaleButtonHandler(ActionEvent event) {
        addSale();
    }

    /**
     * Get Total Button Handler
     * Calls sumTotal() method to add all sales.
     * Calls runAnimation() method to have the label move to the right with a Fadetransition and Scaletransition.
     * @param event 
     */
    @FXML
    private void getTotalButtonHandler(ActionEvent event) {
        sumTotal(); 
        runAnimation();   
    }    
    
    /**
     * Descending Order Button Handler
     * This sorts the data in the Listview in Descending order.
     * @param event 
     */
    @FXML
    private void buttonDescOrderHandler(ActionEvent event) {
      ObservableList sales = listViewSales.getItems();
      Collections.reverse(sales);
    }  
        
    /**
     * Load Database with thread MenuItem Handler
     * Calls longRunningOperation() method to load database with thread.
     * @param event
     * @throws FileNotFoundException
     * @throws SQLException 
     */
    @FXML
    private void loadDBMenuItemHandler(ActionEvent event) {
        Thread t = new Thread(() -> {
            longRunningOperation();
        });
        t.start();
    }

    /**
     * Save To JSON From Database MenuItem Handler
     * Calls saveToJson() method to save database data into JSON file.
     * @param event
     * @throws FileNotFoundException 
     */
    @FXML
    private void saveJSONMenuItemHandler(ActionEvent event) throws FileNotFoundException {
        saveToJson();
    }

    /**
     * Clear Database & JSON MenuItem Handler
     * Calls clearDB() method to clear the database.
     * Calls saveToJson() method to save database data into JSON file.
     * Calls loadDB() method to load the database from JSON and reset GUI.
     * @param event
     * @throws FileNotFoundException
     * @throws SQLException 
     */
    @FXML
    private void ClearDBJSONMenuItemHandler(ActionEvent event) throws FileNotFoundException, SQLException {
        clearDB();
        saveToJson();
        loadDB();        
    }
    
    /**
     * Exit Program MenuItem Handler
     * This exits the program
     * @param event 
     */
    @FXML
    private void exitMenuItemHandler(ActionEvent event) {
        System.exit(0);
    }
}
