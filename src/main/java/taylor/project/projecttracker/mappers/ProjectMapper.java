package taylor.project.projecttracker.mappers;

import taylor.project.projecttracker.entity.Project;
import taylor.project.projecttracker.dto.ProjectRecords.ProjectResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectMapper {

    public static ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getDeadline(),
                project.getStatus()
        );
    }

    public static List<ProjectResponse> toResponseList(List<Project> projects) {
        return projects.stream()
                .map(ProjectMapper::toResponse)
                .collect(Collectors.toList());
    }
}

