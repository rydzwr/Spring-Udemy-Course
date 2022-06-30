package com.rydzwr.logic;

import com.rydzwr.model.Task;
import com.rydzwr.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskService
{
    private final TaskRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository repository)
    {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<List<Task>> findAllAsync()
    {
        logger.info("Async find!");
        return CompletableFuture.supplyAsync(repository::findAll);
    }
}
