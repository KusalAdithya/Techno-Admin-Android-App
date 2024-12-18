package com.waka.techoadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.waka.techoadmin.model.Category;
import com.waka.techoadmin.model.Product;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class AddProductFragment extends Fragment {

    private EditText nameText, descriptionText, priceText, qtyText;
    private Spinner categoryView, brandView, modelView;
    private TextView textViewLong;
    private ImageButton imageButton;
    private LinearLayout layout;
    private String category, model, brand;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private Uri imgUri;
    private ArrayList<String> imagesList = new ArrayList<>();
    private ArrayList<String> downloadImagesList;
    private ProgressBar progressBar;
    private Button addProductBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        brandView = fragment.findViewById(R.id.brandText);
        categoryView = fragment.findViewById(R.id.categoryText);
        modelView = fragment.findViewById(R.id.modelText);
        imageButton = fragment.findViewById(R.id.userDpView);
        layout = fragment.findViewById(R.id.imgLoader);
        nameText = fragment.findViewById(R.id.productNameText);
        descriptionText = fragment.findViewById(R.id.descriptionText);
        priceText = fragment.findViewById(R.id.priceText);
        qtyText = fragment.findViewById(R.id.qtyText);
        progressBar = fragment.findViewById(R.id.progressBar);
        textViewLong = fragment.findViewById(R.id.textViewLong);
        addProductBtn = fragment.findViewById(R.id.addProductBtn);
        downloadImagesList = new ArrayList<>();

        // load category ---------------------------------------------------------------------------
        db.collection("/category").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList categoryArrayList = new ArrayList<>();
                            categoryArrayList.add("Select Category");
                            for (QueryDocumentSnapshot result : task.getResult()) {
                                categoryArrayList.add(result.getId());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext()
                                    , android.R.layout.simple_spinner_item, categoryArrayList);
                            adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                            categoryView.setAdapter(adapter);
                        }
                    }
                }
        );

        // Category Spinner ------------------------------------------------------------------------
        categoryView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                category = adapterView.getItemAtPosition(position).toString();

                // load brands --------------------------------------------------------------------
                db.collection("/category/" + category + "/brand").get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList brandlArrayList = new ArrayList<>();
                                    brandlArrayList.add("Select Brand");
                                    for (QueryDocumentSnapshot result : task.getResult()) {
                                        String name = result.getId();
                                        brandlArrayList.add(name);
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                            android.R.layout.simple_spinner_item, brandlArrayList);
                                    adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                                    brandView.setAdapter(adapter);
                                }
                            }
                        }
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Brand Spinner ---------------------------------------------------------------------------
        brandView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                brand = adapterView.getItemAtPosition(position).toString();

                // load model ----------------------------------------------------------------------
                db.collection("/category/" + category + "/brand/" + brand + "/model").get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList modelArrayList = new ArrayList<>();
                                    modelArrayList.add("Select Model");
                                    for (QueryDocumentSnapshot result : task.getResult()) {
//                                String categoryName = result.getString("name");
                                        modelArrayList.add(result.getId());
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext()
                                            , android.R.layout.simple_spinner_item, modelArrayList);
                                    adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                                    modelView.setAdapter(adapter);
                                }
                            }
                        }
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Model Spinner ------------------------------------------------------------------------
        modelView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                model = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //Image Button -----------------------------------------------------------------------------
        imageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
//                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));
//                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );

        // Add Product Btn -------------------------------------------------------------------------
        addProductBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nameText.getText().toString();
                        String description = descriptionText.getText().toString();
                        String price = priceText.getText().toString();
                        String qty = qtyText.getText().toString();

                        if (name.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter product name", Toast.LENGTH_LONG).show();
                            nameText.requestFocus();
                        } else if (category.equals("Select Category")) {
                            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_LONG).show();
                            categoryView.requestFocus();
                        } else if (brand.equals("Select Brand")) {
                            Toast.makeText(getContext(), "Please select a brand", Toast.LENGTH_LONG).show();
                            brandView.requestFocus();
                        } else if (model.equals("Select Model")) {
                            Toast.makeText(getContext(), "Please select a model", Toast.LENGTH_LONG).show();
                            modelView.requestFocus();
                        } else if (description.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter description", Toast.LENGTH_LONG).show();
                            descriptionText.requestFocus();
                        } else if (price.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter product price", Toast.LENGTH_LONG).show();
                            priceText.requestFocus();
                        } else if (qty.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_LONG).show();
                            qtyText.requestFocus();
                        } else if (imgUri == null) {
                            Toast.makeText(getContext(), "Please enter product images", Toast.LENGTH_LONG).show();
                            imageButton.requestFocus();
                        } else {

                            String pId = String.valueOf(UUID.randomUUID());

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                            String formattedDate = sdf.format(new Date());


                            Product product = new Product();
                            product.setId(pId);
                            product.setName(name);
                            product.setBrand(brand);
                            product.setCategory(category);
                            product.setModel(model);
                            product.setDescription(description);
                            product.setPrice(Double.parseDouble(price));
                            product.setQty(Integer.parseInt(qty));
                            product.setDateTime(formattedDate);
                            product.setProductImage(downloadImagesList);

                            // add to realtime database --------------------------------------------
                            FirebaseDatabase.getInstance().getReference("Products").child(pId)
                                    .setValue(product)
                                    .addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Product add successfully.", Toast.LENGTH_LONG).show();
                                                        loadFragment(new AddProductFragment());
                                                    }
                                                }
                                            }
                                    ).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                    }
                }
        );


    }

    //ImageView methods
    private void addView(ImageView imageView, int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        // setting the margin in linearlayout
        params.setMargins(5, 0, 5, 0);
        imageView.setLayoutParams(params);

        // adding the image in layout
        layout.addView(imageView);

        // add img to db
        addImage(imgUri, imageView);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imgUri = result.getData().getData();

                        ImageView imageView = new ImageView(getActivity());
                        Glide.with(requireContext()).load(imgUri).into(imageView);

                        imagesList.add(String.valueOf(imgUri));

                        addView(imageView, 300, 300);
                    }
                }
            }
    );


    public void addImage(Uri imgUri1, ImageView imageView) {
        FirebaseApp.initializeApp(requireContext());
        storageReference = FirebaseStorage.getInstance().getReference();
        String path = imgUri1.getPath() + UUID.randomUUID().toString();

        StorageReference reference1 = FirebaseStorage.getInstance().getReference().child("productImages/")
                .child(path);
        reference1.putFile(imgUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                downloadImagesList.add(urlImage.toString());

                imageButton.setEnabled(true);
                addProductBtn.setEnabled(true);


                // remove selected img -------------------------------------------------------------
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        StorageReference desertRef = storageReference.child("productImages" + path);

                        // Delete the file ---------------------------------------------------------
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                downloadImagesList.remove(urlImage.toString());
                                Toast.makeText(getContext(), "Image Removed", Toast.LENGTH_LONG).show();
                                imageView.setVisibility(View.GONE);
                                textViewLong.setVisibility(View.INVISIBLE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getContext(), "Image Removing Failed", Toast.LENGTH_LONG).show();
                            }
                        });
                        return true;
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Image add Fail.", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double prograss = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress((int) prograss);
                textViewLong.setVisibility(View.VISIBLE);
                addProductBtn.setEnabled(false);
                imageButton.setEnabled(false);
            }
        });

    }


    public void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }
}