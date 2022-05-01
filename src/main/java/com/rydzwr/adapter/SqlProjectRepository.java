package com.rydzwr.adapter;

import com.rydzwr.model.Project;
import com.rydzwr.model.ProjectRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SqlProjectRepository extends ProjectRepository, JpaRepository<Project, Integer>
{
    @Override
    @Query("from Project p join fetch p.steps")
    List<Project> findAll();
}
