package karbanovich.fit.bstu.todolist;

import java.io.Serializable;

public class Task implements Serializable {

    private String description;
    private String category;
    private String date;

    public Task(String description, String category, String date) {
        this.description = description;
        this.category = category;
        this.date = date;
    }

    public Task() { }

    public String getDescription() {return description;}
    public String getCategory() {return category;}
    public String getDate() {return date;}

    public void setDescription(String description) {this.description = description;}
    public void setCategory(String category) {this.category = category;}
    public void setDate(String date) {this.date = date;}

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Task task = (Task) o;
        return this.description.equals(task.description) &&
                this.category.equals(task.category) &&
                this.date.equals(task.date);
    }
}
