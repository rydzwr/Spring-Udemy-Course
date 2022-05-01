package com.rydzwr.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Task's description must be not empty")
    private String description;
    private boolean done;
    private LocalDateTime deadLine;

    @Embedded
    private Audit audit = new Audit();


    @ManyToOne
    @JoinColumn(name = "task_group_id")
    private TaskGroup group;

    public Task() { }

    public Task(String description, LocalDateTime deadLine)
    {
        this.description = description;
        this.deadLine = deadLine;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean isDone()
    {
        return done;
    }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    public TaskGroup getGroup()
    {
        return group;
    }

    public LocalDateTime getDeadLine()
    {
        return deadLine;
    }

    public void updateFrom(final Task source)
    {
        description = source.description;
        done = source.done;
        deadLine = source.deadLine;
        group = source.group;
    }
}
