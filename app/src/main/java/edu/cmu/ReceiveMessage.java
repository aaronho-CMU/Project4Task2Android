package edu.cmu;

public class ReceiveMessage {
    String status;
    String message;
    String distance;
    String total_carbon_footprint_grams;
    String carbon_footprint_permile_grams;
    String total_carbon_footprint_tons;
    String carbon_footprint_permile_tons;

    public ReceiveMessage(String distance, String total_carbon_footprint_grams, String carbon_footprint_permile_grams, String total_carbon_footprint_tons, String carbon_footprint_permile_tons) {
        this.distance = distance;
        this.total_carbon_footprint_grams = total_carbon_footprint_grams;
        this.carbon_footprint_permile_grams = carbon_footprint_permile_grams;
        this.total_carbon_footprint_tons = total_carbon_footprint_tons;
        this.carbon_footprint_permile_tons = carbon_footprint_permile_tons;
    }
}
