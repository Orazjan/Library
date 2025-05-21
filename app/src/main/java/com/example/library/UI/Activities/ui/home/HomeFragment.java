package com.example.library.UI.Activities.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.Model.Books;
import com.example.library.Model.CategoryModel;
import com.example.library.R;
import com.example.library.UI.Adapters.CategoryAdapter;
import com.example.library.UI.Adapters.ShowAllAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.BuildConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment"; // Добавьте TAG для логов

    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;
    List<CategoryModel> allCategoryModelList;
    private RecyclerView catRecycleView;
    private boolean isShowingAllCategories = false;

    ShowAllAdapter popularBooksAdapter;
    List<Books> popularBooksList;
    List<Books> allPopularBooks;
    private RecyclerView popularBooksRecycleView;
    private boolean isShowingAllPopularBooks = false;

    TextView category_see_all, popular_see_all;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        FirebaseApp.initializeApp(getContext());
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        if (BuildConfig.DEBUG) {
            firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance());
        }

        catRecycleView = root.findViewById(R.id.rec_category);
        popularBooksRecycleView = root.findViewById(R.id.popular_rec);
        category_see_all = root.findViewById(R.id.category_see_all);
        popular_see_all = root.findViewById(R.id.popular_see_all);

        db = FirebaseFirestore.getInstance();

        GridLayoutManager CategoryLayoutManager = new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false);
        catRecycleView.setLayoutManager(CategoryLayoutManager);

        categoryModelList = new ArrayList<>();
        allCategoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelList);
        catRecycleView.setAdapter(categoryAdapter);

        db.collection("Category_name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                allCategoryModelList.add(categoryModel);
                            }

                            Collections.shuffle(allCategoryModelList); // Раскомментируйте, если хотите случайный порядок
                            categoryModelList.clear();

                            int numberOfCategoriesToShow = Math.min(allCategoryModelList.size(), 4);
                            for (int i = 0; i < numberOfCategoriesToShow; i++) {
                                categoryModelList.add(allCategoryModelList.get(i));
                            }

                            if (allCategoryModelList.size() <= 4) {
                                category_see_all.setVisibility(View.GONE);
                            } else {
                                category_see_all.setVisibility(View.VISIBLE);
                                category_see_all.setText("Посмотреть все");
                            }

                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "Error getting category documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting category documents.", e);
                    }
                });

        category_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowingAllCategories) {
                    categoryModelList.clear();
                    int numberOfCategoriesToShow = Math.min(allCategoryModelList.size(), 4);
                    for (int i = 0; i < numberOfCategoriesToShow; i++) {
                        categoryModelList.add(allCategoryModelList.get(i));
                    }
                    category_see_all.setText("Посмотреть все");
                    isShowingAllCategories = false;
                } else {
                    categoryModelList.clear();
                    categoryModelList.addAll(allCategoryModelList);
                    category_see_all.setText("Скрыть");
                    isShowingAllCategories = true;
                }
                categoryAdapter.notifyDataSetChanged();
            }
        });

        GridLayoutManager PopularBookgridLayoutManager = new GridLayoutManager(getActivity(),
                2, RecyclerView.VERTICAL, false);
        popularBooksRecycleView.setLayoutManager(PopularBookgridLayoutManager);

        popularBooksList = new ArrayList<>();
        allPopularBooks = new ArrayList<>();
        popularBooksAdapter = new ShowAllAdapter(getContext(), popularBooksList);
        popularBooksRecycleView.setAdapter(popularBooksAdapter);

        db.collection("allBooks")
                .whereEqualTo("rate", 5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Books popularBooksModel = document.toObject(Books.class);
                                allPopularBooks.add(popularBooksModel);
                            }

                            Collections.shuffle(allPopularBooks);

                            popularBooksList.clear();

                            int numberOfBooksToShow = Math.min(allPopularBooks.size(), 4);
                            for (int i = 0; i < numberOfBooksToShow; i++) {
                                popularBooksList.add(allPopularBooks.get(i));
                            }

                            if (allPopularBooks.size() <= 4) {
                                popular_see_all.setVisibility(View.GONE);
                            } else {
                                popular_see_all.setVisibility(View.VISIBLE);
                                popular_see_all.setText("Посмотреть все");
                            }

                            popularBooksAdapter.notifyDataSetChanged();

                        } else {
                            Log.e(TAG, "Error getting popular books documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting popular books documents.", e);
                    }
                });

        popular_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowingAllPopularBooks) {
                    popularBooksList.clear();
                    int numberOfBooksToShow = Math.min(allPopularBooks.size(), 4);
                    for (int i = 0; i < numberOfBooksToShow; i++) {
                        popularBooksList.add(allPopularBooks.get(i));
                    }
                    popular_see_all.setText("Посмотреть все");
                    isShowingAllPopularBooks = false;
                } else {
                    popularBooksList.clear();
                    popularBooksList.addAll(allPopularBooks);
                    popular_see_all.setText("Скрыть");
                    isShowingAllPopularBooks = true;
                }
                popularBooksAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }
}
