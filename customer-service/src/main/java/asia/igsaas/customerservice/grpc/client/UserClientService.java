package asia.igsaas.customerservice.grpc.client;

import asia.igsaas.customerservice.customer.dto.CustomerDto;
import asia.igsaas.grpc.user.*;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;

import org.slf4j.Marker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;

@Service
@Slf4j
public class UserClientService {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub blockingStub;

    /**
     * Create a new user.
     */
    public CreateUserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with name: " + request.getName() + ", email: " + request.getEmail());
//        CreateUserRequest request = CreateUserRequest.newBuilder()
//                .setName(name)
//                .setEmail(email)
//                .build();
        CreateUserResponse response;
        try {
            response = blockingStub.createUser(request);
            log.info("Created user with id: " + response.getId());
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
            throw e;
        }
        return response;
    }

    /**
     * Get a user by ID.
     */
    public User getUser(String id) {
        log.info("Getting user with id: " + id);
        GetUserRequest request = GetUserRequest.newBuilder().setId(id).build();
        UserResponse response;
        try {
            response = blockingStub.getUser(request);
            log.info("Found user: " + response.getUser().getName());
            return response.getUser();
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
            throw e;
        }
    }

    /**
     * Get all users.
     */
    public List<CustomerDto> getAllUsers() {
        log.info("Getting all users");
        GetAllUsersRequest request = GetAllUsersRequest.newBuilder().build();
        try {
            GetAllUsersResponse response = blockingStub.getAllUsers(request);

            // Convert each user in the response to CustomerDto
            return response.getUsersList().stream()
                    .map(this::convertToCustomerDto)
                    .peek(user -> log.info("Found user: {}", user.getName()))
                    .toList();
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
            throw e;
        }
    }

    /**
     * Update a user.
     */
    public UpdateUserResponse updateUser(String id, String name, String email) {
        log.info("Updating user with id: " + id);
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setId(id)
                .setName(name)
                .setEmail(email)
                .build();
        UpdateUserResponse response;
        try {
            response = blockingStub.updateUser(request);
            log.info("Updated user: " + id);
            return response;
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
            throw e;
        }
    }

    /**
     * Delete a user.
     */
    public DeleteUserResponse deleteUser(String id) {
        log.info("Deleting user with id: " + id);
        DeleteUserRequest request = DeleteUserRequest.newBuilder().setId(id).build();
        DeleteUserResponse response;
        try {
            response = blockingStub.deleteUser(request);
            log.info("Deleted user: " + id);
            return response;
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed {0}: {}", e.getStatus());
            throw e;
        }
    }

    // Helper method to convert User to CustomerDto
    private CustomerDto convertToCustomerDto(User user) {
        return CustomerDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                // Map other fields as needed
                .build();
    }
}
