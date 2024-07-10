import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:accountmanager/accountmanager.dart';

import 'screens/loader.dart';

class AuthScopeWrapper extends StatefulWidget {
  final Widget child;

  const AuthScopeWrapper({super.key, required this.child});

  static AuthScopeWrapperState of(BuildContext context) {
    return (context.dependOnInheritedWidgetOfExactType<AuthScope>())!.data;
  }

  @override
  AuthScopeWrapperState createState() => AuthScopeWrapperState();
}

class AuthScopeWrapperState extends State<AuthScopeWrapper> {
  Account? account;

  final MethodChannel methodChannel =
      const MethodChannel('mobileapp.shadclaiborne.com');

  @override
  void initState() {
    super.initState();

    methodChannel.setMethodCallHandler(_methodCallHandler);

    WidgetsBinding.instance.addPostFrameCallback((_) {
      initAsyncState();
    });
  }

  void initAsyncState() async {
    final Account? account = await _findAccountByType(AuthScope.accountType);
    await _startAuthenticationUnlessSetAccountState(account);
  }

  Future<dynamic> _methodCallHandler(MethodCall call) async {
    switch (call.method) {
      case 'useAccount':
        {
          final Account? account = await _findAccountByName(call.arguments);
          await _startAuthenticationUnlessSetAccountState(account);
          break;
        }
    }
  }

  Future<void> _startAuthentication() async {
    await methodChannel.invokeMethod('startAuthentication');
  }

  Future<Account?> _findAccountByType(final String type) async {
    final nativeAcconts = await AccountManager.getAccounts();

    Account? matchedAccount;

    for (Account nativeAccount in nativeAcconts) {
      if (type.compareTo(nativeAccount.accountType) == 0) {
        matchedAccount = nativeAccount;
      }
    }

    return matchedAccount;
  }

  Future<Account?> _findAccountByName(final String username) async {
    final nativeAcconts = await AccountManager.getAccounts();

    Account? matchedAccount;

    for (Account nativeAccount in nativeAcconts) {
      if (AuthScope.accountType.compareTo(nativeAccount.accountType) == 0 &&
          username.compareTo(nativeAccount.name) == 0) {
        matchedAccount = nativeAccount;
      }
    }

    return matchedAccount;
  }

  Future<void> _startAuthenticationUnlessSetAccountState(
      final Account? account) async {
    if (account == null) {
      await _startAuthentication();
    } else {
      setState(() {
        this.account = account;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return AuthScope(
        data: this,
        account: account,
        child: account == null
            ? const MaterialApp(home: LoaderScreen())
            : widget.child);
  }
}

class AuthScope extends InheritedWidget {
  static const String accountType = "shadclaiborne.com";

  const AuthScope(
      {super.key,
      required this.data,
      required this.account,
      required super.child});

  final Account? account;
  final AuthScopeWrapperState data;

  @override
  bool updateShouldNotify(covariant AuthScope oldWidget) {
    return account != oldWidget.account;
  }
}
