package com.rydzwr.controller;

import com.rydzwr.model.Task;
import com.rydzwr.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
public class TaskController
{
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskRepository repository;

    public TaskController(final TaskRepository repository)
    {
        this.repository = repository;
    }

    @GetMapping(value = "/tasks", params = {"!sort", "!page", "!size"})
    ResponseEntity<List<Task>> readAllTasks()
    {
        logger.warn("Exposing All The Tasks!");
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/tasks")
    ResponseEntity<List<Task>> readAllTasks(Pageable page)
    {
        logger.info("Custom Pageable");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }

    @GetMapping("/tasks/{id}")
    ResponseEntity<Task> readTask(@PathVariable int id)
    {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/tasks/{id}")
    ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate)
    {
       if (!repository.existsById(id))
           return ResponseEntity.notFound().build();

       repository.findById(id)
               .ifPresent(task -> {
                   task.updateFrom(toUpdate);
                   repository.save(task);
               });

       return ResponseEntity.noContent().build();
    }

    @Transactional
    @PatchMapping("/tasks/{id}")
    public ResponseEntity<?> toggleTask(@PathVariable int id)
    {
        if (!repository.existsById(id))
        {
            return ResponseEntity.notFound().build();
        }

        repository.findById(id).ifPresent(task -> task.setDone(!task.isDone()));
        return ResponseEntity.noContent().build();
    }

}
