package com.csit551.m5waitingliststuhlmillerm.view;
/**
 * Created by ravi on 21/02/18.  Adapted by stuhlmillerm on 01/11/20.
 */

import android.content.DialogInterface;
import android.os.Bundle;

import com.csit551.m5waitingliststuhlmillerm.R;
import com.csit551.m5waitingliststuhlmillerm.database.DatabaseHelper;
import com.csit551.m5waitingliststuhlmillerm.database.model.Student;
import com.csit551.m5waitingliststuhlmillerm.utils.MyDividerItemDecoration;
import com.csit551.m5waitingliststuhlmillerm.utils.RecyclerTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private StudentsAdapter mAdapter;
    private List<Student> studentsList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noStudentsView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noStudentsView = findViewById(R.id.empty_students_view);

        db = new DatabaseHelper(this);

        studentsList.addAll(db.getAllStudents());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStudentDialog(false, null, -1);
            }
        });

        mAdapter = new StudentsAdapter(this, studentsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyStudents();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Inserting new student in db
     * and refreshing the list
     */
    private void createStudent(String identifier, String name, String priority) {
        // inserting student in db and getting
        // newly inserted student id
        long id = db.insertStudent(identifier, name, priority);

        // get the newly inserted student from db
        Student s = db.getStudent(id);

        if (s != null) {
            // adding new student - may need to reorder.
            studentsList.clear();
            studentsList.addAll(db.getAllStudents());

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyStudents();
        }
    }

    /**
     * Updating student in db and updating
     * item in the list by its position
     */
    private void updateStudent(String identifier, String name, String priority, int position) {
        Student s = studentsList.get(position);
        String prevPriority = s.getPriority();
        // updating student text values
        s.setIdentifier(identifier);
        s.setName(name);
        s.setPriority(priority);

        // updating student in db
        db.updateStudent(s);

        if (priority.equals(prevPriority)) {
            // Priority is the same, just update the list item
            studentsList.set(position, s);
            mAdapter.notifyItemChanged(position);
        } else {
            // If our priority changed, need to reload the list
            studentsList.clear();
            studentsList.addAll(db.getAllStudents());
            mAdapter.notifyDataSetChanged();
        }

        toggleEmptyStudents();
    }

    /**
     * Deleting student from SQLite and removing the
     * item from the list by its position
     */
    private void deleteStudent(int position) {
        // deleting the student from db
        db.deleteStudent(studentsList.get(position));

        // removing the student from the list
        studentsList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyStudents();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence[] opts = new CharSequence[]{
                getResources().getString(R.string.option_edit_text),
                getResources().getString(R.string.option_delete_text)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.option_title));
        builder.setItems(opts, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showStudentDialog(true, studentsList.get(position), position);
                } else {
                    deleteStudent(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit a student.
     * when shouldUpdate=true, it automatically displays old student and changes the
     * button text to UPDATE
     */
    private void showStudentDialog(final boolean shouldUpdate, final Student student, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.student_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputIdentifier = view.findViewById(R.id.student_identifier);
        final EditText inputName = view.findViewById(R.id.student_name);
        final EditText inputPriority = view.findViewById(R.id.student_priority);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_student_title) : getString(R.string.lbl_edit_student_title));

        if (shouldUpdate && student != null) {
            inputIdentifier.setText(student.getIdentifier());
            inputName.setText(student.getName());
            inputPriority.setText(student.getPriority());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputIdentifier.getText().toString())
                        || TextUtils.isEmpty(inputName.getText().toString())
                        || TextUtils.isEmpty(inputPriority.getText().toString())
                         ) {
                    Toast.makeText(MainActivity.this, "All student information is required!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating student
                if (shouldUpdate && student != null) {
                    // update student by its id
                    updateStudent(inputIdentifier.getText().toString(), inputName.getText().toString(), inputPriority.getText().toString(), position);
                } else {
                    // create new student
                    createStudent(inputIdentifier.getText().toString(), inputName.getText().toString(), inputPriority.getText().toString());
                }
            }
        });
    }

    /**
     * Toggling list and empty students view
     */
    private void toggleEmptyStudents() {

        if (db.getStudentsCount() > 0) {
            noStudentsView.setVisibility(View.GONE);
        } else {
            noStudentsView.setVisibility(View.VISIBLE);
        }
    }
}