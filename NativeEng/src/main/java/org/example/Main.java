package org.example;

import com.google.inject.Guice;
import org.example.configurations.ConfigModule;

public class Main {
    public static void main(String[] args) {
        try (ConfigModule configModule = new ConfigModule()) {
            Guice.createInjector(configModule)
                    .getInstance(App.class)
                    .run();
        } catch (Exception exception) {
            System.out.println("Program terminated: " + exception.getMessage());
        }
    }
}