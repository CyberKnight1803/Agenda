package com.example.notessample.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notessample.R;
import com.example.notessample.entities.Todo;
import com.example.notessample.listeners.TodosListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.TodosViewHolder> {

    private List<Todo> todo;
    private TodosListener todosListener;
    private Timer timerTodo;
    private List<Todo> todoSource;


    public TodosAdapter(List<Todo> todo, TodosListener todosListener) {
        this.todo = todo;
        this.todosListener = todosListener;
        todoSource = todo;

    }

    @NonNull
    @Override
    public TodosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TodosViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_todo,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TodosViewHolder holder, int position) {
        holder.setTodo(todo.get(position));
        holder.layoutTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todosListener.onTodoClicked(todo.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todo.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class TodosViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutTodo;
        RoundedImageView imageTodo;

        TodosViewHolder(@NonNull View itemview) {
            super(itemview);
            textTitle = itemview.findViewById(R.id.textTitleTodos);
            textSubtitle = itemview.findViewById(R.id.textSubtitleTodos);
            textDateTime = itemview.findViewById(R.id.textDateTimeTodo);
            layoutTodo = itemview.findViewById(R.id.layoutTodos);
            imageTodo = itemview.findViewById(R.id.imageTodo);
        }

        void setTodo(Todo todo) {
            textTitle.setText(todo.getTitle());
            if (todo.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            }
            else {
                textSubtitle.setText(todo.getSubtitle());
            }
            textDateTime.setText(todo.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutTodo.getBackground();
            if (todo.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(todo.getColor()));
            }
            else
            {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

        }
    }

    public void searchTodo(final String searchKeyword) {
        timerTodo = new Timer();
        timerTodo.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    todo = todoSource;
                } else {
                    ArrayList<Todo> temp = new ArrayList<>();
                    for (Todo todo : todoSource) {
                        if (todo.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || todo.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || todo.getTodoText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(todo);
                        }
                    }
                    todo = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer() {
        if(timerTodo !=null) {
            timerTodo.cancel();
        }
    }

}
