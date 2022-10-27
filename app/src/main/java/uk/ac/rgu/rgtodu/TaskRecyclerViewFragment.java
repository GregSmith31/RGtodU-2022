package uk.ac.rgu.rgtodu;

import static java.security.AccessController.getContext;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import uk.ac.rgu.rgtodu.data.Task;
import uk.ac.rgu.rgtodu.data.TaskPriority;
import uk.ac.rgu.rgtodu.data.TaskStatus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskRecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskRecyclerViewFragment extends Fragment {

    private static final String TAG = "TaskRecyclerViewFrag";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TaskRecyclerViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskListRecyclerViewActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskRecyclerViewFragment newInstance(String param1, String param2) {
        TaskRecyclerViewFragment fragment = new TaskRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_recycler_view_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        donwloadAllTasks();
    }

    /**
     * Gets all of the Tasks in the remote Firebase Realtime Database
     * @return n a {@link List} of {@link Task} entities from the remote database
     */
    private void donwloadAllTasks(){
        // make my volley request
        String url = "https://cm3110-2022-default-rtdb.firebaseio.com/dcorsar.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // a list of tasks to store them all in
                            List<Task> tasks = new ArrayList<Task>();

                            // Process the JSON response
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject tasksObject = jsonObject.getJSONObject("tasks");
                            // for each task
                            for (Iterator<String> it = tasksObject.keys(); it.hasNext();){
                                String taskId = it.next();
                                // extract the key information
                                JSONObject taskObj = tasksObject.getJSONObject(taskId);
                                String name = taskObj.getString("name");
                                String objective = taskObj.getString("objective");
                                int pomodoros = taskObj.getInt("pomodorosRemaining");
                                long deadlineL = taskObj.getLong("deadline");
                                String priority = taskObj.getString("priority");
                                String status = taskObj.getString("status");

                                // now create a Task based on it
                                Task task = new Task();
                                task.setName(name);
                                task.setObjective(objective);
                                task.setPomodorosRemaining(pomodoros);
                                task.setPriority(TaskPriority.valueOf(priority));
                                // convert the long timestampe to a date
                                Date deadLineDate = new Date();
                                deadLineDate.setTime(deadlineL);
                                task.setDeadline(deadLineDate);
                                // set the status
                                task.setStatus(TaskStatus.valueOf(status));
                                // add that information to the tasks list
                                tasks.add(task);
                            }
                            Log.d(TAG, "downloaded " + tasks.size() + " tasks");
                            Log.d(TAG, tasks.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tasks", error.getLocalizedMessage());
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);
    }
}