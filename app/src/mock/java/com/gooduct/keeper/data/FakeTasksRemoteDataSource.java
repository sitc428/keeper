/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gooduct.keeper.data;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.gooduct.keeper.data.source.TasksDataSource;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
public class FakeTasksRemoteDataSource extends AsyncTask implements TasksDataSource {

    private static FakeTasksRemoteDataSource INSTANCE;

    private static final Map<String, Task> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeTasksRemoteDataSource() {}

    @Override
    protected String doInBackground(Object[] params) {
        /*String fullName = params[0].toString();
        String userName = params[1].toString();
        String passWord = params[2].toString();
        String phoneNumber = params[3].toString();
        String emailAddress = params[4].toString();*/

        String link;
        String data;
        BufferedReader bufferedReader;
        String result;

        try {
            data = "?";
//            data = "?fullname=" + URLEncoder.encode(fullName, "UTF-8");
//            data += "&username=" + URLEncoder.encode(userName, "UTF-8");
//            data += "&password=" + URLEncoder.encode(passWord, "UTF-8");
//            data += "&phonenumber=" + URLEncoder.encode(phoneNumber, "UTF-8");
//            data += "&emailaddress=" + URLEncoder.encode(emailAddress, "UTF-8");

            link = "http://gooduct.com/V1/clips/show" + data;
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            result = bufferedReader.readLine();
            return result;
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }


    public static FakeTasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTasksRemoteDataSource();
        }
        return INSTANCE;
    }

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;
    @Override
    public void getTasks(final @NonNull LoadTasksCallback callback) {
        //callback.onTasksLoaded(Lists.newArrayList(TASKS_SERVICE_DATA.values()));

            Object result = this.execute();

        final Map<String, Task> map = new HashMap<String, Task>();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //callback.onTasksLoaded(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
                callback.onTasksLoaded(Lists.newArrayList(map.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        Task task = TASKS_SERVICE_DATA.get(taskId);
        callback.onTaskLoaded(task);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        TASKS_SERVICE_DATA.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        TASKS_SERVICE_DATA.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        // Not required for the remote data source.
    }

    @Override
    public void activateTask(@NonNull Task task) {
        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
        TASKS_SERVICE_DATA.put(task.getId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        // Not required for the remote data source.
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @VisibleForTesting
    public void addTasks(Task... tasks) {
        for (Task task : tasks) {
            TASKS_SERVICE_DATA.put(task.getId(), task);
        }
    }
}
