package com.rydzwr.controller;

import com.rydzwr.logic.ProjectService;
import com.rydzwr.model.ProjectSteps;
import com.rydzwr.model.projection.ProjectWriteModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/projects")
public class ProjectController
{
    private final ProjectService service;

    public ProjectController(ProjectService service)
    {
        this.service = service;
    }

    @GetMapping
    String showProjects(Model model)
    {
        var projectToEdit = new ProjectWriteModel();
        projectToEdit.setDescription("test");
        model.addAttribute("project", projectToEdit);
        return "projects";
    }

    @PostMapping
    String addProject(@ModelAttribute("project") ProjectWriteModel current, Model model)
    {
        service.save(current);
        model.addAttribute("project", new ProjectWriteModel());
        model.addAttribute("message", "Dodano Projekt");
        return "projects";
    }

    @PostMapping(params = "addStep")
    String addProjectStep(@ModelAttribute("project") ProjectWriteModel current)
    {
        current.getSteps().add(new ProjectSteps());
        return "projects";
    }
}
