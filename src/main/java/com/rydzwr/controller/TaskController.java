package com.rydzwr.controller;

import com.rydzwr.logic.TaskService;
import com.rydzwr.model.Task;
import com.rydzwr.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/tasks")
public class TaskController
{
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskRepository repository;
    private final TaskService service;

    public TaskController(TaskRepository repository, TaskService service)
    {
        this.repository = repository;
        this.service = service;
    }

    @PostMapping
    ResponseEntity<Task> createTask(@RequestBody @Valid Task toCreate)
    {
        Task result = repository.save(toCreate);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @GetMapping(params = {"!sort", "!page", "!size"})
    CompletableFuture<ResponseEntity<List<Task>>> readAllTasks()
    {
        logger.warn("Exposing All The Tasks!");
        return service.findAllAsync().thenApply(ResponseEntity::ok);
    }

    @GetMapping
    ResponseEntity<List<Task>> readAllTasks(Pageable page)
    {
        logger.info("Custom Pageable");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }

    @GetMapping(value = "/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state)
    {
        return ResponseEntity.ok(
                repository.findByDone(state)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<Task> readTask(@PathVariable int id)
    {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
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
    @PatchMapping("/{id}")
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
