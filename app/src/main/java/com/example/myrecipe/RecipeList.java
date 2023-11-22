package com.example.myrecipe;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class RecipeList extends AppCompatActivity implements RecipeAdapter.OnItemClickListener{
    private RecyclerView recipesRecycler;
    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private Toolbar toolbar;
    private TextView noRecipes;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list);

        // initialize views
        recipesRecycler = findViewById(R.id.recipeRecycler);
        noRecipes = findViewById(R.id.noRecipes);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Get data
        db = new DatabaseHelper(this);
        List<Recipe> recipeList = db.getAllRecipes();

        // fill recycler view
        adapter = new RecipeAdapter(recipeList, this, this);
        recipesRecycler.setAdapter(adapter);
        recipesRecycler.setLayoutManager(new LinearLayoutManager(this));

        if (recipeList.size() == 0)
            noRecipes.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(Recipe recipe) {
        Intent intent = new Intent(RecipeList.this, ViewRecipe.class);
        intent.putExtra("RecipeID", recipe.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Recipe recipe) {
        new MaterialAlertDialogBuilder(RecipeList.this)
                .setTitle("Edit recipe")
                .setMessage("Do you want to edit the recipe: " + recipe.getTitle())
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(RecipeList.this, AddRecipe.class);
                        intent.putExtra("RecipeID", recipe.getId());
                        startActivity(intent);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    @Override
    public void onFavClick(Recipe recipe) {

        new MaterialAlertDialogBuilder(RecipeList.this)
                .setTitle("Edit Recipe")
                .setMessage("Do you want to mark " + (!recipe.isFavourite() ? "as favourite recipe? " : " not favourite?"))
                .setNeutralButton(recipe.isFavourite() ? "Mark Not Favourite" : "Mark As Favourite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean updateFav = db.updateFav(recipe.getId(), !recipe.isFavourite());
                        if (updateFav){
                            // update recipes
                            adapter.setRecipeList(db.getAllRecipes());
                        }
                    }
                }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(RecipeList.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
