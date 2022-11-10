package uk.ac.rgu.rgtodu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskRecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskRecyclerViewFragment extends Fragment {

    private static final String TAG = "TaskRecyclerViewFrag";

    // the list of tasks being displayed
    List<Task> mTasks;
    // the RecyclerView adapter being used to display them
    RecyclerView.Adapter rvAdapter;


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
        // create an empty list of data to be displayed
        this.mTasks = new ArrayList<Task>();


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

        // setup the RecyclerView

        // get the RecycylerView on the UI
        RecyclerView rv = view.findViewById(R.id.rv_taskRecyclerView);

        // create a new Adapter for the RecyclerView with the empty list
        rvAdapter = new TaskRecyclerViewAdapter(getContext(), this.mTasks);
        // set the recycler view's rv_adapter
        rv.setAdapter(rvAdapter);
        // setup the layout manager on the recycler view
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // now get the Tasks from the remote endpoint
        // donwloadAllTasks();
        // commented out, as now we're using the local database
        mTasks.clear();
        List<Task> takss = TaskRepository.getRepository(getContext()).getAllTasks();
        mTasks.addAll(takss);
        rvAdapter.notifyDataSetChanged();
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

                            // update the UI
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), R.string.download_error_json, Toast.LENGTH_LONG);

                        } finally {

                            Log.d(TAG, "downloaded " + mTasks.size() + " tasks");
                            Log.d(TAG, mTasks.toString());

                            // update the RecyclerView adapter
                            rvAdapter.notifyDataSetChanged();
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
}