package com.example.myrecipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.carousel.CarouselLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements FavRecipeAdapter.OnItemClickListener {

    private Button addRecipe;
    private Button viewRecipes;
    private ConstraintLayout layout, favLayout;
    private DatabaseHelper db;
    private List<Recipe> recipeArrayList = new ArrayList<>();
    private List<Recipe> favRecipes = new ArrayList<>();
    private TextView noRecipes;
    private TextView noRecipesFav;
    private RecyclerView favRecipeRecycler;
    private FavRecipeAdapter adapter;

    private TextView textViewTitle;
    private TextView textViewCategory;
    private Button edit;
    private Button viewRecipe;
    private ImageView recipeImage;
    private ImageView fav;
    private final int REQUEST_READ_STORAGE_PERMISSION = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize db
        db = new DatabaseHelper(this);
        recipeArrayList = db.getAllRecipes();
        for (Recipe recipe: recipeArrayList){
            if (recipe.isFavourite())
                favRecipes.add(recipe);
        }

        // initialize views
        addRecipe = findViewById(R.id.addRecipeButton);
        viewRecipes = findViewById(R.id.recipeListButton);
        favLayout = findViewById(R.id.favouritesConstraint);
        layout = findViewById(R.id.rdmConstraint);
        noRecipesFav = findViewById(R.id.noRecipesFav);
        noRecipes = findViewById(R.id.noRecipes);
        textViewTitle = findViewById(R.id.title);
        textViewCategory = findViewById(R.id.category);
        recipeImage = findViewById(R.id.recipeImage);
        edit = findViewById(R.id.edit);
        viewRecipe = findViewById(R.id.view);
        fav = findViewById(R.id.fav);

        favRecipeRecycler = findViewById(R.id.favRecipeRecycler);
        adapter = new FavRecipeAdapter(favRecipes, MainActivity.this, this);

        // setup recycler
        favRecipeRecycler.setAdapter(adapter);
        favRecipeRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (recipeArrayList.size() == 0){
            layout.setVisibility(View.INVISIBLE);
            noRecipesFav.setVisibility(View.VISIBLE);
            noRecipes.setVisibility(View.VISIBLE);
        } else if (favRecipes.size() == 0){
            for (int i = 0; i < favLayout.getChildCount(); i++) {
                View child = favLayout.getChildAt(i);
                favLayout.removeView(child);
            }
            favLayout.setVisibility(View.INVISIBLE);
        }

        if (recipeArrayList.size() > 0){
            Random random = new Random();
            int randomNumber = random.nextInt(recipeArrayList.size());
            Recipe recipe = recipeArrayList.get(randomNumber);
            textViewTitle.setText(recipe.getTitle());
            textViewCategory.setText(recipe.getCategory());
            String imagePath = recipe.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                Glide.with(this)
                        .load("file://" + imagePath)
                        .into(recipeImage);
            }

            // Apply the color filter
            String hexColorCode = recipe.isFavourite() ? "#FFD700" : "#808080";
            int color = Color.parseColor(hexColorCode);
            Drawable drawable = fav.getDrawable();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

            fav.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);

            viewRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(recipe);
                }
            });
        }

        // handle events
        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddRecipe.class);
                startActivity(intent);
            }
        });

        viewRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipeList.class);
                startActivity(intent);
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission granted; you can access media files here
        } else {
            // Permission not granted; request it from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE_PERMISSION);
        }


    }

    @Override
    public void onItemClick(Recipe recipe) {
        Intent intent = new Intent(MainActivity.this, ViewRecipe.class);
        intent.putExtra("RecipeID", recipe.getId());
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted; you can access media files here
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied; handle accordingly (e.g., show a message to the user)
                Toast.makeText(this, "Allow access to view images", Toast.LENGTH_SHORT).show();
            }
        }

    }
}