package com.rydzwr.logic;

import com.rydzwr.TaskConfigurationProperties;
import com.rydzwr.model.*;
import com.rydzwr.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest
{

    @Test
    @DisplayName("Should throw IllegalStateException when configured to allow just 1 group and undone group exists")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupExists_throwsIllegalStateException()
    {
        // GIVEN
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(true);

        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(false);

        var mocConfig = mock(TaskConfigurationProperties.class);
        when(mocConfig.getTemplate()).thenReturn(mockTemplate);
        // system under test
        var toTest = new ProjectService(null, mockGroupRepository, mocConfig);

        // WHEN + THEN
        assertThatIllegalStateException().isThrownBy(() -> toTest.createGroup(LocalDateTime.now(), 0));
    }

    @Test
    @DisplayName("More accurate than above")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupExists_throwsIllegalStateException_with_message()
    {
        // GIVEN
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(true);

        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(false);

        var mocConfig = mock(TaskConfigurationProperties.class);
        when(mocConfig.getTemplate()).thenReturn(mockTemplate);
        // system under test
        var toTest = new ProjectService(null, mockGroupRepository, mocConfig);

        // WHEN
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        // THEN
        assertThat(exception).isInstanceOf(IllegalStateException.class).hasMessageContaining("one undone group");
    }

    @Test
    @DisplayName("Simpler way than above && " +
            "should throw IllegalArgumentException when conf. ok and no projects for given id")
    void createGroup_configuration_ok_and_no_projects_throws_IllegalArgumentException()
    {
        // GIVEN
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());

        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(false);

        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        var toTest = new ProjectService(mockRepository, mockGroupRepository, mockConfig);

        // WHEN
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        // THEN
        assertThat(exception).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when configured to allow just one group" +
            " and no groups and no projects for given id")
    void createGroup_noMultipleGroupsConfig_and_noUndoneGroupsExists_noProjects_throwsIllegalargumentException()
    {
        // GIVEN
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());

        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);

        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        var toTest = new ProjectService(mockRepository, mockGroupRepository, mockConfig);

        // WHEN
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        // THEN
        assertThat(exception).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("Should create new group from project")
    void createGroup_configurationOK_existsProject_createsAndSaves_Group()
    {
        // GIVEN
        var today = LocalDate.now().atStartOfDay();

        var project = projectWith("bar", Set.of(-1, -2));
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt()))
                .thenReturn(Optional.of(project));

        InMemoryGroupRepository inMemoryGroupRepo = inMemoryGroupRepository();
        int countBeforeCall = inMemoryGroupRepo.count();

        TaskConfigurationProperties mockConfig = configurationReturning(true);

        var toTest = new ProjectService(mockRepository, inMemoryGroupRepo, mockConfig);

        // WHEN

        GroupReadModel result = toTest.createGroup(today, 1);

        // THEN

        assertThat(result.getDescription()).isEqualTo("bar");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks()).allMatch(task -> task.getDescription().equals("x"));
        assertThat(countBeforeCall + 1).isEqualTo(inMemoryGroupRepo.count());
    }

    private Project projectWith(String projectDescription, Set<Integer> daysToDeadline)
    {
        var result = mock(Project.class);
        when(result.getDescription()).thenReturn(projectDescription);
        Set<ProjectSteps> steps = daysToDeadline.stream()
                .map(days -> {
                    var step = mock(ProjectSteps.class);
                    when(step.getDescription()).thenReturn("x");
                    when(step.getDaysToDeadline()).thenReturn(days);
                    return step;
                })
                .collect(Collectors.toSet());

        when(result.getSteps()).thenReturn(steps);

        return result;
    }

    private TaskGroupRepository groupRepositoryReturning(final boolean b)
    {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(b);
        return mockGroupRepository;
    }

    private TaskConfigurationProperties configurationReturning(final boolean b)
    {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(true);

        var mocConfig = mock(TaskConfigurationProperties.class);
        when(mocConfig.getTemplate()).thenReturn(mockTemplate);

        return mocConfig;
    }

    private InMemoryGroupRepository inMemoryGroupRepository()
    {
        return new InMemoryGroupRepository();
    }

    private static class InMemoryGroupRepository implements TaskGroupRepository
    {
        private Map<Integer, TaskGroup> map = new HashMap<>();
        private int index = 0;

        public int count()
        {
            return map.values().size();
        }

        @Override
        public List<TaskGroup> findAll()
        {
            return new ArrayList<>(map.values());
        }

        @Override
        public Optional<TaskGroup> findById(Integer id)
        {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public TaskGroup save(TaskGroup entity)
        {
            if (entity.getId() == 0)
            {
                try
                {
                    // Access by reflection... not good practise
                    var field = TaskGroup.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, ++index);
                }
                catch (NoSuchFieldException | IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }

            map.put(entity.getId(), entity);

            return entity;
        }

        @Override
        public boolean existsByDoneIsFalseAndProject_Id(Integer projectId)
        {
            return map.values().stream()
                    .filter(group -> !group.isDone())
                    .anyMatch(group -> group.getProject() != null && group.getProject().getId() == projectId);
        }
    };
}