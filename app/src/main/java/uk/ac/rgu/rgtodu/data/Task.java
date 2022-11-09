package uk.ac.rgu.rgtodu.data;

import android.os.Parcel;
import android.os.Parcelable;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * A piece of work that needs to be done
 * @author David Corsar
 */
@Entity(tableName = "task")
public class Task implements Parcelable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int uid;

    // id for the task
    private long id;

    // reference name for the task
    private String name;
    // the objective to be completed
    private String objective;
    // how important the task is
    private TaskPriority priority;
    // what the status of the task is
    private TaskStatus status;
     // estimate of how many pomodoros it will take to complete
     @ColumnInfo(name = "Poms_Left")
    private int pomodorosRemaining;
    // when the task needs to be completed byo
    private Date deadline;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getPomodorosRemaining() {
        return pomodorosRemaining;
    }

    public void setPomodorosRemaining(int pomodorosRemaining) {
        this.pomodorosRemaining = pomodorosRemaining;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        // write all of the fields of the Task to out
        out.writeLong(getId());
        out.writeString(getName());
        out.writeString(getObjective());
        out.writeInt(getPomodorosRemaining());
        out.writeLong(getDeadline().getTime());
        // as TaskPriority is an enum, write the label as a String
        out.writeString(getPriority().getLabel());
        // as TaskStatus is an enum, write the label as a String
        out.writeString(getStatus().getLabel());
    }

    // A creators for reading Tasks back from a Parcel
    public static final Creator<Task> CREATOR
            = new Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            // create and return a new Task based on the contents on in
            Task task = new Task();
            // read the Task fields in the same order they were writter
            task.setId(in.readLong());
            task.setName(in.readString());
            task.setObjective(in.readString());
            task.setPomodorosRemaining(in.readInt());
            task.setDeadline(new Date(in.readLong()));
            // restore proprity using the TaskProprity enum valueOf method for the String that
            // was written to the Parcel
            task.setPriority(TaskPriority.valueOf(in.readString()));
            // same with status
            task.setStatus(TaskStatus.valueOf(in.readString()));
            // now return the new task
            return task;
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", objective='" + objective + '\'' +
                ", priority=" + priority +
                ", pomodoros=" + pomodorosRemaining +
                ", status=" + status +
                ", deadline=" + deadline +
                '}';
    }
}

