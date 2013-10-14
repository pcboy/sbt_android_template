package com.pcboy.androxec

import android.content._
import android.widget.Toast

import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient
import ch.boye.httpclientandroidlib.client.methods.HttpGet
import ch.boye.httpclientandroidlib.client.methods.HttpPost
import ch.boye.httpclientandroidlib.params
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity
import ch.boye.httpclientandroidlib.client.utils.URLEncodedUtils
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext
import ch.boye.httpclientandroidlib.impl.client.BasicCookieStore
import ch.boye.httpclientandroidlib.client.protocol.ClientContext
import ch.boye.httpclientandroidlib.message.{BasicNameValuePair, BasicHeader}
import scalaj.collection.Imports._
import net.liftweb.json._
import android.util.Log

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class Request extends Cache {

  def get(url: String, params: Map[String, String] = null, cacheKey: String = null)
  (implicit sharedPrefs: SharedPreferences): String = {
    var uri = url
    val secretToken = UserPreferences.get("secret_token")

    if (params != null) {
      val httpParams: java.util.List[BasicNameValuePair] =
        (for ((key, value) <- params) yield
          new BasicNameValuePair(key, value)).toList.asJava
      uri = url + "?" + URLEncodedUtils.format(httpParams, "utf-8")
    }
    cache((if (cacheKey == null ) uri else cacheKey), {
      val (client, http) = (new DefaultHttpClient(), new HttpGet(uri))
      val httpContext = new BasicHttpContext
      val out = new java.io.ByteArrayOutputStream

      if (secretToken.length != 0)
        http.addHeader(new BasicHeader("Authorization", "OAuth " + secretToken))
      client.execute(http).getEntity.writeTo(out)
      out.close
      Logger.e(out.toString)
      out.toString
    })
  }

  def post(url: String,
    params: Map[String, String])(implicit sharedPrefs: SharedPreferences): String = {
    val (client, http) = (new DefaultHttpClient(), new HttpPost(url))
    val out = new java.io.ByteArrayOutputStream

    Logger.e(url)
    // Fix "417 expectation failed" errors when a HTTP/1.0 server is behind a proxy
    client.getParams.setBooleanParameter("http.protocol.expect-continue", false)
    val httpParams: java.util.List[BasicNameValuePair] =
      (for ((key, value) <- params) yield new BasicNameValuePair(key, value)).toList.asJava

    val secret = UserPreferences.get("secret_token")
    if (secret.length != 0)
      http.addHeader(new BasicHeader("Authorization", "OAuth " + secret))
    http.setEntity(new UrlEncodedFormEntity(httpParams, "utf-8"))
    client.execute(http).getEntity.writeTo(out)
    out.close
    Logger.e(out.toString)
    out.toString
  }
}

case class loginErrorException(message: String) extends Exception

class Api(context: Context) extends Request with CacheKeys {
  implicit val c: Context = context
  implicit val formats = net.liftweb.json.DefaultFormats
  implicit val sharedPrefs: SharedPreferences =
    context.getSharedPreferences("androxec", Context.MODE_MULTI_PROCESS)
  
  val domain = "http://localhost:3000" //Dev server dns access
  Logger.e("Domain server URL: " + domain)
  
  val apiUrl = domain + "/api"

  val clientId = ""
  val clientSecret = ""

  def login(username: String, password: String): String = {
    val authUrl = domain + "/oauth/access_token"

    val content = parse(post(authUrl,
      Map("grant_type" -> "password",
        "client_id" -> clientId,
        "client_secret" -> clientSecret,
        "scope" -> "read write",
        "username" -> username,
        "password" -> password)))

    val tokens = content.values.asInstanceOf[Map[String, String]]

    if (tokens.get("error_description") != None) {
      throw new loginErrorException(tokens.get("error_description").get)
    } else {
      return tokens("access_token")
    }
  }
}
