package taylor.project.projecttracker.Mappers;

import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Entity.Skill;
import taylor.project.projecttracker.Record.DeveloperRecords.CreateDeveloperRequest;
import taylor.project.projecttracker.Record.DeveloperRecords.DeveloperResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DeveloperMapper {

    public static Developer toEntity(CreateDeveloperRequest request) {
        Developer developer = new Developer();
        developer.setName(request.name());
        developer.setEmail(request.email());
        developer.setSkills(request.skills());
        return developer;
    }

    public static DeveloperResponse toResponse(Developer developer) {
        return new DeveloperResponse(
                developer.getId(),
                developer.getName(),
                developer.getEmail(),
                developer.getSkills().stream()
                        .map(Skill::getId)
                        .collect(Collectors.toSet())
        );
    }

    public static List<DeveloperResponse> toResponseList(List<Developer> developers) {
        return developers.stream()
                .map(DeveloperMapper::toResponse)
                .collect(Collectors.toList());
    }
}
