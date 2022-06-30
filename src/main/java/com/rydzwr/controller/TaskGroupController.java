package com.rydzwr.controller;

import com.rydzwr.logic.TaskGroupService;
import com.rydzwr.logic.TaskService;
import com.rydzwr.model.Task;
import com.rydzwr.model.TaskGroup;
import com.rydzwr.model.TaskRepository;
import com.rydzwr.model.projection.GroupReadModel;
import com.rydzwr.model.projection.GroupWriteModel;
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
@RequestMapping("/groups")
public class TaskGroupController
{
    private static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private final TaskGroupService service;
    private final TaskRepository repository;

    public TaskGroupController(TaskGroupService service, TaskRepository repository)
    {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate)
    {
        return ResponseEntity.created(URI.create("/")).body(service.createGroup(toCreate));
    }

    @GetMapping
    ResponseEntity<List<GroupReadModel>> readAllGroups()
    {
        return ResponseEntity.ok(service.readAll());
    }

    @GetMapping("/{id}")
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id)
    {
        return ResponseEntity.ok(repository.findAllByGroup_Id(id));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleGroup(@PathVariable int id)
    {
        service.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }

}
