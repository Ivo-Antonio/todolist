package br.com.ivoantonio.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ivoantonio.todolist.Utils.Utils;
import jakarta.servlet.http.HttpServletRequest;



@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel TaskModel, HttpServletRequest request){
        System.out.println("Chego no Controller ");
        var idUser = request.getAttribute("idUser");
        TaskModel.setIdUser((UUID)idUser);

        var currentDate = LocalDateTime.now();
        // 10/11/2023 - current
        // 10/10/2023 - start
        if(currentDate.isAfter(TaskModel.getStartAt()) || currentDate.isAfter(TaskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de inicio / data de término deve ser maior do que a data actual");
        }

        if(TaskModel.getStartAt().isAfter(TaskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de inicio deve ser menor do que a data de término");
        }


        var task = this.taskRepository.save(TaskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel>list(HttpServletRequest request){
    var idUser = request.getAttribute("idUser");
    var tasks = this.taskRepository.findByIdUser((UUID)idUser);
    return tasks;
  }

 @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request){
   
    var task =  this.taskRepository.findById(id).orElse(null);

    if (task == null) {
       return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Tarefa não encontrada"); 
    }

    var idUser = request.getAttribute("idUser");

    if(!task.getIdUser().equals(idUser)){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Usuario não tem permissão para alterar esta tarefa");   
    }

    Utils.copyNonNullProperties(taskModel,task);
    
    var taskUpdated = this.taskRepository.save(task);
    
    return ResponseEntity.ok().body(taskUpdated);
  }
  
}
