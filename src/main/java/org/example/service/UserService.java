package org.example.service;

import jakarta.transaction.Transactional;
import org.example.model.User;
import org.example.dto.UserDTO;
import org.example.repository.UserRepository;
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
    public UserDTO createUser(UserDTO userDTO) {
        if(userRepository.existsByEmail(userDTO.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ошибка при попытке создания пользователя: введенный вами email " + userDTO.getEmail() + " уже занят!");
        }
        User user = modelMapper.map(userDTO, User.class);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Transactional
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при попытке получения данных: пользователя с ID " + id + " не существует!"));
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при попытке обновления данных: пользователя с ID " + id + " не существует!"));
        if(userRepository.existsByEmail(userDTO.getEmail()) && !(currentUser.getEmail().equals(userDTO.getEmail()))){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ошибка при попытке обновления пользователя: введенный вами email " + userDTO.getEmail() + " уже занят!");
        }
        modelMapper.map(userDTO, currentUser);
        currentUser.setId(id);
        return modelMapper.map(currentUser, UserDTO.class);
    }

    @Transactional
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при попытке удаления: пользователя с ID " + id + " не существует!");
        }
        userRepository.deleteById(id);
    }

}
