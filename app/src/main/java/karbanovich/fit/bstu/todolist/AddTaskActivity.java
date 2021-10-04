package karbanovich.fit.bstu.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class AddTaskActivity extends AppCompatActivity {

    //View
    ActionBar actionBar;
    EditText description;
    TextView date;
    Spinner category;
    Button save;

    //Data
    String taskDate;
    Task editableTask;
    String[] categories = { "Категория...", "Работа", "Учеба", "Дом", "Хобби", "Прочее"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        binding();
        setListeners();
        setData();
    }

    private void binding() {
        actionBar = getSupportActionBar();
        description = findViewById(R.id.description);
        date = findViewById(R.id.date);
        category = findViewById(R.id.category);
        save = findViewById(R.id.btnSave);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.category_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
    }

    private void setData() {
        actionBar.setDisplayHomeAsUpEnabled(true);
        editableTask = null;

        Intent intent = getIntent();
        taskDate = intent.getExtras().get("selectedDate").toString();
        this.date.setText("Дата: " + taskDate);

        editableTask = (Task) intent.getSerializableExtra("taskToEdit");
        if(editableTask != null) {
            description.setText(editableTask.getDescription());
            category.setSelection(Arrays.asList(categories).indexOf(editableTask.getCategory()));
            date.setText("Дата: " + editableTask.getDate());
        }
    }

    private void setListeners() {
        save.setOnClickListener(view -> {
            if(editableTask != null)
                XMLHelper.deleteTask(this, editableTask);

            String descrip = description.getText().toString();
            if(descrip.equals(""))
                descrip = " ";

            Task task = new Task(descrip, category.getSelectedItem().toString(), taskDate);
            ArrayList<Task> tasks = XMLHelper.readXML(this);

            tasks.add(task);
            XMLHelper.writeXML(this, tasks);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("selectedDate", taskDate);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("selectedDate", taskDate);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {return;}
}