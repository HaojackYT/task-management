package ut.edu.task_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ut.edu.task_management.model.Task;
import ut.edu.task_management.repository.TaskRepository;

import java.util.List;
import java.util.Optional;
import ut.edu.task_management.dto.TaskRequest;
import ut.edu.task_management.exception.ResourceNotFoundException;
import ut.edu.task_management.model.User;
import ut.edu.task_management.util.MapperUtil;
import org.springframework.security.access.AccessDeniedException;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MapperUtil mapper;

    public Task create(Task task) { return taskRepository.save(task); }
    public Optional<Task> findById(Long id) { return taskRepository.findById(id); }
    public List<Task> findByOwnerId(Long ownerId) { return taskRepository.findByOwnerId(ownerId); }

    //  Get All Tasks for current user
    public List<Task> getAllTasksForUser(String username) {
        User owner = userService.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return taskRepository.findByOwnerId(owner.getId());
    }

    // Get Task by ID for current user
    public Task getTaskByIdForUser(Long id, String username) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (task.getOwner() == null || !task.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("Not allowed to view this task");
        }
        return task;
    }

    // Create Task using TaskRequest and username of owner
    public Task createTask(TaskRequest req, String username) {
        User owner = userService.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Task t = mapper.map(req, Task.class);
        t.setOwner(owner);
        return taskRepository.save(t);
    }

    // Update task if owner matches username
    public Task updateTask(Long id, TaskRequest req, String username) {
        Task existing = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (existing.getOwner() == null || !existing.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("Not allowed to update this task");
        }
        // update fields if provided
        if (req.getTitle() != null) existing.setTitle(req.getTitle());
        if (req.getDescription() != null) existing.setDescription(req.getDescription());
        if (req.getCompleted() != null) existing.setCompleted(req.getCompleted());
        return taskRepository.save(existing);
    }

    // Delete task if owner matches username
    public void deleteTask(Long id, String username) {
        Task existing = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (existing.getOwner() == null || !existing.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("Not allowed to delete this task");
        }
        taskRepository.deleteById(id);
    }
}