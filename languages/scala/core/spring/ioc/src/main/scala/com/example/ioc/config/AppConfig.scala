package com.example.ioc.config

import org.springframework.context.annotation.{ComponentScan, Configuration}

@Configuration
@ComponentScan(basePackages = Array("com.example.ioc"))
class AppConfig
