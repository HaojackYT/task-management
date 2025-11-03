package ut.edu.task_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ut.edu.task_management.model.Task;
import ut.edu.task_management.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task create(Task task) { return taskRepository.save(task); }
    public Optional<Task> findById(Long id) { return taskRepository.findById(id); }
    public List<Task> findByOwnerId(Long ownerId) { return taskRepository.findByOwnerId(ownerId); }
}
