package samt.smajilbasic.deduplicator.controller;

import  samt.smajilbasic.deduplicator.exception.Message;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/login")
public class LoginController {

    @RequestMapping("/")
    public @ResponseBody Message checkLogin(){
        return new Message(HttpStatus.OK, "User authenticated sucessfuly");
    }

}