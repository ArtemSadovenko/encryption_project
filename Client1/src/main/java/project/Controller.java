package project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cl1/")
public class Controller {
    @Autowired
    private Service service;

    @PostMapping("/test")
    public String test(@RequestBody String msg) {
        String message = service.encrypt(msg);
        return message +
                "\n\n\n" +
                service.decrypt(message);
    }

    @GetMapping("/all")
    public List<String> getAll(){
        return service.getAll();
    }

    @PostMapping("/send")
    public String send(@RequestBody String msg) {
        String host = "http://localhost:8080/api/cl2/get";
        return service.send(msg, host);
    }

    @PostMapping("/get")
    public String get(@RequestBody String msg){
        return service.get(msg);
    }
}
