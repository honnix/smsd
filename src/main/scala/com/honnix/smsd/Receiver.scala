package com.honnix.smsd

object Receiver {
  def apply(who: String) = "@" + who

  def unapply(str: String) : Option[String] = {
    val parts = str.split("@")
    if (parts.length == 2 && parts(0).isEmpty) Some(parts(1)) else None
  }
}
