package uk.ac.rgu.rgtodu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

import uk.ac.rgu.rgtodu.data.Task;
import uk.ac.rgu.rgtodu.data.TaskPriority;
import uk.ac.rgu.rgtodu.data.TaskRepository;
import uk.ac.rgu.rgtodu.data.TaskStatus;

/**
 * A {@link Fragment} subclass to support the user creating and editing tasks.
 * Use the {@link CreateTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTaskFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateTaskFragment newInstance(String param1, String param2) {
        CreateTaskFragment fragment = new CreateTaskFragment();
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
        return inflater.inflate(R.layout.fragment_create_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // add the action listener for the Save button click
        Button saveBtn = view.findViewById(R.id.btn_save_task);
        saveBtn.setOnClickListener(this);
        // add action listener for the cancel button click
        // just in a more compact notation
        ((Button)view.findViewById(R.id.btn_canclel_new_task)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save_task) {
            // get all of the values that have been entered to create a new Task
            String taskName = String.valueOf(((TextView) getView().findViewById(R.id.et_taskName)).getText());
            String taskObjective = String.valueOf(((TextView) getView().findViewById(R.id.et_taskObjective)).getText());
            Date taskDeadline = new Date(((CalendarView) getView().findViewById(R.id.cv_taskDeadline)).getDate());
            int pomodoros = Integer.parseInt(String.valueOf(((EditText) getView().findViewById(R.id.et_taskPomodorosRemaining)).getText()));

            // create the new task with those values
            Task task = new Task();
            // bit of a hack for the id, but it should be unique
            task.setId(System.currentTimeMillis()/1000);
            task.setName(taskName);
            task.setObjective(taskObjective);
            task.setPomodorosRemaining(pomodoros);
            task.setDeadline(taskDeadline);

            // now set the proiority
            // for some unknown reason the app crashes when the radio buttons are clicked
            // so set all to medium for now
//            int taskPriorityButton = ((RadioGroup) getView().findViewById(R.id.rb_taskPriority)).getCheckedRadioButtonId();
            //        if (taskPriorityButton == R.id.rb_low)
            //            task.setPriority(TaskPriority.LOW);
            //        else if (taskPriorityButtonPriorityButton == R.id.rb_medium)
            task.setPriority(TaskPriority.MEDIUM);
            //        else if (taskPriorityButton == R.id.rb_high)
            //            task.setPriority(TaskPriority.HIGH);

            // now set the status
            Spinner statusSpinner = getView().findViewById(R.id.spinner_taskStatus);
            String selectedStatus = String.valueOf(statusSpinner.getSelectedItem());
            if (selectedStatus.equals(getString(R.string.task_status_not_started))){
                task.setStatus(TaskStatus.NOT_STARTED);
            } else if (selectedStatus.equals(getString(R.string.task_status_in_progress))){
                task.setStatus(TaskStatus.IN_PROGRESS);
            } else if (selectedStatus.equals(getString(R.string.task_status_completed))){
                // this adds a comparison but avoids a potential bug if we add more status,
                // and forget to update in the future if we add more status.
                task.setStatus(TaskStatus.COMPLETE);
            }

            // now save the task
            TaskRepository repo = TaskRepository.getRepository(getContext());
            repo.storeTask(task);

            // Now go back to the home page
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_create_task_to_home);
        } else if (view.getId() == R.id.btn_canclel_new_task) {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_create_task_to_home);
        }
    }
}