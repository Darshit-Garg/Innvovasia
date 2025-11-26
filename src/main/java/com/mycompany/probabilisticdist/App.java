package com.mycompany.probabilisticdist;

import java.util.Locale;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Random; // This import was in your original App.java
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;
import static javafx.application.Application.launch;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class App extends Application {

    // --- Fields from Controller1.java ---
    @FXML
    private TextField Text1;
    @FXML
    private TextField Text2;
    @FXML
    private TextField Text21;
    @FXML
    private BarChart<String, Number> Bar; // Note: This was a LineChart, not BarChart
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private ComboBox<String> Combo;
    @FXML
    private Label L1;
    @FXML
    private Label L2;
    @FXML
    private Label L3;
    public void initialize(){
        Combo.getItems().addAll(
                "Normal",
                "Uniform",
                "Binomial",
                "Central Limit Theorem, Normal Distribution",
                "Central Limit Theorem, Uniform Distribution"
        );
        // Set an initial selection and update labels
        Combo.getSelectionModel().selectFirst(); 
        updateLabels(Combo.getValue());
        
        // Add listener to change labels when a new item is selected
        Combo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                updateLabels(newValue);
            }
        });
    }
    
    private void updateLabels(String distributionName) {
        switch (distributionName) {
            case "Normal":
                L1.setText("Mean (\u03BC):");
                L2.setText("Std Dev (\u03C3):");
                L3.setText("Samples:");
                Text21.setEditable(true);
                break;
            case "Uniform":
                L1.setText("a (Min):");
                L2.setText("b (Max):");
                L3.setText("Samples:");
                Text21.setEditable(true);
                break;
            case "Binomial":
                L1.setText("n (Trials):");
                L2.setText("p (Prob.):");
                L3.setText("(Null)");
                Text21.setText(""); // Clear the value since it's not used
                Text21.setEditable(false);
                break;
            case "Central Limit Theorem, Normal Distribution":
                L1.setText("Mean (\u03BC):");
                L2.setText("Std Dev (\u03C3):");
                L3.setText("Samples:");
                Text21.setEditable(true);
                break;
            case "Central Limit Theorem, Uniform Distribution":
                L1.setText("a (Min):");
                L2.setText("b (Max):");
                L3.setText("Samples:");
                Text21.setEditable(true);
                break;                
            default:
                L1.setText("Param 1:");
                L2.setText("Param 2:");
                L3.setText("Param 3:");
                Text21.setEditable(true);
                break;
        }
    }
    // --- Methods from App.java ---
    @Override
    public void start(Stage stage) throws Exception {
        // Modified to set this class instance as the controller
        FXMLLoader loader = new FXMLLoader(App.class.getResource("Scene1.fxml"));
        loader.setController(this); // Set the controller to this instance of App
        
        Parent root = loader.load(); // Load the FXML
        
        Scene scene1 = new Scene(root);
        stage.setTitle("Normal Distribution Simulation");
        stage.setScene(scene1);
        stage.show();
    }

        // --- Method from Controller1.java ---
    
    /**
     * This method is called by the FXML when the simulation button is clicked.
     * It reads values from the text fields, calculates the normal distribution
     * points, and plots them on the line chart.
     * @param event The action event from the button click.
     */
    @FXML
    public void simulate(ActionEvent event) {
        if (Bar == null || Text1 == null || Text2 == null || Text21 == null) {
            System.err.println("FXML components are not injected. Check your FXML file.");
            return;
        }
        
        if(Combo.getValue().equals("Normal")){
            try {
                
                double mean;
                double SD;
                int samples;
                mean = Double.parseDouble(Text1.getText());
                SD = Double.parseDouble(Text2.getText());
                samples = Integer.parseInt(Text21.getText());

                if (SD <= 0) {
                    System.err.println("Standard Deviation (SD) must be greater than 0.");
                    return;
                }
                if (samples <= 0) {
                     System.err.println("Number of samples must be greater than 0.");
                    return;
                }

                double minX = mean - 4 * SD; // Cover most of the distribution
                double maxX = mean + 4 * SD;
                double[] x = new double[samples];
                XYChart.Series<String, Number> data = new XYChart.Series<>();
                data.setName("Normal Distribution"); // Added a name for clarity

                for (int i = 0; i < samples; i++) {
                    double u1 = ThreadLocalRandom.current().nextDouble();
                    double u2 = ThreadLocalRandom.current().nextDouble();
                    double z0 = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
                    x[i] = z0*SD + mean;
                }

                int bars = 50;

                double min = DoubleStream.of(x).min().getAsDouble();
                double max = DoubleStream.of(x).max().getAsDouble();
                if (min == max) {
                    bars = 1;
                    min = min - 0.5;
                    max = max + 0.5;
                }
                double step = (maxX - minX) / bars;
                long[] y = new long[bars];
                for (double v : x) {
                    int idx = (int) ((v - min) / step);
                    if (idx < 0) idx = 0;
                    if (idx >= bars) idx = bars - 1;
                    y[idx]++;
                }  
                for (int i = 0; i < bars; i++) {
                    double left = minX + i * step;
                    double right = left + step;
                    String label = String.format(Locale.US, "%.2f - %.2f", left, right);
                    data.getData().add(new XYChart.Data<>(label, y[i]));
                }

                yAxis.setLabel("Frequency");


                Bar.getData().clear();
                Bar.getData().add(data);    


            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter valid numbers for mean, SD, and samples.");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("An error occurred during simulation.");
                e.printStackTrace();
            }
        }
        else if(Combo.getValue().equals("Uniform")){
            try {
                double a;
                double b;
                int samples;
                L1.setText("a:");
                L2.setText("b:");
                L3.setText("Samples: ");
                Text21.setEditable(true);
                a = Double.parseDouble(Text1.getText());
                b = Double.parseDouble(Text2.getText());
                samples = Integer.parseInt(Text21.getText());

                if (b<a){
                    System.out.println("b cant be less than a");
                }

                double[] x = new double[samples];
                XYChart.Series<String, Number> data = new XYChart.Series<>();
                data.setName("Normal Distribution"); // Added a name for clarity


                int bars = 50;
                double step = (b - a) / bars;
                
                for (int i = 0; i < samples; i++) {
                    x[i] = ThreadLocalRandom.current().nextDouble() * (b - a) + a;
                }

                double min = DoubleStream.of(x).min().getAsDouble();
                double max = DoubleStream.of(x).max().getAsDouble();

                
                if (min == max) {
                    bars = 1;
                    min = min - 0.5;
                    max = max + 0.5;
                }
                
                long[] y = new long[bars];
                for (double v : x) {
                    int idx = (int) ((v - min) / step);
                    if (idx < 0) idx = 0;
                    if (idx >= bars) idx = bars - 1;
                    y[idx]++;
                }  
                for (int i = 0; i < bars; i++) {
                    double left = min + i * step;
                    double right = left + step;
                    String label = String.format(Locale.US, "%.2f - %.2f", left, right);
                    data.getData().add(new XYChart.Data<>(label, y[i]));
                }

                yAxis.setLabel("Frequency");

                Bar.getData().clear();
                Bar.getData().add(data);    


            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter valid numbers for mean, SD, and samples.");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("An error occurred during simulation.");
                e.printStackTrace();
            }
        }
        else if(Combo.getValue().equals("Binomial")){
            try {
                int n;
                double p;
                int samples;
                L1.setText("n:");
                L2.setText("p:");
                n = Integer.parseInt(Text1.getText());
                p = Double.parseDouble(Text2.getText());
                
                L3.setText("(Null)");
                Text21.setEditable(false);

                XYChart.Series<String, Number> data = new XYChart.Series<>();
                data.setName("Binomial Distribution"); // Added a name for clarity

                int bars = 50;
                
                RandomGenerator random = new Well19937c(); 
                
                BinomialDistribution bin = new BinomialDistribution(random, n, p);
                
                double[] x = new double[n];
                
                for(int i = 0; i < n; i++){
                    x[i] = bin.sample();
                }
                
                double min = DoubleStream.of(x).min().getAsDouble();
                double max = DoubleStream.of(x).max().getAsDouble();
                
                double step = (max-min)/bars;
                
                long[] y = new long[bars];
                for (double v : x) {
                    int idx = (int) ((v - min) / step);
                    if (idx < 0) idx = 0;
                    if (idx >= bars) idx = bars - 1;
                    y[idx]++;
                }  
                for (int i = 0; i < bars; i++) {
                    double left = min + i * step;
                    double right = left + step;
                    String label = String.format(Locale.US, "%.2f - %.2f", left, right);
                    data.getData().add(new XYChart.Data<>(label, y[i]));
                }

                yAxis.setLabel("Frequency");


                Bar.getData().clear();
                Bar.getData().add(data);    


            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter valid numbers for mean, SD, and samples.");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("An error occurred during simulation.");
                e.printStackTrace();
            }
        }
        else if(Combo.getValue().equals("Central Limit Theorem, Uniform Distribution")){
            try {
                double a;
                double b;
                int samples;
                L1.setText("a:");
                L2.setText("b:");
                L3.setText("Samples: ");
                Text21.setEditable(true);
                a = Double.parseDouble(Text1.getText());
                b = Double.parseDouble(Text2.getText());
                samples = Integer.parseInt(Text21.getText());

                if (b<a){
                    System.out.println("b cant be less than a");
                }

                double[] x = new double[samples];
                XYChart.Series<String, Number> data = new XYChart.Series<>();
                data.setName("Normal Distribution"); // Added a name for clarity

                for (int i = 0; i < samples; i++) {
                    int sum = 0;
                    for(int j = 0; j < 10; j++)
                    {
                        sum+=(ThreadLocalRandom.current().nextDouble() * (b - a) + a);
                    }
                    x[i] = sum/30;
                }

                int bars = 100; // Increased for a smoother bell curve
                double min = DoubleStream.of(x).min().getAsDouble();
                double max = DoubleStream.of(x).max().getAsDouble();

                if (min == max) {
                    bars = 1;
                    min = min - 0.5;
                    max = max + 0.5;
                }
                
                double step = (max - min) / bars;
                
                long[] y = new long[bars];
                for (double v : x) {
                    int idx = (int) ((v - min) / step);
                    if (idx < 0) idx = 0;
                    if (idx >= bars) idx = bars - 1;
                    y[idx]++;
                }  
                for (int i = 0; i < bars; i++) {
                    double left = min + i * step;
                    double right = left + step;
                    String label = String.format(Locale.US, "%.2f - %.2f", left, right);
                    data.getData().add(new XYChart.Data<>(label, y[i]));
                }

                yAxis.setLabel("Frequency");
                Bar.getData().clear();
                Bar.getData().add(data);    
            }
            
            catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter valid numbers for mean, SD, and samples.");
            }
            
            catch (Exception e) {
                System.err.println("An error occurred during simulation.");
            }
        }
        else if(Combo.getValue().equals("Central Limit Theorem, Normal Distribution")){
            try {
                
                double mean;
                double SD;
                int samples;
                mean = Double.parseDouble(Text1.getText());
                SD = Double.parseDouble(Text2.getText());
                samples = Integer.parseInt(Text21.getText());

                if (SD <= 0) {
                    System.err.println("Standard Deviation (SD) must be greater than 0.");
                    return;
                }
                if (samples <= 0) {
                     System.err.println("Number of samples must be greater than 0.");
                    return;
                }

                double minX = mean - 4 * SD; // Cover most of the distribution
                double maxX = mean + 4 * SD;
                double[] x = new double[samples];
                XYChart.Series<String, Number> data = new XYChart.Series<>();
                data.setName("Normal Distribution"); // Added a name for clarity

                for (int i = 0; i < samples; i++) {
                    int sum = 0;
                    for(int j = 0; j < 30; j++)
                    {
                        double u1 = ThreadLocalRandom.current().nextDouble();
                        double u2 = ThreadLocalRandom.current().nextDouble();
                        double z0 = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
                        sum+=z0*SD + mean;
                    }
                    x[i] = sum/30;
                }

                int bars = 50;

                double min = DoubleStream.of(x).min().getAsDouble();
                double max = DoubleStream.of(x).max().getAsDouble();
                if (min == max) {
                    bars = 1;
                    min = min - 0.5;
                    max = max + 0.5;
                }
                double step = (maxX - minX) / bars;
                long[] y = new long[bars];
                for (double v : x) {
                    int idx = (int) ((v - min) / step);
                    if (idx < 0) idx = 0;
                    if (idx >= bars) idx = bars - 1;
                    y[idx]++;
                }  
                for (int i = 0; i < bars; i++) {
                    double left = minX + i * step;
                    double right = left + step;
                    String label = String.format(Locale.US, "%.2f - %.2f", left, right);
                    data.getData().add(new XYChart.Data<>(label, y[i]));
                }

                yAxis.setLabel("Frequency");


                Bar.getData().clear();
                Bar.getData().add(data);    


            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter valid numbers for mean, SD, and samples.");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("An error occurred during simulation.");
                e.printStackTrace();
            }
        }

    }
    public static void main(String[] args) {
        launch(args);
    }
}