package com.fcgo.eft.sutra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class EftFcgoSuTraApplication {

    public static void main(String[] args) {
        File file;
        if (args.length == 0) {
            file = new File("/home/tomcat/config/application.properties");
        } else {
            file = new File(args[0]);
        }
        System.out.println(file.getAbsolutePath());


        try {
            List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            for (String line : lines) {
                if (line.startsWith("#")) continue;
                else if (line.trim().isEmpty()) continue;
                else if (line.trim().length() < 10) continue;

                try {
                    int index = line.indexOf("=");
                    String key = line.substring(0, index).trim();
                    String value = line.substring(index + 1).trim();
                    System.setProperty(key, value);
                    log.info("Loaded property: {}={}", key, value);
                } catch (Exception e) {
                    log.warn("Invalid property format in line: '{}'", line, e);
                }

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        SpringApplication.run(EftFcgoSuTraApplication.class, args);
    }

}
