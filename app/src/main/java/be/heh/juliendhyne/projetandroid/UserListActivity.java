package be.heh.juliendhyne.projetandroid;

import androidx.appcompat.app.AppCompatActivity;
import be.heh.juliendhyne.projetandroid.DB.User;
import be.heh.juliendhyne.projetandroid.DB.UserAccessBDD;
import be.heh.juliendhyne.projetandroid.utils.CustomAdapter;
import be.heh.juliendhyne.projetandroid.utils.PasswordSecurity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends Activity {

    ListView list;
    ArrayList<User> tabUser;
    CustomAdapter adapter;
    int position;
    User user;
    long id;
    AlertDialog userLevelDialog;
    UserAccessBDD userDB = new UserAccessBDD(this);
    CharSequence[] rights = {" Read Only "," Read - Write "};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        list = findViewById(R.id.list);
        userDB.openForRead();
        tabUser = userDB.getAllUser();
        Cursor c = userDB.myCursor();
        adapter = new CustomAdapter(this, tabUser);
        list.setAdapter(adapter);
    }


    public void onListClickManager(View v) {
        switch (v.getId()) {
            case R.id.edit_button:
                position = (Integer) v.getTag();
                user = adapter.getItem(position);
                createEditUserDialog(user.getLevel() - 1);
                break;
            case R.id.delete_button:
                position = (Integer) v.getTag();
                user = adapter.getItem(position);
                createDeleteUserDialog();
                break;
            case R.id.password_button:
                position = (Integer) v.getTag();
                user = adapter.getItem(position);
                createPasswordUserDialog();
                break;
        }
    }

    public void createEditUserDialog(int level) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(user.getFirstname() + " " + user.getLastname() + " - Edit Rights");
        builder.setSingleChoiceItems(rights, level, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                userDB.openForWrite();
                switch(item) {
                    case 0:
                        user.setLevel(1);
                        userDB.updateUser(user.getId(), user);
                        break;
                    case 1:
                        user.setLevel(2);
                        userDB.updateUser(user.getId(), user);
                        break;
                }
                tabUser = userDB.getAllUser();
                adapter.clear();
                adapter.addAll(tabUser);
                userDB.Close();
                userLevelDialog.cancel();
            }
        });
        userLevelDialog = builder.create();
        userLevelDialog.show();
    }

    public void createDeleteUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(user.getFirstname() + " " + user.getLastname() + " - Delete User");
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userDB.openForWrite();
                userDB.removeUser(user.getEmail());
                tabUser = userDB.getAllUser();
                adapter.clear();
                adapter.addAll(tabUser);
                userDB.Close();
            }
        })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
    }

    public void createPasswordUserDialog() {

        final EditText input = new EditText(UserListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(user.getFirstname() + " " + user.getLastname() + " - Change Password");
        builder.setView(input);

        builder.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PasswordSecurity password = new PasswordSecurity(input.getText().toString().trim());
                userDB.openForWrite();
                user.setPassword(password.getPasswordSecured());
                userDB.updateUser(user.getId(), user);
                tabUser = userDB.getAllUser();
                adapter.clear();
                adapter.addAll(tabUser);
                userDB.Close();
            }
        })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();

    }
}
