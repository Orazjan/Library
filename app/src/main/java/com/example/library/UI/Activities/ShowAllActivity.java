package com.example.library.UI.Activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.Model.Books;
import com.example.library.R;
import com.example.library.UI.Adapters.ShowAllAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {
    Context context;
    RecyclerView recycler_View;
    ShowAllAdapter showAllAdapter;
    List<Books> showAllModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_all);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String type = getIntent().getStringExtra("type");

        Log.d("TYPE", type);

        recycler_View = findViewById(R.id.recycler_View);
        recycler_View.setLayoutManager((new GridLayoutManager(this, 2)));
        showAllModelList = new ArrayList<>();
        context = this;
        showAllAdapter = new ShowAllAdapter(context, showAllModelList);
        recycler_View.setAdapter(showAllAdapter);
        FirebaseFirestore fireBaseStone = FirebaseFirestore.getInstance();

        if (type.equalsIgnoreCase("biznes")) {
            fireBaseStone.collection("allBooks").whereEqualTo("type", "biznes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("Firestore | Biznes", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        showAllModelList.clear(); // Очищаем список перед добавлением новых данных
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            Books showAllModel = documentSnapshot.toObject(Books.class);
                            showAllModelList.add(showAllModel);
                        }
                        showAllAdapter.notifyDataSetChanged(); // Обновляем адаптер после получения всех изменений
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("detektiv")) {
            fireBaseStone.collection("allBooks").whereEqualTo("type", "detektiv").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("Firestore | detektiv", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        showAllModelList.clear(); // Очищаем список перед добавлением новых данных
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            Books showAllModel = documentSnapshot.toObject(Books.class);
                            showAllModelList.add(showAllModel);
                        }
                        showAllAdapter.notifyDataSetChanged(); // Обновляем адаптер после получения всех изменений
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("fantastic")) {
            fireBaseStone.collection("allBooks").whereEqualTo("type", "fantastic").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("Firestore | fantastic", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        showAllModelList.clear(); // Очищаем список перед добавлением новых данных
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            Books showAllModel = documentSnapshot.toObject(Books.class);
                            showAllModelList.add(showAllModel);
                        }
                        showAllAdapter.notifyDataSetChanged(); // Обновляем адаптер после получения всех изменений
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("loveRomany")) {
            fireBaseStone.collection("allBooks").whereEqualTo("type", "loveRomany").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("Firestore | loveRomany", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        showAllModelList.clear(); // Очищаем список перед добавлением новых данных
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            Books showAllModel = documentSnapshot.toObject(Books.class);
                            showAllModelList.add(showAllModel);
                        }
                        showAllAdapter.notifyDataSetChanged(); // Обновляем адаптер после получения всех изменений
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("obshestvo")) {
            fireBaseStone.collection("allBooks").whereEqualTo("type", "obshestvo").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("Firestore | obshestvo", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        showAllModelList.clear(); // Очищаем список перед добавлением новых данных
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            Books showAllModel = documentSnapshot.toObject(Books.class);
                            showAllModelList.add(showAllModel);
                        }
                        showAllAdapter.notifyDataSetChanged(); // Обновляем адаптер после получения всех изменений
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("poeziyaBooks")) {
            fireBaseStone.collection("allBooks").whereEqualTo("type", "poeziya").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("Firestore | poeziya", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        showAllModelList.clear(); // Очищаем список перед добавлением новых данных
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            Books showAllModel = documentSnapshot.toObject(Books.class);
                            showAllModelList.add(showAllModel);
                        }
                        showAllAdapter.notifyDataSetChanged(); // Обновляем адаптер после получения всех изменений
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("psy")) {
            fireBaseStone.collection("allBooks").whereEqualTo("type", "psy").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("Firestore", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        showAllModelList.clear(); // Очищаем список перед добавлением новых данных
                        for (DocumentSnapshot documentSnapshot : snapshots) {
                            Books showAllModel = documentSnapshot.toObject(Books.class);
                            showAllModelList.add(showAllModel);
                        }
                        showAllAdapter.notifyDataSetChanged(); // Обновляем адаптер после получения всех изменений
                    }
                }
            });
        }
    }
}
