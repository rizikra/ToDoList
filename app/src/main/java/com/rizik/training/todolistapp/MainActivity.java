package com.rizik.training.todolistapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvTodos;
    FloatingActionButton fabAdd;
    EditText edtTodo;
    // Langkah 1 Siapkan Data
    // String[] data = {"Dota 2","Sleep","Dota 2","Eat",}; // diganti menjadi ArrayList berikut :
    ArrayList<String> data = new ArrayList<String>();

    // Langkah 3 Buat Adapter untuk List View
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  Langkah 1 Siapkan data

        //createTodos();

        // Langkah 9.2 panggil method loadDataFromPreferences() agar data dari SP dimasukkan ke array list saat activity pertama dipanggil
        loadDataFromPrefefrences();

        // Langkah 2 Buat List View
        lvTodos = findViewById(R.id.list_aja); // define list view

        // Langkah 3 Buat Adapter dan masukkan parameter yg dibutuhkan. (context, layout_content,tv,data)
        //      parameter data diambil dari Langkah Pertama.
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_data,R.id.tv_list,data);

        // Langkah 4 Set Adapter kepada List View
        lvTodos.setAdapter(arrayAdapter);

        // Langkah 5 Define FAB dan buat onClickListener nya.
        fabAdd = findViewById(R.id.fab);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Langkah 6 Method di bawah ini dibuat sendiri di bawah
                onClickFabAdd();
            }
        });




        // Langkah 7.1 Buat onItemClickListener di list view untuk hapus data
        lvTodos.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Panggil method deleteItem()

                deleteItem (position);
                // Langkah 10.2 Panggil method deleteFromSP untuk menghapus data dari Shared Preferences
                deleteFromSP(position); // Sampai sini akan terjadi error karena key di SP tidak berurutan
            }
        });

        // Langkah 12.4 Buat OnItemClickListener dan panggil method showDialogEdit()
        lvTodos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

                AlertDialog.Builder builderPilihAksi = new AlertDialog.Builder(MainActivity.this);
                builderPilihAksi.setTitle("''"+arrayAdapter.getItem(position)+"''");
                builderPilihAksi.setMessage("So, what would you do?");

                //Fitur Mengubah Kegiatan
                builderPilihAksi.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Panggil Method
                        showDialogEdit(position);
                    }
                });

                //Fitur Menghapus Kegiatan
                builderPilihAksi.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Panggil Method
                        deleteItem(position);
                    }
                });

                builderPilihAksi.setNeutralButton("Cancel", null);
                builderPilihAksi.create();
                builderPilihAksi.show();
            }
        });
    }



    // Langkah 1 Siapkan Dummy Data
    private void createTodos(){
        data.add("Eat");
        data.add("Sleep");
        data.add("Football");
        data.add("Coding");
    }

    // Langkah 7 Buat Method ketika FAB Add di click untuk menambahkan data
    private void onClickFabAdd(){
        //Cara pertama tambah edit text ke dialog
        //EditText edtTodo = new EditText(this);

        //Cara dua tambah edit text ke dialog
        //proses ini disebut dengan inflate layout
        View view = View.inflate(this,R.layout.dialog_add_view, null);

        //EditText ini dideklarisikan di atas di dalam class
        edtTodo = view.findViewById(R.id.edt_todo);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("What do you want to do?");
        dialog.setView(view);
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (edtTodo.length() == 0){
                    Toast.makeText(getApplicationContext(), "No data added", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Langkah 8.2 Hitung size dari arraylist data untuk dijadikan calon key untuk SP :

                    int newKey = data.size();

                    String item = edtTodo.getText().toString();

                    data.add(item); // tambah data ke object ArrayList data.
                    arrayAdapter.notifyDataSetChanged(); // merefresh list view

                    // Langkah 8.3 Tambahkan data ke Shared Preferences
                    // Panggil method addToSP() untuk menyimpan data ke SP
                    addToSP(newKey, item);

                    //Toast.makeText(getApplicationContext(),String.valueOf(newKey), Toast.LENGTH_LONG).show();

                }
            }
        });
        dialog.setNegativeButton("Cancel",null);
        dialog.create();
        dialog.show();
    }


    //7.2 Buat methode deleteItem untuk menghapus dari data array list dan mengupdate view
    private void deleteItem(int position){ // beri parameter position untuk mewadahi action dari list view
        // konstanta untuk menampung data position yang di passing dari ItemLongClickListener

        final int index = position;

        // Buat alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Are you sure want to delete ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // hapus item dari array list data berdasarkan index/position dari item di list view

                data.remove(index); // index didapat position parameter

                // Langkah 11.2 Panggil method reGeneratedAndSortSP()
                // reGeneratedAndSortSP();
                reGenerateAndSortSP();

                // suruh adapter untuk notify ke List View kalau data telah berubah
                // merefresh list view
                arrayAdapter.notifyDataSetChanged();
            }
        });
        dialog.setNegativeButton("No", null);
        dialog.create().show();
    }

    // Langkah 8.1 Buat method untuk input data ke Shared Preferences
    private void addToSP (int key, String item){
        // Buat key untuk SP diambil dari size terakhir array list dta
        String newKey = String.valueOf(key);
        SharedPreferences todosPref = getSharedPreferences("todosPref", MODE_PRIVATE);
        SharedPreferences.Editor todosPrefEditor = todosPref.edit();

        // Simpan ke SP dengan key dari size terakhir array list
        todosPrefEditor.putString(newKey, item);
        todosPrefEditor.apply();

        // atau :
        // todosPrefEditor.commit();
    }

    // Langkah 9.1 Load Data dari Shared Preferences
    // Buat method loadPreferences
    private void loadDataFromPrefefrences(){
        SharedPreferences todosPref = getSharedPreferences("todosPref", MODE_PRIVATE);
        // Cek dalam SP ada data atau tidak
        if (todosPref.getAll().size() > 0){ //2

            // Masukkan semua data di SP ke array list data
            for (int i = 0; i < todosPref.getAll().size(); i++){// i < 2
                String key = String.valueOf(i);
                String item = todosPref.getString(key, null);
                data.add(item);
            }
        }

    }

    // Langkah 10.1 Menghapus data dari Shared Preferences
    // Buat method hapus data dari Shared Preferences
    private void deleteFromSP(int position){
        String key = String.valueOf(position);
        SharedPreferences todosPref = getSharedPreferences("todosPref", MODE_PRIVATE);
        SharedPreferences.Editor todosPrefEditor = todosPref.edit();
        todosPrefEditor.remove(key);
        todosPrefEditor.apply();
    }

    // Langkah 11.1 Fix Error di langkah 10 untuk mengurutkan kembali key dan value di dalam Shared Preference
    private void reGenerateAndSortSP(){
        SharedPreferences todosPref = getSharedPreferences("todosPref", MODE_PRIVATE);
        SharedPreferences.Editor todosPrefEditor = todosPref.edit();
        // Hapus semua data di Shared Preference
        todosPrefEditor.clear();
        todosPrefEditor.apply();

        // Isi ulang Shared Preferences dengan data dari array list yang sudah otomatis terurut
        for (int i = 0; i < data.size(); i++){
            String key = String.valueOf(i);
            todosPrefEditor.putString(key, data.get(i));
        }
         /*int i = 0;
        for (String item: data) {
            String key = String.valueOf(i);
            todosPrefEditor.putString(key,item);
            i++;
        }*/
        todosPrefEditor.apply();

    }

    // Langkah 12.1 Membuat fitur Edit Item
    // Buat method untuk menampilkan AlertDialog data yang hendak diedit

    private void showDialogEdit (final int position){
        View view = View.inflate(this, R.layout.dialog_add_view, null);

        // EditText ini dideklarasikan di atas di dalam class
        edtTodo = view.findViewById(R.id.edt_todo);

        // EditText diisi dengan data dari list view yang dipilih berdasarkan parameter position
        edtTodo.setText(arrayAdapter.getItem(position)); // Diambil dari adapter list view

        //edtTodo.setText(data.get(position)); //diambil dari array list : alternatif dari cara diatas ini.

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("What you want to change?");
        dialog.setView(view);
        dialog.setPositiveButton("Change It", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (edtTodo.length() == 0){
                    Toast.makeText(getApplicationContext(), "No data added", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Langkah 12.3 Panggil method editItem() di bawah yang telah dibuat pada langkah 12.2
                    editItem (position, edtTodo.getText().toString());
                }
            }
        });
        dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();

    }

    // Langkah 12.2 Buat method untuk mengubah item dengan parameter position dan text item baru
    private void editItem(int position, String newItem){
        // Set data di array dengan value baru berdasarkan index/position
        data.set (position, newItem);

        // Jangan lupa Shared Preferences di generate ulang
        reGenerateAndSortSP();

        // Refresh list view
        arrayAdapter.notifyDataSetChanged();
    }
}

