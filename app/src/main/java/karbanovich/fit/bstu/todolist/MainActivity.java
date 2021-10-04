package karbanovich.fit.bstu.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    //View
    Button selectDate;
    TextView selectedDate;
    Calendar calendar;
    FloatingActionButton addTask;
    ListView tasksList;
    FloatingActionButton xsltTemplate;

    //Data
    private static final int MAX_TASKS = 20;
    private static final int MAX_TASKS_DATE = 5;
    static ArrayList<Task> tasks;
    private ArrayList<Task> filteredTasks;
    private CustomListAdapter customListAdapter;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding();
        setListeners();
        setData();
    }

    private void binding() {
        selectDate = findViewById(R.id.btnSelectDate);
        selectedDate = findViewById(R.id.txtSelectDate);
        calendar = Calendar.getInstance();
        addTask = findViewById(R.id.btnAddTask);
        tasksList = findViewById(R.id.tasksList);
        xsltTemplate = findViewById(R.id.btnXsltTemplate);
    }

    private void setData() {
        Bundle arguments = getIntent().getExtras();
        if(arguments != null) {
            String selectedDate = arguments.get("selectedDate").toString();
            this.selectedDate.setText(selectedDate);

            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedDate.substring(0, 2)));
            calendar.set(Calendar.MONTH, Integer.parseInt(selectedDate.substring(3, 5)) - 1);
            calendar.set(Calendar.YEAR, Integer.parseInt(selectedDate.substring(6, 10)));
        } else
            selectedDate.setText(getGeneralDateFormat());

        filteredTasks = new ArrayList<>();
        tasks = XMLHelper.readXML(this);
        setFilteredTasks();
        customListAdapter = new CustomListAdapter(this, filteredTasks, addTask, false);
        tasksList.setAdapter(customListAdapter);
    }

    private void setFilteredTasks() {
        String selectedDate = getGeneralDateFormat();

        filteredTasks = (ArrayList<Task>) tasks.stream()
                .filter(t -> t.getDate().equals(selectedDate)).collect(Collectors.toList());

        if(filteredTasks.size() >= MAX_TASKS_DATE || tasks.size() >= MAX_TASKS)
            addTask.setEnabled(false);
        else
            addTask.setEnabled(true);
    }

    private void setListeners() {
        DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            selectedDate.setText(getGeneralDateFormat());

            setFilteredTasks();
            customListAdapter.updateTasksList(filteredTasks);
        };

        selectDate.setOnClickListener(view -> {
            new DatePickerDialog(MainActivity.this, d,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });

        addTask.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            intent.putExtra("selectedDate", getGeneralDateFormat());
            startActivity(intent);
        });

        xsltTemplate.setOnClickListener(view -> {
            XMLHelper.xslTransform(this);
        });
    }

    private String getGeneralDateFormat() {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String date;

        if(String.valueOf(day).length() == 1) {
            date = "0" + day + ".";
        } else date = day + ".";

        if(String.valueOf(month).length() == 1) {
            date += "0" + month + "." + year;
        } else date += month + "." + year;

        return date;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_xpath, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.xpath:
                Intent intent = new Intent(this, XPathActivity.class);
                intent.putExtra("selectedDate", getGeneralDateFormat());
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {return;}
}