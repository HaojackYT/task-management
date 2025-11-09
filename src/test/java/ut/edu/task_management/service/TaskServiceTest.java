package ut.edu.task_management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ut.edu.task_management.dto.TaskRequest;
import ut.edu.task_management.exception.ResourceNotFoundException;
import ut.edu.task_management.model.Task;
import ut.edu.task_management.model.User;
import ut.edu.task_management.repository.TaskRepository;
import ut.edu.task_management.util.MapperUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private MapperUtil mapper;

    @InjectMocks
    private TaskService taskService;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(10L);
        owner.setUsername("alice");
    }

    @Test
    void createTask_success() {
        TaskRequest req = new TaskRequest();
        req.setTitle("New Task");
        req.setDescription("desc");
        req.setCompleted(false);

        Task mapped = new Task();
        mapped.setTitle(req.getTitle());
        mapped.setDescription(req.getDescription());

        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle(req.getTitle());
        saved.setDescription(req.getDescription());
        saved.setOwner(owner);

        when(userService.findByUsername("alice")).thenReturn(Optional.of(owner));
        when(mapper.map(req, Task.class)).thenReturn(mapped);
        when(taskRepository.save(mapped)).thenAnswer(inv -> { Task t = mapped; t.setId(1L); t.setOwner(owner); return t; });

        Task result = taskService.createTask(req, "alice");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Task", result.getTitle());
        assertEquals(owner, result.getOwner());
        verify(taskRepository, times(1)).save(mapped);
    }

    @Test
    void updateTask_success() {
        Long id = 2L;
        Task existing = new Task();
        existing.setId(id);
        existing.setTitle("old");
        existing.setDescription("old desc");
        existing.setOwner(owner);

        TaskRequest req = new TaskRequest();
        req.setTitle("updated");
        req.setDescription(null);
        req.setCompleted(true);

        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task updated = taskService.updateTask(id, req, "alice");

        assertEquals("updated", updated.getTitle());
        assertEquals("old desc", updated.getDescription()); // unchanged because req.description is null
        assertTrue(updated.isCompleted());
        verify(taskRepository).save(existing);
    }

    @Test
    void updateTask_notOwner_throws() {
        Long id = 3L;
        User other = new User();
        other.setId(99L);
        other.setUsername("bob");

        Task existing = new Task();
        existing.setId(id);
        existing.setOwner(other);

        TaskRequest req = new TaskRequest();
        req.setTitle("x");

        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> taskService.updateTask(id, req, "alice"));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_notFound_throws() {
        when(taskRepository.findById(404L)).thenReturn(Optional.empty());
        TaskRequest req = new TaskRequest();
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(404L, req, "alice"));
    }

    @Test
    void deleteTask_success() {
        Long id = 5L;
        Task existing = new Task();
        existing.setId(id);
        existing.setOwner(owner);

        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));

        taskService.deleteTask(id, "alice");

        verify(taskRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteTask_notOwner_throws() {
        Long id = 6L;
        User other = new User();
        other.setUsername("eve");
        Task existing = new Task();
        existing.setId(id);
        existing.setOwner(other);

        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> taskService.deleteTask(id, "alice"));
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void deleteTask_notFound_throws() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(999L, "alice"));
    }
}
