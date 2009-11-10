package com.honnix.smsd.constant

import smsd.util.PropertiesLoader

object SmsDConstant {
  private val SmsDPropertiesFileName = "smsd.properties"

  val MobileNumber = PropertiesLoader.loadProperties(
    SmsDPropertiesFileName).getProperty("mobile.number")

  val Password = PropertiesLoader.loadProperties(
    SmsDPropertiesFileName).getProperty("password")

  val MonitorDirectory = PropertiesLoader.loadProperties(
    SmsDPropertiesFileName).getProperty("monitor.directory")

  val ScanInterval = PropertiesLoader.loadProperties(
    SmsDPropertiesFileName).getProperty("scan.interval").toLong

  val MaxMessageLength = PropertiesLoader.loadProperties(
    SmsDPropertiesFileName).getProperty("max.message.length").toInt
}
