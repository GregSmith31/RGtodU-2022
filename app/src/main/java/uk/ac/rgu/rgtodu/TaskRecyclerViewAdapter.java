package uk.ac.rgu.rgtodu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.rgu.rgtodu.data.Task;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> tasks;


    public TaskRecyclerViewAdapter(Context context, List<Task> tasks){
        super();
        this.context = context;
        this.tasks = tasks;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.task_list_view_item, parent, false);
        // store in in a ViewHolder
        TaskViewHolder viewHolder = new TaskViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // item to be displayed
        Task task = this.tasks.get(position);

        // update the task name
        TextView tv_taskName = holder.itemView.findViewById(R.id.tv_taskListItemName);
        tv_taskName.setText(task.getName());

        TextView tv_taskPom = holder.itemView.findViewById(R.id.tv_taskListItemPomodoros);
        tv_taskPom.setText(String.valueOf(task.getPomodorosRemaining()));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        private View itemView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
