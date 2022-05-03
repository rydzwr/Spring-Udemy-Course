package com.rydzwr.logic;

import com.rydzwr.TaskConfigurationProperties;
import com.rydzwr.model.*;
import com.rydzwr.model.projection.GroupReadModel;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestScope
public class ProjectService
{
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;

    public ProjectService(ProjectRepository repository, TaskGroupRepository taskGroupRepository, TaskConfigurationProperties config)
    {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
    }

    public List<Project> readAll()
    {
        return repository.findAll();
    }

    public Project save(Project toSave)
    {
        return repository.save(toSave);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId)
    {
        if (!config.isAllowMultipleTasksFromTemplate() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId))
        {
            throw new IllegalStateException("Only one undone group from project is allowed!");
        }

        TaskGroup result = repository.findById(projectId).map(project ->
        {
            var targetGroup = new TaskGroup();
            targetGroup.setDescription(project.getDescription());
            targetGroup.setTasks(
                    project.getSteps().stream()
                            .map(step -> new Task(
                                    step.getDescription(),
                                    deadline.plusDays(step.getDaysToDeadline()))
                            ).collect(Collectors.toSet())
            );
            targetGroup.setProject(project);
            return taskGroupRepository.save(targetGroup);

        }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));

        return new GroupReadModel(result);
    }

}
