package org.example;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void createUser(UserDTO userDTO) {
        if(userRepository.existsByEmail(userDTO.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ошибка при попытке создания пользователя: введенный вами email " + userDTO.getEmail() + " уже занят!");
        }
        User user = modelMapper.map(userDTO, User.class);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при попытке получения данных: пользователя с ID " + id + " не существует!"));
    }

    @Transactional
    public void updateUser(Long id, UserDTO userDTO) {
        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при попытке обновления данных: пользователя с ID " + id + " не существует!"));
        modelMapper.map(userDTO, currentUser);
        currentUser.setId(id);
    }

    @Transactional
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при попытке удаления: пользователя с ID " + id + " не существует!");
        }
        userRepository.deleteById(id);
    }

}
