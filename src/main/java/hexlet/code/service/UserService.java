package hexlet.code.service;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.dto.users.UserDTO;
import hexlet.code.dto.users.UserUpdateDTO;
import hexlet.code.exception.AccessDeniedException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUtils userUtils;

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::mapShow)
                .toList();
    }

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.mapShow(user);
    }

    public UserDTO create(UserCreateDTO userData) {
        var user = userMapper.mapCreate(userData);
        userRepository.save(user);
        return userMapper.mapShow(user);
    }

    public UserDTO update(UserUpdateDTO userData, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        var currentUser = userUtils.getCurrentUser();
        if (currentUser == null || !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        } else {
            userMapper.mapUpdate(userData, user);
            userRepository.save(user);
            return userMapper.mapShow(user);
        }
    }

    public void delete(Long id) {
        var currentUser = userUtils.getCurrentUser();
        if (currentUser == null || !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        } else {
            userRepository.deleteById(id);
        }
    }
}
