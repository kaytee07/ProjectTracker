package taylor.project.projecttracker.Record.TokenRecords;

public record TokenRecordResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
