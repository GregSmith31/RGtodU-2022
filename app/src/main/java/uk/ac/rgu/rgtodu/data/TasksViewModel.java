package uk.ac.rgu.rgtodu.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * A {@link androidx.lifecycle.ViewModel} for all the tasks in the database
 * Based on https://developer.android.com/codelabs/android-training-livedata-viewmodel?index=..%2F..%2Fandroid-training#8
 */
public class TasksViewModel extends AndroidViewModel {
    private TaskRepository mRepository;
    private LiveData<List<Task>> mAllTasks;

    public TasksViewModel(Application application){
        super(application);
        mRepository = TaskRepository.getRepository(application.getApplicationContext());
        mAllTasks = mRepository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks(){
        return mAllTasks;
    }
    public void storeTask(Task task) {
        mRepository.storeTask(task);
    }
    public void deleteTask(Task task){
        mRepository.deleteTask(task);
    }
}
