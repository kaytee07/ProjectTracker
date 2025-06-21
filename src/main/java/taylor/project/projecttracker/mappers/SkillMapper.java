package taylor.project.projecttracker.mappers;


import taylor.project.projecttracker.entity.Skill;
import taylor.project.projecttracker.entity.User;
import taylor.project.projecttracker.dto.SkillRecords.CreateSkillRequest;
import taylor.project.projecttracker.dto.SkillRecords.SkillResponse;

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

