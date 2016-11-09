package com.fredrik.wakeup.adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.fredrik.wakeup.R;
import com.fredrik.wakeup.other.MorningTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskEditAdapter  extends RecyclerView.Adapter<TaskEditAdapter.ViewHolder> {

    private List<MorningTask> tasks;
    private boolean[] fieldSetToDefaultValue;
    private Drawable originalDrawable;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public EditText taskTitleInput, taskTimeInput;
        public View removeButton;
        public TaskTextWatcher taskTitleWatcher, taskTimeWatcher;
        public ViewHolder(View mainView, EditText taskTitleInput, EditText taskTimeInput, View removeButton, TaskTextWatcher taskTitleWatcher, TaskTextWatcher taskTimeWatcher) {
            super(mainView);
            this.taskTitleInput = taskTitleInput;
            this.taskTimeInput = taskTimeInput;
            this.removeButton = removeButton;
            this.taskTitleWatcher = taskTitleWatcher;
            this.taskTimeWatcher = taskTimeWatcher;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TaskEditAdapter(MorningTask[] previousTasks) {
        this.tasks = new ArrayList<>(Arrays.asList(previousTasks));
        this.fieldSetToDefaultValue = new boolean[previousTasks.length];
    }


    // Create new views (invoked by the layout manager)
    @Override
    public TaskEditAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_edit_row, parent, false);
        EditText taskTitleInput = (EditText)v.findViewById(R.id.task_title_input);
        EditText taskTimeInput = (EditText)v.findViewById(R.id.task_time_input);
        View taskRemoveButton = v.findViewById(R.id.task_remove_button);
        // set the view's size, margins, paddings and layout parameters
        TaskTextWatcher taskTitleWatcher = new TaskTextWatcher(true);
        TaskTextWatcher taskTimeWatcher = new TaskTextWatcher(false);
        taskTitleInput.addTextChangedListener(taskTitleWatcher);
        taskTimeInput.addTextChangedListener(taskTimeWatcher);

        return new ViewHolder(v, taskTitleInput, taskTimeInput, taskRemoveButton, taskTitleWatcher, taskTimeWatcher);
    }

    private class TaskTextWatcher implements TextWatcher {

        private int position;
        private boolean watchingNameInsteadOfTime;



        TaskTextWatcher(boolean watchingNameInsteadOfTime){
            this.watchingNameInsteadOfTime = watchingNameInsteadOfTime;
        }

        void setPosition(int position){
            this.position = position;
        }


        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String textChangedTo = charSequence.toString();
            MorningTask thisTask = tasks.get(position);
            if(watchingNameInsteadOfTime){
                thisTask.setName(textChangedTo);
            }
            else{
                if(!textChangedTo.equals("")) {
                    thisTask.setSecondsToDoIt(Integer.parseInt(textChangedTo));
                }
            }
            tasks.set(position,thisTask);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(!fieldSetToDefaultValue[position]){
            MorningTask thisTask = tasks.get(position);
            holder.taskTitleInput.setText(thisTask.getName());
            holder.taskTimeInput.setText(Integer.toString(thisTask.getSecondsToDoIt()));
            fieldSetToDefaultValue[position] = true;
        }
        holder.taskTitleWatcher.setPosition(position);
        holder.taskTimeWatcher.setPosition(position);

        if(position == 0){
            holder.taskTitleInput.setFocusable(false);
            holder.taskTitleInput.setCursorVisible(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.taskTitleInput.setBackgroundResource(android.R.color.transparent);
            }
            holder.removeButton.setVisibility(View.INVISIBLE);
        }
        else{
            holder.taskTimeInput.setFocusable(true);
            holder.taskTimeInput.setCursorVisible(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && originalDrawable != null) {
                holder.taskTitleInput.setBackground(originalDrawable);
            }
            holder.removeButton.setVisibility(View.VISIBLE);
            originalDrawable = holder.taskTimeInput.getBackground();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tasks.size();
    }
}