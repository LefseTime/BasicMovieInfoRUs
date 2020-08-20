package com.e.basicmovieinforus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //UI elements
    ImageButton btnSearch;
    AutoCompleteTextView etSearch;

    //DB of previous search terms
    SearchDB searchDB;
    ArrayList<String> data = new ArrayList<String>();
    ArrayAdapter<String> suggestionAdapter;

    //Current search
    String searchTitle;
    String searchResults;
    JSONObject resultsObj;

    ArrayList<Movie> loadedMovies = new ArrayList<Movie>();
    Integer totalPages;
    Integer currentPage;

    RecyclerView recyclerView;
    RecyclerView.Adapter resultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchDB = new SearchDB(this);
        searchDB.open();
        data = searchDB.getData();
        searchDB.close();


        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        suggestionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        etSearch.setAdapter(suggestionAdapter);

        recyclerView = findViewById(R.id.rvList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        initAdapter();

        etSearch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                etSearch.showDropDown();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                currentPage = 0;
                searchResults = "";
                resultsObj = null;
                loadedMovies.clear();
                searchTitle = etSearch.getText().toString().toLowerCase().trim();
                hideKeyboard(MainActivity.this);

                new SearchForMovie().execute();
            }
        });

    }

    public class SearchForMovie extends AsyncTask<Integer, Integer, String> {

        public SearchForMovie() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... integers) {

            try {
                currentPage++;
                URL url = new URL("http://api.themoviedb.org/3/search/movie?api_key=2696829a81b1b5827d515ff121700838&query="
                        + searchTitle + "&page=" + currentPage);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String result = stringBuilder.toString();

                    resultsObj = new JSONObject(result);

                    totalPages = resultsObj.getInt("total_pages");

                    if(resultsObj.getInt("total_results") == 0 || resultsObj == null){
                        return null;
                    } else{
                        Boolean exists = false;
                        for(Integer i = 0; i < data.size(); i++){
                            if (data.get(i).toString().equals(searchTitle)){
                                exists = true;
                                break;
                            }
                        }
                        if (!exists){
                            searchDB.open();
                            searchDB.createEntry(searchTitle);
                            data = searchDB.getData();
                            searchDB.close();

                        }
                        return result;
                    }
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("Sorry, your search doesn't make sense.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Try Again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface popupDialog, int id) {
                                popupDialog.cancel();
                                etSearch.setText("");
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {
                searchResults = s;
                populateData();
            }
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void populateData() {
        try {
            resultsObj = new JSONObject(searchResults);
            String resultsStr = null;
            resultsStr = resultsObj.getString("results");

            Movie currentMovie;
            JSONArray resultsArr = null;

            resultsArr = new JSONArray(resultsStr);
//            loadedMovies.clear();


            for(int i=0; i<resultsArr.length(); i++){
                JSONObject currentObj = null;

                currentObj = resultsArr.getJSONObject(i);

                currentMovie = new Movie(currentObj.getString("poster_path"),currentObj.getString("title"),currentObj.getString("release_date"),currentObj.getString("overview"));
                loadedMovies.add(currentMovie);

            }

            if (currentPage == 1){
                recyclerView.getAdapter().notifyDataSetChanged();
            } else {
                recyclerView.getAdapter().notifyItemInserted(loadedMovies.size());
            }
//            recyclerView.getAdapter().notifyItemInserted(loadedMovies.size());
//            recyclerView.getAdapter().notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initAdapter() {
        resultsAdapter = new MovieAdapter(loadedMovies);
        recyclerView.setAdapter(resultsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if (totalPages > currentPage + 1){
                        new SearchForMovie().execute();
                    }
                }
            }
        });

        suggestionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        etSearch.setAdapter(suggestionAdapter);

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}