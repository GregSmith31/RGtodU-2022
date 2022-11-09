package uk.ac.rgu.rgtodu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

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

import uk.ac.rgu.rgtodu.data.JsonFirebaseTasksToTaskConverter;
import uk.ac.rgu.rgtodu.data.Task;
import uk.ac.rgu.rgtodu.data.TaskPriority;
import uk.ac.rgu.rgtodu.data.TaskRepository;
import uk.ac.rgu.rgtodu.data.TaskStatus;

/**
 * A simple {@link Fragment} subclass for displaying Tasks using a ListView and custom adapter
 * Use the {@link TaskListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListViewFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "TaskListViewFrag";

    public TaskListViewFragment() {
        // Required empty public constructor
    }

    // the list of tasks being displayed
    List<Task> mTasks;
    // the ListView being used to display them
    ListView mLvTasks;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TaskListViewFragment.
     */
    public static TaskListViewFragment newInstance() {
        TaskListViewFragment fragment = new TaskListViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        // an empty list of Tasks that will be used to store the task being displayed
        this.mTasks = new ArrayList<Task>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // set ListView where details of the Tasks will be displayed
        mLvTasks = view.findViewById(R.id.lv_tasks);

        // download the tasks
        donwloadAllTasks();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        // called when the user clicks on an item in the list
        Log.d("TASKS", "ListView item at " + position + " clicked");
        // adapterView may have Tasks or Strings depending on which is being displayed
        // so get the tas from the mTasks list
        Task task = mTasks.get(position);
        // create a Bundle and navigate to the ViewTaskFragment
        Bundle bundle = new Bundle();
        // it shouldn't but just in case
        if (task != null){
            bundle.putParcelable(ViewTaskFragment.ARG_TASK, task);
        }
        Log.d("TASKS", "ListView item is " + task.getName());

        // launch the action
        Navigation.findNavController(view).navigate(R.id.action_taskListViewFragment_to_viewTaskFragment, bundle);

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
                        // empty the list of tasks that are currently being displayed
                        mTasks.clear();
                        try {
                            JsonFirebaseTasksToTaskConverter converter = new JsonFirebaseTasksToTaskConverter();

                            // convert the respond to the root Json object
                            JSONObject jsonObject = new JSONObject(response);
                            mTasks.addAll(converter.convertJsonTasks(jsonObject));

                            // update the UI later
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), R.string.download_error_json, Toast.LENGTH_LONG);

                        } finally {

                            // do something with the results
                            Log.d(TAG, "downloaded " + mTasks.size() + " tasks");
                            Log.d(TAG, mTasks.toString());

                            // if we have something to display
                            if (mTasks.size() > 0) {
                                // uncomment whichever method you want to use
                                displayTasksAsBasicStrings();
                                //displayTasksAsCustomStrings();
                                //displayTasksWithCustomAdapter();
                            }
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error with downloading ");
                // display message to the user
                Toast.makeText(getContext(), R.string.download_error, Toast.LENGTH_LONG);
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);

    }

    /**
     * Displays the content of mTasks using a basic ArrayAdapter with a String for each Task
     * based on Task.toString90
     */
    private void displayTasksAsBasicStrings() {
        // for just using the toString method of Task
        ArrayAdapter<Task> lv_adapter = new ArrayAdapter<Task>(
                getContext(),
                android.R.layout.simple_list_item_1,
                mTasks);
        // associate the adapter with the list
        mLvTasks.setAdapter(lv_adapter);
        // set a listener for when the user clicks on a row in the ListView
        mLvTasks.setOnItemClickListener(this);
    }

    /**
     * Displays the content of mTasks using a basic ArrayAdapter with a custom String for each Task
     */
    private void displayTasksAsCustomStrings() {
        // for providing a List of alternative Strings

         List<String> taskStrs = new ArrayList<String>();
         for (Task task : mTasks){
            taskStrs.add(task.getName());
         }
         // create a new adapter for the list of alternative strings
         ArrayAdapter<String> lv_adapter = new ArrayAdapter<String>(
         getContext(),
         android.R.layout.simple_list_item_1,
         taskStrs);

         mLvTasks.setAdapter(lv_adapter);

         // set a listener for when the user clicks on a row in the ListView
        mLvTasks.setOnItemClickListener(this);
    }

    /**
     * Displays the content of mTasks using the Custom ArrayAdapter
     */
    private void displayTasksWithCustomAdapter() {
        // for using a custom Adapter to display each task
        TaskListItemViewAdapter lv_adapter = new TaskListItemViewAdapter(
                getContext(), R.layout.task_list_view_item, mTasks
        );
        // Associate the Adapter with the ListView
        mLvTasks.setAdapter(lv_adapter);
        // no need to add click listener, as its done on the Button on the row
    }


}