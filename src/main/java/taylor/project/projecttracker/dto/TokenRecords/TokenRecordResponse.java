package taylor.project.projecttracker.dto.TokenRecords;

public record TokenRecordResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
