package com.example.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private GridLayout categoryGrid;
    private ProgressBar progressBar;
    private TextView progressPercentage;
    private List<Category> categories = new ArrayList<>();
    private RecyclerView recentActivityRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> recentTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up GridLayout
        categoryGrid = findViewById(R.id.category_grid);
        setupCategoryGrid();

        // Set up ProgressBar
        progressBar = findViewById(R.id.progress_bar);
        progressPercentage = findViewById(R.id.progress_percentage);
        updateProgress(30); // Example: Set initial progress to 30%

        // Set up FAB (Floating Action Button)
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCategoryDialog();
            }
        });

        // Set up the Recent Activity RecyclerView
        recentActivityRecyclerView = findViewById(R.id.recent_activity_list);
        recentActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(recentTasks);
        recentActivityRecyclerView.setAdapter(taskAdapter);

        // Initialize with some example tasks
        addCompletedTask("Initial Task");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // Handle settings click
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to show a dialog for adding a new category
    private void showAddCategoryDialog() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View dialogView = inflater.inflate(R.layout.activity_add_category, null);

        final EditText etCategoryName = dialogView.findViewById(R.id.et_category_name);
        final EditText etTaskCount = dialogView.findViewById(R.id.et_task_count);
        final ImageView ivSelectedIcon = dialogView.findViewById(R.id.iv_selected_icon);
        Button btnSelectIcon = dialogView.findViewById(R.id.btn_select_icon);

        // List of icons to choose from
        final int[] iconIds = {
                R.drawable.ic_work, R.drawable.ic_personal, R.drawable.ic_fitness,
                R.drawable.ic_widgets, R.drawable.ic_chat
        };

        final String[] iconNames = {"Work", "Personal", "Fitness", "Learning", "Interests"};
        final int[] selectedIcon = {iconIds[0]}; // Default to the first icon

        btnSelectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog with icon choices
                AlertDialog.Builder iconDialog = new AlertDialog.Builder(MainActivity.this);
                iconDialog.setTitle("Select Icon");
                iconDialog.setItems(iconNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update selected icon
                        selectedIcon[0] = iconIds[which];
                        ivSelectedIcon.setImageResource(iconIds[which]);
                    }
                });
                iconDialog.show();
            }
        });

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Add New Category");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String categoryName = etCategoryName.getText().toString();
                String taskCountStr = etTaskCount.getText().toString();

                if (!categoryName.isEmpty() && !taskCountStr.isEmpty()) {
                    int taskCount = Integer.parseInt(taskCountStr);

                    // Create the new category with the selected icon
                    Category newCategory = new Category(categoryName, selectedIcon[0], taskCount);
                    categories.add(newCategory);

                    // Refresh the category grid
                    setupCategoryGrid();

                    // Add a completed task to the recent activity
                    addCompletedTask(categoryName + " Task Completed");
                } else {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    // Method to set up the category grid
    private void setupCategoryGrid() {
        categoryGrid.removeAllViews(); // Clear existing views

        for (Category category : categories) {
            View categoryView = getLayoutInflater().inflate(R.layout.item_category, categoryGrid, false);

            ((ImageView) categoryView.findViewById(R.id.category_icon)).setImageResource(category.getIconResId());
            ((TextView) categoryView.findViewById(R.id.category_name)).setText(category.getName());
            ((TextView) categoryView.findViewById(R.id.task_count)).setText(String.valueOf(category.getTaskCount()));

            categoryGrid.addView(categoryView);
        }
    }

    // Method to update the progress bar
    private void updateProgress(int progress) {
        progressBar.setProgress(progress);
        progressPercentage.setText(progress + "%");
    }

    // Method to add a completed task
    private void addCompletedTask(String taskName) {
        String completionTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        Task newTask = new Task(taskName, completionTime);

        // Add the task to the list
        recentTasks.add(newTask);

        // Sort tasks by the most recent
        Collections.sort(recentTasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t2.getCompletionTime().compareTo(t1.getCompletionTime());
            }
        });

        // Update the RecyclerView
        taskAdapter.updateTasks(recentTasks);
    }
}
