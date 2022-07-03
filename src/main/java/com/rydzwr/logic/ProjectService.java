package com.rydzwr.logic;

import com.rydzwr.TaskConfigurationProperties;
import com.rydzwr.model.*;
import com.rydzwr.model.projection.GroupReadModel;
import com.rydzwr.model.projection.GroupTaskWriteModel;
import com.rydzwr.model.projection.GroupWriteModel;
import com.rydzwr.model.projection.ProjectWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectService
{
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;
    private TaskGroupService taskGroupService;

    public ProjectService(ProjectRepository repository, TaskGroupRepository taskGroupRepository, TaskConfigurationProperties config, TaskGroupService taskGroupService)
    {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
        this.taskGroupService = taskGroupService;
    }

    public List<Project> readAll()
    {
        return repository.findAll();
    }

    public Project save(ProjectWriteModel toSave)
    {
        return repository.save(toSave.toProject());
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId)
    {
        if (!config.isAllowMultipleTasksFromTemplate() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId))
        {
            throw new IllegalStateException("Only one undone group from project is allowed!");
        }

        GroupReadModel result = repository.findById(projectId)
                .map(project ->
        {
            var targetGroup = new GroupWriteModel();
            targetGroup.setDescription(project.getDescription());
            targetGroup.setTasks(
                    project.getSteps().stream()
                            .map(projectStep -> {
                                var task = new GroupTaskWriteModel();
                                task.setDescription(project.getDescription());
                                task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                return task;
                            }
                            ).collect(Collectors.toSet())
            );
            return taskGroupService.createGroup(targetGroup, project);
        }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
        return result;
    }

}
