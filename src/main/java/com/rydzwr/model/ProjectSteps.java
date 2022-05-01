package com.rydzwr.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "project_steps")
public class ProjectSteps
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Project step's description must be not empty")
    private String description;

    private int daysToDeadline;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public int getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    public int getDaysToDeadline()
    {
        return daysToDeadline;
    }

    public Project getProject()
    {
        return project;
    }
}

