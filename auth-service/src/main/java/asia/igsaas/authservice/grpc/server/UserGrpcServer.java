package asia.igsaas.authservice.grpc.server;

import asia.igsaas.grpc.user.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.*;

@GrpcService
public class UserGrpcServer extends UserServiceGrpc.UserServiceImplBase {

    // In-memory store for demonstration
    private final Map<String, User> userStore = new HashMap<>();

    public UserGrpcServer() {
        userStore.put("1", User.newBuilder().setId("1").setName("John").setEmail("john@doe.com").build());
        userStore.put("2", User.newBuilder().setId("2").setName("Jane").setEmail("jane@doe.com").build());
        userStore.put("3", User.newBuilder().setId("3").setName("Bob").setEmail("bob@doe.com").build());
    }

    @Override
    public void createUser(CreateUserRequest request,
                           StreamObserver<CreateUserResponse> responseObserver) {
        try {
            String id = UUID.randomUUID().toString();
            User user = User.newBuilder()
                    .setId(id)
                    .setName(request.getName())
                    .setEmail(request.getEmail())
                    .build();
            System.out.println(user);
            userStore.put(id, user);

            responseObserver.onNext(CreateUserResponse.newBuilder()
                    .setId(id)
                    .setMessage("User created successfully")
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error creating user: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUser(GetUserRequest request,
                        StreamObserver<UserResponse> responseObserver) {
        try {
            User user = userStore.get(request.getId());
            if (user == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found")
                        .asRuntimeException());
                return;
            }

            responseObserver.onNext(UserResponse.newBuilder()
                    .setUser(user)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error fetching user: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getAllUsers(GetAllUsersRequest request,
                            StreamObserver<GetAllUsersResponse> responseObserver) {
        try {
            List<User> users = new ArrayList<>(userStore.values());

            responseObserver.onNext(GetAllUsersResponse.newBuilder()
                    .addAllUsers(users)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error fetching users: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateUser(UpdateUserRequest request,
                           StreamObserver<UpdateUserResponse> responseObserver) {
        try {
            if (!userStore.containsKey(request.getId())) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found")
                        .asRuntimeException());
                return;
            }

            User updatedUser = User.newBuilder()
                    .setId(request.getId())
                    .setName(request.getName())
                    .setEmail(request.getEmail())
                    .build();

            userStore.put(request.getId(), updatedUser);

            responseObserver.onNext(UpdateUserResponse.newBuilder()
                    .setMessage("User updated successfully")
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error updating user: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteUser(DeleteUserRequest request,
                           StreamObserver<DeleteUserResponse> responseObserver) {
        try {
            if (!userStore.containsKey(request.getId())) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found")
                        .asRuntimeException());
                return;
            }

            userStore.remove(request.getId());

            responseObserver.onNext(DeleteUserResponse.newBuilder()
                    .setMessage("User deleted successfully")
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error deleting user: " + e.getMessage())
                    .asRuntimeException());
        }
    }

}
