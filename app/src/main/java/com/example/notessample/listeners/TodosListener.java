package com.example.notessample.listeners;

import com.example.notessample.entities.Todo;

public interface TodosListener {
    void onTodoClicked(Todo todo, int position);

}
