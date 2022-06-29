package com.rydzwr.logic;

import com.rydzwr.TaskConfigurationProperties;
import com.rydzwr.model.ProjectRepository;
import com.rydzwr.model.TaskGroupRepository;
import com.rydzwr.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogicConfiguration
{
    @Bean
    ProjectService projectService(
            final ProjectRepository projectRepository,
            final TaskGroupRepository taskGroupRepository,
            final TaskConfigurationProperties config,
            final TaskGroupService taskGroupService)
    {
        return new ProjectService(projectRepository, taskGroupRepository, config, taskGroupService);
    }

    @Bean
    TaskGroupService taskGroupService(
            final TaskGroupRepository taskGroupRepository,
            final TaskRepository taskRepository
            )
    {
        return new TaskGroupService(taskGroupRepository, taskRepository);
    }
}
