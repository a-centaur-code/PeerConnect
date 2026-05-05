package com.example.project.peerconnect.controller;

import com.example.project.peerconnect.model.Request;
import com.example.project.peerconnect.repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@CrossOrigin
public class RequestController {

    @Autowired
    RequestRepo repo;

    @PostMapping
    public Request send(@RequestBody Request r){
        r.setStatus("PENDING");
        return repo.save(r);
    }

    @GetMapping
    public List<Request> all(){
        return repo.findAll();
    }

    @PutMapping("/{id}/approve")
    public Request approve(@PathVariable int id){
        Request r=repo.findById(id).get();
        r.setStatus("ACCEPTED");
        return repo.save(r);
    }
}
