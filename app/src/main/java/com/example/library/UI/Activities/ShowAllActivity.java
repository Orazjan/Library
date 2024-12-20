package com.example.library.UI.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.Model.showAllModel;
import com.example.library.R;
import com.example.library.UI.Adapters.showAllAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private showAllAdapter showAllAdapter;
    private List<showAllModel> showAllModelList;
    private FirebaseFirestore firebaseFirestore;
    private static final Map<String, String> COLLECTION_MAP = new HashMap<>();

    static {
        COLLECTION_MAP.put("fantastic", "FantastikaBooks");
        COLLECTION_MAP.put("loveromany", "LoveRomanyBooks");
        COLLECTION_MAP.put("biznes", "BiznesBooks");
        COLLECTION_MAP.put("obshestvo", "ObshestwoBooks");
        COLLECTION_MAP.put("poeziyabooks", "PoeziyaBooks");
        COLLECTION_MAP.put("psy", "PsyBooks");
    }

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

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_View);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        showAllModelList = new ArrayList<>();
        showAllAdapter = new showAllAdapter(this, showAllModelList);
        recyclerView.setAdapter(showAllAdapter);

        String type = getIntent().getStringExtra("type");
        fetchData(type);
    }

    private void fetchData(String type) {
        String collection = "new_books"; // Коллекция по умолчанию
        String typeLower = type != null ? type.toLowerCase() : null; // Преобразуем type в нижний регистр один раз

        if (typeLower != null && COLLECTION_MAP.containsKey(typeLower)) {
            collection = COLLECTION_MAP.get(typeLower);
        }

        firebaseFirestore.collection(collection)
                .whereEqualTo("type", typeLower != null && COLLECTION_MAP.containsKey(typeLower) ? typeLower : collection.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            showAllModelList.clear(); // Очищаем список перед добавлением
                            for (DocumentSnapshot document : task.getResult()) {
                                showAllModel model = document.toObject(showAllModel.class);
                                if (model != null) {
                                    showAllModelList.add(model);
                                } else {
                                    Log.e("ShowAllActivity", "Ошибка преобразования DocumentSnapshot в showAllModel");
                                }
                            }
                            showAllAdapter.notifyDataSetChanged(); // Уведомляем адаптер один раз
                        } else {
                            Log.e("ShowAllActivity", "Ошибка получения данных из Firestore", task.getException());
                        }
                    }
                });
    }
}