package by.lunamretic.chat;

import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    private String idMsg;
    private String username;
    private Button sendMsg;
    private EditText textField;
    private ListView chatHistory;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.title_text);

        sendMsg = (Button) findViewById(R.id.btnSendMsg);
        textField = (EditText) findViewById(R.id.textField);
        chatHistory = (ListView) findViewById(R.id.listHistoryMsg);


        root = FirebaseDatabase.getInstance().getReference();

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
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
