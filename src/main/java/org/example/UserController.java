package org.example;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void createUser(@RequestBody UserDTO userDTO) {
        userService.createUser(userDTO);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") Long id, @RequestBody UserDTO userDTO) {
        userService.updateUser(id, userDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }
}
