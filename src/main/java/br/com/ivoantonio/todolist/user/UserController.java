package br.com.ivoantonio.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

// Modificadores de acesso
// public
// private
// protected


@RestController
@RequestMapping("/users")
public class UserController{

    @Autowired
    private IUserRepository userRepository;

    // Tipos de retorno
    // String
    // Integer
    // Double
    // float
    // char
    // date
    // void

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel UserModel){
        var user = this.userRepository.findByUsername(UserModel.getUsername());

        if(user != null){
            // Mensagem de erro
            // Status Code
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário Já existe");
        }

        var passwordhashred = BCrypt.withDefaults().hashToString(12, UserModel.getPassword().toCharArray());

        UserModel.setPassword(passwordhashred);

        var userCreated = this.userRepository.save(UserModel);
        return ResponseEntity.status(HttpStatus.OK).body(userCreated); 
    }

}