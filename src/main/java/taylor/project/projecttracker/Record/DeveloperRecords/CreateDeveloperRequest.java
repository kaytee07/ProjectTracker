package taylor.project.projecttracker.Record.DeveloperRecords;

import taylor.project.projecttracker.Entity.Skill;

import java.util.Set;

public record CreateDeveloperRequest(
        String name,
        String email,
        Set<Skill> skills
) {}
