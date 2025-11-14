package ut.edu.task_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ut.edu.task_management.dto.TaskRequest;
import ut.edu.task_management.dto.TaskResponse;
import ut.edu.task_management.model.Task;
import ut.edu.task_management.service.TaskService;
import ut.edu.task_management.util.MapperUtil;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private MapperUtil mapper;

    // Endpoint Get All Tasks (GET /api/tasks)
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(Principal principal) {
        String username = principal != null ? principal.getName() : null;
        List<Task> tasks = taskService.getAllTasksForUser(username);
        
        List<TaskResponse> responses = tasks.stream()
            .map(task -> mapper.map(task, TaskResponse.class))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }
    
    // Endpoint Get Task by ID (GET /api/tasks/{id})
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        Task task = taskService.getTaskByIdForUser(id, username);
        
        TaskResponse response = mapper.map(task, TaskResponse.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest req, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        Task created = taskService.createTask(req, username);
        TaskResponse resp = mapper.map(created, TaskResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest req, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        Task updated = taskService.updateTask(id, req, username);
        TaskResponse resp = mapper.map(updated, TaskResponse.class);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        taskService.deleteTask(id, username);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/sorted")
    public List<TaskResponse> getSortedTasks(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return taskService.getAllSortedTasks(sortBy, direction);
    }

}