package com.example.myrecipe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipeBook.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    private static final String TABLE_NAME = "recipes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_INGREDIENTS = "ingredients";
    private static final String COLUMN_INSTRUCTIONS = "instructions";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_IMAGE_PATH = "imagePath";
    private static final String COLUMN_IS_FAV = "isFavourite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_INGREDIENTS + " TEXT, " +
                COLUMN_INSTRUCTIONS + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_IMAGE_PATH + " TEXT, " +
                COLUMN_IS_FAV + " INTEGER)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // CRUD Operations

    // Add a new recipe
    public boolean addRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, recipe.getTitle());
        cv.put(COLUMN_INGREDIENTS, recipe.getIngredients());
        cv.put(COLUMN_INSTRUCTIONS, recipe.getInstructions());
        cv.put(COLUMN_CATEGORY, recipe.getCategory());
        cv.put(COLUMN_IMAGE_PATH, recipe.getImagePath());
        cv.put(COLUMN_IS_FAV, recipe.isFavourite() ? 1 : 0);

        long insert = db.insert(TABLE_NAME, null, cv);
        db.close();
        return insert != -1;
    }

    // Get all recipes
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String ingredients = cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS));
                String instructions = cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTIONS));
                String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                String imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
                boolean isFav = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FAV)) == 1;

                Recipe recipe = new Recipe(title, ingredients, instructions, category, imagePath);
                recipe.setId(id);
                recipe.setFavourite(isFav);
                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recipeList;
    }

    public Recipe getRecipeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[] { COLUMN_ID, COLUMN_TITLE, COLUMN_INGREDIENTS, COLUMN_INSTRUCTIONS, COLUMN_CATEGORY, COLUMN_IMAGE_PATH, COLUMN_IS_FAV },
                COLUMN_ID + " = ?",
                new String[] {String.valueOf(id)},
                null, null, null);

        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String ingredients = cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS));
            String instructions = cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTIONS));
            String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
            String imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
            boolean isFav = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FAV)) == 1;

            recipe = new Recipe(title, ingredients, instructions, category, imagePath);
            recipe.setFavourite(isFav);
            recipe.setId(id);
        }

        cursor.close();
        db.close();
        return recipe;
    }

    // Update a recipe
    public boolean updateRecipe(Recipe recipe, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, recipe.getTitle());
        cv.put(COLUMN_INGREDIENTS, recipe.getIngredients());
        cv.put(COLUMN_INSTRUCTIONS, recipe.getInstructions());
        cv.put(COLUMN_CATEGORY, recipe.getCategory());
        cv.put(COLUMN_IMAGE_PATH, recipe.getImagePath());

        int update = db.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
        db.close();
        return update > 0;
    }

    public boolean updateFav(int id, boolean isFav){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_IS_FAV, isFav ? 1 : 0);

        int update = db.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
        db.close();
        return update > 0;
    }

    // Delete a recipe
    public boolean deleteRecipe(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
        db.close();
        return delete > 0;
    }
}
