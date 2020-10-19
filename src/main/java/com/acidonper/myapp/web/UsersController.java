package com.acidonper.myapp.web;

import com.acidonper.myapp.dtos.UserDto;
import com.acidonper.myapp.entities.User;
import com.acidonper.myapp.entities.repositories.UserRepository;
import com.acidonper.myapp.mappers.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UsersController {

    @RequestMapping("/home")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    private final UserRepository repository;
    UsersController(UserRepository repository) {
        this.repository = repository;
    }

    List<UserDto> usersDto  = new ArrayList<UserDto>();

    @GetMapping("/users")
    List<UserDto> run() {
        List<User> users = repository.findAll();
        for(User user : users) {
            UserDto userDto = UserMapper.INSTANCE.userToUserDTO(user);
            usersDto.add(userDto);
        }
        return usersDto;
    }

    @GetMapping("/users/{id}")
    UserDto run(@PathVariable String id) throws Exception {
        List<User> users = repository.findByDni(id);
        try{
            UserDto userDto = UserMapper.INSTANCE.userToUserDTO(users.get(0));
            return(userDto);
        }
        catch(Exception handlerException){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users")
    String run(@Valid @RequestBody UserDto newUser) {
        User user = UserMapper.INSTANCE.userDTOtoUser(newUser);

        // Obtain User with DNI in the database
        List<User> usersCreated = repository.findByDni(user.dni);

        // Save creation status
        String reqStatus = null;

        if(usersCreated.isEmpty()) {
            repository.save(user);
            reqStatus = "created";
        }
        else {
            reqStatus = "exists";
        }
        return "User " + user.firstName + " " + reqStatus;

    }
}