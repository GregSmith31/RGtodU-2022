package uk.ac.rgu.rgtodu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import uk.ac.rgu.rgtodu.data.Task;
import uk.ac.rgu.rgtodu.data.TaskRepository;

/**
 * A simple {@link Fragment} subclass for displaying Tasks using a ListView and custom adapter
 * Use the {@link TaskListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListViewFragment extends Fragment implements AdapterView.OnItemClickListener {

    public TaskListViewFragment() {
        // Required empty public constructor
    }

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

        // Create our own adapter to use to display the Tasks in the ListView
        adapter = new TaskListItemViewAdapter(
                getContext(),
                R.layout.task_list_view_item);
    }

    TaskListItemViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // the List of Tasks to be displayed
        List<Task> tasks = TaskRepository.getRepository(getContext()).getSyntheticTasks(1000);

        // the ListView where details of the Tasks will be displayed
        ListView lv_Tasks = view.findViewById(R.id.lv_tasks);

        // for just using the toString method of Task
        /** Uncomment this to use just the toString() ***
        ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(
                                 getContext(),
                          android.R.layout.simple_list_item_1,
                          tasks);
        // associate the adapter with the list
        lv_Tasks.setAdapter(adapter);

         // set a listener for when the user clicks on a row in the ListView
         lv_Tasks.setOnItemClickListener(this);
         **** end of code for just using the toString() */

        // for providing a List of alternative Strings
        /*** Uncomment this to use an adapter with alternative Strings ***
        List<String> taskStrs = new ArrayList<String>();
        for (Task task : tasks){
            taskStrs.add(task.getName());
        }
        // create a new adapter for the list of alternative strings
        ArrayAdapter<String> adapterAltStr = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                taskStrs);

        lv_Tasks.setAdapter(adapterAltStr);

         // set a listener for when the user clicks on a row in the ListView
         lv_Tasks.setOnItemClickListener(this);
        *** End of code for using alternative strings for the list ***/

        // for using a custom Adapter to display each task
        /*** Comment out the following few lines if using a different approach ***/
        TaskListItemViewAdapter customAdapter = new TaskListItemViewAdapter(
                getContext(), R.layout.task_list_view_item, tasks
        );
        // Associate the Adapter with the ListView
        lv_Tasks.setAdapter(customAdapter);
        /* end of code to comment out for custom adapter */
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d("TASKS", "ListView item at " + position + " clicked");
        Task taskStr = (Task)adapterView.getItemAtPosition(position);
        Log.d("TASKS", "ListView item is " + taskStr.getName());
    }



}