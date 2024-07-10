package com.example.flutter_application_1;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthenticatorActivity extends Activity {
  private final String TAG = "AuthenticatorActivity";

  public final static String KEY_USER_TYPE = "USER_TYPE";
  public final static String KEY_USER_FIRST_NAME = "USER_FIRST_NAME";
  public final static String KEY_USER_LAST_NAME = "USER_LAST_NAME";

  private AccountManager accountManager;
  private String authTokenType;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.act_login);

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

  public void submit() {
    final String username = ((TextView) findViewById(R.id.accountName)).getText().toString();
    final String password = ((TextView) findViewById(R.id.accountPassword)).getText().toString();

    final String accountType = getIntent().getStringExtra(Authenticator.KEY_ACCOUNT_TYPE);

    ExecutorService es = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    es.execute(new Runnable() {
      @Override 
      public void run() {
        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        data.putString(Authenticator.KEY_ACCOUNT_PASSWORD, password);

        try {
          String authToken = "FakeAuthToken";
          data.putString(AccountManager.KEY_AUTHTOKEN, authToken);

          data.putString(AuthenticatorActivity.KEY_USER_TYPE, "employee");
          data.putString(AuthenticatorActivity.KEY_USER_FIRST_NAME, "Fake");
          data.putString(AuthenticatorActivity.KEY_USER_LAST_NAME, "Employee");
        } catch(Exception e) {
          e.printStackTrace();

          data.putString(Authenticator.KEY_ERROR_MESSAGE, e.getMessage());
        }

        final Intent result = new Intent();
        result.putExtras(data);

        handler.post(new Runnable() {
          @Override 
          public void run() {
            if (result.hasExtra(Authenticator.KEY_ERROR_MESSAGE)) {
              Toast.makeText(getBaseContext(), result.getStringExtra(Authenticator.KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
            } else {
              finishAuthentication(result);
            }
          }
        });
      }
    });
  }
  
  private void finishAuthentication(Intent result) {
    Log.d(TAG, "finishAuthentication invoked");

    String username = result.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
    String password = result.getStringExtra(Authenticator.KEY_ACCOUNT_PASSWORD);
    final Account account = new Account(username, result.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

    if (getIntent().getBooleanExtra(Authenticator.KEY_ACCOUNT_ADD, false)) {
      final Bundle userData = new Bundle();
      userData.putString(AuthenticatorActivity.KEY_USER_TYPE, result.getStringExtra(AuthenticatorActivity.KEY_USER_TYPE));
      userData.putString(AuthenticatorActivity.KEY_USER_FIRST_NAME, result.getStringExtra(AuthenticatorActivity.KEY_USER_FIRST_NAME));
      userData.putString(AuthenticatorActivity.KEY_USER_LAST_NAME, result.getStringExtra(AuthenticatorActivity.KEY_USER_LAST_NAME));

      accountManager.addAccountExplicitly(account, password, userData);
    } else {
      accountManager.setPassword(account, password);
    }

    String authToken = result.getStringExtra(AccountManager.KEY_AUTHTOKEN);
    accountManager.setAuthToken(account, authTokenType, authToken);

    setResult(RESULT_OK, result);

    finish();
  }
}