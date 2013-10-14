package com.pcboy.androxec

import android.content._
import android.util.Log

trait CacheKeys {
}

trait Cache {

  private def stemKey = "cache_"
  private def stemTimeKey = stemKey + "time_"

  private def cacheKey(key: String) = stemKey + key 
  private def cacheTimeKey(key: String) = stemTimeKey + key

  private def updateCache(cacheKey: String, cacheTimeKey: String, block: => String)
  (implicit sharedPrefs: SharedPreferences) = {
    UserPreferences.set(cacheKey, block)
    UserPreferences.set(cacheTimeKey, System.currentTimeMillis.toString)
  }

  def cache(key: String, block: => String)
  (implicit sharedPrefs: SharedPreferences): String = {
    val timeKey = cacheTimeKey(key)
    val cKey = cacheKey(key)

    Logger.e("cache(" + key + ")")
    UserPreferences.get(timeKey, "0") match {
      case x: String => try {
        if (x.toLong  + (60 * 5 * 1000) < System.currentTimeMillis) {
          Logger.e("updateCache(" + cKey + "," + timeKey + ")")
          updateCache(cKey, timeKey, block)
        }
      } catch { case e => e.printStackTrace; updateCache(cKey, timeKey, block) }
      case _ => updateCache(cKey, timeKey, block)
    }
    UserPreferences.get(cKey)
  }

  def invalidateCache(key: String)(implicit sharedPrefs: SharedPreferences) = {
    UserPreferences.del(cacheKey(key))
    UserPreferences.del(cacheTimeKey(key))
  }
}
