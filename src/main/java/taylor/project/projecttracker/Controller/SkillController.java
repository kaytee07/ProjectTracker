package taylor.project.projecttracker.Controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Entity.Skill;
import taylor.project.projecttracker.Record.DeveloperRecords.DeveloperResponse;
import taylor.project.projecttracker.Record.SkillRecords.SkillResponse;
import taylor.project.projecttracker.Service.SkillService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(@RequestBody Skill skill,
                                                     @RequestParam String actorName) {
        Skill createdSkill = skillService.createSkill(skill, actorName);
        return ResponseEntity.status(HttpStatus.CREATED).body(toSkillResponse(createdSkill));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateSkill(@PathVariable Long id,
                                                     @RequestBody Skill updatedSkill,
                                                     @RequestParam String actorName) {
        Skill skill = skillService.updateSkill(id, updatedSkill, actorName);
        return ResponseEntity.ok(toSkillResponse(skill));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id,
                                            @RequestParam String actorName) {
        skillService.deleteSkill(id, actorName);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        List<SkillResponse> skills = skillService.getAllSkills()
                .stream()
                .map(this::toSkillResponse)
                .toList();
        return ResponseEntity.ok(skills);
    }

    @PostMapping("/{skillId}/developers/{developerId}")
    public ResponseEntity<SkillResponse> addSkillToDeveloper(@PathVariable Long skillId,
                                                             @PathVariable Long developerId,
                                                             @RequestParam String actorName) {
        Skill skill = skillService.addSkillToDeveloper(skillId, developerId, actorName);
        return ResponseEntity.ok(toSkillResponse(skill));
    }


    @DeleteMapping("/{skillId}/developers/{developerId}")
    public ResponseEntity<SkillResponse> removeSkillFromDeveloper(@PathVariable Long skillId,
                                                                  @PathVariable Long developerId,
                                                                  @RequestParam String actorName) {
        Skill skill = skillService.removeSkillFromDeveloper(skillId, developerId, actorName);
        return ResponseEntity.ok(toSkillResponse(skill));
    }

    private SkillResponse toSkillResponse(Skill skill) {
        Set<Long> developerIds = skill.getDevelopers()
                .stream()
                .map(Developer::getId)
                .collect(Collectors.toSet());
        return new SkillResponse(skill.getId(), skill.getName(), developerIds);
    }
}
