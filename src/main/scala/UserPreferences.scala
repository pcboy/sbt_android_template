package com.pcboy.androxec

import android.content.Context
import android.widget.Toast
import android.content.SharedPreferences
import scala.collection.JavaConverters._

import android.util.Log

object UserPreferences {

  def resetCache(implicit sharedPrefs: SharedPreferences) = {
    val pattern = "(cache_.+)".r
    sharedPrefs.getAll.asScala.map {
      p => p._1 match {
        case pattern(c) => del(c)
        case _ =>
      }
    }
  }

  def del(key: String)(implicit sharedPrefs: SharedPreferences): Boolean = {
    sharedPrefs
      .edit()
      .remove(key)
      .commit()
  }

  def set(key: String, value: String)(implicit sharedPrefs: SharedPreferences): Boolean = {
    sharedPrefs
      .edit()
      .remove(key)
      .putString(key, value)
      .commit()
  }

  def get(key: String, defValue: String = "")(implicit sharedPrefs: SharedPreferences): String = {
    sharedPrefs.getString(key, defValue)
  }

}
