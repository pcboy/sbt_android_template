package com.pcboy.androxec

import android.util.Log

object Logger {

  val TAG = "ANDROEXEC"

  def e(msg: String) = {
    Log.e(TAG, msg)
  }
  
  def d(msg: String) = {
    Log.d(TAG, msg)
  }
}
