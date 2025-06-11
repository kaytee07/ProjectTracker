package taylor.project.projecttracker.Mappers;

import taylor.project.projecttracker.Entity.User;
import taylor.project.projecttracker.Record.UserRecords.CreateUserRequest;
import taylor.project.projecttracker.Record.UserRecords.UserResponse;

public class UserMapper {
    public static User toEntity(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setFirstName(createUserRequest.firstName());
        user.setLastName(createUserRequest.lastName());
        user.setEmail(createUserRequest.email());
        user.setRole(createUserRequest.role());
        return user;
    }

    public  static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(), user.
                getRole());
    }
}
