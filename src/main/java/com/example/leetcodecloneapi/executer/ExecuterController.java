package com.example.leetcodecloneapi.executer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/exec",produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin("*")
public class ExecuterController {
    @Autowired
    ExecuterService runner;

    @PostMapping
    private HashMap<String, String> runCode(@RequestBody CodeEntity codeEntity){
        HashMap<String, String> response = new HashMap<>();
        String result = runner.runCode(codeEntity);
        response.put("result",result);
        return response;
    }
}
