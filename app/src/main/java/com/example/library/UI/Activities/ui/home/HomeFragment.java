package com.example.library.UI.Activities.ui.home;

import static android.content.ContentValues.TAG;

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
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;
    private RecyclerView catRecycleView;

    ShowAllAdapter popularBooksAdapter;
    List<Books> popularBooksList;
    private RecyclerView popularBooksRecycleView;

    TextView category_see_all, popular_see_all;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        FirebaseApp.initializeApp(getContext());
        FirebaseAppCheck firebaseAppCheck;
        firebaseAppCheck = FirebaseAppCheck.getInstance();
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

        category_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_see_all.setVisibility(View.GONE);
                categoryModelList = new ArrayList<>();
                categoryAdapter = new CategoryAdapter(getContext(), categoryModelList);
                catRecycleView.setAdapter(categoryAdapter);
                db.collection("Category_name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModelList.add(categoryModel);
                                categoryAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting documents.", e);
                    }
                });
            }
        });

        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelList);
        catRecycleView.setAdapter(categoryAdapter);
        db.collection("Category_name").limit(4).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CategoryModel categoryModel = document.toObject(CategoryModel.class);
                        categoryModelList.add(categoryModel);
                        categoryAdapter.notifyDataSetChanged();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting documents.", e);
            }
        });
        GridLayoutManager PopularBookgridLayoutManager = new GridLayoutManager(getActivity(),
                2, RecyclerView.VERTICAL, false);
        popularBooksRecycleView.setLayoutManager(PopularBookgridLayoutManager);

// Убедитесь, что эти переменные объявлены на уровне класса/Fragment:
// private List<Books> popularBooksList;
// private ShowAllAdapter popularBooksAdapter;
// private RecyclerView popularBooksRecycleView;
// private Button popular_see_all; // Или TextView, в зависимости от того, что у вас popular_see_all

        popular_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popular_see_all.setVisibility(View.GONE); // Скрываем кнопку "Показать все"

                // Очищаем существующий список и создаем новый адаптер, если необходимо.
                // Обычно достаточно просто очистить список и вызвать notifyDataSetChanged.
                // Если popularBooksList и popularBooksAdapter уже инициализированы глобально,
                // то следующие 2 строки можно опустить.
                // popularBooksList = new ArrayList<>();
                // popularBooksAdapter = new ShowAllAdapter(getContext(), popularBooksList);
                // popularBooksRecycleView.setAdapter(popularBooksAdapter);

                db.collection("allBooks")
                        .whereEqualTo("rate", 5) // Получаем ВСЕ книги с рейтингом 5
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Очищаем список перед добавлением новых элементов,
                                    // чтобы избежать дублирования, если список уже содержит книги.
                                    popularBooksList.clear();

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Books popularBooksModel = document.toObject(Books.class);
                                        popularBooksList.add(popularBooksModel);
                                    }

                                    // Шаг 1: Перемешиваем весь список полученных книг
                                    // Теперь при "Показать все" книги тоже будут в случайном порядке.
                                    Collections.shuffle(popularBooksList);

                                    // Шаг 2: Уведомляем адаптер об изменениях
                                    popularBooksAdapter.notifyDataSetChanged();

                                } else {
                                    Log.e(TAG, "Error getting all documents for 'See All': ", task.getException());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error getting all documents for 'See All'.", e);
                            }
                        });
            }
        });
        popularBooksList = new ArrayList<>();
        popularBooksAdapter = new ShowAllAdapter(getContext(), popularBooksList);
        popularBooksRecycleView.setAdapter(popularBooksAdapter);

        db.collection("allBooks")
                .whereEqualTo("rate", 5) // Получаем все книги с рейтингом 5
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Временный список для всех книг с рейтингом 5
                            List<Books> allRatedBooks = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Books popularBooksModel = document.toObject(Books.class);
                                allRatedBooks.add(popularBooksModel);
                            }

                            // Шаг 1: Перемешиваем весь список полученных книг
                            Collections.shuffle(allRatedBooks);

                            // Шаг 2: Очищаем текущий список в адаптере
                            popularBooksList.clear();

                            // Шаг 3: Добавляем до 4 случайных книг (или меньше, если их не хватает)
                            int numberOfBooksToShow = Math.min(allRatedBooks.size(), 4); // Выбираем 4 или меньше, если доступно
                            for (int i = 0; i < numberOfBooksToShow; i++) {
                                popularBooksList.add(allRatedBooks.get(i));
                            }

                            // Шаг 4: Уведомляем адаптер об изменениях
                            popularBooksAdapter.notifyDataSetChanged();

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting documents.", e);
                    }
                });

        return root;
    }
}
