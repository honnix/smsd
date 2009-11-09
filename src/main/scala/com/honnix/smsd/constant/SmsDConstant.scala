package com.honnix.smsd.constant

import smsd.util.PropertiesLoader

object SmsDConstant {
  private val SmsDPropertiesFileName = "smsd.properties"

  val MonitorDirectory = PropertiesLoader.loadProperties(
    SmsDPropertiesFileName).getProperty("monitor.directory")

  val ScanInterval = PropertiesLoader.loadProperties(
    SmsDPropertiesFileName).getProperty("scan.interval").toLong
}
