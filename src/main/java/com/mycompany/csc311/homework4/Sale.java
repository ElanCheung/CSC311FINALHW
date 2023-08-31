/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csc311.homework4;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Elan
 */
public class Sale {
    @SerializedName("saledate")
    private String date;
    @SerializedName("saleamount")
    private double amount;
    @SerializedName("paymenttype")
    private String payment;
    
    /**
     * No-Arg Constructor constructs a Sale object and initializes date,
     * amount and payment to default.
     */    
    public Sale() 
    {
        date = "";
        amount = 0;
        payment = "";
    }
    
    /**
     * Constructor constructs a Sale object and initializes date, amount, and
     * payment according to arguments.
     * 
     * @param date The date of the Sale.
     * @param amount The amount of the Sale.
     * @param payment The payment of the Sale.
     */
    public Sale(String date, double amount, String payment) {
        this.date = date;
        this.amount = amount;
        this.payment = payment;
    }
    
    /**
     * Returns the value in date.
     * 
     * @return The reference in the date field.
     */
    public String getDate() {
        return date;
    }
    
    /**
     * Sets the date field according to a String argument
     * 
     * @param date The string to store in date.
     */
    public void setDate(String date) {
        this.date = date;
    }
    
    /**
     * Returns the value in amount.
     * 
     * @return The double in the amount field. 
     */
    public double getAmount() {
        return amount;
    }
    
    /**
     * Sets the amount field according to a double argument.
     * 
     * @param amount The double to store in amount.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    /**
     * Returns the value in payment.
     * 
     * @return The reference in the payment field.
     */
    public String getPayment() {
        return payment;
    }
    
    /**
     * Sets the payment field according to a String argument.
     * 
     * @param payment The string to store in payment.
     */
    public void setPayment(String payment) {
        this.payment = payment;
    }
}
