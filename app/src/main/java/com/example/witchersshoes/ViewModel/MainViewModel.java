package com.example.witchersshoes.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
//
import com.example.witchersshoes.Model.CategoryModel;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.Model.SliderModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private MutableLiveData<List<SliderModel>> _slider = new MutableLiveData<>();
    private MutableLiveData<List<CategoryModel>> _category = new MutableLiveData<>();
    private MutableLiveData<List<ProductModel>> _bestSeller = new MutableLiveData<>();

    public LiveData<List<ProductModel>> getBestSeller() {
        return _bestSeller;
    }

    public LiveData<List<CategoryModel>> getCategory() {
        return _category;
    }

    public LiveData<List<SliderModel>> getSlider() {
        return _slider;
    }

    public void loadSlider() {
        CollectionReference ref = firestore.collection("Banner");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle error if necessary
                    return;
                }
                List<SliderModel> lists = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshot) {
                    SliderModel list = document.toObject(SliderModel.class);
                    lists.add(list);
                }
                _slider.setValue(lists);
            }
        });
    }

    public void loadCategory() {
        CollectionReference ref = firestore.collection("Categories");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle error if necessary
                    return;
                }
                List<CategoryModel> lists = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshot) {
                    CategoryModel list = document.toObject(CategoryModel.class);
                    String documentId = document.getId();
                    list.setId(documentId);
                    lists.add(list);
                }
                _category.setValue(lists);
            }
        });
    }

    public void loadBestSeller() {
        CollectionReference ref = firestore.collection("Products");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore Error", error.getMessage());
                    return;
                }

                List<ProductModel> lists = new ArrayList<>();
                lists.clear();
                for (QueryDocumentSnapshot document : snapshot) {
                    try {
                        ProductModel list = document.toObject(ProductModel.class);
                        String documentId = document.getId();
                        Boolean invisible = document.getBoolean("invisible");
                        if(invisible != true) {
                            list.setID(documentId);
                            Log.d("ProductModel", "Title: " + list.getTitle() + ", Price: " + list.getPrice());
                            lists.add(list);
                        }
                    } catch (Exception e) {
                        Log.e("Data Parsing Error", e.getMessage());
                    }
                }
                _bestSeller.setValue(lists);
            }
        });
    }
}
