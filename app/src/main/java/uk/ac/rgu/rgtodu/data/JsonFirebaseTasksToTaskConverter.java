package uk.ac.rgu.rgtodu.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Utilty class to convert the JSON document of Tasks provided by the Firebase Realtime database into a {@link java.util.List} of {@link Task}s
 */
public class JsonFirebaseTasksToTaskConverter {

    /**
     * Coverts the tasks described in the rootObject to a {@link java.util.List} of {@link Task}s.
     * @param rootObject The root object returned from the Firebase Realtime database for the tasks associated with a user account.
     * @return All of the tasks described in the rootObject
     */
    public List<Task> convertJsonTasks(JSONObject rootObject) throws JSONException {

        // list of tasks to return
        List<Task> tasks = new ArrayList<>();
        JSONObject tasksObject = rootObject.getJSONObject("tasks");
        for (Iterator<String> it = tasksObject.keys(); it.hasNext(); ) {
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
        return tasks;
    }
}
