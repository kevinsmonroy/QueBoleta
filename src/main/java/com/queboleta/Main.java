package com.queboleta;

import com.queboleta.view.WindowView;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(Main.class)
                .headless(false)
                .run(args);

        java.awt.EventQueue.invokeLater(() -> {
            WindowView view = context.getBean(WindowView.class);
            view.setVisible(true);
        });
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://ep-dry-sun-am3k3lhs-pooler.c-5.us-east-1.aws.neon.tech/QueBoleta%21?sslmode=require");
        dataSource.setUsername("neondb_owner");
        dataSource.setPassword("npg_z7KFmLkpidn9");
        return dataSource;
    }
}