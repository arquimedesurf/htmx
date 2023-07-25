package com.example.htmx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.stream.Stream;

@SpringBootApplication
public class HtmxApplication {

	public static void main(String[] args) {
		SpringApplication.run(HtmxApplication.class, args);
	}

}

@Component
class Initializer{
	private final TodoRepository repository;

	public Initializer(TodoRepository repository) {
		this.repository = repository;
	}

	@EventListener(ApplicationReadyEvent.class)
	void reset(){
		this.repository.deleteAll();
		Stream.of("Learn HTMX",
					"Learn Spring ViewComponent",
					"Learn Hotwire",
					"Make some coffee")
				.forEach(t -> this.repository.save(new Todo(null, t)));
	}
}

@RequestMapping("/todos")
@Controller
class TodoController{
	private final TodoRepository repository;

	TodoController(TodoRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	String todos(Model model){
		model.addAttribute("todos", this.repository.findAll());
		return "todos";
	}

	@ResponseBody
	@DeleteMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
	String delete(@PathVariable Integer id){
		System.out.println("going to delete Todo # " + id);
		repository.deleteById(id);
		return "";
	}
}

interface TodoRepository extends CrudRepository<Todo, Integer>{}

record Todo (@Id Integer id, String title){}