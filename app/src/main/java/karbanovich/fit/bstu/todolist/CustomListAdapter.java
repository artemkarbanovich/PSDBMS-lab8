package karbanovich.fit.bstu.todolist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {

    private ArrayList<Task> tasks;
    private Context context;
    private FloatingActionButton addTask;
    private boolean displayDate;

    public CustomListAdapter(Context context, ArrayList<Task> tasks, FloatingActionButton addTask, boolean displayDate) {
        this.context = context;
        this.tasks = tasks;
        this.addTask = addTask;
        this.displayDate = displayDate;
    }

    public CustomListAdapter(Context context, ArrayList<Task> tasks, boolean displayDate) {
        this.context = context;
        this.tasks = tasks;
        this.displayDate = displayDate;
    }

    public int getCount() {return tasks.size();}

    @Override
    public Object getItem(int i) {return null;}

    @Override
    public long getItemId(int i) {return 0;}

    public void updateTasksList(ArrayList<Task> filteredTasks) {
        tasks.clear();
        tasks.addAll(filteredTasks);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.task_item, null);

        TextView itemDescription = (TextView) view.findViewById(R.id.itemDescription);
        TextView itemCategory = (TextView) view.findViewById(R.id.itemCategory);
        ImageButton editItem = (ImageButton) view.findViewById(R.id.btnEditItem);
        ImageButton deleteItem = (ImageButton) view.findViewById(R.id.btnDeleteItem);
        TextView itemDate = (TextView) view.findViewById(R.id.itemDate);

        if(!displayDate) itemDate.setVisibility(View.GONE);
        else itemCategory.setVisibility(View.VISIBLE);

        editItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTaskActivity.class);
            intent.putExtra("selectedDate", tasks.get(position).getDate());
            intent.putExtra("taskToEdit", tasks.get(position));
            context.startActivity(intent);
        });

        deleteItem.setOnClickListener(v -> {
            XMLHelper.deleteTask(context, tasks.get(position));
            MainActivity.tasks = XMLHelper.readXML(context);

            tasks.remove(tasks.get(position));
            this.notifyDataSetChanged();

            if(!displayDate) addTask.setEnabled(true);
        });

        itemDescription.setText(tasks.get(position).getDescription());
        itemCategory.setText(tasks.get(position).getCategory());
        itemDate.setText("Дата: " + tasks.get(position).getDate());

        return view;
    }
}
