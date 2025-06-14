package taylor.project.projecttracker.Mappers;


import taylor.project.projecttracker.Entity.Skill;
import taylor.project.projecttracker.Entity.User;
import taylor.project.projecttracker.Record.SkillRecords.CreateSkillRequest;
import taylor.project.projecttracker.Record.SkillRecords.SkillResponse;

import java.util.stream.Collectors;

public class SkillMapper {

    public static Skill toEntity(CreateSkillRequest request) {
        Skill skill = new Skill();
        skill.setName(request.name());
        return skill;
    }

    public static SkillResponse toResponse(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getUsers().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet())
        );
    }
}

