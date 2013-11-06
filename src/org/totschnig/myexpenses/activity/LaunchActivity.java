package org.totschnig.myexpenses.activity;

import static org.totschnig.myexpenses.provider.DatabaseConstants.KEY_ACCOUNTID;

import org.totschnig.myexpenses.MyApplication;
import org.totschnig.myexpenses.R;
import org.totschnig.myexpenses.dialog.ContribInfoDialogFragment;
import org.totschnig.myexpenses.dialog.VersionDialogFragment;
import org.totschnig.myexpenses.model.Transaction;
import org.totschnig.myexpenses.preference.SharedPreferencesCompat;
import org.totschnig.myexpenses.provider.DbUtils;
import org.totschnig.myexpenses.provider.TransactionProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public abstract class LaunchActivity extends ProtectedFragmentActivity {

  protected SharedPreferences mSettings;
  /**
   * check if this is the first invocation of a new version
   * in which case help dialog is presented
   * also is used for hooking version specific upgrade procedures
   */
  public void newVersionCheck() {
    Editor edit = mSettings.edit();
    int prev_version = mSettings.getInt(MyApplication.PREFKEY_CURRENT_VERSION, -1);
    int current_version = CommonCommands.getVersionNumber(this);
    if (prev_version == current_version)
      return;
    if (prev_version == -1) {
      //edit.putLong(MyApplication.PREFKEY_CURRENT_ACCOUNT, mCurrentAccount.id).commit();
      SharedPreferencesCompat.apply(edit.putInt(MyApplication.PREFKEY_CURRENT_VERSION, current_version));
    } else if (prev_version != current_version) {
      SharedPreferencesCompat.apply(edit.putInt(MyApplication.PREFKEY_CURRENT_VERSION, current_version));
      if (prev_version < 19) {
        //renamed
        edit.putString(MyApplication.PREFKEY_SHARE_TARGET,mSettings.getString("ftp_target",""));
        edit.remove("ftp_target");
        edit.commit();
      }
      if (prev_version < 28) {
        Log.i("MyExpenses",String.format("Upgrading to version 28: Purging %d transactions from datbase",
            getContentResolver().delete(TransactionProvider.TRANSACTIONS_URI,
                KEY_ACCOUNTID + " not in (SELECT _id FROM accounts)", null)));
      }
      if (prev_version < 30) {
        if (mSettings.getString(MyApplication.PREFKEY_SHARE_TARGET,"") != "") {
          edit.putBoolean(MyApplication.PREFKEY_PERFORM_SHARE,true).commit();
        }
      }
      if (prev_version < 40) {
        DbUtils.fixDateValues(getContentResolver());
        //we do not want to show both reminder dialogs too quickly one after the other for upgrading users
        //if they are already above both tresholds, so we set some delay
        mSettings.edit().putLong("nextReminderContrib",Transaction.getSequenceCount()+23).commit();
      }
      VersionDialogFragment.newInstance(prev_version)
        .show(getSupportFragmentManager(),"VERSION_INFO");
    }
  }
  /* (non-Javadoc)
   * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
   * The Preferences activity can be launched from activities of this subclass and we handle here 
   * the need to restart if the restore command has been called
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, 
      Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    //configButtons();
    if (requestCode == ACTIVITY_PREFERENCES && resultCode == RESULT_FIRST_USER) {
      Intent i = new Intent(this, ManageAccounts.class);
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      finish();
      startActivity(i);
    }
  }
  @Override
  public boolean dispatchCommand(int command, Object tag) {
    Intent i;
    switch(command) {
    case R.id.SETTINGS_COMMAND:
      i = new Intent(this, MyPreferenceActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivityForResult(i,ACTIVITY_PREFERENCES);
      return true;
    }
    return super.dispatchCommand(command, tag);
  }
}
