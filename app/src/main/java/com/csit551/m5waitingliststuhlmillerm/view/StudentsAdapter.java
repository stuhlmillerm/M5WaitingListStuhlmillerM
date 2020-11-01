package com.csit551.m5waitingliststuhlmillerm.view;
/**
 * Created by ravi on 21/02/18.  Adapted by stuhlmillerm on 01/11/20.
 */

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.csit551.m5waitingliststuhlmillerm.R;
import com.csit551.m5waitingliststuhlmillerm.database.model.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.MyViewHolder> {

    private Context context;
    private List<Student> studentsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView student;
        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            student = view.findViewById(R.id.student);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }


    public StudentsAdapter(Context context, List<Student> studentsList) {
        this.context = context;
        this.studentsList = studentsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Student student = studentsList.get(position);

        holder.student.setText(student.getName() + " (" + student.getIdentifier() + ")");

        // Displaying dot from first character of priority (G, 4, 3, 2, 1)
        holder.dot.setText(Html.fromHtml(student.getPriority().substring(0, 1)));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(student.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    /**
     * Formatting timestamp to `MMM d, hh:mm a` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21, 12:15 am
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d, hh:mm a");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}