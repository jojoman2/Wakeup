package com.fredrik.wakeup.other;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Fredrik on 02-Nov-16.
 */
public class TaskFormatTransform {

    private static final String TASK_JSON_NAME = "name";
    private static final String TASK_JSON_TIME = "time";

    private static String stripShitChars(String theString){
        return theString.replace(" ","").replace("\n","").replace("\t","");
    }

    public static String toJson(MorningTask[] tasks){
        try {
            JSONArray jsonArray = new JSONArray();
            for (MorningTask task : tasks) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put(TASK_JSON_NAME, task.getName());
                jsonObject.put(TASK_JSON_TIME, task.getSecondsToDoIt());

                jsonArray.put(jsonObject);
            }
            return jsonArray.toString();
        }
        catch (JSONException e) {
            throw(new RuntimeException("Could create JSON. Error: " + e.getMessage()));
        }
    }

    public static MorningTask[] fromJson(String taskJson){
        try {
            JSONArray jsonArray = new JSONArray(taskJson);

            //Loops through tasks
            int noTasks = jsonArray.length();
            MorningTask[] tasks = new MorningTask[noTasks];
            for (int i = 0; i < noTasks; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String taskName = jsonObject.getString(TASK_JSON_NAME);
                int taskTime = jsonObject.getInt(TASK_JSON_TIME);
                MorningTask thisTask = new MorningTask(taskName, taskTime);

                tasks[i] = thisTask;
            }
            return tasks;
        }
        catch (JSONException e){
            throw(new RuntimeException("Could not parse JSON. Error: "+e.getMessage()));
        }
    }

    public static MorningTask[] fromTextfieldInput(String textFieldInput){
        if(stripShitChars(textFieldInput).length() < 1){
            throw(new IllegalArgumentException("The textfield can't be empty "));
        }

        List<MorningTask> tasks = new LinkedList<>();
        String[] rows = textFieldInput.split("\n");
        for(int i = 0; i < rows.length; i++){
            String[] rowParts = rows[i].split(":");
            if(rowParts.length > 2){
                throw(new IllegalArgumentException("Two many colon on line "+Integer.toString(i+1)));
            }
            if(rowParts.length == 1){
                String thisLine = stripShitChars(rows[i]);
                if(thisLine.length() != 0){
                    throw(new IllegalArgumentException("Line " +Integer.toString(i+1) + " contains no colon"));
                }
            }
            else{
                String taskName = rowParts[0].trim();
                String taskTimeStr = stripShitChars(rowParts[1]);

                int taskTime;
                try{
                    taskTime = Integer.parseInt(taskTimeStr);
                }
                catch (NumberFormatException e){
                    throw(new IllegalArgumentException("The task time on line "+ Integer.toString(i+1) + " is not numeric"));
                }
                MorningTask thisTask = new MorningTask(taskName, taskTime);
                tasks.add(thisTask);
            }
        }
        return  tasks.toArray(new MorningTask[tasks.size()]);
    }

    public static String toTextFieldText(MorningTask[] tasks){
        String textFieldText = "";
        for(int i = 0; i < tasks.length; i++){
            MorningTask task = tasks[i];
            textFieldText += task.getName() + ": " + task.getSecondsToDoIt();
            if(i < tasks.length - 1){
                textFieldText += "\n";
            }
        }
        return textFieldText;
    }
}
