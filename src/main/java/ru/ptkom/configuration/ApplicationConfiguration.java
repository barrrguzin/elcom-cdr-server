package ru.ptkom.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.LocalDate;


@Configuration
@ComponentScan(basePackages = "ru.ptkom")
@PropertySource("classpath:application.properties")
public class ApplicationConfiguration {}