package com.honnix.smsd.util

import java.util.Properties

import scala.collection.mutable.HashMap
import scala.collection.mutable.SynchronizedMap

object PropertiesLoader {
  private val propertiesMap = new HashMap[String, Properties] with
    SynchronizedMap[String, Properties]

  def loadProperties(fileName: String): Properties = {
    if (!propertiesMap.contains(fileName)) {
      // It is still possible that two threads enter here
      // at the same time. We don't synchronize here since
      // the later one will overwrite the first one, and this
      // is no problem.
      val properties = new Properties
      properties.load(getClass.getClassLoader.getResourceAsStream(fileName))
      propertiesMap += (fileName -> properties)

      properties
    }
    else propertiesMap(fileName)
  }
}
