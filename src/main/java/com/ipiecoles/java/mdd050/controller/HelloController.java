package com.ipiecoles.java.mdd050.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// Controller d'exemple permettant de créer du contenu dans des chemins spécifiques
@RestController
@RequestMapping(value = "/sayHello")
public class HelloController {

    // http://localhost:5367/sayHello
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "text/plain")
    public String hello(){
        return "Hello World!";
    }

    // http://localhost:5367/sayHello/html
    @RequestMapping(value = "/html", method = RequestMethod.GET, produces = "text/html")
    public String helloHtml(){
        return "<h1>Hello World!</h1> <p>How are you ?</p>";
    }

}
