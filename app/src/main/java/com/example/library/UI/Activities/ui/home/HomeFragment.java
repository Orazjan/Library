package com.example.library.UI.Activities.ui.home;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.Model.CategoryModel;
import com.example.library.R;
import com.example.library.UI.Adapters.CategoryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;
    private RecyclerView catRecycleView;

    FirebaseFirestore db;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        catRecycleView = root.findViewById(R.id.rec_category);
        db = FirebaseFirestore.getInstance();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        catRecycleView.setLayoutManager(gridLayoutManager);

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


        return root;
    }
}
