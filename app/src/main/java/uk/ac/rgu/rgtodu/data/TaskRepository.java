package uk.ac.rgu.rgtodu.data;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class provides the single point of truth in the app for {@link TaskRepository}s, and
 * will in the future deal with downloading, storing, and retrieving them.
 * @author  David Corsar
 */
public class TaskRepository {

    // tag for logging
    private static final String TAG = "TaskRepository";

    // URL for the list of tasks for dcorsar
    private final String REMOTE_TASKS_URL_BASE ="https://cm3110-2022-default-rtdb.firebaseio.com/dcorsar/tasks";
    private final String REMOTE_TASK_LIST_URL = REMOTE_TASKS_URL_BASE + ".json";

    /**
     * Member field for database operations
     */
    private TaskDao mTaskDao;

    /**
     * A field for how dates should be formatted before displaying to users
     * with the day of the month as a number, and the month as text
     */
    private static final String DATE_FORMAT = "dd MMM";

    /**
     * The Singleton instance for this repository
     */
    private static TaskRepository INSTANCE;

    /**
     * The Context that the app is operating within
     */
    private Context context;

    /**
     * Create a new @{@link TaskRepository}
     * @param context The {@link Context} for the database to operate in
     */
    private TaskRepository(Context context){
        super();
        this.context = context;

        // setup for taskDao for accessing the database
        mTaskDao = TaskDatabase.getDatabase(context).taskDao();
    }

    /**
     * Gets the singleton {@link TaskRepository} for use when managing {@link Task}s
     * in the app.
     * @return The {@link TaskRepository} to be used for managing {@link Task}s in the app.
     */
    public static TaskRepository getRepository(Context context){
        if (INSTANCE == null){
            synchronized (TaskRepository.class) {
                if (INSTANCE == null)
                    INSTANCE = new TaskRepository(context);
            }
        }
        return INSTANCE;
    }

    /**
     * Returns a Task with the id id, if one exists in the local database
     * @return a fake {@link Task} for testing
     */

    public Task getTask(long id){
        return mTaskDao.findTaskById(id);
    }

    /**
     * Gets all the tasks in the local database
     * @return a {@link List} of sample {@link Task} entities for testing
     */
    public List<Task> getAllTasks(){
        return mTaskDao.getAllTasks();
    }

    /**
     * Returns a list containing the specified number of Tasks generated by getTask
     * @param number The number of Tasks to return
     * @return
     */
    public List<Task> getSyntheticTasks(int number){
        List<Task> tasks = new ArrayList<>(number);
        for (int i = 0; i < number; i++){
            Task t = getSyntheticTask();
            t.setName(String.format("Task %s", i));
            tasks.add(t);
        }
        return tasks;
    }




    /**
     * Returns a {@link Task} with randomly generated info.
     * @return a {@link Task} for tomorrow, with randomly generated details.
     */
    public Task getSyntheticTask(){
        // create a new Task
        Task t = new Task();

        // A random number generator for populating the fields
        Random random = new Random();

        int id = random.nextInt(10000);
        // set the name randomly
        t.setName(String.format("Task %s", id));

        // set the description to some placeholder
        t.setObjective("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");

        // set estimated hours to completely to be random between 1 and 5
        t.setPomodorosRemaining(random.nextInt(5));

        // set the priority randomly
        t.setPriority(TaskPriority.values()[random.nextInt(3)]);

        // set the status randomly
        t.setStatus(TaskStatus.values()[random.nextInt(3)]);

        // schedule it for up to 14 days in the future
        long offset = 1000*60*60*24 * random.nextInt(14);
        t.setDeadline(new Date(System.currentTimeMillis() + offset));

        return t;
    }


    /**
     * Stores task in the cloud data store, and if that's successful, the local database
     * @param task The {@link Task} to store in the databases.
     */
    public void storeTask(Task task) {
        Log.d(TAG, "Saving task " + task);


        // store in remote Firebase Database
        storeTaskInRemoteDatabase(task);
    }

    /**
     * Stores task in a Firebase Realtime Database
     * @param task
     */
    private void storeTaskInRemoteDatabase(Task task){
        // convert Task to a JSON object for uploading
        JSONObject js = new JSONObject();
        try {
            JSONObject taskObj = new JSONObject();
            taskObj.put("name", task.getName());
            taskObj.put("objective", task.getObjective());
            taskObj.put("pomodorosRemaining", String.valueOf(task.getPomodorosRemaining()));
            taskObj.put("deadline",String.valueOf(task.getDeadline().getTime()));
            taskObj.put("priority",task.getPriority().getLabel());
            taskObj.put("status",task.getStatus().getLabel());
            js.put(String.valueOf(task.getId()), taskObj);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            // check we have something to upload
            if (js.has((String.valueOf(task.getId())))){

                // using a Patch request to update the list of tasks
                // see https://firebase.google.com/docs/reference/rest/database
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, REMOTE_TASK_LIST_URL, js,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // TODO: something more useful here
                                Log.d(TAG, "Successfully uploaded task ");
                                // store in the local database
                                mTaskDao.insert(task);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: something more useful here
                        Log.e(TAG, "Error uploaded task ");
                    }
                });

                // make the request
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        }
    }

    /**
     * Stores the list of tasks in the database
     * @param tasks The {@link List} of {@link Task}s to store in the Room database.
     */
    public void storeTasks(List<Task> tasks){
        // store in the local database
        this.mTaskDao.insertTasks(tasks);

        // TODO store in the remote database
        // although as the app never stores multiple tasks,
        // this isn't really needed
    }

    /**
     * Updates task in the database
     * @param task The {@link Task} to store in the Room database.
     */
    public void updateTask(Task task){
        this.mTaskDao.update(task);
      // todo update task in remote database, although
        // as the app never updates tasks, this isn't really necessary
    }

    /**
     * Stores the list of tasks in the database
     * @param tasks The {@link List} of {@link Task}s to store in the Room database.
     */
    public void updateTasks(List<Task> tasks){
        this.mTaskDao.updateTasks(tasks);
       // todo update tasks in the remote database,, although
        // as the app never updates tasks, this isn't really necessary
    }

    /**
     * Deletes task from the databases
     * @param task The {@link Task} to delete from the database.
     */
    public void deleteTask(Task task){
        // delete task in local database
        mTaskDao.delete(task);

       // delete in the remote database
       deleteTaskInRemoteDatabase(task);
    }

    /**
     * Deletes the task in the remote Firebase Realtime database
     * @param task
     */
    private void deleteTaskInRemoteDatabase(Task task){
        // create the URL for this task
        // which is the REMOTE_TASK_URL_BASE/<task id>.json
        Uri uri = Uri.parse(String.format("%s/%s.json", REMOTE_TASKS_URL_BASE, String.valueOf(task.getId())));

        // using a Delete request to remove the task from the list of tasks
        // see https://firebase.google.com/docs/reference/rest/database
        StringRequest request = new StringRequest(Request.Method.DELETE, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO: something more useful here
                        Log.d(TAG, "Successfully uploaded task ");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: something more useful here
                Log.e(TAG, "Error uploaded task ");
            }
        });

        // make the request
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }






}
