package com.example.flutter_application_1;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class Authenticator extends AbstractAccountAuthenticator {
  private final static String TAG = "Authenticator";

  public final static String KEY_ACCOUNT_TYPE = "ACCOUNT_TYPE";
  public final static String KEY_AUTH_TOKEN_TYPE = "AUTH_TOKEN_TYPE";
  public final static String KEY_ACCOUNT_NAME = "ACCOUNT_NAME";
  public final static String KEY_ACCOUNT_PASSWORD = "ACCOUNT_PASSWORD";
  public final static String KEY_ACCOUNT_ADD = "ACCOUNT_ADD";

  public final static String KEY_ERROR_MESSAGE = "ERR_MSG";

  private final Context context;

  public Authenticator(Context context) {
    super(context);

    this.context = context;
  }

  @Override 
  public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
    Log.d(TAG, "addAccount invoked");

    final Intent intent = new Intent(context, AuthenticatorActivity.class);
    intent.putExtra(Authenticator.KEY_ACCOUNT_TYPE, accountType);
    intent.putExtra(Authenticator.KEY_AUTH_TOKEN_TYPE, authTokenType);
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    intent.putExtra(Authenticator.KEY_ACCOUNT_ADD, true);

    final Bundle bundle = new Bundle();
    bundle.putParcelable(AccountManager.KEY_INTENT, intent);

    return bundle;
  }

  @Override
  public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
    Log.d(TAG, "getAuthToken invoked");

    final AccountManager am = AccountManager.get(context);

    String authToken = am.peekAuthToken(account, authTokenType);

    if (TextUtils.isEmpty(authToken)) {
      final String password = am.getPassword(account);
      if (!TextUtils.isEmpty(password)) {
        try {
          // network call
          authToken = "FakeAuthToken";
        } catch(Exception e) {}
      }
    }

    if (!TextUtils.isEmpty(authToken)) {
      final Bundle authBundle = new Bundle();
      authBundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
      authBundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
      authBundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
      return authBundle;
    }

    final Intent intent = new Intent(context, AuthenticatorActivity.class);
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    intent.putExtra(Authenticator.KEY_ACCOUNT_TYPE, account.type);
    intent.putExtra(Authenticator.KEY_AUTH_TOKEN_TYPE, authTokenType);
    intent.putExtra(Authenticator.KEY_ACCOUNT_NAME, account.name);

    final Bundle intentBundle = new Bundle();
    intentBundle.putParcelable(AccountManager.KEY_INTENT, intent);

    return intentBundle;
  }

  @Override
  public Bundle editProperties(
      AccountAuthenticatorResponse response, String s) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle confirmCredentials(AccountAuthenticatorResponse response,
      Account account, Bundle bundle) throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAuthTokenLabel(String s) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle updateCredentials(AccountAuthenticatorResponse response,
      Account account, String strings, Bundle bundle)
      throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle hasFeatures(AccountAuthenticatorResponse response,
      Account account, String[] strings) throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }
}