package com.example.notessample.ui.todos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notessample.R;
import com.example.notessample.activities.CreateTodoActivity;
import com.example.notessample.adapters.TodosAdapter;
import com.example.notessample.database.TodosDatabase;
import com.example.notessample.entities.Todo;
import com.example.notessample.listeners.TodosListener;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class TodosFragment extends Fragment implements TodosListener {

    //used for diaryMainActivity
    public static final int REQUEST_CODE_ADD_TODO = 1;
    public static final int REQUEST_CODE_UPDATE_TODO = 2;
    public static final int REQUEST_CODE_SHOW_TODO = 3;

    private RecyclerView todosRecyclerView;
    private List<Todo> todoList;
    private TodosAdapter todosAdapter;

    private int todoClickedPosition = -1;

    private com.example.notessample.ui.todos.TodosViewModel todosViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        todosViewModel =
                new ViewModelProvider(this).get(com.example.notessample.ui.todos.TodosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_todo, container, false);

        //usedForDiaryMainActivity
        ImageView imageAddTodoMain = root.findViewById(R.id.imageAddTodoMain);
        imageAddTodoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getContext(), CreateTodoActivity.class),
                        REQUEST_CODE_ADD_TODO
                );
            }
        });

        todosRecyclerView = root.findViewById(R.id.todoRecyclerView);
        todosRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        todoList = new ArrayList<>();
        todosAdapter = new TodosAdapter(todoList, this);
        todosRecyclerView.setAdapter(todosAdapter);

        getTodos(REQUEST_CODE_SHOW_TODO, false); //Part of onCreate, need to show all diarys

        EditText inputSearch = root.findViewById(R.id.inputSearchTodo);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                todosAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(todoList.size() != 0) {
                    todosAdapter.searchTodo(editable.toString());
                }
            }
        });


        return root;
    }

    @Override
    public void onTodoClicked(Todo todo, int position) {
        todoClickedPosition = position;
        Intent intent = new Intent(getContext(), CreateTodoActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("todo", todo);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_TODO);
    }

    private void getTodos(final int requestCode, final boolean isTodoDeleted) {

        @SuppressLint("StaticFieldLink")
        class GetTodosTask extends AsyncTask<Void, Void, List<Todo>> {

            @Override
            protected List<Todo> doInBackground(Void... voids) {
                return TodosDatabase
                        .getDatabase(getContext())
                        .todoDao().getAllTodos();
            }

            @Override
            protected void  onPostExecute(List<Todo> todos) {
                super.onPostExecute(todos);
                if(requestCode == REQUEST_CODE_SHOW_TODO) { //add all todos from DB to noteList and notify Adapter of change
                    todoList.addAll(todos);
                    todosAdapter.notifyDataSetChanged();
                }
                else if (requestCode == REQUEST_CODE_ADD_TODO) {//add first (newest added) note from DB to noteList, notify Adapter, scroll to the top
                    todoList.add(0, todos.get(0));
                    todosAdapter.notifyItemInserted(0);
                    todosRecyclerView.smoothScrollToPosition(0);
                }
                else if(requestCode == REQUEST_CODE_UPDATE_TODO) {//remove from clicked position from noteList, add to clicked position from DB, notify adapter of change at clicked position
                    todoList.remove(todoClickedPosition);
                    if(isTodoDeleted){
                        todosAdapter.notifyItemRemoved(todoClickedPosition);
                    }
                    else
                    {
                        todoList.add(todoClickedPosition, todos.get(todoClickedPosition));
                        todosAdapter.notifyItemChanged(todoClickedPosition);
                    }
                }
            }
        }

        new GetTodosTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_TODO && resultCode == RESULT_OK) { //RESULT_OK IN CreateNoteActivity and REQUEST_CODE_ADD_NOTE in DashboardFragment
            getTodos(REQUEST_CODE_ADD_TODO, false); //from onActivity, request is for adding note and it is okayed
        }
        else if (requestCode == REQUEST_CODE_UPDATE_TODO && resultCode == RESULT_OK) {
            if(data != null){
                getTodos(REQUEST_CODE_UPDATE_TODO, data.getBooleanExtra("isTodoDeleted", false)); //from onActivity, request is for updating note and it is okayed
            }
        }
    }
}