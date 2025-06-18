package org.tetokeguii.phase1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Phase1Application {

    public static void main(String[] args) {
        ApplicationContext appContext  = SpringApplication.run(Phase1Application.class, args);
        appContext.getBean(BinarySearch.class).binarySearch(new int[]{1,5,4,4,5}, 3);
    }

}
