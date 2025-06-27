package taylor.project.projecttracker.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class TaskMetricsService {

    private final Counter tasksProcessedCounter;

    public TaskMetricsService(MeterRegistry meterRegistry) {
        this.tasksProcessedCounter = Counter.builder("tasks.processed.total")
                .description("Total number of tasks processed")
                .register(meterRegistry);
    }

    public void processTask() {
        // Your task processing logic here
        tasksProcessedCounter.increment(); // Increment the counter
        System.out.println("Task processed!");
    }
}