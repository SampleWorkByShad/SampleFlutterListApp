package com.example.flutter_application_1

import android.accounts.AccountManager
import android.content.Intent
import androidx.annotation.NonNull

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity(), MethodChannel.MethodCallHandler {
  private val CHANNEL = "mobileapp.shadclaiborne.com"

  private val AUTH_REQUEST = 1

  private lateinit var methodChannel : MethodChannel

  override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
    super.configureFlutterEngine(flutterEngine)

    methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
    methodChannel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
    when(call.method) {
      "startAuthentication" -> {
        startAuthentication()
        result.success(null)
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(requestCode, resultCode, data)

    when(requestCode) {
      AUTH_REQUEST -> {
        if (resultCode == RESULT_OK) {
          val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
          methodChannel.invokeMethod("useAccount", accountName)
        }
      }
    }
  }

  private fun startAuthentication() {
    val intent = Intent(this, AuthenticatorActivity::class.java)
    intent.putExtra(Authenticator.KEY_ACCOUNT_ADD, true)
    intent.putExtra(Authenticator.KEY_ACCOUNT_TYPE, "shadclaiborne.com")
    startActivityForResult(intent, AUTH_REQUEST)
  }
}