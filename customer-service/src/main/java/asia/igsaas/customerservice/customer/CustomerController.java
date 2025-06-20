package asia.igsaas.customerservice.customer;

import asia.igsaas.customerservice.customer.dto.CustomerDto;
import asia.igsaas.customerservice.grpc.client.UserClientService;
import asia.igsaas.grpc.user.CreateUserRequest;
import asia.igsaas.grpc.user.CreateUserResponse;
import asia.igsaas.grpc.user.User;
import asia.igsaas.grpc.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final UserClientService userClientService;


    @GetMapping()
    public List<CustomerDto> getAllCustomers() {
        return userClientService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getCustomer(@PathVariable String id) {
        return userClientService.getUser(id);
    }

    @PostMapping()
    public CreateUserResponse createCustomer(@RequestBody CreateUserRequest request) {
        return userClientService.createUser(request);
    }
//
//    @PutMapping("/{id}")
//    public CustomerEntity updateCustomer(@PathVariable String id, @RequestBody CustomerEntity customer) {
//        return customerService.updateCustomer(id, customer);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteCustomer(@PathVariable String id) {
//        customerService.deleteCustomer(id);
//    }

}
