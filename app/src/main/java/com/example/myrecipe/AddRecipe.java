package com.example.myrecipe;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AddRecipe extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageButton addImage;
    private TextView imagePath;
    private Toolbar toolbar;
    private EditText editTextTitle;
    private EditText editTextIngredients;
    private EditText editTextInstructions;
    private EditText editTextCategory;
    private Button buttonSave;
    private String imageUri;
    private Boolean isUpdate = false;
    private Recipe recipe;
    private DatabaseHelper db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);

        // get intents
        int recipeID = getIntent().getIntExtra("RecipeID", -1);

        // initialize views
        addImage = findViewById(R.id.imageButton);
        imagePath = findViewById(R.id.imagePath);
        toolbar = findViewById(R.id.toolbar);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextIngredients = findViewById(R.id.editTextIngredients);
        editTextInstructions = findViewById(R.id.editTextInstructions);
        editTextCategory = findViewById(R.id.editTextCategory);
        buttonSave = findViewById(R.id.buttonSave);

        // initialize db
        db = new DatabaseHelper(this);

        // setup page for update
        if (recipeID != -1){
            isUpdate = true;
            recipe = db.getRecipeById(recipeID);
            editTextTitle.setText(recipe.getTitle());
            editTextCategory.setText(recipe.getCategory());
            editTextIngredients.setText(recipe.getIngredients());
            editTextInstructions.setText(recipe.getInstructions());
            String path = recipe.getImagePath();
            imagePath.setText(path.substring(path.lastIndexOf("/") + 1));
            imageUri = path;
        }

        // setup toolbar
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // handle events
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRecipe();
            }
        });
    }

    private void submitRecipe() {
        String title = editTextTitle.getText().toString();
        String ingredients = editTextIngredients.getText().toString();
        String instructions = editTextInstructions.getText().toString();
        String category = editTextCategory.getText().toString();
        String path = imagePath.getText().toString();

        boolean isValid = true;

        if (title.isEmpty()) {
            editTextTitle.setError("Title is required");
            isValid = false;
        }
        if (ingredients.isEmpty()) {
            editTextIngredients.setError("Ingredients are required");
            isValid = false;
        }
        if (instructions.isEmpty()) {
            editTextInstructions.setError("Instructions are required");
            isValid = false;
        }
        if (category.isEmpty()) {
            editTextCategory.setError("Category is required");
            isValid = false;
        }

        if (isValid) {
            // Add your logic to save the recipe data
            if (path.equals("Add Image")) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_LONG).show();
            } else {
                if (isUpdate){
                    boolean res = db.updateRecipe(new Recipe(title, ingredients, instructions, category, imageUri), recipe.getId());

                    if (res) {
                        Toast.makeText(this, "Recipe updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddRecipe.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    boolean res = db.addRecipe(new Recipe(title,ingredients,instructions,category,imageUri));
                    if (res) {
                        Toast.makeText(this, "Recipe added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddRecipe.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Get the file path from the image URI
            String path = getRealPathFromURI(selectedImageUri);

            // Get the image name from the file path
            String imageName = path.substring(path.lastIndexOf("/") + 1);

            imagePath.setText(imageName);

            imageUri = path;

            // Show the image added toast
            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to get the real file path from a URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    // handle back press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
