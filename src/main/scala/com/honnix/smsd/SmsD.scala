package com.honnix.smsd;

import java.io.File

import scala.actors.TIMEOUT
import scala.actors.Actor
import scala.actors.Actor.loop
import scala.io.{Source, BufferedSource}

import jfetion.{FetionSessionControl, FetionMessageControl}
import jfetion.impl.FetionFactory
import org.apache.commons.logging.{Log, LogFactory}

import smsd.constant.SmsDConstant

object SmsD {
  private lazy val Log = LogFactory.getLog(getClass)

  private val FilePattern = "^.+\\.sms$".r
  private val MonitorDirectory = new File(SmsDConstant.MonitorDirectory)
  private val FetionSession = FetionFactory.getFetionSessionControl
  private val FetionMessage = FetionFactory.getFetionMessageControl
}

object Main extends Application {
  (new SmsD).start
}

class SmsD extends Actor {
  private def sendMessage(content: List[String])(f: (String) => Boolean) {
    val message = content.drop(1).mkString("\n")

    val truncatedMessage =
      if (message.length <= 70)
        message
      else if (message.length <= SmsDConstant.MaxMessageLength) {
        SmsD.FetionMessage.setLongSmsEnabled(true)
        message
      } else {
          SmsD.Log.warn("Message too long, truncated.")
          message.substring(0, SmsDConstant.MaxMessageLength)
      }

    f(truncatedMessage)
  }

  private def processFile(file: File) {
    val source = Source.fromFile(file).asInstanceOf[BufferedSource]
    val content = (for (line <- source.getLines) yield line.replaceAll("[\\r\\n]", "")).toList
    source.close

    if (SmsD.Log.isDebugEnabled)
      SmsD.Log.debug("Sms file content: " + content.toString)

    content.head match {
      case "@self" =>
        sendMessage(content) {
          SmsD.FetionMessage.sendSmsToSelf
        }
        file.delete
      case Receiver(who) =>
        sendMessage(content) {
          SmsD.FetionMessage.sendSmsByMobileNumber(who, _)
        }
        file.delete
      case _ =>
        SmsD.Log.warn("Sms file content is not valid.")
        file.renameTo(new File(file.getAbsolutePath + ".bad"))
    }
  }

  def act {
    loop {
      reactWithin(SmsDConstant.ScanInterval * 1000) {
        case 'exit => exit
        case TIMEOUT =>
          if (SmsD.Log.isDebugEnabled)
            SmsD.Log.debug("Scan new sms files.")

          val files = SmsD.MonitorDirectory.listFiles.filter((x) =>
            SmsD.FilePattern.findFirstIn(x.getName) != None && x.length != 0)
          if (files.length > 0) {
            if (SmsD.Log.isDebugEnabled)
              SmsD.Log.debug("New sms files found.")

            SmsD.FetionSession.init
            SmsD.FetionSession.login(SmsDConstant.MobileNumber, SmsDConstant.Password)

            if (SmsD.Log.isDebugEnabled)
              SmsD.Log.debug("Fetion login successfully.")
            
            files.foreach{
              processFile
            }

            SmsD.FetionSession.logout
            SmsD.FetionSession.closeNetwork
            SmsD.FetionSession.terminate

            if (SmsD.Log.isDebugEnabled)
              SmsD.Log.debug("Fetion terminate successfully.")
          }
      }
    }
  }
}
