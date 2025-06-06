package taylor.project.projecttracker.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Mappers.DeveloperMapper;

import taylor.project.projecttracker.Record.DeveloperRecords.CreateDeveloperRequest;
import taylor.project.projecttracker.Record.DeveloperRecords.DeveloperResponse;
import taylor.project.projecttracker.Record.DeveloperRecords.UpdateDeveloperRequest;
import taylor.project.projecttracker.Record.DeveloperRecords.UpdateDeveloperSkillRequest;
import taylor.project.projecttracker.Service.DeveloperService;
import taylor.project.projecttracker.UtilityInterfaces.DeveloperTaskCount;

import java.util.List;

@RestController
@RequestMapping("/api/developers")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    @PostMapping
    public ResponseEntity<DeveloperResponse> createDeveloper(@RequestBody CreateDeveloperRequest request, @RequestParam String actorName) {
        Developer response = developerService.createDeveloper(DeveloperMapper.toEntity(request), actorName);
        return ResponseEntity.ok(DeveloperMapper.toResponse(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeveloperResponse> updateDeveloper(@PathVariable Long id,
                                                             @RequestBody UpdateDeveloperRequest request, @RequestParam String actorName) {
        DeveloperResponse response = DeveloperMapper.toResponse(developerService.updateDeveloper(id, request, actorName));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDeveloper(@PathVariable Long id, @RequestParam String actorName) {
        developerService.deleteDeveloper(id, actorName);
        return ResponseEntity.ok("Deleted deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<DeveloperResponse>> getAllDevelopers() {
        return ResponseEntity.ok(DeveloperMapper.toResponseList(developerService.findAllDevelopers()));
    }

    @PutMapping("/{developerId}/skills")
    public ResponseEntity<DeveloperResponse> updateSkills(@PathVariable Long developerId,
                                                          @RequestBody UpdateDeveloperSkillRequest request) {
        DeveloperResponse response = DeveloperMapper.toResponse(developerService.updateDeveloperSkills(developerId, request));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/developers/top")
    public ResponseEntity<List<DeveloperTaskCount>> getTop5Developers() {
        return ResponseEntity.ok(developerService.getTop5DevelopersByTaskCount());
    }


    @GetMapping("/paginated")
    public ResponseEntity<Page> getPaginatedDevelopers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Page developers = developerService.getPaginatedDevelopers(page, size, sortBy, direction);
        return ResponseEntity.ok(developers);
    }
}

