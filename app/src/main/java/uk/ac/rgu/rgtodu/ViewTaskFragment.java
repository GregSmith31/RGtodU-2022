package uk.ac.rgu.rgtodu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import uk.ac.rgu.rgtodu.data.Task;
import uk.ac.rgu.rgtodu.data.TaskPriority;
import uk.ac.rgu.rgtodu.data.TaskRepository;
import uk.ac.rgu.rgtodu.data.TaskStatus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewTaskFragment extends Fragment implements AdapterView.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewTaskFragment newInstance(String param1, String param2) {
        ViewTaskFragment fragment = new ViewTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // tag for logging
    private static final String TAG = "ViewTaskFragment";

    // key for storing the mTask during configuration changes
    private static final String KEY_TASK = "mtask";

    // Field variable for storing the task being displayed
    private Task mTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on create");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // we could check the if savedInstanceState has a task in it
        // here (which we would for Activities) but for fragments
        // that goes into the onViewCreated


        // initialise the data to be displayed in this UI
        // it would be better programming to put this in onViewCreated
        // given that we're now handing restoring state to avoid
        // calling this if its not needed
        this.mTask = TaskRepository.getRepository(getContext()).getSyntheticTask();

        Log.d(TAG, mTask.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "on start");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "on resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "on stop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on destroy");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_TASK, this.mTask);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // add click listener to the delete button
        Button btnDelete = view.findViewById(R.id.btn_viewDeleteTask);
        btnDelete.setOnClickListener(this);

        // check if savedInstanceState has a task in it to display
        // i.e. recovering from a configuration change
        // savedInstanceState will be null if its the first time this is running
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_TASK)){
            Task t = savedInstanceState.getParcelable(KEY_TASK);
            // t shouldn't be null, but defensive programming just incase
            if (t != null){
                this.mTask = t;
            }
        }
        displayTask(view, this.mTask);


    }

    /**
     * Updates the UI to display details of task
     * @param task
     */
    private void displayTask(View v, Task task) {
        // display the task name
        TextView tv_viewTaskName = v.findViewById(R.id.tv_viewTaskName);
        tv_viewTaskName.setText(task.getName());

        // display the task objective
        TextView tv_viewTaskObjective = v.findViewById(R.id.tv_viewTaskObjective);
        tv_viewTaskObjective.setText(task.getObjective());

        // display the task priority
        TextView tv_taskPriority = v.findViewById(R.id.tv_viewPriorityValue);
        TaskPriority priority = task.getPriority();
        switch (priority){
            case LOW: tv_taskPriority.setText(getString(R.string.rb_low));
                break;
            case MEDIUM:tv_taskPriority.setText(getString(R.string.rb_medium));
                break;
            case HIGH:tv_taskPriority.setText(getString(R.string.rb_high));
                break;
        }

        // display the task status
        TextView tv_taskStatus = v.findViewById(R.id.tv_viewStatusValue);
        TaskStatus taskStatus = task.getStatus();
        switch (taskStatus){
            case NOT_STARTED: tv_taskStatus.setText(getString(R.string.task_status_not_started));
                break;
            case IN_PROGRESS:tv_taskStatus.setText(getString(R.string.task_status_in_progress));
                break;
            case COMPLETE:tv_taskStatus.setText(getString(R.string.task_status_completed));
                break;
        }

        // display the pomodoros to completion
        TextView tv_pomodoros = v.findViewById(R.id.tv_viewPomodorosRemainingValue);
        tv_pomodoros.setText(getString(R.string.tv_pomodorosRemainingValue,
                task.getPomodorosRemaining()));

        // display the task deadline
        TextView tv_dateValue = v.findViewById(R.id.tv_viewTaskDeadlineValue);
        DateFormat format = SimpleDateFormat.getDateInstance();
        String formattedDate = format.format(task.getDeadline());
        tv_dateValue.setText(formattedDate);
    }

    @Override
    public void onClick(View view) {
        // for the delete task button
        if (view.getId() == R.id.btn_viewDeleteTask){
            // if we're displaying a task, then delete it
            if (this.mTask != null){
                TaskRepository.getRepository(getContext()).deleteTask(this.mTask);
            }
        }
    }
}