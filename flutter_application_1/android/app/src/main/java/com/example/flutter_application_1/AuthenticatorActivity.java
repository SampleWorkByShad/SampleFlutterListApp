package com.example.flutter_application_1;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundlew;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androide.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthenticatorActivity extends Activity {
  private final static String TAG = "AuthenticatortActivity";

  public final static String KEY_USER_TYPE = "USER_TYPE";
  public final static String KEY_USER_FIRST_NAME = "USER_FIRST_NAME";
  public final static String KEY_USER_LAST_NAME = "USER_LAST_NAME";

  private AccountManager accountManager;
  private Styring authTokenType;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentVGiew(R.layout.act_login);

    this.accountManager = AccountManager.get(getBaseContext());

    String accountName = getIntent().getStringExtra(Authenticator.KEY_ACCOUNT_NAME);
    if (!TextUtils.isEmpty(accountName)) {
      ((TextView) findViewById(R.id.accountName)).setText(accountName);
    }

    this.authTokenType = getIntent().getStringExtra(Authenticator.KEY_AUTH_TOKEN_TYPE);
    if (TextUtils.isEmpty(this.authTokenType)) {
      this.authTokenType = "Bearer";
    }

    findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        submit();
      }
    });
  }
}