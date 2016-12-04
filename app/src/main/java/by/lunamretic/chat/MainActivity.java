package by.lunamretic.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import by.lunamretic.chat.account.SettingsActivity;
import by.lunamretic.chat.account.change.UsernameActivity;
import by.lunamretic.chat.authorization.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private String idMsg;
    private Button sendMsg;
    private EditText textField;
    private ListView chatHistory;

    private TextView navUsername;
    private TextView navEmail;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.title_messages_activity);

        sendMsg = (Button) findViewById(R.id.btnSendMsg);
        textField = (EditText) findViewById(R.id.textField);
        chatHistory = (ListView) findViewById(R.id.listHistoryMsg);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textNavUsername);
        navEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textNavEmail);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            username = user.getDisplayName();
            if (TextUtils.isEmpty(username)) {
                navUsername.setText(R.string.empty_username);
                Toast.makeText(this, R.string.enter_username, Toast.LENGTH_SHORT).show();
            } else {
                navUsername.setText(username);
            }

            navEmail.setText(user.getEmail());
        }




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {

                    case R.id.nav_messages:
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_settings:
                        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settingsIntent);
                        break;

                    case R.id.nav_logout:
                        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                        adb.setMessage("Are you sure you want to log out?");
                        adb.setNegativeButton("Cancel", null);
                        adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();

                                finish();
                                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                            }});
                        adb.show();
                        break;
                }
                return false;
            }
        });


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        root = FirebaseDatabase.getInstance().getReference();

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(textField.getText().toString())) {
                    return;
                }

                if (TextUtils.isEmpty(username)) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    adb.setMessage(R.string.enter_username);
                    adb.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent usernameIntent = new Intent(MainActivity.this, UsernameActivity.class);
                            startActivity(usernameIntent);
                        }});
                    adb.show();

                } else {
                    Map<String, Object> map = new HashMap<>();
                    idMsg = root.push().getKey();
                    root.updateChildren(map);

                    DatabaseReference rootMsg = root.child(idMsg);

                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("user", username);
                    map2.put("message", textField.getText().toString());

                    rootMsg.updateChildren(map2);
                    textField.setText("");
                }
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendHistoryMsg(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendHistoryMsg(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                if (arrayList.get(position).author.equals(user.getDisplayName())) {
                    AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
                    adb.setMessage("Are you sure you want to delete this message?");
                    final int positionToRemove = position;
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            root.child(arrayList.get(positionToRemove).id).removeValue();
                            adapter.remove(adapter.getItem(positionToRemove));
                        }});
                    adb.show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class Message {
        String author;
        String msg;
        String id;

        Message (String msg, String author, String id) {
            this.author = author;
            this.msg = msg;
            this.id = id;
        }

        @Override
        public String toString() {
            return author + "\n" + msg;
        }

    }
    List<Message> arrayList = new ArrayList<>();
    ArrayAdapter<Message> adapter;
    private void appendHistoryMsg(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {
            arrayList.add(new Message((String) ((DataSnapshot)i.next()).getValue(), (String) ((DataSnapshot)i.next()).getValue(), dataSnapshot.getKey()));
        }

        adapter = new ArrayAdapter<>(this, R.layout.msg_row, arrayList);
        chatHistory.setAdapter(adapter);

        //adapter.notifyDataSetChanged();
    }
}
