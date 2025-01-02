package bgu.spl.mics.application;

import com.google.gson.Gson;
import java.io.FileReader;

import bgu.spl.mics.application.objects.*;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");
        // Parse configuration file.
        Configuration configuration = new Configuration();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(args[0])) {
            configuration = gson.fromJson(reader, Configuration.class);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        // TODO:Initialize system components and services.
      
        // TODO: Start the simulation.
    }
}
