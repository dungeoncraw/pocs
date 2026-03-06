package com.example

import org.springframework.context.annotation.{Bean, Configuration, ComponentScan}

@Configuration
@ComponentScan(Array("com.example"))
class AppConfig:
  @Bean(initMethod = "init")
  def slow(): SlowBean = new SlowBean()

  @Bean(initMethod = "init")
  def fast(): FastBean = new FastBean()

class SlowBean:
  def init(): Unit = Thread.sleep(100)

class FastBean:
  def init(): Unit = ()
