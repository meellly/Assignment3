package edu.cse4230.schilbe.assignment3;

//Melissa Schilbe
//CSE 4230
//Assignment 3
//Source code can be found on GitHub

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    ListView listView = null;
    ProductAdapter productAdapter = null;
    int itemIndex = -1;
    String currentOperation = "";
    ImageButton btnAdd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.Page1);

        productAdapter = new ProductAdapter();

        productAdapter.addProduct("Galaxy Note 8", "$700", R.drawable.galaxy);
        productAdapter.addProduct("iPhone", "$900", R.drawable.iphone);
        productAdapter.addProduct("Nikon", "$1500.45", R.drawable.nikon);

        listView.setAdapter(productAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemIndex = i;
                currentOperation = "";
                Toast toast = null;

                long viewId = view.getId();
                if (viewId == R.id.btnDelete) {
                    productAdapter.removeProduct(i);
                    listView.setAdapter(productAdapter);
                    toast = Toast.makeText(view.getContext(), "Product was successfully deleted", Toast.LENGTH_SHORT);
                    toast.show();
                }

                if (viewId == R.id.btnUpdate) {
                    currentOperation = "EDIT";

                    Intent intent = new Intent(view.getContext(), EditItem.class);
                    intent.putExtra("OperationType", currentOperation);
                    intent.putExtra("ProductName", ((Product) productAdapter.getItem(i)).getName());
                    intent.putExtra("ProductPrice", ((Product) productAdapter.getItem(i)).getPrice());
                    if (((Product) productAdapter.getItem(i)).getImage() != -1) {
                        intent.putExtra("ProductImageId", ((Product) productAdapter.getItem(i)).getImage());
                    } else {
                        intent.putExtra("ProductImageId", ((Product) productAdapter.getItem(i)).getImageUri());
                    }
                    startActivityForResult(intent, 555);
                }
            }
        });

        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditItem.class);
                intent.putExtra("OperationType", "ADD");
                startActivityForResult(intent, 555);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 555 && resultCode == RESULT_OK && null != data) {
                if (data.getStringExtra("OperationType").equals("ADD")) {
                    Toast.makeText(this, "Product was successfully added", Toast.LENGTH_SHORT).show();

                    //Grab image
                    File imgFile = new File(data.getStringExtra("ImagePath"));
                    Uri uri = Uri.fromFile(imgFile);

                    productAdapter.addProduct(data.getStringExtra("ProductName"), data.getStringExtra("ProductPrice"), uri);
                    listView.setAdapter(productAdapter);

                }

                if (data.getStringExtra("OperationType").equals("EDIT")) {
                    Toast.makeText(this, "Product was successfully updated", Toast.LENGTH_SHORT).show();

                    //Grab image
                    File imgFile = new File(data.getStringExtra("ImagePath"));
                    Uri uri = Uri.fromFile(imgFile);

                    ((Product) productAdapter.getItem(itemIndex)).setName(data.getStringExtra("ProductName"));
                    ((Product) productAdapter.getItem(itemIndex)).setPrice(data.getStringExtra("ProductPrice"));
                    ((Product) productAdapter.getItem(itemIndex)).setImageUri(uri);
                    ((Product) productAdapter.getItem(itemIndex)).setImage(-1);
                    listView.setAdapter(productAdapter);

                }
            } else {
                Toast.makeText(this, "Product was not updated", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Must select an image", Toast.LENGTH_SHORT).show();
        }
    }
}
